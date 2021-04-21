package com.maximeg.nfc4tuya.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.maximeg.nfc4tuya.R;
import com.maximeg.nfc4tuya.enums.RequestEnum;
import com.maximeg.nfc4tuya.interfaces.IRequestHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RequestUtils {

    private final IRequestHandler listener;

    private final String clientID;
    private final String secret;
    private final String uid;
    private final String deviceID;
    private final String homeID;

    private final String TAG = RequestUtils.class.getName();

    private final Context context;

    public RequestUtils(IRequestHandler listener, Context context){
        this.context = context;
        this.listener = listener;

        SharedPreferences sp = context.getSharedPreferences(context.getString(R.string.preferences), 0);

        clientID = sp.getString(context.getString(R.string.clientID), null);
        secret = sp.getString(context.getString(R.string.secret), null);
        deviceID = sp.getString(context.getString(R.string.deviceID), null);

        uid = sp.getString(context.getString(R.string.uid), null);
        homeID = sp.getString(context.getString(R.string.homeID), null);
    }

    public void createRequest(RequestEnum requestType, Object extra, Object extra2){
        if(!checkDefaultCredentialValues()){
            listener.onRequestError(context.getString(R.string.error_token), RequestEnum.GET_TOKEN);
            return;
        }

        Long timestamp = System.currentTimeMillis();

        String signValue = clientID + timestamp;

        String hash = HashUtils.generateSHA256Hash(signValue, secret).toUpperCase();

        OkHttpClient client = new OkHttpClient();

        Request tokenRequest = new Request.Builder()
                .addHeader("client_id", clientID)
                .addHeader("sign", hash)
                .addHeader("sign_method", "HMAC-SHA256")
                .addHeader("t", timestamp + "")
                .addHeader("grant_type", 1 + "")
                .url("https://openapi.tuyaeu.com/v1.0/token")
                .build();

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Response response = (client.newCall(tokenRequest)).execute();
                    JSONObject json = new JSONObject(response.body().string());

                    if(json.get("success").equals(true)) {
                        String token = json.getJSONObject("result").getString("access_token");

                        switch (requestType) {
                            case GET_DEVICES:
                                getDevices(token, timestamp);
                                break;
                            case GET_DEVICE:
                                getDevice(token, timestamp, (String) extra);
                                break;
                            case SEND_COMMANDS:
                                sendCommands(token, timestamp, (String) extra, (Map<String, Object>) extra2);
                                break;
                            case GET_UID:
                                getUID(token, timestamp);
                                break;
                            case GET_HOME_ID:
                                if (uid == null || uid.isEmpty()) {
                                    listener.onRequestError(context.getString(R.string.error_homeID), RequestEnum.GET_TOKEN);
                                    return;
                                }
                                getHomeID(token, timestamp);
                                break;
                            case GET_SCENES:
                                if(homeID == null || homeID.isEmpty()){
                                    listener.onRequestError(context.getString(R.string.error_scenes), RequestEnum.GET_TOKEN);
                                    return;
                                }
                                getScenes(token, timestamp);
                                break;
                            case TRIGGER_SCENE:
                                if(homeID == null || homeID.isEmpty()){
                                    listener.onRequestError(context.getString(R.string.error_scenes), RequestEnum.GET_TOKEN);
                                    return;
                                }
                                triggerScene(token, timestamp, (String) extra);
                                break;
                        }
                    }
                    else {
                        listener.onRequestError(context.getString(R.string.error_request), RequestEnum.GET_TOKEN);
                    }
                } catch (Exception e) {
                    listener.onRequestError(e.getMessage(), RequestEnum.GET_TOKEN);
                }
            }
        });
    }

    private boolean checkDefaultCredentialValues(){
        return this.clientID != null && !this.clientID.isEmpty() && this.secret != null && !this.secret.isEmpty() && this.deviceID != null && !this.deviceID.isEmpty();
    }

    private void getDevices(String token, long timestamp){
        String signValue = clientID + token + timestamp;

        String hash = HashUtils.generateSHA256Hash(signValue, secret).toUpperCase();

        OkHttpClient client = new OkHttpClient();

        Request deviceRequest = new Request.Builder()
                .addHeader("client_id", clientID)
                .addHeader("sign", hash)
                .addHeader("sign_method", "HMAC-SHA256")
                .addHeader("t", timestamp+"")
                .addHeader("access_token", token)
                .url("https://openapi.tuyaeu.com/v1.0/users/" +  uid  + "/devices")
                .build();

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Response response = (client.newCall(deviceRequest)).execute();
                    JSONObject json = new JSONObject(response.body().string());

                    if(json.get("success").equals(true)){
                        JSONArray array = json.getJSONArray("result");

                        List<JSONObject> objects = new ArrayList<>();

                        for(int i = 0; i < array.length(); i++){
                            objects.add((JSONObject) array.get(i));
                        }

                        listener.onRequestCompleted(objects, RequestEnum.GET_DEVICES);
                    }
                    else{
                        listener.onRequestError(context.getString(R.string.error_request_device), RequestEnum.GET_DEVICES);
                    }
                } catch (Exception e) {
                    listener.onRequestError(e.getMessage(), RequestEnum.GET_DEVICES);
                }
            }
        });
    }

    private void getUID(String token, long timestamp){
        String signValue = clientID + token + timestamp;

        String hash = HashUtils.generateSHA256Hash(signValue, secret).toUpperCase();

        OkHttpClient client = new OkHttpClient();

        Request deviceRequest = new Request.Builder()
                .addHeader("client_id", clientID)
                .addHeader("sign", hash)
                .addHeader("sign_method", "HMAC-SHA256")
                .addHeader("t", timestamp+"")
                .addHeader("access_token", token)
                .url("https://openapi.tuyaeu.com/v1.0/devices/" + deviceID)
                .build();

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Response response = (client.newCall(deviceRequest)).execute();

                    JSONObject json = new JSONObject(response.body().string());

                    if(json.get("success").equals(true)){
                        String uuid = json.getJSONObject("result").get("uid").toString();
                        listener.onRequestCompleted(uuid, RequestEnum.GET_UID);
                    }
                    else{
                        listener.onRequestError(context.getString(R.string.error_request_device), RequestEnum.GET_UID);
                    }
                } catch (Exception e) {
                    listener.onRequestError(e.getMessage(), RequestEnum.GET_UID);
                }
            }
        });
    }

    private void getHomeID(String token, long timestamp){
        String signValue = clientID + token + timestamp;

        String hash = HashUtils.generateSHA256Hash(signValue, secret).toUpperCase();

        OkHttpClient client = new OkHttpClient();

        Request deviceRequest = new Request.Builder()
                .addHeader("client_id", clientID)
                .addHeader("sign", hash)
                .addHeader("sign_method", "HMAC-SHA256")
                .addHeader("t", timestamp+"")
                .addHeader("access_token", token)
                .url("https://openapi.tuyaeu.com/v1.0/users/" + uid + "/homes")
                .build();

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Response response = (client.newCall(deviceRequest)).execute();

                    JSONObject json = new JSONObject(response.body().string());

                    if(json.get("success").equals(true)){
                        String uuid = json.getJSONArray("result").getJSONObject(0).get("home_id").toString();
                        listener.onRequestCompleted(uuid, RequestEnum.GET_HOME_ID);
                    }
                    else{
                        listener.onRequestError(context.getString(R.string.error_request_uid), RequestEnum.GET_HOME_ID);
                    }
                } catch (Exception e) {
                    listener.onRequestError(e.getMessage(), RequestEnum.GET_HOME_ID);
                }
            }
        });
    }

    private void getDevice(String token, Long timestamp, String device){
        String signValue = clientID + token + timestamp;

        String hash = HashUtils.generateSHA256Hash(signValue, secret).toUpperCase();

        OkHttpClient client = new OkHttpClient();

        Request deviceRequest = new Request.Builder()
                .addHeader("client_id", clientID)
                .addHeader("sign", hash)
                .addHeader("sign_method", "HMAC-SHA256")
                .addHeader("t", timestamp+"")
                .addHeader("access_token", token)
                .url("https://openapi.tuyaeu.com/v1.0/devices/" + device)
                .build();

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Response response = (client.newCall(deviceRequest)).execute();

                    JSONObject json = new JSONObject(response.body().string());

                    if(json.get("success").equals(true)){
                        listener.onRequestCompleted(json.getJSONObject("result"), RequestEnum.GET_DEVICE);
                    }
                    else{
                        listener.onRequestError(context.getString(R.string.error_request), RequestEnum.GET_DEVICE);
                    }
                } catch (Exception e) {
                    listener.onRequestError(e.getMessage(), RequestEnum.GET_DEVICE);
                }
            }
        });
    }

    private void getScenes(String token, Long timestamp){
        String signValue = clientID + token + timestamp;

        String hash = HashUtils.generateSHA256Hash(signValue, secret).toUpperCase();

        OkHttpClient client = new OkHttpClient();

        Request deviceRequest = new Request.Builder()
                .addHeader("client_id", clientID)
                .addHeader("sign", hash)
                .addHeader("sign_method", "HMAC-SHA256")
                .addHeader("t", timestamp+"")
                .addHeader("access_token", token)
                .url("https://openapi.tuyaeu.com/v1.0/homes/" + homeID + "/scenes")
                .build();

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Response response = (client.newCall(deviceRequest)).execute();
                    JSONObject json = new JSONObject(response.body().string());

                    if(json.get("success").equals(true)){
                        JSONArray array = json.getJSONArray("result");

                        List<JSONObject> objects = new ArrayList<>();

                        for(int i = 0; i < array.length(); i++){
                            objects.add((JSONObject) array.get(i));
                        }

                        Collections.reverse(objects);

                        listener.onRequestCompleted(objects, RequestEnum.GET_SCENES);
                    }
                    else{
                        listener.onRequestError(context.getString(R.string.error_request_homeID), RequestEnum.GET_SCENES);
                    }
                } catch (Exception e) {
                    listener.onRequestError(e.getMessage(), RequestEnum.GET_SCENES);
                }
            }
        });
    }

    private void triggerScene(String token, Long timestamp, String nfcValue){
        String signValue = clientID + token + timestamp;

        String sceneID = nfcValue.substring( 0, nfcValue.indexOf("|"));
        String sceneName = nfcValue.substring(nfcValue.indexOf("|") + 1);

        String hash = HashUtils.generateSHA256Hash(signValue, secret).toUpperCase();

        OkHttpClient client = new OkHttpClient();

        RequestBody body = RequestBody.create(new byte[]{});

        Request deviceRequest = new Request.Builder()
                .addHeader("client_id", clientID)
                .addHeader("sign", hash)
                .addHeader("sign_method", "HMAC-SHA256")
                .addHeader("t", timestamp+"")
                .addHeader("access_token", token)
                .post(body)
                .url("https://openapi.tuyaeu.com/v1.0/homes/" + homeID + "/scenes/" + sceneID + "/trigger")
                .build();

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Response response = (client.newCall(deviceRequest)).execute();
                    JSONObject json = new JSONObject(response.body().string());

                    if(json.get("success").equals(true)){
                        listener.onRequestCompleted(sceneName, RequestEnum.TRIGGER_SCENE);
                    }
                    else{
                        listener.onRequestError(context.getString(R.string.error_request_homeID), RequestEnum.TRIGGER_SCENE);
                    }
                } catch (Exception e) {
                    listener.onRequestError(e.getMessage(), RequestEnum.TRIGGER_SCENE);
                }
            }
        });
    }

    private void sendCommands(String token, Long timestamp, String device, Map<String, Object> values){
        String signValue = clientID + token + timestamp;

        String hash = HashUtils.generateSHA256Hash(signValue, secret).toUpperCase();

        OkHttpClient client = new OkHttpClient();

        JSONObject globalJson = new JSONObject();

        JSONArray jsonArray = new JSONArray();

        try {
            for(Map.Entry<String, Object> map : values.entrySet()){
                JSONObject jsonCommand = new JSONObject();

                jsonCommand.put("code", map.getKey());
                jsonCommand.put("value", map.getValue());

                jsonArray.put(jsonCommand);
            }

            globalJson.put("commands", jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, globalJson.toString());

        Request deviceRequest = new Request.Builder()
                .addHeader("client_id", clientID)
                .addHeader("sign", hash)
                .addHeader("sign_method", "HMAC-SHA256")
                .addHeader("t", timestamp+"")
                .addHeader("access_token", token)
                .post(body)
                .url("https://openapi.tuyaeu.com/v1.0/devices/" + device + "/commands")
                .build();

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Response response = (client.newCall(deviceRequest)).execute();
                    JSONObject json = new JSONObject(response.body().string());

                    if(json.get("success").equals(true)){
                        listener.onRequestCompleted(json.getJSONObject("result").toString(), RequestEnum.SEND_COMMANDS);
                    }
                    else{
                        listener.onRequestError(context.getString(R.string.error_request), RequestEnum.SEND_COMMANDS);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
