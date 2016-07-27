package com.zhtaxi.haodidriver.ui.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import com.nickkong.commonlibrary.ui.activity.BaseActivity;
import com.zhtaxi.haodidriver.R;

public class MainActivity extends BaseActivity {

    private String TAG = getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
    }

    @Override
    protected void initView() {

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (KeyEvent.KEYCODE_HEADSETHOOK == keyCode) { //按下了耳机键
            Log.d(TAG,"event.getRepeatCount()="+event.getRepeatCount());
            if (event.getRepeatCount() == 0) {  //如果长按的话，getRepeatCount值会一直变大
                //短按
                Toast.makeText(this,"短按",Toast.LENGTH_SHORT).show();
            }
//            else {
//                //长按
//                Toast.makeText(this,"长按",Toast.LENGTH_SHORT).show();
//            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
