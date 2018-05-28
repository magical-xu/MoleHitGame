package com.magical.molehitgame;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;
import butterknife.BindView;
import com.magical.molehitgame.base.BaseFragment;
import com.magical.molehitgame.base.UserInfo;
import com.magical.molehitgame.game.BackgroundMusic;
import com.magical.molehitgame.game.SoundPoolManager;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by magical.zhang on 2018/5/21.
 * Description : 游戏结算页
 */
public class GameResultFragment extends BaseFragment {

    private static final String TAG = GameResultFragment.class.getSimpleName();

    @BindView(R.id.id_recycler) RecyclerView mRecyclerView;
    @BindView(R.id.id_game_rate) TextView mGameRateView;
    @BindView(R.id.id_pride_text) TextView mPrideTextView;

    private FavorPersonAdapter mAdapter;
    private SoundPoolManager mSoundManager;
    private BackgroundMusic mBgMusicManager;

    private boolean playOnce;

    @Override
    protected void doLoadData() {

        int score = ((GameMainActivity) mContext).getScore();
        loadRecommendUser(score);
    }

    @Override
    protected void doInit() {

        LinearLayoutManager secondManager = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(secondManager);
        mRecyclerView.setHasFixedSize(true);

        mAdapter = new FavorPersonAdapter();
        mRecyclerView.setAdapter(mAdapter);

        initSoundEffect();
    }

    private void initSoundEffect() {

        mSoundManager = new SoundPoolManager();
        int[] effect = new int[] {
                R.raw.amazing
        };
        mSoundManager.init(mContext, effect);

        mBgMusicManager = new BackgroundMusic(mContext);
    }

    @Override
    protected boolean isNeedShowLoadingView() {
        return false;
    }

    @Override
    protected int getLayoutName() {
        return R.layout.fragment_game_result;
    }

    /**
     * 加载打招呼推荐用户数据
     */
    public void loadRecommendUser(final int score) {

        mRecyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {

                buildFakeData(score);
            }
        }, 500);
    }

    private void buildFakeData(int score) {

        try {

            String ratingText;
            if (score > 20) {
                ratingText = "游戏达人";
            } else if (score > 10) {
                ratingText = "新手玩家";
            } else {
                ratingText = "小白一个";
            }
            mGameRateView.setText(ratingText);

            List<UserInfo> dataList = new ArrayList<>();
            for (int i = 0; i < 3; i++) {
                dataList.add(UserInfo.newInstance());
            }
            mAdapter.setNewData(dataList);

            mPrideTextView.setText(R.string.pride_format);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (!playOnce) {
            mGameRateView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mSoundManager.play(0);
                    mBgMusicManager.playBackgroundMusic("win.mp3", false);
                }
            }, 750);
            playOnce = true;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (null != mBgMusicManager) {
            mBgMusicManager.stopBackgroundMusic();
            mBgMusicManager.end();
        }
    }
}
