package org.cdlib.cursive.store.memory;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;
import org.cdlib.cursive.core.*;
import org.cdlib.cursive.core.rx.*;
import org.cdlib.cursive.store.rx.RxStore;
import org.cdlib.cursive.store.util.RxUtils;

public class RxMemoryStore implements RxStore {

  // ------------------------------
  // Fields

  private final MemoryStore store;

  // ------------------------------
  // Constructors

  public RxMemoryStore() {
    this(new MemoryStore());
  }

  public RxMemoryStore(MemoryStore store) {
    this.store = store;
  }

  // ------------------------------
  // RxStore

  @Override
  public Observable<RxCWorkspace> workspaces() {
    return null;
  }

  @Override
  public Single<RxCWorkspace> createWorkspace() {
    return null;
  }

  @Override
  public Observable<RxCCollection> collections() {
    return null;
  }

  @Override
  public Single<RxCCollection> createCollection() {
    return null;
  }

  @Override
  public Observable<RxCObject> objects() {
    return null;
  }

  @Override
  public Single<RxCObject> createObject() {
    return null;
  }

  @Override
  public Observable<RxCFile> files() {
    return null;
  }

  @Override
  public Observable<RxCRelation> relations() {
    return null;
  }

  // ------------------------------
  // Helper classes

  private class RxCCollectionImpl implements RxCCollection {
    private final CCollection collection;

    private RxCCollectionImpl(CCollection collection) {
      this.collection = collection;
    }

    @Override
    public Maybe<RxCWorkspace> parentWorkspace() {
      return null;
    }

    @Override
    public Maybe<RxCCollection> parentCollection() {
      return null;
    }

    @Override
    public Observable<RxCObject> memberObjects() {
      return null;
    }

    @Override
    public Single<RxCObject> createObject() {
      return null;
    }

    @Override
    public Observable<RxCCollection> memberCollections() {
      return null;
    }

    @Override
    public Single<RxCCollection> createCollection() {
      return null;
    }
  }

  private class RxCObjectImpl implements RxCObject {
    private final CObject object;

    public RxCObjectImpl(CObject object) {
      this.object = object;
    }

    @Override
    public Maybe<RxCObject> parentObject() {
      return RxUtils.toMaybe(object.parentObject()).map(RxCObjectImpl::new);
    }

    @Override
    public Maybe<RxCCollection> parentCollection() {
      return RxUtils.toMaybe(object.parentCollection()).map(RxCCollectionImpl::new);
    }

    @Override
    public Observable<RxCFile> memberFiles() {
      return Observable.fromIterable(object.memberFiles()).map(RxCFileImpl::new);
    }

    @Override
    public Single<RxCFile> createFile() {
      return Single.just(object.createFile()).map(RxCFileImpl::new);
    }

    @Override
    public Observable<RxCObject> memberObjects() {
      return Observable.fromIterable(object.memberObjects()).map(RxCObjectImpl::new);
    }

    @Override
    public Single<RxCObject> createObject() {
      return null;
    }

    @Override
    public Observable<RxCObject> relatedObjects() {
      return null;
    }

    @Override
    public Single<RxCRelation> relateTo(RxCObject toObject) {
      return null;
    }

    @Override
    public Observable<RxCRelation> outgoingRelations() {
      return null;
    }

    @Override
    public Observable<RxCRelation> incomingRelations() {
      return null;
    }
  }

  private class RxCFileImpl implements RxCFile {
    private final CFile file;

    public RxCFileImpl(CFile file) {
      this.file = file;
    }

    @Override
    public Single<RxCObject> parentObject() {
      return Single.just(file.parentObject()).map(RxCObjectImpl::new);
    }
  }

  private class RxCRelationImpl implements RxCRelation {
    private final CRelation relation;

    public RxCRelationImpl(CRelation relation) {
      this.relation = relation;
    }

    @Override
    public Single<RxCObject> fromObject() {
      return Single.just(relation.fromObject()).map(RxCObjectImpl::new);
    }

    @Override
    public Single<RxCObject> toObject() {
      return Single.just(relation.toObject()).map(RxCObjectImpl::new);
    }
  }

}
