package com.zhtaxi.haodidriver.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.LogoPosition;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;
import com.nickkong.commonlibrary.service.LocationService;
import com.nickkong.commonlibrary.ui.activity.BaseActivity;
import com.nickkong.commonlibrary.ui.listener.OnDialogClickListener;
import com.nickkong.commonlibrary.util.HttpUtil;
import com.nickkong.commonlibrary.util.Tools;
import com.umeng.analytics.MobclickAgent;
import com.zhtaxi.haodidriver.HaodidriverApplication;
import com.zhtaxi.haodidriver.R;
import com.zhtaxi.haodidriver.util.Constant;
import com.zhtaxi.haodidriver.util.PublicResource;
import com.zhtaxi.haodidriver.util.RequestAddress;
import com.zhtaxi.haodidriver.util.UpdateManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import cn.jpush.android.api.JPushInterface;

public class MainActivity extends BaseActivity implements OnClickListener{

    private String TAG = getClass().getSimpleName();

    private static final int APPEAR_DELAY = 2000;
    private static final int DISAPPEAR_DELAY = 2500;
    private static final int UPLOADGPS_PERIOD = 5000;

    private static final int SUCCESSCODE_UPLOADGPS = 1;
    private static final int SUCCESSCODE_QUERYNEARBYUSERS = 2;
    private static final int HANDLER_UPLOADGPS = 3;
    private static final int SUCCESSCODE_CONFIRMWAVE = 4;

    public static boolean isForeground = false;
    public static final String KEY_MESSAGE = "message";
    private long exitTime = 0;
    private int screenWidth;
    private LocationService locationService;
//    private CustomViewPager vp_control;
    private List<Fragment> pages;
    public MyLocationListenner myListener = new MyLocationListenner();
    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private BDLocation mylocation;
    private boolean isFirstLoc = true;

    private StringBuffer sb = new StringBuffer();
    private Timer timer;
    private TimerTask task;

    private Button btn_control_start,btn_control_empty,btn_control_full;
    private ImageView btn_control_ring;

    private String matchingKey;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;

        setContentView(R.layout.activity_main);

//        LatLng point1 = new LatLng(23.065671,113.143372);
//        LatLng point2 = new LatLng(23.065669,113.143335);
//        double distance = DistanceUtil.getDistance(point1,point2);
//        Log.d(TAG,"distance===="+distance);

        initView();

        initMap();

        checkUpdate();

