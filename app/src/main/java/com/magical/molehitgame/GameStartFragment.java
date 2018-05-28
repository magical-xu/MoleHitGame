package com.magical.molehitgame;

import android.view.View;
import butterknife.BindView;
import com.magical.molehitgame.base.BaseFragment;

/**
 * Created by magical.zhang on 2018/5/21.
 * Description : 游戏开始页
 */
public class GameStartFragment extends BaseFragment {

    @BindView(R.id.id_start_next) View mNextView;

    @Override
    protected void doLoadData() {
    }

    @Override
    protected void doInit() {

        mNextView.setSelected(true);
        mNextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ((GameMainActivity) mContext).changePage(1);
            }
        });
    }

    @Override
    protected boolean isNeedShowLoadingView() {
        return false;
    }

    @Override
    protected int getLayoutName() {
        return R.layout.fragment_game_start;
    }
}
