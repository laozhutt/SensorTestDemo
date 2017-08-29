package testbiao.example.demo.utils;

/**
 * Created by herbertxu on 5/29/16.
 */

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;

import testbiao.example.demo.R;

public class ConnectionHandler {
    HttpURLConnection connection;
    URL url;
    String baseurl;
    String IMEI;
    Context context;

    public static String VERSION = "99999";
    private static String TAG = "ConnectHandler";
    final static int MESSAGE_CURRENT_UPLOAD_FINISH = 13;
    private static int optimizing_count = 0;
    private double th_acc = 0.7;

    public ConnectionHandler(String address, String port, String imei) {
        baseurl = address + ":" + port;
//        IMEI = "1";
        IMEI = imei;
        context = ResourceManager.getContext();
    }


    public boolean postData(final String filePath, String method) {
        HttpRequestManager post = new HttpRequestManager();
        post.setCharset(HTTP.UTF_8).setConnectionTimeout(5000000)
                .setSoTimeout(10000000);
        final ContentType TEXT_PLAIN = ContentType.create("text/plain",
                Charset.forName(HTTP.UTF_8));


        post.setOnHttpRequestListener(new HttpRequestManager.OnHttpRequestListener() {

            @Override
            public void onRequest(HttpRequestManager request)
                    throws Exception {
                // 设置发送请求的header信息

                // 配置要POST的数据
                MultipartEntityBuilder builder = request
                        .getMultipartEntityBuilder();
                builder.addTextBody("imei", IMEI, TEXT_PLAIN);// 中文

                // 附件部分
                builder.addBinaryBody("path", new File(filePath));

                request.buildPostEntity();
            }

            @Override
            public String onSucceed(int statusCode,
                                    HttpRequestManager request) throws Exception {
                return request.getInputStream();
            }

            @Override
            public String onFailed(int statusCode,
                                   HttpRequestManager request) throws Exception {
                return request.getInputStream();
            }
        });

        try {
            if (method.equals("train")) {
//                Log.e("traintesult","nnn");
                String result = post.post("http://" + baseurl + "/" + method + "/");
                JSONObject obj = new JSONObject(result);


                if (obj.getInt("result") == 0) {
                    int sitFileNum = obj.getInt("numsitarff");
                    int walkFileNum = obj.getInt("numwalkarff");
                    int requiredFileNum = obj.getInt("requiredfiles");
                    SharedPreferences.Editor editor = context.getSharedPreferences(context.getString(R.string.progressPref), Context.MODE_PRIVATE).edit();
                    editor.putInt(context.getString(R.string.numsitarff), sitFileNum);
                    editor.putInt(context.getString(R.string.numwalkarff), walkFileNum);
                    editor.putInt(context.getString(R.string.numfilesrequired), requiredFileNum);
                    editor.commit();

                    Intent updateProgressIntent = new Intent();
                    updateProgressIntent.setAction("com.train.updateProgress");
                    context.sendBroadcast(updateProgressIntent);
                    Log.d("trainUpload", "ok");
                    return true;
                }
            }
            if (method.equals("test")) {
                String result = post.post("http://" + baseurl + "/" + method + "/");
                Log.e("testresult", result);
                JSONObject obj = new JSONObject(result);
                int i = obj.getInt("max_version");

                if (i > 0) {
                    Log.e("testUpload", "ok");
                    Log.e("VERSION", String.valueOf(i));
                    VERSION = String.valueOf(i);

                    Intent serviceIntent = new Intent();
                    serviceIntent.setAction("com.detect");
                    serviceIntent.putExtra("state", "alarm");
                    context.sendBroadcast(serviceIntent);

                    return true;
                }
            }

        } catch (Exception e) {

            Looper.prepare();
            Toast toast = Toast.makeText(context.getApplicationContext(), "network error or plane data", Toast.LENGTH_SHORT);
            toast.show();
            Looper.loop();

            e.printStackTrace();
        }

        return false;
    }

