package org.cdlib.cursive.core.rx;

import io.reactivex.Observable;
import io.reactivex.Single;
import org.cdlib.cursive.core.CWorkspace;

public interface RxCWorkspace extends CWorkspace {
  Observable<RxCCollection> memberCollectionsAsync();
  Single<RxCCollection> createCollectionAsync();
}
