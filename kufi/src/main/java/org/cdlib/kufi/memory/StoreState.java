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
import static org.cdlib.kufi.Transaction.initTransaction;
import static org.cdlib.kufi.Version.initVersion;

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
  private final Map<UUID, Tombstone<?>> deadResources;

  private final Multimap<UUID, Link> linksBySource;
  private final Multimap<UUID, Link> linksByTarget;

  // ------------------------------------------------------------
  // Constructors

  StoreState() {
    this(initTransaction(), HashMap.empty(), HashMap.empty(), HashMultimap.withSet().empty(), HashMultimap.withSet().empty());
  }

  private StoreState(Transaction tx, Map<UUID, Resource<?>> liveResources, Map<UUID, Tombstone<?>> deadResources, Multimap<UUID, Link> linksBySource, Multimap<UUID, Link> linksByTarget) {
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

  Option<Tombstone<?>> findTombstone(UUID id) {
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
    var ws = new MemoryWorkspace(id, initVersion(txNext), store);
    var lrNext = liveResources.put(id, ws);

    var storeNext = new StoreState(txNext, lrNext, deadResources, linksBySource, linksByTarget);
    return CreateResult.of(ws, storeNext);
  }

  <P extends Resource<P>, C extends Resource<C>> CreateResult<C> createChild(MemoryStore store, P parent, Builder<C> builder, Builder<P> pBuilder) {
    var parentId = parent.id();
    var parentCurrent = current(parent);
    var parentVersionCurrent = parentCurrent.version();

    var childId = newId();
    var txNext = tx.next();

    var child = builder.build(childId, initVersion(txNext), store);
    var parentNext = pBuilder.build(parentId, parentVersionCurrent.next(txNext), store);

    var p2c = Link.create(parent, PARENT_OF, child, txNext);
    var c2p = Link.create(child, CHILD_OF, parent, txNext);

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

    var liveBySource = linksBySource(id).filter(Link::isLive);
    var liveByTarget = linksByTarget(id).filter(Link::isLive);

    var liveLinks = List.of(liveBySource, liveByTarget).flatMap(Function.identity());
    var liveToDead = liveLinks.map(l -> Tuple.of(l, l.deleted(txNext)));

    var lbsNext = liveToDead.foldLeft(linksBySource, (lbs, t) -> lbs.replace(t._1.sourceId(), t._1, t._2));
    var lbtNext = liveToDead.foldLeft(linksByTarget, (lbt, t) -> lbt.replace(t._1.sourceId(), t._1, t._2));

    var lrNext = liveResources.remove(id);
    var drNext = deadResources.put(id, new Tombstone(txNext, current));

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
