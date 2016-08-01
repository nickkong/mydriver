package com.zhtaxi.haodidriver.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;

import com.nickkong.commonlibrary.ui.activity.BaseActivity;
import com.umeng.analytics.MobclickAgent;
import com.zhtaxi.haodidriver.R;

/**
 * 欢迎页
 * Created by NickKong on 16/8/1.
 */
public class WelcomeActivity extends BaseActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_welcome);

        initView();

        MobclickAgent.enableEncrypt(true);
    }

    @Override
    protected void initView() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(needLogin()){
                    startActivityByFade(new Intent(WelcomeActivity.this,LoginActivity.class),true);
                }else {
                    startActivityByFade(new Intent(WelcomeActivity.this,MainActivity.class),true);
                }

            }
        }, 2000);

    }

    /**
     * 禁止返回退出
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
