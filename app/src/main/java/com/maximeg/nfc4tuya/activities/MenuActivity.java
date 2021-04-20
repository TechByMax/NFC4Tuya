package com.maximeg.nfc4tuya.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.maximeg.nfc4tuya.interfaces.INFCListener;
import com.maximeg.nfc4tuya.interfaces.IRequestHandler;
import com.maximeg.nfc4tuya.R;
import com.maximeg.nfc4tuya.enums.RequestEnum;
import com.maximeg.nfc4tuya.utils.RequestUtils;

public class MenuActivity extends AppCompatActivity implements IRequestHandler, INFCListener {

    private final String TAG = MenuActivity.class.getName();
    private NfcAdapter nfcAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        if(getSupportActionBar() != null){
            getSupportActionBar().show();
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.darkItem)));
        }

        Button scenesButton = findViewById(R.id.scenes_button);
        Button credentialsButton = findViewById(R.id.credentials_button);

        scenesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MenuActivity.this, SceneActivity.class));
            }
        });

        credentialsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MenuActivity.this, CredentialsActivity.class));
            }
        });

        initNfcAdapter();

        if(getIntent() != null){
            onNewIntent(getIntent());
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction()) ) {
            Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            if (rawMsgs != null) {
                runSceneFromData(rawMsgs);
            }
        }
    }

    private void runSceneFromData(Parcelable[] rawMsgs) {
        for (int i = 0; i < rawMsgs.length; i++) {
            NdefMessage ndefMessage = (NdefMessage) rawMsgs[i];
            NdefRecord[] records = ndefMessage.getRecords();
            new RequestUtils(MenuActivity.this, getApplicationContext()).createRequest(RequestEnum.TRIGGER_SCENE, new String(records[0].getPayload()), null);
        }
    }

    private IntentFilter[] getIntentFilters() {
        IntentFilter ndefFilter = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);

        try {
            ndefFilter.addDataType(getString(R.string.application) + getPackageName());
        } catch (Exception e) {
            Log.e(TAG, "Problem in parsing mime type for nfc reading", e);
        }

        return new IntentFilter[]{ndefFilter};
    }

    @Override
    protected void onResume() {
        super.onResume();

        enableNfcForegroundDispatch();
    }

    @Override
    protected void onPause() {
        disableNfcForegroundDispatch();

        super.onPause();
    }

    private void initNfcAdapter() {
        NfcManager nfcManager = (NfcManager) getSystemService(Context.NFC_SERVICE);
        nfcAdapter = nfcManager.getDefaultAdapter();
    }

    private void enableNfcForegroundDispatch() {
        try {
            Intent intent = new Intent(MenuActivity.this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
            nfcAdapter.enableForegroundDispatch(this, pendingIntent, getIntentFilters(), null);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), getString(R.string.error) + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void disableNfcForegroundDispatch() {
        try {
            nfcAdapter.disableForegroundDispatch(MenuActivity.this);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), getString(R.string.error) + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestCompleted(Object result, RequestEnum request) {
        if(request == RequestEnum.TRIGGER_SCENE){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), (String) result, Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    @Override
    public void onRequestError(String message, RequestEnum request) {
        if(request == RequestEnum.TRIGGER_SCENE){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), RequestEnum.TRIGGER_SCENE.name() + " : " + message, Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    @Override
    public void onWritingDone(boolean done) {

    }
}