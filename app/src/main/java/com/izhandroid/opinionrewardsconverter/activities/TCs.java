/*
 * Copyright (c) 2019. Izhan Ali. Accessing/leaking/modifying source codes is prohibited. Legal action will be taken for if you do such things.
 */

package com.izhandroid.opinionrewardsconverter.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.izhandroid.opinionrewardsconverter.R;

public class TCs extends AppCompatActivity {
    WebView webView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tcs);

        webView = findViewById(R.id.webView);
        webView.loadUrl("https://izhansaqib.github.io/tncrcapp/");
        webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                setTitle("Terms and Conditions");
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {


                setTitle("Terms and Conditions");
                super.onPageStarted(view, url, favicon);

            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {



                if(errorCode==2){
                    webView.loadData("Unable to connect!", "text/html", "utf-8");

                }else {
                    webView.loadData("Cannot load " + description + errorCode, "text/html", "utf-8");

                }


            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return super.shouldOverrideUrlLoading(view, url);
            }


        });
    }

    @Override
    protected void onDestroy() {
        webView.clearCache(true);
        super.onDestroy();
    }
}
