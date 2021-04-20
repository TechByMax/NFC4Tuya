package com.maximeg.nfc4tuya.adapters;

import android.app.Activity;
import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.maximeg.nfc4tuya.R;
import com.maximeg.nfc4tuya.activities.NFCLoaderActivity;
import com.maximeg.nfc4tuya.models.Scene;

import java.util.List;

public class SceneAdapter extends RecyclerView.Adapter<SceneAdapter.ViewHolder> {
    private final List<Scene> scenesList;
    private Context context;

    public class ViewHolder extends RecyclerView.ViewHolder {
        public RelativeLayout cellLayout;
        public TextView cellTitle;

        public ViewHolder(View view) {
            super(view);

            cellLayout = view.findViewById(R.id.cell_layout);
            cellTitle = view.findViewById(R.id.cell_title);
        }
    }

    public SceneAdapter(List<Scene> scenesList) {
        this.scenesList = scenesList;
    }

    @Override
    public SceneAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_scene, parent, false);

        context = parent.getContext();

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Scene scene = scenesList.get(position);

        holder.cellTitle.setText(scene.getName());

        holder.cellLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent((Activity)context, NFCLoaderActivity.class);
                intent.putExtra(context.getString(R.string.name), scene.getName());
                intent.putExtra(context.getString(R.string.sceneID), scene.getId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return scenesList.size();
    }
}


