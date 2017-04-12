package cn.moon.live.data.model;

import android.content.Context;

import java.io.File;

/**
 * Created by Moon on 2017/3/29.
 */

public interface IUserModel {
    void register(Context context, String userName, String nickName,
                  String password, OnCompleteListener<String> listener);
    void login(Context context, String userName,
               String password, OnCompleteListener<String> listener);
    void unRegister(Context context, String userName,
                    OnCompleteListener<String> listener);
    void loadUserInfo(Context context, String userName,
                      OnCompleteListener<String> listener);

    void updateNick(Context context, String userName,
                    String newNickName, OnCompleteListener<String> listener);

    void updateAvatar(Context context, String username,
                      File file, OnCompleteListener<String> listener);
    void addContact(Context context, String username, String cname,
                    OnCompleteListener<String> listener);
    void deleteContact(Context context, String username, String cname,
                       OnCompleteListener<String> listener);
    void loadContactList(Context context, String username,
                         OnCompleteListener<String> listener);
}