    public boolean postAdvData(final String filePath, String method) {
        HttpRequestManager post = new HttpRequestManager();
        post.setCharset(HTTP.UTF_8).setConnectionTimeout(5000000)
                .setSoTimeout(10000000);
        final ContentType TEXT_PLAIN = ContentType.create("text/plain",
                Charset.forName(HTTP.UTF_8));


        post.setOnHttpRequestListener(new HttpRequestManager.OnHttpRequestListener() {

            @Override
            public void onRequest(HttpRequestManager request)
                    throws Exception {
                // 设置发送请求的header信息

                // 配置要POST的数据
                MultipartEntityBuilder builder = request
                        .getMultipartEntityBuilder();
                builder.addTextBody("imei", IMEI, TEXT_PLAIN);// 中文

                // 附件部分
                builder.addBinaryBody("path", new File(filePath));

                request.buildPostEntity();
            }

            @Override
            public String onSucceed(int statusCode,
                                    HttpRequestManager request) throws Exception {
                return request.getInputStream();
            }

            @Override
            public String onFailed(int statusCode,
                                   HttpRequestManager request) throws Exception {
                return request.getInputStream();
            }
        });

        try {
            if (method.equals("train")) {
//                Log.e("traintesult","nnn");
                String result = post.post("http://" + baseurl + "/" + method + "/");
                JSONObject obj = new JSONObject(result);


                if (obj.getInt("result") == 0) {
                    int sitFileNum = obj.getInt("numsitarff");
                    int walkFileNum = obj.getInt("numwalkarff");
                    int requiredFileNum = obj.getInt("requiredfiles");
                    SharedPreferences.Editor editor = context.getSharedPreferences(context.getString(R.string.progressPref), Context.MODE_PRIVATE).edit();
                    editor.putInt(context.getString(R.string.numsitarff), sitFileNum);
                    editor.putInt(context.getString(R.string.numwalkarff), walkFileNum);
                    editor.putInt(context.getString(R.string.numfilesrequired), requiredFileNum);
                    editor.commit();

                    Intent updateProgressIntent = new Intent();
                    updateProgressIntent.setAction("com.train.updateProgress");
                    context.sendBroadcast(updateProgressIntent);
                    Log.d("trainUpload", "ok");
                    return true;
                }
            }
            if (method.equals("test")) {
                String result = post.post("http://" + baseurl + "/" + method + "/");
                Log.e("testresult", result);
                JSONObject obj = new JSONObject(result);
                int i = obj.getInt("max_version");

                if (i > 0) {
                    Log.e("testUpload", "ok");
                    Log.e("VERSION", String.valueOf(i));
                    VERSION = String.valueOf(i);
                    Intent serviceIntent = new Intent();
                    serviceIntent.setAction("com.detect");
                    serviceIntent.putExtra("state", "advAlarm");
                    context.sendBroadcast(serviceIntent);
                    return true;
                }
            }

        } catch (Exception e) {

            Looper.prepare();
            Toast toast = Toast.makeText(context.getApplicationContext(), "network error or plane data", Toast.LENGTH_SHORT);
            toast.show();
            Looper.loop();

            e.printStackTrace();
        }

        return false;
    }

    public boolean postSimuData(final String filePath, String method) {
        HttpRequestManager post = new HttpRequestManager();
        post.setCharset(HTTP.UTF_8).setConnectionTimeout(5000000)
                .setSoTimeout(10000000);
        final ContentType TEXT_PLAIN = ContentType.create("text/plain",
                Charset.forName(HTTP.UTF_8));


        post.setOnHttpRequestListener(new HttpRequestManager.OnHttpRequestListener() {

            @Override
            public void onRequest(HttpRequestManager request)
                    throws Exception {
                // 设置发送请求的header信息

                // 配置要POST的数据
                MultipartEntityBuilder builder = request
                        .getMultipartEntityBuilder();
                builder.addTextBody("imei", IMEI, TEXT_PLAIN);// 中文

                // 附件部分
                builder.addBinaryBody("path", new File(filePath));

                request.buildPostEntity();
            }

            @Override
            public String onSucceed(int statusCode,
                                    HttpRequestManager request) throws Exception {
                return request.getInputStream();
            }

            @Override
            public String onFailed(int statusCode,
                                   HttpRequestManager request) throws Exception {
                return request.getInputStream();
            }
        });

        try {
            if (method.equals("test")) {
                String result = post.post("http://" + baseurl + "/" + method + "/");
                Log.e("simutestresult", result);
                JSONObject obj = new JSONObject(result);
                int i = obj.getInt("max_version");

                if (i > 0) {
                    Log.e("simutestUpload", "ok");
                    Log.e("simuVERSION", String.valueOf(i));
                    VERSION = String.valueOf(i);
                    Intent serviceIntent = new Intent();
                    serviceIntent.setAction("com.detect");
                    serviceIntent.putExtra("state", "simuAlarm");
                    context.sendBroadcast(serviceIntent);
                    return true;
                }
            }

        } catch (Exception e) {

            Looper.prepare();
            Toast toast = Toast.makeText(context.getApplicationContext(), "network error or plane data", Toast.LENGTH_SHORT);
            toast.show();

            e.printStackTrace();
        }

        return false;
    }


