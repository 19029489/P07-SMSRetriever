package com.myapplicationdev.android.p07_smsretriever;

import android.Manifest;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.PermissionChecker;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class SecondFragment extends Fragment {

    Button btnRetrieveSMS;
    TextView tvSMS;
    EditText etSMS;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_second, container, false);

        tvSMS = view.findViewById(R.id.tvSMS);
        btnRetrieveSMS = view.findViewById(R.id.btnRetSMS);
        etSMS = view.findViewById(R.id.etSMS);

        btnRetrieveSMS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String data = etSMS.getText().toString();
                tvSMS.setText(data);

            }
        });
        return view;


    }
}