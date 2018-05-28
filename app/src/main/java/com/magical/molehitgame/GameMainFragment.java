package com.magical.molehitgame;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.airbnb.lottie.LottieAnimationView;
import com.magical.molehitgame.base.BaseFragment;
import com.magical.molehitgame.game.GameMsg;
import com.magical.molehitgame.game.RandomThread;
import com.magical.molehitgame.game.SoundPoolManager;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by magical.zhang on 2018/5/21.
 * Description : 游戏主页
 */
public class GameMainFragment extends BaseFragment {

    private static final String TAG = GameMainFragment.class.getSimpleName();

    private TextView mCountDownView;

    private ArrayList<ImageView> mMoleList = new ArrayList<>(9);
    private ArrayList<LottieAnimationView> mLottieList = new ArrayList<>(9);
    private ArrayList<AnimationDrawable> mRatAnimList = new ArrayList<>(9);
    private ArrayList<AnimationDrawable> mMushRoomAnimList = new ArrayList<>(9);
    private ArrayList<AnimationDrawable> mRatHitAnimList = new ArrayList<>(9);
    private ArrayList<AnimationDrawable> mMushRoomHitAnimList = new ArrayList<>(9);

    private RandomThread randomThread;
    private HashSet<Integer> mKeepSet = new HashSet<>();

    private SoundPoolManager mSoundManager;
    private TextView mScoreView;
    private ViewGroup mShadowView;
    private TextView mCountDown;

    private int score;
    private boolean startFlag;  //标记是否可点地洞

