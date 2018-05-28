package com.magical.molehitgame;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.magical.molehitgame.base.HeadImageView;
import com.magical.molehitgame.base.UserInfo;

/**
 * Created by magical.zhang on 2018/5/3.
 * Description : 兴趣相同的TA
 */
public class FavorPersonAdapter extends BaseQuickAdapter<UserInfo, BaseViewHolder> {

    public FavorPersonAdapter() {
        super(R.layout.item_favor_person);
    }

    @Override
    protected void convert(BaseViewHolder helper, UserInfo item) {

        try {

            if (null != item) {

                helper.setText(R.id.id_item_hello, item.text);

                HeadImageView view = helper.getView(R.id.id_item_avatar);
                view.setHeadImageUrl(item.avatar);
                helper.setText(R.id.id_item_nick, item.nickname);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
