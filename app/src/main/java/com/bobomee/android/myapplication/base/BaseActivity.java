/*
 * Copyright (C) 2016.  BoBoMEe(wbwjx115@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.bobomee.android.myapplication.base;

import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import com.bobomee.android.common.mvp.MvpPresenter;
import com.bobomee.android.common.mvp.MvpView;
import com.bobomee.android.common.util.UIUtil;
import com.bobomee.android.data.di.internal.HasComponent;
import com.bobomee.android.htttp.receiver.NetWorkReceiver;
import com.bobomee.android.htttp.util.HttpNetUtil;
import com.bobomee.android.myapplication.R;
import com.bobomee.android.myapplication.di.ReposComponent;
import com.bobomee.android.myapplication.mvp.view.MvpActivity;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * Created on 2016/10/27.下午5:27.
 *
 * @author bobomee.
 * @description
 */

public abstract class BaseActivity<V extends MvpView, P extends MvpPresenter<V>> extends MvpActivity<V,P>
    implements HttpNetUtil.Networkreceiver,HasComponent<ReposComponent> {

  protected Toolbar mToolbar;
  private CompositeSubscription mCompositeSubscription;
  private BroadcastReceiver mReceiver;
  protected ReposComponent mInitialize;

  public void addSubscription(Subscription s) {
    if (this.mCompositeSubscription == null) {
      this.mCompositeSubscription = new CompositeSubscription();
    }
    if (null != s) {
      this.mCompositeSubscription.add(s);
    }
  }

  private void unsubscribe() {
    if (this.mCompositeSubscription != null) {
      mCompositeSubscription.unsubscribe();
    }
  }

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    registerReceiver();

    HttpNetUtil.INSTANCE.addNetWorkListener(this);

    mInitialize = ReposComponent.Init.initialize(this);
  }

  private void registerReceiver() {

    mReceiver = new NetWorkReceiver();

    IntentFilter intentFilter = new IntentFilter();
    intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);

    registerReceiver(mReceiver, intentFilter);

  }

  @Override protected void onDestroy() {
    super.onDestroy();

    unregisterReceiver(mReceiver);

    UIUtil.getMainThreadHandler().removeCallbacksAndMessages(null);

    HttpNetUtil.INSTANCE.removeNetWorkListener(this);

    unsubscribe();
  }

  @Override public void setContentView(@LayoutRes int layoutResID) {
    super.setContentView(layoutResID);
    setupToolbar();
  }

  @Override public void setContentView(View view) {
    super.setContentView(view);
    setupToolbar();
  }

  @Override public void setContentView(View view, ViewGroup.LayoutParams params) {
    super.setContentView(view, params);
    setupToolbar();
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        FragmentManager fm = getFragmentManager();
        if (fm.getBackStackEntryCount() > 0) {
          fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        } else {
          onBackPressed();
        }
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  @Override public void onBackPressed() {
    super.onBackPressed();
    supportFinishAfterTransition();
  }

  private void setupToolbar() {
    if (null == getSupportActionBar()) {
      mToolbar = ButterKnife.findById(this, R.id.toolbar);
      if (null != mToolbar) setSupportActionBar(mToolbar);
    }
    ActionBar actionBar = getSupportActionBar();
    if (null != actionBar) actionBar.setDisplayHomeAsUpEnabled(true);
  }

  @Override public void onConnected(boolean collect) {

    if (!collect) {
      UIUtil.showToastSafe("页面飞走了");
    } else {
      // start load ,if not loaded
    }

  }

  @Override public ReposComponent getComponent() {
    return mInitialize;
  }
}