    /**
     * 利用返回为-1,0,1来对应区别不同情况
     * -1：检测出有异常
     * 0：返回空{}
     * 1：返回检测出没有异常，或者timeout
     * 2:不存在的model与文件所属类型不一致
     * 3：walk模型正常
     * 4：sit模型正常
     *
     * @param version
     * @return
     */
    public int getResult(String version) {

        final String Version = version;
        HttpRequestManager post = new HttpRequestManager();
        post.setCharset(HTTP.UTF_8).setConnectionTimeout(5000)
                .setSoTimeout(10000);
        final ContentType TEXT_PLAIN = ContentType.create("text/plain",
                Charset.forName(HTTP.UTF_8));


        post.setOnHttpRequestListener(new HttpRequestManager.OnHttpRequestListener() {

            @Override
            public void onRequest(HttpRequestManager request)
                    throws Exception {
                // 设置发送请求的header信息

                // 配置要POST的数据
                MultipartEntityBuilder builder = request
                        .getMultipartEntityBuilder();
                builder.addTextBody("imei", IMEI, TEXT_PLAIN);// 中文
                builder.addTextBody("version", Version, TEXT_PLAIN);//测试时候改为指定数值，方便test


                request.buildPostEntity();
            }

            @Override
            public String onSucceed(int statusCode,
                                    HttpRequestManager request) throws Exception {
                return request.getInputStream();
            }

            @Override
            public String onFailed(int statusCode,
                                   HttpRequestManager request) throws Exception {
                return request.getInputStream();
            }
        });

        try {


            String result = post.post("http://" + baseurl + "/query/");
            Log.e("run", result);
            if (!result.equals("{}")) {//不为空

                JSONObject obj = new JSONObject(result);

                String retString = obj.getString("result");

                Log.e(TAG, "query result: " + retString);
                String sitAccuracyString = retString.substring(retString.indexOf("sit_accuracy="), retString.indexOf(",walk_accuracy="));
                Log.e(TAG, "sitAccuracyString: " + sitAccuracyString);
                sitAccuracyString = sitAccuracyString.replaceAll("sit_accuracy=", "");
                Log.e(TAG, "sitAccuracyString: " + sitAccuracyString);
                String walkAccuracyString = retString.substring(retString.indexOf("walk_accuracy="), retString.indexOf("\nsit_precision="));
                walkAccuracyString = walkAccuracyString.replaceAll("walk_accuracy=", "");
                Log.e(TAG, "query result: " + "sit" + sitAccuracyString + "walk" + walkAccuracyString);
                double sitAccuracyDouble = Double.valueOf(sitAccuracyString);
                double walkAccuracyDouble = Double.valueOf(walkAccuracyString);
                double resultAccuracy = sitAccuracyDouble > walkAccuracyDouble ? sitAccuracyDouble : walkAccuracyDouble;

                Log.e("AccuracyDouble" , resultAccuracy+"");
                Log.e(TAG, "resultAccuracy" + resultAccuracy);
                //新增该文件的是属于walk还是sit,
                /**
                 * if file is A and A's model not exist, return 2
                 * A contain sit and walk
                 * 其余照常
                 */
                String isSitModelExistsString = retString.substring(retString.indexOf("isSitModelExists="), retString.indexOf(",isWalkModelExists="));
                isSitModelExistsString = isSitModelExistsString.replaceAll("isSitModelExists=", "");
                String isWalkModelExistsString = retString.substring(retString.indexOf("isWalkModelExists="), retString.indexOf(",isSit="));
                isWalkModelExistsString = isWalkModelExistsString.replaceAll("isWalkModelExists=", "");

                String isSitString = retString.substring(retString.indexOf("isSit="));
                isSitString = isSitString.replaceAll("isSit=", "");
                Log.e(TAG, "query result: " + "isSitModelExistsString:" + isSitModelExistsString + "isWalkModelExistsString:" + isWalkModelExistsString + "isSitString:" + isSitString);
                //首先判断是否有model不存在，如不存在，判断不存在的model是否与文件所属类型一致
                if (isSitModelExistsString.equals("False") || isWalkModelExistsString.equals("False")) {
                    if ((isSitModelExistsString.equals("False") && isSitString.equals("True")) || (isWalkModelExistsString.equals("False") && isSitString.equals("False"))) {
                        return 2;

                    }
                }
                //判断一个model存在一个model不存在的情况，这时候文件与存再model不一致的情况已在上面讨论，现只讨论对应model的文件的精度
                if (isSitModelExistsString.equals("False") && isWalkModelExistsString.equals("True")) {
                    if (walkAccuracyDouble > th_acc) {
                        return 3;
                    }
                }
                if (isSitModelExistsString.equals("True") && isWalkModelExistsString.equals("False")) {
                    if (sitAccuracyDouble > th_acc) {
                        return 4;
                    }
                }

                if (resultAccuracy > th_acc) {
                    return 1;
                } else {
                    return -1;
                }
            }

        } catch (Exception e) {
            Log.e("C", e.toString());
        }

        Log.e("Fuwuqi", "busy");
        return 0;
    }