        registerMessageReceiver();

    }

    /**
     * 初始化控件
     */
    @Override
    public void initView(){
        btn_control_start = (Button) findViewById(R.id.btn_control_start);
        btn_control_start.setOnClickListener(this);
        btn_control_empty = (Button) findViewById(R.id.btn_control_empty);
        btn_control_empty.setOnClickListener(this);
        btn_control_full = (Button) findViewById(R.id.btn_control_full);
        btn_control_full.setOnClickListener(this);
        btn_control_ring = (ImageView) findViewById(R.id.btn_control_ring);
    }

    /**
     * 初始化地图
     */
    private void initMap(){

        mMapView = (MapView) findViewById(R.id.bmapView);
        mMapView.showZoomControls(false);
        mMapView.showScaleControl(false);
        mMapView.setLogoPosition(LogoPosition.logoPostionleftTop);
        mBaiduMap = mMapView.getMap();
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        BitmapDescriptor mCurrentMarker = BitmapDescriptorFactory
                .fromResource(R.mipmap.car_bearing);
        mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(
                MyLocationConfiguration.LocationMode.FOLLOWING, true, null));
        //佛山 23.031033,113.131019
        //珠海 22.256915,113.562447
        LatLng ll = new LatLng(22.256915,113.562447);
        MapStatus.Builder builder = new MapStatus.Builder();
        builder.target(ll).zoom(12.0f);
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));

    }

    /**
     * 检查更新
     */
    private void checkUpdate(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    //wifi状态检查更新
                    if(Tools.isWifiConnected(MainActivity.this)){
                        UpdateManager mUpdateManager = new UpdateManager(MainActivity.this);
                        mUpdateManager.sendUpdateRequest();
                    }
                } catch (Exception e) {
                }
            }
        }, DISAPPEAR_DELAY);
    }

    /**
     * 定时执行上传gps
     */
    private void doUploadGps(){

        timer = new Timer(true);

        task = new TimerTask() {

            public void run() {
                mHandler.sendEmptyMessage(HANDLER_UPLOADGPS);
            }
        };

        timer.schedule(task, UPLOADGPS_PERIOD, UPLOADGPS_PERIOD);

    }

    private void getNearByUsers(){

        Map<String, Object> params = new HashMap();
        params.put("lat", mylocation.getLatitude()+"");
        params.put("lng", mylocation.getLongitude()+"");
//        params.put("distanceLessThan", "5");
        HttpUtil.doGet(TAG,this,mHandler, Constant.HTTPUTIL_FAILURECODE,SUCCESSCODE_QUERYNEARBYUSERS,
                RequestAddress.queryNearByUsers,params);

    }

    /**
     * 上传gps
     * userId,licensePlate(车牌号码)，isTrip(是否行程中)，orderNo(订单号)，mapType(地图类型)，locations
     * isTrip为0时不用传orderNo
     * locations=纬度1,经度1,时间1long型;纬度2,经度2,时间2long型;纬度3,经度3,时间3long型;纬度4,经度4,时间4long型;
     */
    private void uploadGps(){
        //有位置信息才上传
        if(sb.toString().length()>0){
            Map params = generateRequestMap();
//        params.put("licensePlate", "粤Y99999");
            params.put("isTrip", "0");
//        params.put("orderNo", "0");
            params.put("mapType", "0");
            params.put("locations", sb.toString());
            HttpUtil.doGet(TAG,this,mHandler, Constant.HTTPUTIL_FAILURECODE,SUCCESSCODE_UPLOADGPS,
                    RequestAddress.uploadGps,params);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_control_start:

                //出车中，切换为收车
                if(PublicResource.isTrip){
                    btn_control_start.setText("出车");
                    btn_control_start.setBackgroundResource(R.mipmap.main_control_panel_btn_start_off_normal);
                    btn_control_start.setTextColor(getResources().getColor(R.color.white));
                    btn_control_empty.setVisibility(View.GONE);
                    btn_control_full.setVisibility(View.GONE);
                    btn_control_ring.clearAnimation();
                    btn_control_ring.setVisibility(View.GONE);
                    PublicResource.isTrip = false;
                    PublicResource.isFull = false;
                }
                //收车中，切换为出车
                else {
                    btn_control_empty.setVisibility(View.VISIBLE);
                    btn_control_full.setVisibility(View.VISIBLE);
                    btn_control_start.setText("听单中");
                    btn_control_start.setTextColor(getResources().getColor(R.color.MAIN));
                    btn_control_start.setBackgroundResource(R.mipmap.main_control_panel_btn_listening_cover);
                    btn_control_ring.setVisibility(View.VISIBLE);
                    //设置听单中的旋转效果
                    RotateAnimation rotateAnimation = new RotateAnimation(0f,360f, Animation.RELATIVE_TO_SELF,
                            0.5f, Animation.RELATIVE_TO_SELF,0.5f);
                    rotateAnimation.setDuration(1000);
                    rotateAnimation.setRepeatCount(100000);//设置重复次数
                    rotateAnimation.setInterpolator(new LinearInterpolator());//不停顿
                    btn_control_ring.startAnimation(rotateAnimation);
                    PublicResource.isTrip = true;
                }
                break;
            case R.id.btn_control_empty:
                //载客，切换为空车
                if(PublicResource.isFull){
                    btn_control_empty.setBackgroundResource(R.mipmap.main_control_panel_btn_mode_normal);
                    btn_control_empty.setTextColor(getResources().getColor(R.color.MAIN));
                    btn_control_full.setBackgroundResource(R.mipmap.main_control_panel_btn_end_off_normal);
                    btn_control_full.setTextColor(getResources().getColor(R.color.white));
                    PublicResource.isFull = false;
                }
                //空车，弹出设置项
                else {
                    //设置中，收起弹出框
                    if(PublicResource.isEmptySetting){
                        other_popupWindow.dismiss();
                        btn_control_empty.setBackgroundResource(R.mipmap.main_control_panel_btn_mode_normal);
                        PublicResource.isEmptySetting = false;
                    }else {
                        showPopupWindow();
                        btn_control_empty.setBackgroundResource(R.mipmap.main_control_panel_btn_mode_normal_reverse);
                        PublicResource.isEmptySetting = true;
                    }

                }
                break;
            case R.id.btn_control_full:
                PublicResource.isEmptySetting = false;
                //载客，弹出设置项
                if(PublicResource.isFull){

                }
                //空车，切换为载客
                else {
                    btn_control_empty.setBackgroundResource(R.mipmap.main_control_panel_btn_end_off_normal);
                    btn_control_empty.setTextColor(getResources().getColor(R.color.white));
                    btn_control_full.setBackgroundResource(R.mipmap.main_control_panel_btn_mode_normal);
                    btn_control_full.setTextColor(getResources().getColor(R.color.MAIN));
                    PublicResource.isFull = true;
                }
                break;
        }
    }

    /**
     * 定位SDK监听函数
     */
    public class MyLocationListenner implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null || mMapView == null) {
                return;
            }
            mylocation = location;

            double lat = location.getLatitude();
            double lng = location.getLongitude();

            //经度和纬度都不是返回4.9E-324才记录
            if(!Constant.LOCATION_ERROR.equals(lat+"") && !Constant.LOCATION_ERROR.equals(lng+"")){
                sb.append(lat+","+lng+","+System.currentTimeMillis()+";");
            }

            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    .direction(location.getDerect()).latitude(lat)
                    .longitude(lng).build();

            mBaiduMap.setMyLocationData(locData);

            if (isFirstLoc) {

                isFirstLoc = false;
                LatLng ll = new LatLng(lat,lng);
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(ll).zoom(18.0f);
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));

            }
        }
    }

    /**
     * 添加附近覆盖物
     */
    private void addmarker(double lat, double lng){
        LatLng point = new LatLng(lat, lng);

        //构建Marker图标
        BitmapDescriptor bitmap = BitmapDescriptorFactory
                .fromResource(R.mipmap.hand);
        //构建MarkerOption，用于在地图上添加Marker
        OverlayOptions makeroption = new MarkerOptions()
                .position(point).icon(bitmap);
        //在地图上添加Marker，并显示
        mBaiduMap.addOverlay(makeroption);
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            String message = (String) msg.obj;
            switch (msg.what) {
                case Constant.HTTPUTIL_FAILURECODE:

                    break;
                //上传gps
                case SUCCESSCODE_UPLOADGPS:
                    try {
                        JSONObject jsonObject = new JSONObject(message);
                        String result = jsonObject.getString("result");
                        //注册/登录成功，返回上一页
                        if (Constant.RECODE_SUCCESS.equals(result)) {

                        }
                        else if (Constant.RECODE_FAILED.equals(result)) {
                            String errMsgs = jsonObject.getString("errMsgs");

                        }
                        else if (Constant.RECODE_FAILED_SESSION_WRONG.equals(result)) {
                            reLogin();
                            showTipsDialog("登录信息失效，请重新登录",1,dialogClickListener);
                            //取消自动上传位置信息
                            if(task!=null){
                                task.cancel();
                                task = null;
                            }
                            if(timer!=null){
                                timer.cancel();
                                timer = null;
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                //获取附近车辆
                case SUCCESSCODE_QUERYNEARBYUSERS:

                    break;
                //定时上传GPS
                case HANDLER_UPLOADGPS:
                    uploadGps();
                    sb = new StringBuffer();
                    break;
                //匹配挥手成功
                case SUCCESSCODE_CONFIRMWAVE:

                    break;
            }
        }
    };

    /**
     * 按钮事件监听
     */
    private OnDialogClickListener dialogClickListener = new OnDialogClickListener() {
        @Override
        public void doConfirm() {
            //未登录，跳转注册/登录页面
            if(needLogin()){
                startActivityByFade(new Intent(MainActivity.this, LoginActivity.class),false);
            }
        }

        @Override
        public void doConfirm(int type) {

        }
    };

    /**
     * 按钮事件监听
     */
    private OnDialogClickListener confirmIsTripClickListener = new OnDialogClickListener() {
        @Override
        public void doConfirm() {
            //确认上车
            Map<String, Object> params = new HashMap();
            params.put("haode_session_id", sp_user.getString("sessionId",""));
            params.put("matchingKey", matchingKey);
            HttpUtil.doGet(TAG,MainActivity.this,mHandler, Constant.HTTPUTIL_FAILURECODE,SUCCESSCODE_CONFIRMWAVE,
                    RequestAddress.confirmWave,params);
        }

        @Override
        public void doConfirm(int type) {

        }
    };

    private void speech() {
        //1.创建SpeechSynthesizer对象, 第二个参数：本地合成时传InitListener
        SpeechSynthesizer mTts = SpeechSynthesizer.createSynthesizer(this, null);
        //2.合成参数设置，详见《科大讯飞MSC API手册(Android)》SpeechSynthesizer 类
        mTts.setParameter(SpeechConstant.VOICE_NAME, "xiaoyan");//设置发音人
        mTts.setParameter(SpeechConstant.SPEED, "50");//设置语速
        mTts.setParameter(SpeechConstant.VOLUME, "80");//设置音量，范围0~100
        mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD); //设置云端
        //3.开始合成
        mTts.startSpeaking("距离你150米有乘客挥手", mSynListener);
    }

    //合成监听器
    private SynthesizerListener mSynListener = new SynthesizerListener() {

        //会话结束回调接口，没有错误时，error为null
        public void onCompleted(SpeechError error) {
        }

        //缓冲进度回调
        //percent为缓冲进度0~100，beginPos为缓冲音频在文本中开始位置，endPos表示缓冲音频在文本中结束位置，info为附加信息。
        public void onBufferProgress(int percent, int beginPos, int endPos, String info) {
        }

        //开始播放
        public void onSpeakBegin() {
        }

        //暂停播放
        public void onSpeakPaused() {
        }

        //播放进度回调
        //percent为播放进度0~100,beginPos为播放音频在文本中开始位置，endPos表示播放音频在文本中结束位置.
        public void onSpeakProgress(int percent, int beginPos, int endPos) {
        }

        //恢复播放回调接口
        public void onSpeakResumed() {
        }

        //会话事件回调接口
        public void onEvent(int arg0, int arg1, int arg2, Bundle arg3) {
        }
    };

    private PopupWindow other_popupWindow;
    private LinearLayout popupWindowLayout;
    private ListView popupwindow_listview;
    private List<Map<String, Object>> popupWindowList = new ArrayList<>();
    private SimpleAdapter popupWindowAdapter;

    /**
     * 弹出设置
     */
    private void showPopupWindow() {

        if (popupWindowLayout == null) {

            popupWindowLayout = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.popupwindow_view, null);
            popupWindowLayout.setHorizontalGravity(Gravity.CENTER);

//            popupwindow_listview = (ListView) popupWindowLayout.findViewById(R.id.popupwindow_listview);
//            popupwindow_listview.setClickable(true);
//            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) popupwindow_listview.getLayoutParams();
//            layoutParams.width = screenWidth / 3 - getResources().getDimensionPixelSize(R.dimen.line) * 2;
//            layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
//            layoutParams.gravity = Gravity.CENTER;
//            popupwindow_listview.setLayoutParams(layoutParams);
//
//            popupwindow_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                @Override
//                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
//                    other_popupWindow.dismiss();
//
//                    switch ((int) ((Map<String, Object>) parent.getAdapter().getItem(position)).get("tag")) {
//                        case 1:
//                            Intent openCameraIntent = new Intent(MainActivity.this, CaptureActivity.class);
//                            startActivity(openCameraIntent);
//                            break;
//                        case 2:
//                            findViewById(R.id.btn_message).performClick();
//                            break;
//
//                    }
//
//                }
//            });

//            popupWindowAdapter = new SimpleAdapter(this, popupWindowList, R.layout.popupwindow_item_view,
//                    new String[]{"img", "txt"}, new int[]{R.id.img, R.id.txt});
//            popupwindow_listview.setAdapter(popupWindowAdapter);

            other_popupWindow = new PopupWindow(popupWindowLayout,
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, false);
            other_popupWindow.setBackgroundDrawable(new BitmapDrawable(getResources()));
            other_popupWindow.setFocusable(true);
            other_popupWindow.setTouchable(false);
            other_popupWindow.setOutsideTouchable(false);

        }

//        popupWindowList.clear();
//
//        Map<String, Object> item = new HashMap<>();
//        item.put("img", R.mipmap.start);
//        item.put("txt", "扫一扫");
//        item.put("tag", 1);
//        popupWindowList.add(item);
//
//        item = new HashMap<>();
//        item.put("img", R.mipmap.end);
//        item.put("txt", "消息");
//        item.put("tag", 2);
//        popupWindowList.add(item);

//        popupWindowAdapter.notifyDataSetChanged();

        other_popupWindow.showAsDropDown(findViewById(R.id.btn_control_empty), screenWidth , 0);
    }

    @Override
    protected void onStart() {
        Log.d(TAG,"===onStart===");
        super.onStart();
    }

    @Override
    protected void onStop() {
        Log.d(TAG,"===onStop===");
        super.onStop();
    }

    @Override
    public void onPause() {
        Log.d(TAG,"===onPause===");

        isForeground = false;

        mMapView.onPause();

        locationService.unregisterListener(myListener);
        locationService.stop();

        //取消自动上传位置信息
        if(task!=null){
            task.cancel();
            task = null;
        }
        if(timer!=null){
            timer.cancel();
            timer = null;
        }
        JPushInterface.onPause(this);
        super.onPause();
    }

    @Override
    public void onResume() {
        Log.d(TAG,"===onResume===");

        isForeground = true;

        mMapView.onResume();

        locationService = ((HaodidriverApplication)getApplication()).locationService;
        locationService.registerListener(myListener);
        locationService.setLocationOption(locationService.getDefaultLocationClientOption());
        locationService.start();

        //定时上传gps
        if(!needLogin()){
            sb = new StringBuffer();
            doUploadGps();
        }

//        if(!needLogin()){
//            getNearByUsers();
//        }

        JPushInterface.onResume(this);
        super.onResume();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG,"===onDestroy===");
        unregisterReceiver(mMessageReceiver);
        // 关闭定位图层
        mBaiduMap.setMyLocationEnabled(false);
        mMapView.onDestroy();
        mMapView = null;

        btn_control_ring.clearAnimation();
        super.onDestroy();
    }

    private MessageReceiver mMessageReceiver;

    public void registerMessageReceiver() {
        mMessageReceiver = new MessageReceiver();
        IntentFilter filter = new IntentFilter();
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        filter.addAction(Constant.MESSAGE_RECEIVED_ACTION);
        registerReceiver(mMessageReceiver, filter);
    }

    public class MessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (Constant.MESSAGE_RECEIVED_ACTION.equals(intent.getAction())) {
                String message = intent.getStringExtra(KEY_MESSAGE);
                Log.d(TAG,"message=="+message);
                try {
                    JSONObject jsonObject = new JSONObject(message);
                    String event = jsonObject.getString("event");
                    //有乘客挥手
                    if(Constant.EVENT_HUISHOUSTART.equals(event)){
                        String userId = jsonObject.getString("userId");
                        String lng = jsonObject.getString("lng");
                        String lat = jsonObject.getString("lat");

                        double d_lat = Double.parseDouble(lat);
                        double d_lng = Double.parseDouble(lng);

                        addmarker(d_lat,d_lng);
                        speech();
                    }
                    //成功挥手匹配，弹出确认
                    if(Constant.EVENT_HUISHOUMATCH.equals(event)){
                        matchingKey = jsonObject.getString("matchingKey");
                        showTipsDialog("已成功为您匹配到乘客，乘客是否已上车？",2,confirmIsTripClickListener);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }
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
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                MobclickAgent.onKillProcess(this);
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
