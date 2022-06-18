package ideanity.oceans.antitheftapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity implements SensorEventListener {

    Switch motionSwitch, proximitySwitch, chargerSwitch;
    CountDownTimer cdt;
    private SensorManager sensorMan;
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private Sensor accelerometer;
    private float[] mGravity;
    private float mAccel;
    private float mAccelCurrent;
    private float mAccelLast;
    AlertDialog alertDialog;
    private static final int SENSOR_SENSITIVITY = 4;

    TextView rememberPassword;

    int mSwitchSet,pSwitchSet = 0;
    int chargerFlag, chargerFlag1, chargerFlag2 = 0;


    @Override
    public void onResume() {
        super.onResume();
        sensorMan.registerListener((SensorEventListener) this, accelerometer,
                SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(this, mSensor,
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorMan.unregisterListener(this);
        mSensorManager.unregisterListener(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_home);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        sensorMan = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorMan.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        alertDialog = new AlertDialog.Builder(this).create();
        chargerSwitch = (Switch) findViewById(R.id.sCharger);
        BroadcastReceiver receiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                int plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);

                if (plugged == BatteryManager.BATTERY_PLUGGED_AC || plugged==BatteryManager.BATTERY_PLUGGED_USB) {
                    chargerFlag = 1;
                } else if (plugged == 0) {
                    chargerFlag1 = 1;
                    chargerFlag = 0;
                    func();

                }
            }
        };
        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(receiver, filter);

        chargerSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {

                if (isChecked) {

                    if (chargerFlag != 1) {
                        Toast.makeText(HomeActivity.this, "Connect To Charger", Toast.LENGTH_SHORT).show();
                        chargerSwitch.setChecked(false);
                    } else {
                        Toast.makeText(HomeActivity.this, "Charger Protection Mode On", Toast.LENGTH_SHORT).show();
                        chargerFlag2 = 1;
                        func();
                    }


                } else {
                    chargerFlag2 = 0;
                }

            }
        });

        //alertDialog.show();

        mAccel = 0.00f;
        mAccelCurrent = SensorManager.GRAVITY_EARTH;
        mAccelLast = SensorManager.GRAVITY_EARTH;
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 20, 0);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        motionSwitch = (Switch) findViewById(R.id.sMotion);
        motionSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {

                if (isChecked) {
                    alertDialog.setTitle("Will Be Activated In 10 Seconds");
                    alertDialog.setMessage("00:10");

                    cdt = new CountDownTimer(10000, 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            alertDialog.setMessage("00:" + (millisUntilFinished / 1000));
                        }

                        @Override
                        public void onFinish() {
                            //info.setVisibility(View.GONE);
                            mSwitchSet = 1;
                            alertDialog.hide();
                            Toast.makeText(HomeActivity.this, "Motion Detection Mode Activated", Toast.LENGTH_SHORT).show();

                        }
                    }.start();
                    alertDialog.show();
                    alertDialog.setCancelable(false);


                } else {
                    Toast.makeText(HomeActivity.this, "Motion Switch Off", Toast.LENGTH_SHORT).show();
                    mSwitchSet = 0;
                }

            }
        });
        proximitySwitch = (Switch) findViewById(R.id.sProximity);
        proximitySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {

                if (isChecked) {
                    alertDialog.setTitle("Keep Phone In Your Pocket");
                    alertDialog.setMessage("00:10");
                    //Toast.makeText(MainActivity.this, "Motion Switch On", Toast.LENGTH_SHORT).show();


                    cdt = new CountDownTimer(10000, 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            alertDialog.setMessage("00:" + (millisUntilFinished / 1000));
                        }

                        @Override
                        public void onFinish() {
                            //info.setVisibility(View.GONE);
                            pSwitchSet = 1;
                            alertDialog.hide();
                            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                startForegroundService(new Intent(HomeActivity.this, PocketService.class));
                            } else {
                                startService(new Intent(HomeActivity.this, PocketService.class));
                            }
                        }
                    }.start();
                    alertDialog.show();
                    alertDialog.setCancelable(false);

                } else {
                    Toast.makeText(HomeActivity.this, "Motion Switch Off", Toast.LENGTH_SHORT).show();
                    pSwitchSet = 0;
                }

            }
        });


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void func() {
        if (chargerFlag == 0 && chargerFlag1 == 1 && chargerFlag2 == 1) {
            startActivity(new Intent(HomeActivity.this, EnterPin.class));
            chargerFlag2 = 0;
            finish();
        }

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            mGravity = event.values.clone();
            // Shake detection
            float x = mGravity[0];
            float y = mGravity[1];
            float z = mGravity[2];
            mAccelLast = mAccelCurrent;
            mAccelCurrent = (float) Math.sqrt(x * x + y * y + z * z);
            float delta = mAccelCurrent - mAccelLast;
            mAccel = mAccel * 0.9f + delta;
            // Make this higher or lower according to how much
            // motion you want to detect
            if (mAccel > 0.5) {
                //Toast.makeText(MainActivity.this, "Sensor Run Hua Bc", Toast.LENGTH_SHORT).show();
                //MediaPlayer mPlayer = MediaPlayer.create(MainActivity.this, R.raw.siren);
                //mPlayer.start();
                if (mSwitchSet == 1) {
//                    wakeDevice();
                    startActivity(new Intent(HomeActivity.this, EnterPin.class));
                    finish();
                }

            }
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void share(View view) {

        Intent a = new Intent(Intent.ACTION_SEND);
        final String appPackageName = getApplicationContext().getPackageName();
        String strAppLink = "";
        try
        {
            strAppLink = "https://play.google.com/store/apps/details?id=" + appPackageName;
        }
        catch (android.content.ActivityNotFoundException anfe)
        {
            strAppLink = "https://play.google.com/store/apps/details?id=" + appPackageName;
        }
        a.setType("text/link");
        String shareBody = "Hey! Download by app for free and win amazing cash prizes." +
                "\n"+""+strAppLink;
        String shareSub = "APP NAME/TITLE";
        a.putExtra(Intent.EXTRA_SUBJECT, shareSub);
        a.putExtra(Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(a, "Share Using"));
    }

}