    public boolean selfOrOthers(String version, String signal) {

        final String Version = version;
        final String Signal = signal;
        HttpRequestManager post = new HttpRequestManager();
        post.setCharset(HTTP.UTF_8).setConnectionTimeout(5000000)
                .setSoTimeout(10000000);
        final ContentType TEXT_PLAIN = ContentType.create("text/plain",
                Charset.forName(HTTP.UTF_8));


        post.setOnHttpRequestListener(new HttpRequestManager.OnHttpRequestListener() {

            @Override
            public void onRequest(HttpRequestManager request)
                    throws Exception {
                // 设置发送请求的header信息

                // 配置要POST的数据
                MultipartEntityBuilder builder = request
                        .getMultipartEntityBuilder();
                builder.addTextBody("imei", IMEI, TEXT_PLAIN);// 中文
                builder.addTextBody("version", Version, TEXT_PLAIN);
                builder.addTextBody("signal", Signal, TEXT_PLAIN);


                request.buildPostEntity();
            }

            @Override
            public String onSucceed(int statusCode,
                                    HttpRequestManager request) throws Exception {
                return request.getInputStream();
            }

            @Override
            public String onFailed(int statusCode,
                                   HttpRequestManager request) throws Exception {
                return request.getInputStream();
            }
        });

        try {

            String result = post.post("http://" + baseurl + "/manual_fix/");
            Log.e(TAG, "mannulfix：" + result);


            JSONObject obj = new JSONObject(result);
            Boolean isValid = obj.getBoolean("isValid");
            if (!isValid) {//异常数据，返回false
                return false;//测试时候改为true,快速上传test
            }
            int i = obj.getInt("received_signal");

            if (i == 0 || i == 1) {
                //成功修改为0或1
                return true;
            }
            return false;


        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }


    public void testAndroidHttpClient() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                //用HttpClient发送请求，分为五步
                //第一步：创建HttpClient对象
                HttpClient httpCient = new DefaultHttpClient();
                //第二步：创建代表请求的对象,参数是访问的服务器地址
                HttpGet httpGet = new HttpGet("http://www.baidu.com");

                try {
                    //第三步：执行请求，获取服务器发还的相应对象
                    HttpResponse httpResponse = httpCient.execute(httpGet);
                    //第四步：检查相应的状态是否正常：检查状态码的值是200表示正常
                    if (httpResponse.getStatusLine().getStatusCode() == 200) {
                        //第五步：从相应对象当中取出数据，放到entity当中
                        HttpEntity entity = httpResponse.getEntity();
                        String response = EntityUtils.toString(entity, "utf-8");//将entity当中的数据转换为字符串
                        Log.e("httpResponse", response);
                    }

                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
        }).start();//这个start()方法不要忘记了
    }


}
