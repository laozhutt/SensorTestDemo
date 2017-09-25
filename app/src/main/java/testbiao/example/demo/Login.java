package testbiao.example.demo;

import android.app.ProgressDialog;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import testbiao.example.demo.utils.Answer;
import testbiao.example.demo.utils.ConnectionHandler;
import testbiao.example.demo.utils.SensorData;

public class Login extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private SensorData sensorData;
    private int repeatNum = 1;
    private int repeat = 1;
    public static boolean uploadTest = false;
    private List<SensorData> sensorDataList;
    private static String TAG = "CollectDataService";

    public static boolean isFinish;
    private boolean isAppRunning;
    private int num = 100;
    private int interval = 20;
    private String filePath;
    private int pointNum = 0;
    private ConnectionHandler connection;
    private ConnectionHandler conn;
    ProgressDialog m_pDialog;
    public static Handler messageHandler;

    final static int REQUEST_PERMISSION = 1;

    final static int MESSAGE_NO_NETWORK = 2;
    final static int MESSAGE_NO_SENSOR = 3;

    @Override
    public void onSensorChanged(SensorEvent event) {

        switch (event.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                sensorData.accelerometerX = event.values[0];
                sensorData.accelerometerY = event.values[1];
                sensorData.accelerometerZ = event.values[2];
                break;
            case Sensor.TYPE_GYROSCOPE:
                sensorData.gyroscopeX = event.values[0];
                sensorData.gyroscopeY = event.values[1];
                sensorData.gyroscopeZ = event.values[2];
                break;
            case Sensor.TYPE_GRAVITY:
                sensorData.gravityX = event.values[0];
                sensorData.gravityY = event.values[1];
                sensorData.gravityZ = event.values[2];
                break;
            default:
                break;
        }
        if (repeat <= 0) {
            sensorManager.unregisterListener(this);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.autologin);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensorDataList = new ArrayList<SensorData>();
        sensorData = new SensorData();

        Button psb = (Button) findViewById(R.id.psbut);
        psb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startPasswordActivity();
            }
        });

        Button autobt = (Button) findViewById(R.id.autobut);
        autobt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                autocheck();
            }
        });

        TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        if(hasPerssion()){

        }else{
            getPermission();
//				Toast.makeText(MainActivity.this,"请打开应用存储权限申请",Toast.LENGTH_SHORT).show();

        }

        messageHandler = new Handler(){
            public void handleMessage(Message m){
                switch (m.what){
                    case MESSAGE_NO_NETWORK:
                        Toast.makeText(getApplicationContext(),"network not availiable",Toast.LENGTH_SHORT).show();
                        break;
                    case MESSAGE_NO_SENSOR:
                        Toast.makeText(getApplicationContext(), "该机型传感器不完整，无法利用SensorDemo检测！ ", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
            }
        };
    }

    @Override
    public void onBackPressed() {
        return;
    }

    private boolean isNetworkAvailable() {

        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo info = cm.getActiveNetworkInfo();
            if (info != null && info.isConnected()) {
                // 当前网络是连接的
                if (info.getState() == NetworkInfo.State.CONNECTED) {
                    // 当前所连接的网络可用
                    return true;
                }
            }
        }
        return false;
    }

    private boolean check_is_user_Random() {
        Random random = new Random();
        return random.nextBoolean();
    }

    private boolean check_is_user() {
        if(!collect()){
            return false;
        }
        Log.e("collected", "ok");
        if(pointNum < 100){
            return false;
        }
        query();
        int res = Answer.get();
        while (res == -2) res = Answer.get();

        if (res == 1 || res == 3 || res == 4) {
            return true;
        }
        return false;
    }

    public void writeTxt() {
        String dir = Environment.getExternalStorageDirectory().getPath() + "/SensorDemoData";
        File fileDir = new File(dir);
        if (!fileDir.exists()) {
            fileDir.mkdir();
        }
        try {
            pointNum = 0;
            String path = Environment.getExternalStorageDirectory().getPath() + "/SensorDemoData/" + System.currentTimeMillis() + ".txt";
            filePath = path;
            File file = new File(path);
            Writer writer = new FileWriter(file, true);

            for (SensorData sensorData : sensorDataList) {
                if (Math.abs(sensorData.gravityX) < 0.1 && Math.abs(sensorData.gravityY) < 0.1 && Math.abs(sensorData.gravityZ) < 0.1) {
                    Log.e("device", "error");
                    continue;
                }
                if (Math.abs(sensorData.gravityX) < 1.5 && Math.abs(sensorData.gravityY) < 1.5 && Math.abs(sensorData.gravityZ) > 9) {
                    Log.e("device", "flat");
                    continue;
                }
                if (sensorData.gyroscopeX == 0 && sensorData.gyroscopeY == 0 && sensorData.gyroscopeZ == 0) {
                    Log.e("device", "exception");
                    continue;
                }

                pointNum++;
                String string = sensorData.accelerometerX + "\n" +
                        sensorData.accelerometerY + "\n" +
                        sensorData.accelerometerZ + "\n" +
                        sensorData.gyroscopeX + "\n" +
                        sensorData.gyroscopeY + "\n" +
                        sensorData.gyroscopeZ + "\n" +
                        sensorData.gravityX + "\n" +
                        sensorData.gravityY + "\n" +
                        sensorData.gravityZ + "\n";
                writer.write(string);
            }

            writer.close();

        } catch (IOException e) {

            Looper.prepare();
            Toast.makeText(this,"请打开应用存储权限申请",Toast.LENGTH_SHORT).show();
            Looper.loop();
            e.printStackTrace();
        }
    }

    public boolean collect() {
        if (!isNetworkAvailable()) {
            Log.e(TAG, "network not availiable");
            Message m = new Message();
            m.what = MESSAGE_NO_NETWORK;
            messageHandler.sendMessage(m);
            return false;
        }
        //判断机型是否合适
        if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) == null || sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY) == null || sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE) == null) {
            Message m = new Message();
            m.what = MESSAGE_NO_SENSOR;
            messageHandler.sendMessage(m);
            return false;
        }

        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE),
                SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY),
                SensorManager.SENSOR_DELAY_FASTEST);
        sensorDataList.clear();
        saveData();
        return true;

    }

    private void saveData() {
        uploadTest = false;
        isAppRunning = true;


        if (isFinish == true) {
            num = 150;
        } else {
            num = 150;
        }
        repeat = repeatNum;

        while (sensorDataList.size() < num) {
            SensorData data = new SensorData();
            data.accelerometerX = sensorData.accelerometerX;
            data.accelerometerY = sensorData.accelerometerY;
            data.accelerometerZ = sensorData.accelerometerZ;
            data.gravityX = sensorData.gravityX;
            data.gravityY = sensorData.gravityY;
            data.gravityZ = sensorData.gravityZ;
            data.gyroscopeX = sensorData.gyroscopeX;
            data.gyroscopeY = sensorData.gyroscopeY;
            data.gyroscopeZ = sensorData.gyroscopeZ;
            sensorDataList.add(data);
         //   Log.e("data", data.toString());
            try {
                //50HZ
                Thread.sleep(interval);
            } catch (Exception e) {
                Log.e("CollectDataService", e.getMessage());
            }

        }
        writeTxt();
        Log.e("Num",pointNum+"");
        if (pointNum < 100) {
            Log.e("pointNum", "less than 100");
            return;
        }

        TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);

        connection = new ConnectionHandler(getString(R.string.server_address),
                getString(R.string.server_port),
                tm.getDeviceId());
        uploadTest = connection.postData(filePath, getString(R.string.server_test_method));

    }


    private void query() {

        TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);

        conn = new ConnectionHandler(getString(R.string.server_address),
                getString(R.string.server_port),
                tm.getDeviceId());

        Answer.inital();

        Log.e("alarmDetect", "begin");
        for (int i = 0; i < 10; i++) {
            Answer.set(conn.getResult(ConnectionHandler.VERSION));
            int intResult = Answer.get();
            Log.e("intResult", intResult + "");
            if (intResult != 0) {
                Log.e("stop", "1");
                return;
            } else if (intResult == 0) {

                try {
                    Thread.sleep(500);
                    Answer.set(conn.getResult(ConnectionHandler.VERSION));
                    intResult = Answer.get();
                } catch (Exception e) {
                    Log.e("CollectDataService", e.getMessage());
                }
            }
        }
    }


    private void autocheck() {
        m_pDialog = new ProgressDialog(this);
        m_pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        m_pDialog.setMessage("认证中");
        m_pDialog.setIndeterminate(false);
        m_pDialog.setCancelable(false);
        m_pDialog.show();

        new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub


                boolean isSelf = check_is_user();

                if (isSelf) {
                    Intent it = new Intent(Login.this, Sucess.class);
                    startActivity(it);
                } else {
                    Intent it = new Intent(Login.this, Fail.class);
                    startActivity(it);
                }
                m_pDialog.cancel();
//                m_pDialog.dismiss();

            }
        }).start();
    }

    private void startPasswordActivity() {
        Intent it = new Intent(Login.this, PasswordLogin.class);
        startActivity(it);
    }




    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if(requestCode == REQUEST_PERMISSION){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
//                TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
//                configureObject.setProperty(getString(R.string.property_imei),tm.getDeviceId());

            }else{

            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void getPermission(){

        if(ContextCompat.checkSelfPermission(Login.this, android.Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(Login.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
//            TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
//            configureObject.setProperty(getString(R.string.property_imei),tm.getDeviceId());
        }
        else{
            ActivityCompat.requestPermissions(Login.this, new String[]{android.Manifest.permission.READ_PHONE_STATE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION );
        }

    }

    private boolean hasPerssion(){
        PackageManager pm = getPackageManager();
        boolean permission = (PackageManager.PERMISSION_GRANTED == pm.checkPermission("android.permission.READ_PHONE_STATE", "testbiao.example.demo") &&
                PackageManager.PERMISSION_GRANTED == pm.checkPermission("android.permission.READ_PHONE_STATE", "testbiao.example.demo"));
        if (permission) {
            return true;
        }
        return false;
    }
}
