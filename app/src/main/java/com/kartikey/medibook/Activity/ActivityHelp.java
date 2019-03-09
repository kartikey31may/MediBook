package com.kartikey.medibook.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.kartikey.medibook.R;


public class ActivityHelp extends AppCompatActivity {

    ProgressBar progressBar;
    WebView webview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        progressBar=findViewById(R.id.progressBar);
        webview=findViewById(R.id.webview);
        progressBar.setVisibility(View.VISIBLE);
        WebSettings ws = webview.getSettings();
        ws.setJavaScriptEnabled(true);
        webview.loadUrl("file:///android_asset/index.html");
        webview.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageCommitVisible(WebView view, String url) {
                super.onPageCommitVisible(view, url);
                progressBar.setVisibility(View.INVISIBLE);
            }
        });

    }
}
