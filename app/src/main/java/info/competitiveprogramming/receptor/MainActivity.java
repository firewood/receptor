package info.competitiveprogramming.receptor;

import android.databinding.DataBindingUtil;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import info.competitiveprogramming.receptor.databinding.ActivityMainBinding;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    Call<SlackService.BaseResponse> postMessageCall;
    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.setActivity(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        cancelPreviousSlackCall();
    }

    public void onMeetingButtonClick(View v) {
        postToSlack("meeting");
    }

    public void onDeliveryButtonClick(View v) {
        postToSlack("delivery");
    }

    public void onInterviewButtonClick(View v) {
        postToSlack("interview");
    }

    public void onOtherButtonClick(View v) {
        postToSlack("other");
    }

    void cancelPreviousSlackCall() {
        if (postMessageCall != null) {
            postMessageCall.cancel();
            postMessageCall = null;
        }
    }

    void onPostMessageSucceeded() {
        Snackbar.make(binding.mainLayout, R.string.toast_calling, Snackbar.LENGTH_LONG).show();
    }

    void onPostMessageFailed() {
        Snackbar.make(binding.mainLayout, R.string.toast_error, Snackbar.LENGTH_LONG).show();
    }

    void postToSlack(String channel) {
        final String arrivedMessage = "Arrived";
        cancelPreviousSlackCall();
        postMessageCall = SlackService.getInstance(this).postMessage(channel, arrivedMessage);
        postMessageCall.enqueue(new Callback<SlackService.BaseResponse>() {
            @Override
            public void onResponse(Call<SlackService.BaseResponse> call, Response<SlackService.BaseResponse> response) {
                if (response.body() != null && response.body().ok) {
                    onPostMessageSucceeded();
                } else {
                    onPostMessageFailed();
                }
            }

            @Override
            public void onFailure(Call<SlackService.BaseResponse> call, Throwable t) {
                onPostMessageFailed();
            }
        });
    }
}
