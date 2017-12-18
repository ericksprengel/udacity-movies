package br.com.ericksprengel.android.movies;

import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

abstract class BaseActivity extends AppCompatActivity {

    // Content View
    private View mContent;

    // Error view
    private View mErrorView;
    private TextView mErrorMessageView;
    private Button mErrorButton;

    // Loading view
    private View mLoadingView;
    private TextView mLoadingMessageView;

    // Animations
    private Animation mAnimFadeOut;
    private Animation mAnimFadeIn;


    void initBaseActivity() {
        // load animations
        mAnimFadeOut = AnimationUtils.loadAnimation(this, android.R.anim.fade_out);
        mAnimFadeIn = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);

        // load error view
        mErrorView = findViewById(R.id.layout_error_view);
        mErrorMessageView = findViewById(R.id.layout_error_message);
        mErrorButton = mErrorView.findViewById(R.id.layout_error_button);

        // load loading view
        mLoadingView = findViewById(R.id.layout_loading_view);
        mLoadingMessageView = findViewById(R.id.layout_loading_message);

        // load content view
        mContent = findViewById(R.id.content_view);
    }

    void showError(String message) {
        if(mContent.getVisibility() != View.GONE) { mContent.startAnimation(mAnimFadeOut); }
        if(mLoadingView.getVisibility() != View.GONE) { mLoadingView.startAnimation(mAnimFadeOut); }
        if(mErrorView.getVisibility() != View.VISIBLE) { mErrorView.startAnimation(mAnimFadeIn); }

        mErrorMessageView.setText(message);
        mErrorView.setVisibility(View.VISIBLE);
        mContent.setVisibility(View.GONE);
        mLoadingView.setVisibility(View.GONE);

    }

    void showLoading(String message) {
        if(mContent.getVisibility() != View.GONE) { mContent.startAnimation(mAnimFadeOut); }
        if(mErrorView.getVisibility() != View.GONE) { mErrorView.startAnimation(mAnimFadeOut); }
        if(mLoadingView.getVisibility() != View.VISIBLE) { mLoadingView.startAnimation(mAnimFadeIn); }

        mLoadingMessageView.setText(message != null ? message : "");
        mLoadingView.setVisibility(View.VISIBLE);
        mContent.setVisibility(View.GONE);
        mErrorView.setVisibility(View.GONE);
    }

    void showContent() {
        if(mErrorView.getVisibility() != View.GONE) { mErrorView.startAnimation(mAnimFadeOut); }
        if(mLoadingView.getVisibility() != View.GONE) { mLoadingView.startAnimation(mAnimFadeOut); }
        if(mContent.getVisibility() != View.VISIBLE) { mContent.startAnimation(mAnimFadeIn); }

        mErrorView.setVisibility(View.GONE);
        mLoadingView.setVisibility(View.GONE);
        mContent.setVisibility(View.VISIBLE);
    }

    void setOnErrorClickListener(View.OnClickListener onClickListener) {
        mErrorButton.setOnClickListener(onClickListener);
    }
}
