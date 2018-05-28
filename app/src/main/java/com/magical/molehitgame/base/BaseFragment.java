package com.magical.molehitgame.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;

/**
 * Created by daniel.xiao on 2017/3/17.
 */

public abstract class BaseFragment extends Fragment {

    private static final String TAG = BaseFragment.class.getSimpleName();
    private static final String PARAM_BASEFRAGMENT_STATE = "basefragment_state";

    protected View mRootView;
    protected Context mContext;

    /**
     * 状态
     */
    protected Bundle mBundle;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mBundle = savedInstanceState.getBundle(PARAM_BASEFRAGMENT_STATE);
        }
        if (mBundle == null) {
            mBundle = getArguments();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mBundle != null) {
            outState.putBundle(PARAM_BASEFRAGMENT_STATE, mBundle);
        }
    }

    /**
     * 使用FragmentTabHost后,每次切换Fragment都会调用onDestroyView导致需要重新走onCreateView
     * 所以此处要特殊处理下
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        if (mRootView != null) {//初始化过了 就不需要再创建
            ViewGroup parent = (ViewGroup) mRootView.getParent();//清除自己 再返回 否则会重复设置了父控件
            if (parent != null) {
                parent.removeView(mRootView);
            }
            return mRootView;
        }

        if (0 != getLayoutName()) {

            if (isNeedShowLoadingView()) {
                //需要加载动画
                //mRootView = inflateWitchBlankLoading(inflater, getLayoutName());
            } else {
                //不需要加载动画
                mRootView = inflater.inflate(getLayoutName(), container, false);
            }
            ButterKnife.bind(this, mRootView);

            doInit();
            doLoadData();
            return mRootView;
        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    protected abstract void doLoadData();

    protected abstract void doInit();

    /**
     * 是否开启加载动画
     * 默认不开启
     * 如果需要加载动画 可以在子类重写此方法 并返回 true
     */
    protected abstract boolean isNeedShowLoadingView();

    /**
     * 获取layout的名字
     *
     * @return String
     */
    protected abstract int getLayoutName();

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Nullable
    public <T extends View> T findViewById(@IdRes int idsId) {

        if (null != mRootView) {
            return mRootView.findViewById(idsId);
        }
        return null;
    }
}
