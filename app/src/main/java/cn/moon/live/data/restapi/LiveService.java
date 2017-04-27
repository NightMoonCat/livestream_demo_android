package cn.moon.live.data.restapi;

import cn.moon.live.I;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Moon on 2017/4/13.
 */

public interface LiveService {
    @GET("live/getAllGifts")
    Call<String> getAllGifts();

    @GET("findUserByUserName")
    Call<String> loadUserInfo(@Query(I.User.USER_NAME) String username);

    @GET("live/createChatRoom")
    Call<String> createLiveRoom(
        @Query(("auth")) String auth,
        @Query(("name")) String roomName,
        @Query(("description")) String description,
        @Query(("owner")) String owner,
        @Query(("maxusers")) int maxUsers,
        @Query(("members")) String members
    );

    @GET("live/deleteChatRoom")
    Call<String> deleteLiveRoom(
        @Query(("auth")) String auth,
        @Query(("chatRoomId")) String chatRoomId

    );
    @GET("live/getBalance")
    Call<String> getBalance(
        @Query(("uname")) String username
    );
    @GET("live/getGiftStatementsByAnchor")
    Call<String> getGiftStatementsByAnchor(
        @Query(("anchor")) String anchor
    );
    @GET("live/recharge")
    Call<String> recharge(
        @Query(("uname")) String username,
        @Query(("rmb")) int rmbCount
    );
}
