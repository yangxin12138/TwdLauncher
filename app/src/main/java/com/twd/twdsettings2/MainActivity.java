package com.twd.twdsettings2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnFocusChangeListener {

    private final static String TAG = MainActivity.class.getSimpleName();
    private TextView tv_time;
    private TextView tv_day;
    private ImageButton bt_hdmi;
    private ImageButton bt_setting;
    private ImageButton bt_androidCast;
    private ImageButton bt_airdrop;
    private ImageButton bt_video;
    private ImageButton bt_music;
    private ImageButton bt_pic;
    private ImageButton bt_office;

    private ImageButton bt_dlan;

    private ImageButton bt_wired;
    private ImageView im_usb;

    private Handler timeHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        //初始化时间
        updateTimeRunnable.run();
    }

    private Runnable updateTimeRunnable = new Runnable() {
        @Override
        public void run() {
            //获取当前时间和日期
            Calendar calendar = Calendar.getInstance();
            Date currentDate = calendar.getTime();

            //设置日期的格式
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd EEEE");
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
        unregisterReceiver(usbDeviceReceiver);
    }

    private void initView(){
        tv_time = findViewById(R.id.tv_time);
        tv_day = findViewById(R.id.tv_day);
        bt_hdmi = findViewById(R.id.imButton_hdmi);
        bt_setting = findViewById(R.id.imButton_setting);
        bt_androidCast = findViewById(R.id.imButton_androidcast);
        bt_airdrop = findViewById(R.id.imButton_airdrop);
        bt_video = findViewById(R.id.imButton_video);
        bt_music = findViewById(R.id.imButton_music);
        bt_pic = findViewById(R.id.imButton_pic);
        bt_office = findViewById(R.id.imButton_office);
        bt_dlan = findViewById(R.id.im_dlan);
        bt_wired = findViewById(R.id.im_wired);
        im_usb = findViewById(R.id.im_usb);

        bt_hdmi.setFocusable(true);
        bt_hdmi.setFocusableInTouchMode(true);
        bt_setting.setFocusable(true);
        bt_setting.setFocusableInTouchMode(true);
        bt_androidCast.setFocusable(true);
        bt_androidCast.setFocusableInTouchMode(true);
        bt_airdrop.setFocusable(true);
        bt_airdrop.setFocusableInTouchMode(true);
        bt_video.setFocusable(true);
        bt_video.setFocusableInTouchMode(true);
        bt_music.setFocusable(true);
        bt_music.setFocusableInTouchMode(true);
        bt_pic.setFocusable(true);
        bt_pic.setFocusableInTouchMode(true);
        bt_office.setFocusable(true);
        bt_office.setFocusableInTouchMode(true);
        bt_dlan.setFocusable(true);
        bt_dlan.setFocusableInTouchMode(true);
        bt_wired.setFocusable(true);
        bt_wired.setFocusableInTouchMode(true);

        bt_hdmi.setOnFocusChangeListener(this);
        bt_setting.setOnFocusChangeListener(this);
        bt_androidCast.setOnFocusChangeListener(this);
        bt_airdrop.setOnFocusChangeListener(this);
        bt_video.setOnFocusChangeListener(this);
        bt_music.setOnFocusChangeListener(this);
        bt_pic.setOnFocusChangeListener(this);
        bt_office.setOnFocusChangeListener(this);
        bt_dlan.setOnFocusChangeListener(this);
        bt_wired.setOnFocusChangeListener(this);

        bt_hdmi.setOnClickListener(this);
        bt_setting.setOnClickListener(this);
        bt_androidCast.setOnClickListener(this);
        bt_airdrop.setOnClickListener(this);
        bt_video.setOnClickListener(this);
        bt_music.setOnClickListener(this);
        bt_pic.setOnClickListener(this);
        bt_office.setOnClickListener(this);
        bt_dlan.setOnClickListener(this);
        bt_wired.setOnClickListener(this);

        //获取UsbManager的实例
        UsbManager usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        IntentFilter filter = new IntentFilter();
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        registerReceiver(usbDeviceReceiver,filter);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.imButton_hdmi){
            Toast.makeText(this, "点击hdmi", Toast.LENGTH_SHORT).show();
            Log.i(TAG, "------------onClick: bt_hdmi");
        } else if (v.getId() == R.id.imButton_setting) {
            Toast.makeText(this, "点击Setting", Toast.LENGTH_SHORT).show();
            Log.i(TAG, "------------onClick:  bt_setting");
        } else if (v.getId() == R.id.imButton_androidcast) {
            Toast.makeText(this, "点击androidCast", Toast.LENGTH_SHORT).show();
            Log.i(TAG, "------------onClick:  bt_androidCast");
        } else if (v.getId() == R.id.imButton_airdrop) {
            Toast.makeText(this, "点击airdrop", Toast.LENGTH_SHORT).show();
            Log.i(TAG, "------------onClick:  bt_airdrop");
        } else if (v.getId() == R.id.imButton_video) {
            Toast.makeText(this, "点击video", Toast.LENGTH_SHORT).show();
            Log.i(TAG, "------------onClick:  bt_video");
        } else if (v.getId() == R.id.imButton_pic) {
            Toast.makeText(this, "点击picture", Toast.LENGTH_SHORT).show();
            Log.i(TAG, "-------------onClick:  bt_picture");
        } else if (v.getId() == R.id.imButton_music) {
            Toast.makeText(this, "点击music", Toast.LENGTH_SHORT).show();
            Log.i(TAG, "-------------onClick:  bt_music");
        } else if (v.getId() == R.id.imButton_office) {
            Toast.makeText(this, "点击office", Toast.LENGTH_SHORT).show();
            Log.i(TAG, "--------------onClick:  bt_office");
        } else if (v.getId() == R.id.im_dlan) {
            Toast.makeText(this, "点击dlan", Toast.LENGTH_SHORT).show();
            Log.i(TAG, "--------------onClick:  bt_dlan");
        } else if (v.getId() == R.id.im_wired) {
            Toast.makeText(this, "点击wired", Toast.LENGTH_SHORT).show();
            Log.i(TAG, "--------------onClick:  bt_wired");
        }
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
                //USB设备已经插入
                UsbDevice usbDevice = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                //执行相应的逻辑
                im_usb.setImageResource(R.drawable.icon_usb_normal);
                Toast.makeText(context, "-----插入------", Toast.LENGTH_SHORT).show();
            } else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
                //USB设备已经拔出
                UsbDevice usbDevice = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                //执行相应的逻辑
                im_usb.setImageResource(R.drawable.icon_usb_remove);
                Toast.makeText(context, "-----拔出------", Toast.LENGTH_SHORT).show();
                try {
                    Thread.sleep(2000);
                }catch (Exception e){
                    e.printStackTrace();
                }
                im_usb.setImageDrawable(null);
            }
        }
    };
}