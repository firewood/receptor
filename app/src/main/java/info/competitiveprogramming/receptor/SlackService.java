package info.competitiveprogramming.receptor;

import android.content.Context;

import com.google.gson.annotations.SerializedName;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.POST;
import retrofit2.http.Query;

public class SlackService {

    private static SlackService instance;
    private static Retrofit retrofit;
    private static String token;
    private static String userName;

    public static SlackService getInstance(Context context) {
        if (instance == null) {
            instance = new SlackService(context);
        }
        return instance;
    }

    public SlackService(Context context) {
        token = context.getString(R.string.slack_token);
        userName = context.getString(R.string.bot_name);
        retrofit = new Retrofit.Builder()
                .baseUrl("https://slack.com/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(HttpClientBuilder.getInstance().getOkHttpClient())
                .build();
    }

    public SlackApi getSlackAPI() {
        return retrofit.create(SlackApi.class);
    }

    public Call<BaseResponse> postMessage(String channelId, String message) {
        return getSlackAPI().postMessage(token, userName, channelId, message);
    }

    public interface SlackApi {
        @POST("chat.postMessage")
        Call<BaseResponse> postMessage(
                @Query("token") String token,
                @Query("username") String username,
                @Query("channel") String channelId,
                @Query("text") String message);
    }

    public static class BaseResponse {
        @SerializedName("ok")
        public boolean ok;

        @SerializedName("error")
        public String error;
    }
}
