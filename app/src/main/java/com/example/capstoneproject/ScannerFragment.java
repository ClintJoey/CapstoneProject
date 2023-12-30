package com.example.capstoneproject;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.VideoView;

public class ScannerFragment extends Fragment {
    Button redirectToMainActivityBtn;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_scanner, container, false);
        redirectToMainActivityBtn = rootView.findViewById(R.id.redirectToMainActivityBtn);
        LinearLayout pasturePalInfo = rootView.findViewById(R.id.pasturePalInfo);

        redirectToMainActivityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
            }
        });

        pasturePalInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPasturePalInfoDialog(rootView);
            }
        });

        return rootView;
    }
    private void openPasturePalInfoDialog(View rootview) {
        LinearLayout layout = rootview.findViewById(R.id.pasturePalInfoLayout);
        View view = LayoutInflater.from(rootview.getContext()).inflate(R.layout.pasture_pal_info_layout, layout);
        VideoView pasturePalDemoVideo = view.findViewById(R.id.pasturePalDemoVideo);

        String videoPath = "android.resource://" + requireActivity().getPackageName() + "/" + R.raw.pasture_pal_demo;
        pasturePalDemoVideo.setVideoPath(videoPath);
        pasturePalDemoVideo.start();

        pasturePalDemoVideo.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                pasturePalDemoVideo.start();
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(rootview.getContext());
        builder.setView(view);
        AlertDialog dialog = builder.create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        dialog.show();
    }
}