    @SuppressLint("HandlerLeak") private Handler mainHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            int what = msg.what;
            switch (what) {
                case GameMsg.MSG_WHAT_START:

                    onGameStart();
                    break;

                case GameMsg.MSG_WHAT_REFRESH:

                    onGameRefresh(msg);
                    break;

                case GameMsg.MSG_WHAT_GAP:

                    int remainTime = msg.arg1;
                    mCountDownView.setText("剩余时间：" + String.valueOf(remainTime) + "秒");
                    break;

                case GameMsg.MSG_WHAT_END:

                    onGameOver();
                    break;

                case GameMsg.MSG_ANIM_STOP:

                    notifyHoleAnimStop(msg);
                    break;
            }
        }
    };

    /**
     * 通知播放的帧动画停止 防止随机线程栈溢出
     */
    private void notifyHoleAnimStop(Message msg) {

        int holeStop = msg.arg1;
        int modelStop = msg.arg2;

        AnimationDrawable animationDrawable;
        if (modelStop == GameMsg.MODEL_RAT) {
            animationDrawable = mRatAnimList.get(holeStop);
        } else {
            animationDrawable = mMushRoomAnimList.get(holeStop);
        }
        animationDrawable.stop();
        animationDrawable.selectDrawable(0);
    }

    /**
     * 刷新洞
     */
    private void onGameRefresh(Message msg) {

        int hole = msg.arg1;
        int model = msg.arg2;
        //Log.d(TAG, "出现的洞" + hole);
        playHoleAnim(hole, model);
    }

    /**
     * 播放地洞动画
     *
     * @param hole 地洞索引
     * @param model 精灵模型
     */
    private void playHoleAnim(int hole, int model) {

        AnimationDrawable targetDrawable;
        if (model == GameMsg.MODEL_RAT) {

            targetDrawable = mRatAnimList.get(hole);
        } else {

            targetDrawable = mMushRoomAnimList.get(hole);
        }

        ImageView imageView = mMoleList.get(hole);
        imageView.setBackgroundResource(0);
        imageView.setBackground(targetDrawable);
        imageView.setTag(R.id.game_model_key, model);
        targetDrawable.setVisible(true, true);
        targetDrawable.start();

        int duration = 0;
        for (int i = 0; i < targetDrawable.getNumberOfFrames(); i++) {
            duration += targetDrawable.getDuration(i);
        }

        //Log.d(TAG, " playHoleAnim : 持续时间" + duration);
        Message obtain = Message.obtain();
        obtain.what = GameMsg.MSG_ANIM_STOP;
        obtain.arg1 = hole;
        obtain.arg2 = model;
        obtain.obj = targetDrawable;
        mainHandler.sendMessageDelayed(obtain, duration);
    }

    /**
     * 开始游戏
     */
    private void onGameStart() {

        //初始化地洞背景
        for (int i = 0; i < mMoleList.size(); i++) {
            mMoleList.get(i).setBackgroundResource(R.drawable.img_sign_in_whack_mole_pit_0);
        }

        score = 0;
        startFlag = true;
        randomThread = new RandomThread(mContext, mainHandler, mKeepSet);
        randomThread.setAnimList(mRatAnimList, mMushRoomAnimList);

        //线程开启轮询
        randomThread.start();
        randomThread.startGame();
    }

    @Override
    protected void doLoadData() {

    }

    @Override
    protected void doInit() {

        initResource();

        initFunc();
    }

    @Override
    protected boolean isNeedShowLoadingView() {
        return false;
    }

    @Override
    protected int getLayoutName() {
        return R.layout.fragment_game_main;
    }

    /**
     * 初始化资源
     */
    private void initResource() {

        //顺序别乱
        initSoundEffect();
        initView();
        initFrame();
        initEvent();
    }

    private void initFunc() {
        findViewById(R.id.id_a).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onGameGuide();
            }
        });

        findViewById(R.id.id_running).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ((GameMainActivity) mContext).changePage(2);
            }
        });
    }

    private void initSoundEffect() {

        mSoundManager = new SoundPoolManager();
        int[] effect = new int[] {
                R.raw.rat_hit, R.raw.mushroom_hit, R.raw.amazing
        };
        mSoundManager.init(mContext, effect);
    }

    /**
     * 初始化View
     */
    private void initView() {
        //mLottieView = findViewById(R.id.id_anim_view);

        mScoreView = findViewById(R.id.total_score);
        mCountDownView = findViewById(R.id.id_count_time);

        mShadowView = findViewById(R.id.id_shadow_view);
        mCountDown = findViewById(R.id.id_count_down_view);
        mCountDown.setScaleX(0);
        mCountDown.setScaleY(0);

        ImageView mMoleView1 = findViewById(R.id.id_mole1);
        ImageView mMoleView2 = findViewById(R.id.id_mole2);
        ImageView mMoleView3 = findViewById(R.id.id_mole3);
        ImageView mMoleView4 = findViewById(R.id.id_mole4);
        ImageView mMoleView5 = findViewById(R.id.id_mole5);
        ImageView mMoleView6 = findViewById(R.id.id_mole6);
        ImageView mMoleView7 = findViewById(R.id.id_mole7);
        ImageView mMoleView8 = findViewById(R.id.id_mole8);
        ImageView mMoleView9 = findViewById(R.id.id_mole9);

        mMoleList.add(mMoleView1);
        mMoleList.add(mMoleView2);
        mMoleList.add(mMoleView3);
        mMoleList.add(mMoleView4);
        mMoleList.add(mMoleView5);
        mMoleList.add(mMoleView6);
        mMoleList.add(mMoleView7);
        mMoleList.add(mMoleView8);
        mMoleList.add(mMoleView9);

        LottieAnimationView lottie1 = findViewById(R.id.id_anim_view1);
        LottieAnimationView lottie2 = findViewById(R.id.id_anim_view2);
        LottieAnimationView lottie3 = findViewById(R.id.id_anim_view3);
        LottieAnimationView lottie4 = findViewById(R.id.id_anim_view4);
        LottieAnimationView lottie5 = findViewById(R.id.id_anim_view5);
        LottieAnimationView lottie6 = findViewById(R.id.id_anim_view6);
        LottieAnimationView lottie7 = findViewById(R.id.id_anim_view7);
        LottieAnimationView lottie8 = findViewById(R.id.id_anim_view8);
        LottieAnimationView lottie9 = findViewById(R.id.id_anim_view9);

        mLottieList.add(lottie1);
        mLottieList.add(lottie2);
        mLottieList.add(lottie3);
        mLottieList.add(lottie4);
        mLottieList.add(lottie5);
        mLottieList.add(lottie6);
        mLottieList.add(lottie7);
        mLottieList.add(lottie8);
        mLottieList.add(lottie9);
    }

    /**
     * 初始化帧动画
     */
    private void initFrame() {

        //地鼠动画
        initRatFrame();
        initRatHitFrame();

        //蘑菇动画
        initMushRoomFrame();
        initMushRoomHitFrame();

        //初始化地洞背景
        for (int i = 0; i < mMoleList.size(); i++) {
            mMoleList.get(i).setBackgroundResource(R.drawable.img_sign_in_whack_mole_pit_0);
        }
    }

    private void initEvent() {

        for (int i = 0; i < mMoleList.size(); i++) {

            final int finalI = i;
            mMoleList.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    onHitMole(v, finalI);
                }
            });
        }
    }

    /**
     * 游戏引导 开始游戏入口
     */
    private void onGameGuide() {

        mMoleList.get(5).setBackgroundResource(R.drawable.img_sign_in_whack_mole_rat_normal_18);
        mMoleList.get(6)
                .setBackgroundResource(R.drawable.img_sign_in_whack_mole_mushroom_normal_17);
        mShadowView.setVisibility(View.VISIBLE);
        mShadowView.setAlpha(1);

        startGuideCountDown();

        mShadowView.postDelayed(new Runnable() {
            @Override
            public void run() {

                mShadowView.animate()
                        .alpha(0)
                        .setInterpolator(new LinearInterpolator())
                        .setDuration(1000)
                        .start();
            }
        }, 3300);
    }

    /**
     * 初始化老鼠动画
     */
    private void initRatFrame() {

        AnimationDrawable moleAnim = (AnimationDrawable) ContextCompat.getDrawable(mContext,
                R.drawable.mole_frame_animation);
        mRatAnimList.add(moleAnim);
        Drawable mutate2 = moleAnim.getConstantState().newDrawable();
        mRatAnimList.add((AnimationDrawable) mutate2);
        Drawable mutate3 = moleAnim.getConstantState().newDrawable();
        mRatAnimList.add((AnimationDrawable) mutate3);
        Drawable mutate4 = moleAnim.getConstantState().newDrawable();
        mRatAnimList.add((AnimationDrawable) mutate4);
        Drawable mutate5 = moleAnim.getConstantState().newDrawable();
        mRatAnimList.add((AnimationDrawable) mutate5);
        Drawable mutate6 = moleAnim.getConstantState().newDrawable();
        mRatAnimList.add((AnimationDrawable) mutate6);
        Drawable mutate7 = moleAnim.getConstantState().newDrawable();
        mRatAnimList.add((AnimationDrawable) mutate7);
        Drawable mutate8 = moleAnim.getConstantState().newDrawable();
        mRatAnimList.add((AnimationDrawable) mutate8);
        Drawable mutate9 = moleAnim.getConstantState().newDrawable();
        mRatAnimList.add((AnimationDrawable) mutate9);
    }

    private void initRatHitFrame() {

        AnimationDrawable moleAnim = (AnimationDrawable) ContextCompat.getDrawable(mContext,
                R.drawable.rat_hit_frame_animation);
        mRatHitAnimList.add(moleAnim);
        Drawable mutate2 = moleAnim.getConstantState().newDrawable();
        mRatHitAnimList.add((AnimationDrawable) mutate2);
        Drawable mutate3 = moleAnim.getConstantState().newDrawable();
        mRatHitAnimList.add((AnimationDrawable) mutate3);
        Drawable mutate4 = moleAnim.getConstantState().newDrawable();
        mRatHitAnimList.add((AnimationDrawable) mutate4);
        Drawable mutate5 = moleAnim.getConstantState().newDrawable();
        mRatHitAnimList.add((AnimationDrawable) mutate5);
        Drawable mutate6 = moleAnim.getConstantState().newDrawable();
        mRatHitAnimList.add((AnimationDrawable) mutate6);
        Drawable mutate7 = moleAnim.getConstantState().newDrawable();
        mRatHitAnimList.add((AnimationDrawable) mutate7);
        Drawable mutate8 = moleAnim.getConstantState().newDrawable();
        mRatHitAnimList.add((AnimationDrawable) mutate8);
        Drawable mutate9 = moleAnim.getConstantState().newDrawable();
        mRatHitAnimList.add((AnimationDrawable) mutate9);
    }

    /**
     * 初始化蘑菇动画
     */
    private void initMushRoomFrame() {

        AnimationDrawable mushroomAnim = (AnimationDrawable) ContextCompat.getDrawable(mContext,
                R.drawable.mushroom_frame_animation);
        mMushRoomAnimList.add(mushroomAnim);
        Drawable mutate2 = mushroomAnim.getConstantState().newDrawable();
        mMushRoomAnimList.add((AnimationDrawable) mutate2);
        Drawable mutate3 = mushroomAnim.getConstantState().newDrawable();
        mMushRoomAnimList.add((AnimationDrawable) mutate3);
        Drawable mutate4 = mushroomAnim.getConstantState().newDrawable();
        mMushRoomAnimList.add((AnimationDrawable) mutate4);
        Drawable mutate5 = mushroomAnim.getConstantState().newDrawable();
        mMushRoomAnimList.add((AnimationDrawable) mutate5);
        Drawable mutate6 = mushroomAnim.getConstantState().newDrawable();
        mMushRoomAnimList.add((AnimationDrawable) mutate6);
        Drawable mutate7 = mushroomAnim.getConstantState().newDrawable();
        mMushRoomAnimList.add((AnimationDrawable) mutate7);
        Drawable mutate8 = mushroomAnim.getConstantState().newDrawable();
        mMushRoomAnimList.add((AnimationDrawable) mutate8);
        Drawable mutate9 = mushroomAnim.getConstantState().newDrawable();
        mMushRoomAnimList.add((AnimationDrawable) mutate9);
    }

    private void initMushRoomHitFrame() {

        AnimationDrawable mushroomAnim = (AnimationDrawable) ContextCompat.getDrawable(mContext,
                R.drawable.mushroom_hit_frame_animation);
        mMushRoomHitAnimList.add(mushroomAnim);
        Drawable mutate2 = mushroomAnim.getConstantState().newDrawable();
        mMushRoomHitAnimList.add((AnimationDrawable) mutate2);
        Drawable mutate3 = mushroomAnim.getConstantState().newDrawable();
        mMushRoomHitAnimList.add((AnimationDrawable) mutate3);
        Drawable mutate4 = mushroomAnim.getConstantState().newDrawable();
        mMushRoomHitAnimList.add((AnimationDrawable) mutate4);
        Drawable mutate5 = mushroomAnim.getConstantState().newDrawable();
        mMushRoomHitAnimList.add((AnimationDrawable) mutate5);
        Drawable mutate6 = mushroomAnim.getConstantState().newDrawable();
        mMushRoomHitAnimList.add((AnimationDrawable) mutate6);
        Drawable mutate7 = mushroomAnim.getConstantState().newDrawable();
        mMushRoomHitAnimList.add((AnimationDrawable) mutate7);
        Drawable mutate8 = mushroomAnim.getConstantState().newDrawable();
        mMushRoomHitAnimList.add((AnimationDrawable) mutate8);
        Drawable mutate9 = mushroomAnim.getConstantState().newDrawable();
        mMushRoomHitAnimList.add((AnimationDrawable) mutate9);
    }

    /**
     * 击打某个地洞
     *
     * @param v 被击打的ImageView
     * @param finalI 被击打的位置
     */
    private void onHitMole(View v, final int finalI) {

        if (!startFlag) {
            return;
        }

        try {
            int model = (int) v.getTag(R.id.game_model_key);
            boolean isRat = model == GameMsg.MODEL_RAT;
            AnimationDrawable animationDrawable =
                    isRat ? mRatAnimList.get(finalI) : mMushRoomAnimList.get(finalI);

            if (null != animationDrawable && animationDrawable.isRunning()) {

                mainHandler.removeMessages(GameMsg.MSG_ANIM_STOP, animationDrawable);
                animationDrawable.stop();
                animationDrawable.selectDrawable(0);

                LottieAnimationView lottieAnimationView = mLottieList.get(finalI);
                if (isRat) {

                    score++;

                    mSoundManager.play(0);

                    lottieAnimationView.setImageAssetsFolder("success/images");
                    lottieAnimationView.setAnimation("success/data.json");
                } else {

                    if (score != 0) {
                        score--;
                    }

                    mSoundManager.play(1);

                    lottieAnimationView.setImageAssetsFolder("error/images");
                    lottieAnimationView.setAnimation("error/data.json");
                }
                lottieAnimationView.playAnimation();

                final ImageView imageView = mMoleList.get(finalI);
                //imageView.setBackground(ContextCompat.getDrawable(mContext,
                //        isRat ? R.drawable.img_sign_in_whack_mole_rat_beaten
                //                : R.drawable.img_sign_in_whack_mole_mushroom_beaten));
                AnimationDrawable hitAnimationDrawable =
                        isRat ? mRatHitAnimList.get(finalI) : mMushRoomHitAnimList.get(finalI);
                hitAnimationDrawable.stop();
                hitAnimationDrawable.selectDrawable(0);
                imageView.setBackground(hitAnimationDrawable);
                hitAnimationDrawable.start();

                mKeepSet.add(finalI);
                imageView.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        //imageView.setBackground(null);
                        imageView.setBackgroundResource(R.drawable.img_sign_in_whack_mole_pit_0);

                        //animationDrawable.selectDrawable(0);
                        //imageView.setImageDrawable(animationDrawable);
                        mKeepSet.remove(finalI);
                    }
                }, 1000);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void startGuideCountDown() {

        mCountDown.setVisibility(View.VISIBLE);
        mCountDown.setScaleY(0);
        mCountDown.setScaleX(0);
        final CountDownTimer timer = new CountDownTimer(3300, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                //Log.d(TAG,"onTick " + millisUntilFinished);
                float remain = millisUntilFinished / 1000f;
                int round = Math.round(remain);
                showGuideCountDownAnim(round);
            }

            @Override
            public void onFinish() {

                mCountDown.setVisibility(View.GONE);
                mainHandler.sendEmptyMessage(GameMsg.MSG_WHAT_START);
            }
        };

        mCountDown.postDelayed(new Runnable() {
            @Override
            public void run() {
                timer.start();
            }
        }, 300);
    }

    private void showGuideCountDownAnim(int remain) {

        if (remain == 0) {
            return;
        }

        mCountDown.setText(String.valueOf(remain));

        ObjectAnimator scaleX = ObjectAnimator.ofFloat(mCountDown, "scaleX", 0, 1);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(mCountDown, "scaleY", 0, 1);

        AnimatorSet set = new AnimatorSet();
        set.setDuration(800);
        set.setInterpolator(new AccelerateDecelerateInterpolator());
        set.playTogether(scaleX, scaleY);
        set.start();
    }

    private void onGameOver() {

        mCountDownView.setText("游戏结束");

        if (score < 0) {
            score = 0;
        }
        mScoreView.setText("总分：" + score);
        Toast.makeText(getActivity(), "恭喜你！！！" + score + "分", Toast.LENGTH_LONG).show();

        for (int i = 0; i < mMoleList.size(); i++) {

            ImageView imageView = mMoleList.get(0);
            imageView.setBackgroundResource(R.drawable.img_sign_in_whack_mole_pit_0);

            AnimationDrawable ratDrawable = mRatAnimList.get(i);
            AnimationDrawable mushroomDrawable = mMushRoomAnimList.get(i);
            ratDrawable.stop();
            ratDrawable.selectDrawable(0);
            mushroomDrawable.stop();
            mushroomDrawable.selectDrawable(0);
        }
        randomThread.stopGame();
        startFlag = false;

        mCountDownView.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {

                    ((GameMainActivity) mContext).postScore(score);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }, 1200);
    }

    @Override
    public void onResume() {
        super.onResume();

        onGameGuide();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (null != randomThread) {
            randomThread.release();
        }
        mainHandler.removeCallbacksAndMessages(null);
    }
}
