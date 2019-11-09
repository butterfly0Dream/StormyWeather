package com.fallgod.weather.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by JackPan on 2019/10/30
 * Describe:
 */
public class HttpUtil {

    /**
     * http请求
     * @param address 请求的地址
     * @param callback 服务器返回的数据回调
     */
    public static void sendOkHttpResquest(String address,okhttp3.Callback callback){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(address).build();
        client.newCall(request).enqueue(callback);
    }

    /**
     *获得HttpURLConnection对象
     * @return HttpURLConnection
     */
    private static HttpURLConnection getConnnect(String url, String method){
        try {
            URL mUrl = new URL(url);
            try {
                HttpURLConnection connection = (HttpURLConnection) mUrl.openConnection();
                //设置请求类型
                connection.setRequestMethod(method);
                //设置连接超时时间
                connection.setConnectTimeout(3000);
                //设置读取超时时间
                connection.setReadTimeout(3000);
                // 设置是否向 httpUrlConnection 输出，
                // 对于post请求，参数要放在 http 正文内，因此需要设为true。
                // 默认情况下是false;
                connection.setDoOutput(true);
                // 设置是否从 httpUrlConnection 读入，默认情况下是true;
                connection.setDoInput(true);

                return connection;
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    //网络请求的入口
    public static void sendRequset(String url,final HttpCallbackListener listener){
        new Thread(new Runnable() {
            @Override
            public void run() {
//                HttpURLConnection connection = getConnnect(url,"GET");
                try {
                    URL url1 = new URL(url);
                    HttpURLConnection connection = (HttpURLConnection) url1.openConnection();
                    BufferedWriter writer = null;
                    BufferedReader reader = null;
                    connection.connect();
//                    //利用 getOutputStream() 传输 POST 消息
//                    writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
//                    writer.write("");
//                    writer.flush();
//                    writer.close();

                    //利用 getInputStream() 获得返回的数据
                    reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null){
                        response.append(line);
                    }
                    if (listener != null){
                        //回调onFinish方法
                        listener.onFinish(response.toString());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    if (listener != null){
                        //回调onError方法
                        listener.onError(e);
                    }
                } finally {
                    //断开连接
//                    if (connection != null){
//                        connection.disconnect();
//                    }
                }
            }
        }).start();
    }

    //http请求回调
    public interface HttpCallbackListener{
        void onFinish(String text);
        void onError(Exception e);
    }

}
