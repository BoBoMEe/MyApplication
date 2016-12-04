package com.bobomee.android.common.mvp;

import java.lang.ref.WeakReference;

/**
 * Created on 2016/12/3.下午4:55.
 *
 * @author bobomee.
 * @description
 */

public abstract class MvpBasePresenter<V extends MvpView> implements MvpPresenter<V> {

  private WeakReference<V> viewRef;

  /**
   * 将view attach到presenter
   */
  @Override
  public void attachView(V view) {
    viewRef = new WeakReference<>(view);
  }

  /**
   * 会在view被destroyed的时候被调用. 通常该方法会发生在
   * <code>Activity.detachView()</code> 或 <code>Fragment.onDestroyView()</code>
   */
  @Override
  public void detachView(boolean retainInstance) {
    if (viewRef != null) {
      viewRef.clear();
      viewRef = null;
    }
  }

  @Override
  public V getView() {
    return viewRef == null ? null : viewRef.get();
  }

  @Override
  public void resume() {

  }

  @Override
  public void pause() {

  }

  @Override
  public void destroy() {

  }

  /**
   * 检查一个view是否被attached到该presenter，须在调用 {@link #getView()} 获得view前调用本方法.
   */
  protected boolean isViewAttached() {
    return viewRef != null && viewRef.get() != null;
  }
}
