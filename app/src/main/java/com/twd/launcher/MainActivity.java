package com.twd.launcher;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;

import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.twd.launcher.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnFocusChangeListener {

    private final static String TAG = MainActivity.class.getSimpleName();
    private TextView tv_time;
    private TextView tv_day;
    private ImageView im_usb;
    private ImageView im_wifi;
    private ImageView im_headset;

    private Handler timeHandler = new Handler();
    private boolean hasNetworkConnection ;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        //初始化时间
        sharedPreferences = getSharedPreferences("NetworkConnected",Context.MODE_PRIVATE);
        hasNetworkConnection = sharedPreferences.getBoolean("hasNetworkConnection",false);
        updateTimeRunnable.run();
    }

    private Runnable updateTimeRunnable = new Runnable() {
        @Override
        public void run() {

            if (!hasNetworkConnection){
                //检查网络连接状态
                ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

                if (networkInfo != null && networkInfo.isConnected()){
                    // 如果设备已连接到网络，从网络获取时间和日期数据
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("hasNetworkConnection",true);
                    editor.apply();
                    hasNetworkConnection = true;
                    // 获取当前时间和日期
                    Calendar calendar = Calendar.getInstance();
                    Date currentDate = calendar.getTime();
                    // 设置日期的格式
                    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    String formattedDate = dateFormat.format(currentDate);
                    // 设置时间的格式
                    DateFormat timeFormat = new SimpleDateFormat("HH:mm");
                    String formattedTime = timeFormat.format(currentDate);
                    // 在TextView上更新日期和时间
                    tv_day.setText(formattedDate);
                    tv_time.setText(formattedTime);
                    // 每隔一秒更新一次时间
                    timeHandler.postDelayed(this, 1000);
                } else {
                    // 如果设备未连接到网络，设置时间为--:--，日期不显示
                    tv_time.setText("--:--");
                    tv_day.setText("");
                    // 每隔一秒检查网络连接状态
                    timeHandler.postDelayed(this, 1000);
                    return;
                }
            }
            //获取当前时间和日期
            Calendar calendar = Calendar.getInstance();
            Date currentDate = calendar.getTime();

            //设置日期的格式
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd ");
            String formattedDate = dateFormat.format(currentDate);

            //设置时间的格式
            DateFormat timeFormat = new SimpleDateFormat("HH:mm");
            String formattedTime = timeFormat.format(currentDate);

            //在TextView上更新日期和时间
            tv_day.setText(formattedDate);
            tv_time.setText(formattedTime);

            //每隔一秒更新一次时间
            timeHandler.postDelayed(this,1000);
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timeHandler.removeCallbacks(updateTimeRunnable);
        //unregisterReceiver(usbDeviceReceiver);
        unregisterReceiver(customReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateTimeRunnable.run();

        IntentFilter customFilter = new IntentFilter();
        customFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        customFilter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        customFilter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        customFilter.addAction(Intent.ACTION_HEADSET_PLUG);
        registerReceiver(customReceiver,customFilter);
    }

    private void initView(){
        tv_time = findViewById(R.id.tv_time);
        tv_day = findViewById(R.id.tv_day);
        im_usb = findViewById(R.id.im_usb);
        im_wifi = findViewById(R.id.im_wifi);
        im_headset = findViewById(R.id.im_headset);

        im_usb.setVisibility(usbIsConnect() ? View.VISIBLE : View.GONE);
        im_headset.setVisibility(headsetIsWired() ? View.VISIBLE : View.GONE);
        /*IntentFilter filter = new IntentFilter();
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        registerReceiver(usbDeviceReceiver,filter);*/
    }

    @Override
    public void onClick(View v) {
    }
    @Override
    public void onFocusChange(View view, boolean hasFocus) {
        if (hasFocus) {
            // 此处为得到焦点时的处理内容
            ViewCompat.animate(view)
                    .scaleX(1.10f)
                    .scaleY(1.10f)
                    .translationZ(1)
                    .start();

        } else {
            // 此处为失去焦点时的处理内容
            ViewCompat.animate(view)
                    .scaleX(1)
                    .scaleY(1)
                    .translationZ(1)
                    .start();
        }
    }

    private BroadcastReceiver usbDeviceReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)){
                //执行相应的逻辑
                im_usb.setImageResource(R.drawable.icon_usb);
                Toast.makeText(context, "-----插入------", Toast.LENGTH_SHORT).show();
            } else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
                //执行相应的逻辑
                im_usb.setImageDrawable(null);
                Toast.makeText(context, "-----拔出------", Toast.LENGTH_SHORT).show();
            }
        }
    };

    private BroadcastReceiver customReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action != null){
                switch (action){
                    case ConnectivityManager.CONNECTIVITY_ACTION:
                        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                        if (networkInfo != null && networkInfo.isConnected()) {
                            // Network connected
                            Log.d("BroadcastReceiver", "网络连接");
                            im_wifi.setVisibility(View.VISIBLE);
                        } else {
                            // Network disconnected
                            Log.d("BroadcastReceiver", "网络未连接");
                            im_wifi.setVisibility(View.GONE);
                        }
                        break;
                    case UsbManager.ACTION_USB_DEVICE_ATTACHED:
                        // USB device attached
                        Log.d("BroadcastReceiver", "USB d插入");
                        im_usb.setVisibility(View.VISIBLE);
                        break;

                    case UsbManager.ACTION_USB_DEVICE_DETACHED:
                        // USB device detached
                        Log.d("BroadcastReceiver", "USB 拔出");
                        im_usb.setVisibility(View.GONE);
                        break;
                    case Intent.ACTION_HEADSET_PLUG:
                        // Headset plugged or unplugged
                        int state = intent.getIntExtra("state", -1);
                        if (state == 0) {
                            // Headset unplugged
                            Log.d("BroadcastReceiver", "耳机插入");
                            im_headset.setVisibility(View.VISIBLE);
                        } else if (state == 1) {
                            // Headset plugged in
                            Log.d("BroadcastReceiver", "耳机拔出");
                            im_headset.setVisibility(View.GONE);
                        }
                        break;
                }
            }
        }
    };

    private boolean usbIsConnect(){
        UsbManager usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        HashMap<String, UsbDevice> deviceList = usbManager.getDeviceList();
        for (Map.Entry<String,UsbDevice> entry:deviceList.entrySet()){
            UsbDevice device = entry.getValue();
            //检测设备是否已经挂载到USB设备
            return usbManager.hasPermission(device);
        }
        return false;
    }

    private boolean headsetIsWired(){
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        return audioManager.isWiredHeadsetOn();
    }
}