package com.twd.launcher;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;

import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
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

    private ImageView im_cast;
    private ImageView im_file;
    private ImageView im_hdmi;
    private ImageView im_setting;

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

        im_cast = findViewById(R.id.im_cast);
        im_hdmi = findViewById(R.id.im_hdmi);
        im_file = findViewById(R.id.im_file);
        im_setting = findViewById(R.id.im_setting);

        im_cast.requestFocus();
        im_file.setBackgroundResource(0);
        im_hdmi.setBackgroundResource(0);
        im_setting.setBackgroundResource(0);
        im_cast.setOnFocusChangeListener(this::onFocusChange);
        im_hdmi.setOnFocusChangeListener(this::onFocusChange);
        im_file.setOnFocusChangeListener(this::onFocusChange);
        im_setting.setOnFocusChangeListener(this::onFocusChange);

        im_cast.setOnClickListener(this::onClick);
        im_hdmi.setOnClickListener(this::onClick);
        im_file.setOnClickListener(this::onClick);
        im_setting.setOnClickListener(this::onClick);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        if (v.getId() == R.id.im_cast){
            Log.i(TAG, "onClick: ---cast---");
            //intent.setComponent(new ComponentName("com.android.toofifi","com.android.toofifi.ui.activity.MainActivity"));
        } else if (v.getId() == R.id.im_file) {
            Log.i(TAG, "onClick: ---file---");
            //intent.setComponent(new ComponentName("com.softwinner.miracastReceiver","com.softwinner.miracastReceiver.Miracast"));
        } else if (v.getId() == R.id.im_hdmi) {
            Log.i(TAG, "onClick: ---hdmi---");
            //intent.setComponent(new ComponentName("com.allwinnertech.platinum.media","com.allwinnertech.platinum.media.activity.SettingsActivity"));
        } else if (v.getId() == R.id.im_setting) {
            Log.i(TAG, "onClick: ---setting---");
            //intent.setComponent(new ComponentName("com.allwinnertech.platinum.media","com.allwinnertech.platinum.media.activity.SettingsActivity"));
        }

        // 启动应用程序
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Log.e(TAG, "No app found to handle the intent");
        }
    }
    @Override
    public void onFocusChange(View view, boolean hasFocus) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
        if (hasFocus) {
                if (view.getId() == R.id.im_cast){
                    im_cast.setForeground(getDrawable(R.drawable.border_black));
                } else if (view.getId() == R.id.im_file) {
                    im_file.setForeground(getDrawable(R.drawable.border_black));
                } else if (view.getId() == R.id.im_hdmi) {
                    im_hdmi.setForeground(getDrawable(R.drawable.border_black));
                } else if (view.getId() == R.id.im_setting) {
                    im_setting.setForeground(getDrawable(R.drawable.border_black));
                }
            } else {
                if (view.getId() == R.id.im_cast){
                    im_cast.setForeground(null);
                } else if (view.getId() == R.id.im_file) {
                    im_file.setForeground(null);
                } else if (view.getId() == R.id.im_hdmi) {
                    im_hdmi.setForeground(null);
                } else if (view.getId() == R.id.im_setting) {
                    im_setting.setForeground(null);
                }
            }
        }

    }


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
                            im_wifi.setImageResource(R.drawable.icon_wifi);
                        } else {
                            // Network disconnected
                            Log.d("BroadcastReceiver", "网络未连接");
                            im_wifi.setImageResource(R.drawable.icon_wifi_no);
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
                            Log.d("BroadcastReceiver", "耳机拔出");
                            im_headset.setVisibility(View.GONE);
                        } else if (state == 1) {
                            // Headset plugged in
                            Log.d("BroadcastReceiver", "耳机插入");
                            im_headset.setVisibility(View.VISIBLE);
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