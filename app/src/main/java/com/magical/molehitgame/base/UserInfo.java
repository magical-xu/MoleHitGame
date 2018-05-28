package com.magical.molehitgame.base;

/**
 * Created by magical on 2018/5/28.
 * Description :
 */
public class UserInfo {

    public static final String AVATAR_1 = "http://f2.topitme.com/2/6a/bc/113109954583dbc6a2o.jpg";
    public static final String AVATAR_2 =
            "http://img0.imgtn.bdimg.com/it/u=2500696144,3818976486&fm=27&gp=0.jpg";
    public static final String AVATAR_3 = "http://img01.taopic.com/160626/240389-1606260Z54370.jpg";

    public String nickname;
    public String avatar;
    public String text;
    public static int count;

    public static UserInfo newInstance() {

        UserInfo userInfo = new UserInfo();
        if (count == 0) {

            userInfo.avatar = AVATAR_1;
            userInfo.nickname = "大宝So3";
            userInfo.text = "处CP吗，我铁观音";
        } else if (count == 1) {

            userInfo.avatar = AVATAR_2;
            userInfo.nickname = "doubleSo3";
            userInfo.text = "叔叔我们不约";
        } else {

            userInfo.avatar = AVATAR_3;
            userInfo.nickname = "大宝天天见";
            userInfo.text = "皇后大道东哦皇后大道西";
        }

        count++;
        return userInfo;
    }
}
