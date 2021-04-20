package com.maximeg.nfc4tuya.activities;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.maximeg.nfc4tuya.interfaces.IRequestHandler;
import com.maximeg.nfc4tuya.R;
import com.maximeg.nfc4tuya.enums.RequestEnum;

public class DeviceActivity extends AppCompatActivity implements IRequestHandler {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scene);

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        setTitle(R.string.devices);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        else if(item.getItemId() == R.id.refresh_button){

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestCompleted(Object result, RequestEnum request) {

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
