package com.maximeg.nfc4tuya.activities;

import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.maximeg.nfc4tuya.R;
import com.maximeg.nfc4tuya.interfaces.INFCListener;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static android.nfc.NdefRecord.TNF_MIME_MEDIA;

public class NFCLoaderActivity extends AppCompatActivity implements INFCListener {

    private NfcAdapter nfcAdapter;
    private final String TAG = NFCLoaderActivity.class.getName();

    private String sceneID;
    private String sceneName;

    private boolean hasWritten = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfc_loader);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.darkItem)));
        }

        sceneID = getIntent().getStringExtra(getString(R.string.sceneID));
        sceneName = getIntent().getStringExtra(getString(R.string.name));

        setTitle(getIntent().getStringExtra(getString(R.string.name)));

        initNfcAdapter();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if(!hasWritten){
            hasWritten = true;

            byte[] typeBytes = (getString(R.string.application)+ getPackageName()).getBytes();
            byte[] payload = (sceneID + "|" + sceneName).getBytes();
            NdefRecord r1 = new NdefRecord(TNF_MIME_MEDIA, typeBytes, null, payload);

            NdefRecord r2 = NdefRecord.createApplicationRecord(getApplicationContext().getPackageName());

            NdefMessage message = new NdefMessage(r1, r2);

            Tag tagIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

            try {
                WritableTag writableTag = new WritableTag(tagIntent, NFCLoaderActivity.this);
                writableTag.writeData(message);
                writableTag.close();
            } catch (Exception e) {
                Log.e(TAG, "Unsupported tag tapped");
            }
        }
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
            Intent intent = new Intent(NFCLoaderActivity.this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
            nfcAdapter.enableForegroundDispatch(this, pendingIntent, null, null);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), getString(R.string.error) + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void disableNfcForegroundDispatch() {
        try {
            nfcAdapter.disableForegroundDispatch(NFCLoaderActivity.this);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), getString(R.string.error) + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
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
    public void onWritingDone(boolean done) {
        if(!done){
            hasWritten = false;
        }
        else{
            AlertDialog alertDialog = new AlertDialog.Builder(NFCLoaderActivity.this).create();
            alertDialog.setCancelable(false);
            alertDialog.setTitle(R.string.info);
            alertDialog.setMessage(getString(R.string.message));
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(android.R.string.ok),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            finish();
                        }
                    });
            alertDialog.show();
        }
    }

    public class WritableTag  {
        private final String NDEF = Ndef.class.getCanonicalName();
        private final String NDEF_FORMATABLE = NdefFormatable.class.getCanonicalName();

        private INFCListener listener;

        private Ndef ndef;
        private NdefFormatable ndefFormatable;

        public WritableTag(Tag tag, INFCListener listener){
            this.listener = listener;

            String[] technologies = tag.getTechList();
            List<String> tagTechs = Arrays.asList(technologies);
            if (tagTechs.contains(NDEF)) {
                ndef = Ndef.get(tag);
                ndefFormatable = null;
            } else if (tagTechs.contains(NDEF_FORMATABLE)) {
                ndefFormatable = NdefFormatable.get(tag);
                ndef = null;
            }
        }

        public void writeData(NdefMessage message){
            try {
                if (ndef != null) {
                    ndef.connect();
                    if (ndef.isConnected()) {
                        ndef.writeNdefMessage(message);
                        listener.onWritingDone(true);
                        return;
                    }
                } else if (ndefFormatable != null) {
                    ndefFormatable.connect();
                    if (ndefFormatable.isConnected()) {
                        ndefFormatable.format(message);
                        listener.onWritingDone(true);
                        return;
                    }
                }
            } catch (Exception e){
                listener.onWritingDone(false);
                return;
            }

            listener.onWritingDone(false);
        }

        public void close() {
            try {
                if(ndef != null){
                    ndef.close();
                }
                else if(ndefFormatable != null){
                    ndefFormatable.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}