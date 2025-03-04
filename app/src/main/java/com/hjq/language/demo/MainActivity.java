package com.hjq.language.demo;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.hjq.bar.OnTitleBarListener;
import com.hjq.bar.TitleBar;
import com.hjq.language.MultiLanguages;

import java.util.Locale;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/MultiLanguages
 *    time   : 2019/08/10
 *    desc   : 多语种切换演示
 */
public final class MainActivity extends BaseActivity
        implements RadioGroup.OnCheckedChangeListener, OnTitleBarListener {

    private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mWebView = findViewById(R.id.wv_main_web);

        TitleBar titleBar = findViewById(R.id.tb_main_bar);
        RadioGroup radioGroup = findViewById(R.id.rg_main_languages);

        titleBar.setOnTitleBarListener(this);

        mWebView.setWebViewClient(new WebViewClient());
        mWebView.setWebChromeClient(new WebChromeClient());
        mWebView.loadUrl("https://developer.android.google.cn/index.html");

        //((TextView) findViewById(R.id.tv_language_activity)).setText(this.getResources().getString(R.string.current_language));
        ((TextView) findViewById(R.id.tv_main_language_application)).setText(getApplication().getResources().getString(R.string.current_language));
        ((TextView) findViewById(R.id.tv_main_language_system)).setText(MultiLanguages.getLanguageString(this, MultiLanguages.getSystemLanguage(), R.string.current_language));

        if (MultiLanguages.isSystemLanguage()) {
            radioGroup.check(R.id.rb_main_language_auto);
        } else {
            Locale locale = MultiLanguages.getAppLanguage();
            if (Locale.CHINA.equals(locale)) {
                radioGroup.check(R.id.rb_main_language_cn);
            } else if (Locale.TAIWAN.equals(locale)) {
                radioGroup.check(R.id.rb_main_language_tw);
            } else if (Locale.ENGLISH.equals(locale)) {
                radioGroup.check(R.id.rb_main_language_en);
            } else {
                radioGroup.check(R.id.rb_main_language_auto);
            }
        }

        radioGroup.setOnCheckedChangeListener(this);
    }

    /**
     * {@link RadioGroup.OnCheckedChangeListener}
     */
    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        // 是否需要重启
        boolean restart = false;

        if (checkedId == R.id.rb_main_language_auto) {
            // 跟随系统
            restart = MultiLanguages.clearAppLanguage(this);
        } else if (checkedId == R.id.rb_main_language_cn) {
            // 简体中文
            restart = MultiLanguages.setAppLanguage(this, Locale.CHINA);
        } else if (checkedId == R.id.rb_main_language_tw) {
            // 繁体中文
            restart = MultiLanguages.setAppLanguage(this, Locale.TAIWAN);
        } else if (checkedId == R.id.rb_main_language_en) {
            // 英语
            restart = MultiLanguages.setAppLanguage(this, Locale.ENGLISH);
        }

        if (restart) {
            // 1.使用recreate来重启Activity的效果差，有闪屏的缺陷
            // recreate();

            // 2.使用常规startActivity来重启Activity，有从左向右的切换动画，稍微比recreate的效果好一点，但是这种并不是最佳的效果
            // startActivity(new Intent(this, LanguageActivity.class));
            // finish();

            // 3.我们可以充分运用 Activity 跳转动画，在跳转的时候设置一个渐变的效果，相比前面的两种带来的体验是最佳的
            startActivity(new Intent(this, MainActivity.class));
            overridePendingTransition(R.anim.activity_alpha_in, R.anim.activity_alpha_out);
            finish();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mWebView.onResume();
        mWebView.resumeTimers();
    }

    @Override
    public void onPause() {
        super.onPause();
        mWebView.onPause();
        mWebView.pauseTimers();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //清除历史记录
        mWebView.clearHistory();
        //停止加载
        mWebView.stopLoading();
        //加载一个空白页
        mWebView.loadUrl("about:blank");
        mWebView.setWebChromeClient(null);
        mWebView.setWebViewClient(null);
        //移除WebView所有的View对象
        mWebView.removeAllViews();
        //销毁此的WebView的内部状态
        mWebView.destroy();
    }

    @Override
    public void onTitleClick(TitleBar titleBar) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("https://github.com/getActivity/MultiLanguages"));
        startActivity(intent);
    }
}