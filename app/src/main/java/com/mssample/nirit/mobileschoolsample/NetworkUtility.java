package com.mssample.nirit.mobileschoolsample;

import com.squareup.okhttp.*;
import okio.BufferedSink;

import java.io.IOException;
import java.io.InputStream;

/*
    Wrapper class
 */
public class NetworkUtility {

    private static final OkHttpClient client = new OkHttpClient();

    public static void get(String url, HttpCallback cb) {
        call("GET", url, cb);
    }

    public static void post(String url, HttpCallback cb) {
        call("POST", url, cb);
    }

    private static void call(String method, String url, final HttpCallback cb) {
        Request request = new Request.Builder().url(url).method(method, method.equals("GET") ? null : new RequestBody() {
            @Override
            public MediaType contentType() {
                return null;
            }

            @Override
            public void writeTo(BufferedSink sink) throws IOException {

            }
        }).build();

        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Request request, IOException e) {
                cb.onFailure();

            }

            @Override
            public void onResponse(Response response) throws IOException {
                if (!response.isSuccessful()) {
                    cb.onFailure();
                    return;
                }
                try {
                    cb.onSuccess(response.body().byteStream());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    public interface HttpCallback  {

        void onFailure();
        void onSuccess(InputStream inputStream);
    }

}
