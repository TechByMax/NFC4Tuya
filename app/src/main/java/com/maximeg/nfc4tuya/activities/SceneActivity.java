package com.maximeg.nfc4tuya.activities;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.maximeg.nfc4tuya.adapters.SceneAdapter;
import com.maximeg.nfc4tuya.db.SceneDBHelper;
import com.maximeg.nfc4tuya.interfaces.IRequestHandler;
import com.maximeg.nfc4tuya.R;
import com.maximeg.nfc4tuya.enums.RequestEnum;
import com.maximeg.nfc4tuya.models.Scene;
import com.maximeg.nfc4tuya.utils.RequestUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SceneActivity extends AppCompatActivity implements IRequestHandler {

    private final String TAG = SceneActivity.class.getName();

    private SceneDBHelper sceneDB;
    private SceneAdapter sceneAdapter;

    private final List<Scene> scenesList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scene);

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.darkItem)));
        }

        setTitle(R.string.tuya_scenes);

        sceneDB = new SceneDBHelper(getApplicationContext());

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        sceneAdapter = new SceneAdapter(scenesList);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(sceneAdapter);

        prepareSceneData();
        sceneAdapter.notifyDataSetChanged();
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
            new RequestUtils(SceneActivity.this, getApplicationContext()).createRequest(RequestEnum.GET_SCENES, null, null);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void prepareSceneData() { //Retrieve all scenes
        SQLiteDatabase db = sceneDB.getReadableDatabase();

        Cursor cursor = db.rawQuery("select * from " + SceneDBHelper.SceneEntry.TABLE_NAME, null);

        while(cursor.moveToNext()) {
            Scene scene = new Scene();

            scene.setId(cursor.getString(cursor.getColumnIndexOrThrow(SceneDBHelper.SceneEntry.COLUMN_ID)));
            scene.setName(cursor.getString(cursor.getColumnIndexOrThrow(SceneDBHelper.SceneEntry.COLUMN_NAME)));

            scenesList.add(scene);
        }

        cursor.close();
    }

    @Override
    protected void onDestroy() {
        sceneDB.close();
        super.onDestroy();
    }

    private void insertScenesInDB(List<JSONObject> scenes){
        SQLiteDatabase db = sceneDB.getWritableDatabase();
        sceneDB.deleteEntries(db);
        scenesList.clear();

        for(JSONObject scene : scenes){
            try {
                Scene s = new Scene(scene.getString("scene_id"), scene.getString("name"));

                ContentValues values = new ContentValues();

                values.put(SceneDBHelper.SceneEntry.COLUMN_NAME, s.getName());
                values.put(SceneDBHelper.SceneEntry.COLUMN_ID, s.getId());

                db.insert(SceneDBHelper.SceneEntry.TABLE_NAME, null, values);

                scenesList.add(s);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        sceneAdapter.notifyDataSetChanged();
    }

    @Override
    @SuppressWarnings({"unchecked"})
    public void onRequestCompleted(Object result, RequestEnum request) {
        if(request == RequestEnum.GET_SCENES){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    insertScenesInDB((List<JSONObject>) result);
                }
            });
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
