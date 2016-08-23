package com.zhtaxi.haodidriver.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.nickkong.commonlibrary.ui.activity.BaseActivity;
import com.nickkong.commonlibrary.util.Constant;
import com.nickkong.commonlibrary.util.HttpUtil;
import com.nickkong.commonlibrary.util.MD5;
import com.nickkong.commonlibrary.util.Tools;
import com.zhtaxi.haodidriver.R;
import com.zhtaxi.haodidriver.util.PublicResource;
import com.zhtaxi.haodidriver.util.RequestAddress;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cn.jpush.android.api.JPushInterface;

/**
 * 登录
 * Created by NickKong on 16/8/1.
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener{

    private String TAG = getClass().getSimpleName();

    private static final int SUCCESSCODE_LOGIN = 1;

    private EditText et_phone,et_pwd;

    private SharedPreferences.Editor editor;

    private String phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        initView();
    }

    @Override
    protected void initView() {

        //首次注册推送服务后，getRegistrationID才不为空
        if(!"".equals(JPushInterface.getRegistrationID(this))){
            PublicResource.REGISTRATION_ID = JPushInterface.getRegistrationID(this);
        }
        SharedPreferences sp = getSharedPreferences(getString(R.string.app_name), Activity.MODE_PRIVATE);
        editor = sp.edit();

        et_phone = (EditText) findViewById(R.id.et_phone);
        et_pwd = (EditText) findViewById(R.id.et_pwd);
        Button btn_login = (Button) findViewById(R.id.btn_login);
        btn_login.setOnClickListener(this);

        //回显登录过的手机号码
        String sp_phone = sp.getString(Constant.SP_PHONE_KEY, "");
        if (!"".equals(sp_phone.trim())) {
            et_phone.setText(sp_phone);
            et_pwd.requestFocus();
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_login:
                if(et_phone.getText().toString().trim().matches(getString(R.string.mobile_matches))&&
                        et_pwd.getText().toString().trim().length()>0){
                    login();
                }else if(!et_phone.getText().toString().trim().matches(getString(R.string.mobile_matches))){
                    Tools.showToast(this,getString(R.string.matchphone_tips));
                }else if(et_pwd.getText().toString().trim().length()==0){
                    Tools.showToast(this,getString(R.string.pwd_tips));
                }
                break;
        }
    }

    /**
     * 登录
     */
    private void login(){

        showLoadingDialog("登录中...",1);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                phone = et_phone.getText().toString().trim();
                Map params = new HashMap();
                params.put("mobilePhone", phone);
                params.put("pwd", MD5.GetMD5Code(et_pwd.getText().toString().trim()));
                params.put("userType", "1"); //0:乘客，1:司机
                params.put("deviceId", PublicResource.REGISTRATION_ID);
                HttpUtil.doGet(TAG,LoginActivity.this,mHandler, Constant.HTTPUTIL_FAILURECODE,SUCCESSCODE_LOGIN,
                        RequestAddress.login,params);

            }
        }, 2000);
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            String message = (String) msg.obj;
            switch (msg.what) {
                case Constant.HTTPUTIL_FAILURECODE:

                    disLoadingDialog();

                    Tools.showToast(LoginActivity.this,"登录失败");
                    break;
                //登录
                case SUCCESSCODE_LOGIN:

                    disLoadingDialog();

                    try {
                        JSONObject jsonObject = new JSONObject(message);
                        String result = jsonObject.getString("result");
                        //注册/登录成功，返回上一页
                        if (Constant.RECODE_SUCCESS.equals(result)) {

                            String userId = jsonObject.getString("userId");
                            String sessionId = jsonObject.getString("sessionId");

                            //当前有正在行程中订单时返回1
                            String isTrip = jsonObject.getString("isTrip");
                            if("1".equals(isTrip)){
                                //当前有正在行程中订单时返回
                                String orderNo = jsonObject.getString("orderNo");
                                //司机端或有在行程中的用户则有返回当前的车辆id
                                String carId = jsonObject.getString("carId");
                                //司机端或有在行程中的用户则有返回当前的车牌号码
                                String licensePlate = jsonObject.getString("licensePlate");
                            }

                            editor_user.putString("userId",userId);
                            editor_user.putString("sessionId",sessionId);
                            editor_user.commit();

                            //保存手机号码，方便下次登录时可以回显
                            editor.putString(Constant.SP_PHONE_KEY, phone);
                            editor.commit();

                            startActivity(new Intent(LoginActivity.this,MainActivity.class),true);
//                            doFinishByFade();
                        }
                        else if (Constant.RECODE_FAILED.equals(result)) {
                            String errMsgs = jsonObject.getString("errMsgs");
                            Tools.showToast(LoginActivity.this,errMsgs);

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
