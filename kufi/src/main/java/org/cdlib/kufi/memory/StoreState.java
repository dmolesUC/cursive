package org.cdlib.kufi.memory;

import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.NoArgGenerator;
import io.vavr.Tuple;
import io.vavr.collection.*;
import io.vavr.control.Option;
import org.cdlib.kufi.*;

import java.security.SecureRandom;
import java.util.UUID;
import java.util.function.Function;

import static org.cdlib.kufi.LinkType.CHILD_OF;
import static org.cdlib.kufi.LinkType.PARENT_OF;
import static org.cdlib.kufi.ResourceType.WORKSPACE;
import static org.cdlib.kufi.Transaction.initTransaction;

class StoreState {

  // ------------------------------------------------------------
  // Class fields

  /**
   * Separate SecureRandom instance per thread to avoid contention
   */
  private static final ThreadLocal<NoArgGenerator> generator = ThreadLocal.withInitial(
    () -> Generators.randomBasedGenerator(new SecureRandom())
  );

  // ------------------------------------------------------------
  // Instance fields

  private final Transaction tx;

  private final Map<UUID, Resource<?>> liveResources;
  private final Map<UUID, Resource<?>> deadResources;

  private final Multimap<UUID, Link> linksBySource;
  private final Multimap<UUID, Link> linksByTarget;

  // ------------------------------------------------------------
  // Constructors

  StoreState() {
    this(initTransaction(), HashMap.empty(), HashMap.empty(), HashMultimap.withSet().empty(), HashMultimap.withSet().empty());
  }

  private StoreState(Transaction tx, Map<UUID, Resource<?>> liveResources, Map<UUID, Resource<?>> deadResources, Multimap<UUID, Link> linksBySource, Multimap<UUID, Link> linksByTarget) {
    this.tx = tx;
    this.liveResources = liveResources;
    this.deadResources = deadResources;
    this.linksBySource = linksBySource;
    this.linksByTarget = linksByTarget;
  }


  // ------------------------------------------------------------
  // Package-private instance methods

  Transaction transaction() {
    return tx;
  }

  // ------------------------------
  // Finders

  Option<Resource<?>> find(UUID id) {
    return liveResources.get(id);
  }

  Option<Resource<?>> findTombstone(UUID id) {
    return deadResources.get(id);
  }

  <R extends Resource<R>> Traversable<R> findChildrenOfType(UUID id, ResourceType<R> type) {
    return findChildren(id)
      .flatMap(r -> r.as(type));
  }

  Option<Resource<?>> findParent(Resource<?> child) {
    return Option.narrow(linksBySource(child.id())
      .filter(l -> l.type() == CHILD_OF)
      .filter(Link::isLive)
      .map(Link::target)
      .headOption());
  }

  // ------------------------------
  // Creators & Deletors

  CreateResult<Workspace> createWorkspace(MemoryStore store) {
    var id = newId();
    var txNext = tx.next();
    var ws = MemoryResource.createNew(WORKSPACE, id, txNext, store);
    var lrNext = liveResources.put(id, ws);

    var storeNext = new StoreState(txNext, lrNext, deadResources, linksBySource, linksByTarget);
    return CreateResult.of(ws, storeNext);
  }

  <P extends Resource<P>, C extends Resource<C>> CreateResult<C> createChild(MemoryStore store, P parent, ResourceType<C> childType) {
    var parentId = parent.id();
    var parentCurrent = current(parent);

    var childId = newId();
    var txNext = tx.next();

    var child = MemoryResource.createNew(childType, childId, txNext, store);
    var parentNext = parentCurrent.nextVersion(txNext);

    var p2c = Link.create(parentNext, PARENT_OF, child, txNext);
    var c2p = Link.create(child, CHILD_OF, parentNext, txNext);

    var lrNext = liveResources
      .put(parentId, parentNext)
      .put(childId, child);

    var lbsNext = linksBySource
      .put(parentId, p2c)
      .put(childId, c2p);

    var lbtNext = linksByTarget
      .put(childId, p2c)
      .put(parentId, c2p);

    var stateNext = new StoreState(txNext, lrNext, deadResources, lbsNext, lbtNext);
    return CreateResult.of(child, stateNext);
  }

  <R extends Resource<R>> StoreState delete(R r) {
    var childCount = countChildren(r.id());
    if (childCount > 0) {
      throw new IllegalStateException("Can't delete " + r + "; " + childCount + " children");
    }
    return deleteRecursive(r);
  }

  <R extends Resource<R>> StoreState deleteRecursive(R r) {
    return deleteRecursive(r, tx.next());
  }

  // ------------------------------------------------------------
  // Private instance methods

  private Traversable<Resource<?>> findChildren(UUID id) {
    return linksBySource(id)
      .filter(l -> l.type() == PARENT_OF)
      .filter(Link::isLive)
      .map(Link::target);
  }

  /**
   * Recursively delete the specified resource and its children without incrementing
   * the transaction.
   *
   * @param r The resource to delete.
   * @param txNext The final transaction.
   * @return The final state.
   */
  private StoreState deleteRecursive(Resource<?> r, Transaction txNext) {
    var current = current(r);
    var id = current.id();
    var tombstone = current.delete(txNext);

    var liveBySource = linksBySource(id).filter(Link::isLive);
    var liveByTarget = linksByTarget(id).filter(Link::isLive);

    var lrNext = liveResources.remove(id);
    var drNext = deadResources.put(id, tombstone);

    var l2dBySource = liveBySource.map(l -> Tuple.of(l, l.deleted(tombstone, l.target().nextVersion(txNext), txNext)));
    var l2dByTarget = liveByTarget.map(l -> Tuple.of(l, l.deleted(l.source().nextVersion(txNext), tombstone, txNext)));
    var liveToDead = List.of(l2dBySource, l2dByTarget).flatMap(Function.identity());

    var lbsNext = liveToDead.foldLeft(linksBySource, (lbs, t) -> lbs.replace(t._1.sourceId(), t._1, t._2));
    var lbtNext = liveToDead.foldLeft(linksByTarget, (lbt, t) -> lbt.replace(t._1.targetId(), t._1, t._2));

    var stateNext = new StoreState(txNext, lrNext, drNext, lbsNext, lbtNext);

    var children = liveBySource.filter(l -> l.type() == PARENT_OF).map(Link::target);
    return children.foldLeft(stateNext, (storeState, r1) -> storeState.deleteRecursive(r1, txNext));
  }

  private <R extends Resource<R>> R findAs(UUID id, ResourceType<R> type) {
    return liveResources.get(id).flatMap(r -> r.as(type)).getOrElseThrow(() -> new ResourceNotFoundException(id, type));
  }

  private Resource<?> current(Resource<?> resource) {
    var id = resource.id();
    var type = resource.type();
    return findAs(id, type);
  }

  private Traversable<Link> linksBySource(UUID id) {
    return linksBySource.get(id)
      .getOrElse(HashSet.empty());
  }

  private Traversable<Link> linksByTarget(UUID id) {
    return linksByTarget.get(id)
      .getOrElse(HashSet.empty());
  }

  private int countChildren(UUID id) {
    return findChildren(id).size();
  }

  // ------------------------------------------------------------
  // Private class methods

  private static UUID newId() {
    return generator.get().generate();
  }

  // ------------------------------------------------------------
  // Helper classes

}
