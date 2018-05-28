package com.magical.molehitgame;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Window;
import java.util.ArrayList;

/**
 * Created by magical.zhang on 2018/5/21.
 * Description : 兴趣标签、（游戏主体、打招呼）
 */
public class GameMainActivity extends AppCompatActivity {

    private static final String EXTRA_PAGE = "extra_page";
    private ArrayList<Fragment> mFragmentList = new ArrayList<>();
    private GameStartFragment mStartFragment;
    private GameMainFragment mGameFragment;
    private GameResultFragment mResultFragment;
    private int score;

    public int getScore() {
        return score;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_game_main);

        initPage();
        changePage(0);
    }

    private void initPage() {

        mFragmentList.clear();
        mStartFragment = new GameStartFragment();
        mGameFragment = new GameMainFragment();
        mResultFragment = new GameResultFragment();
        mFragmentList.add(mStartFragment);
        mFragmentList.add(mGameFragment);
        mFragmentList.add(mResultFragment);
    }

    public void changePage(int index) {

        try {

            Fragment fragment = mFragmentList.get(index);
            if (null != fragment) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                        android.R.anim.fade_out);
                fragmentTransaction.replace(R.id.id_game_root, fragment);
                fragmentTransaction.commit();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 告知游戏分数
     *
     * @param score 分数
     */
    public void postScore(int score) {

        try {

            this.score = score;
            changePage(2);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        //游戏页屏蔽返回键
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
}
