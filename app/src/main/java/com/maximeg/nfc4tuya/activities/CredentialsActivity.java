package com.maximeg.nfc4tuya.activities;

import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.maximeg.nfc4tuya.views.BetterEditText;
import com.maximeg.nfc4tuya.interfaces.BetterEditTextListener;
import com.maximeg.nfc4tuya.interfaces.IRequestHandler;
import com.maximeg.nfc4tuya.R;
import com.maximeg.nfc4tuya.enums.RequestEnum;
import com.maximeg.nfc4tuya.utils.RequestUtils;

public class CredentialsActivity extends AppCompatActivity implements IRequestHandler {

    private SharedPreferences sp;

    private TextView uuidTextView;
    private TextView homeIDTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credentials);

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.darkItem)));
        }

        setTitle(R.string.credentials);

        sp = getSharedPreferences(getString(R.string.preferences), 0);

        BetterEditText clientEditText = findViewById(R.id.client_edit_text);
        BetterEditText secretEditText = findViewById(R.id.secret_edit_text);
        BetterEditText deviceEditText = findViewById(R.id.device_edit_text);
        uuidTextView = findViewById(R.id.uuid_text_view);
        homeIDTextView = findViewById(R.id.homeid_text_view);
        Button idsButton = findViewById(R.id.ids_button);

        String clientValue = sp.getString(getString(R.string.clientID), null);
        if(clientValue != null){
            clientEditText.setText(clientValue);
        }

        String secretValue = sp.getString(getString(R.string.secret), null);
        if(secretValue != null){
            secretEditText.setText(secretValue);
        }

        String deviceValue = sp.getString(getString(R.string.deviceID), null);
        if(deviceValue != null){
            deviceEditText.setText(deviceValue);
        }

        String uid = sp.getString(getString(R.string.uid), null);
        if(uid != null){
            uuidTextView.setText(uid);
        }

        String homeID = sp.getString(getString(R.string.homeID), null);
        if(homeID != null){
            homeIDTextView.setText(homeID);
        }

        clientEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                SharedPreferences.Editor editor = sp.edit();
                editor.putString(getString(R.string.clientID), s.toString().trim());
                editor.apply();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        clientEditText.addActionListener(new BetterEditTextListener() {
            @Override
            public void onTextPaste(String currentText) {
                SharedPreferences.Editor editor = sp.edit();
                editor.putString(getString(R.string.clientID), currentText.trim());
                editor.apply();
            }

            @Override
            public void onTextCopy(String currentText) {

            }

            @Override
            public void onTextCut(String currentText) {

            }
        });

        secretEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                SharedPreferences.Editor editor = sp.edit();
                editor.putString(getString(R.string.secret), s.toString().trim());
                editor.apply();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        secretEditText.addActionListener(new BetterEditTextListener() {
            @Override
            public void onTextPaste(String currentText) {
                SharedPreferences.Editor editor = sp.edit();
                editor.putString(getString(R.string.secret), currentText.trim());
                editor.apply();
            }

            @Override
            public void onTextCopy(String currentText) {

            }

            @Override
            public void onTextCut(String currentText) {

            }
        });

        deviceEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                SharedPreferences.Editor editor = sp.edit();
                editor.putString(getString(R.string.deviceID), s.toString().trim());
                editor.apply();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        deviceEditText.addActionListener(new BetterEditTextListener() {
            @Override
            public void onTextPaste(String currentText) {
                SharedPreferences.Editor editor = sp.edit();
                editor.putString(getString(R.string.deviceID), currentText.trim());
                editor.apply();
            }

            @Override
            public void onTextCopy(String currentText) {

            }

            @Override
            public void onTextCut(String currentText) {

            }
        });

        idsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new RequestUtils(CredentialsActivity.this, getApplicationContext()).createRequest(RequestEnum.GET_UID, null, null);
            }
        });
    }

    private void setUID(String text){
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(getString(R.string.uid), text);
        editor.apply();
        uuidTextView.setText(text);
    }

    private void setHomeID(String text){
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(getString(R.string.homeID), text);
        editor.apply();
        homeIDTextView.setText(text);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestCompleted(Object result, RequestEnum request) {
        if(request == RequestEnum.GET_UID){
            setUID(result.toString());
            new RequestUtils(CredentialsActivity.this, getApplicationContext()).createRequest(RequestEnum.GET_HOME_ID, null, null);
        }
        else if(request == RequestEnum.GET_HOME_ID){
            setHomeID(result.toString());
        }
    }

    @Override
    public void onRequestError(String message, RequestEnum request) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), request.name() + " : " + message, Toast.LENGTH_LONG).show();
            }
        });
    }


}