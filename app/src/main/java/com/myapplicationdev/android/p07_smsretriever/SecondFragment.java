package com.myapplicationdev.android.p07_smsretriever;

import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.PermissionChecker;
import androidx.fragment.app.Fragment;

import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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

                int permissionCheck = PermissionChecker.checkSelfPermission(getContext(), Manifest.permission.READ_SMS);
                if (permissionCheck != PermissionChecker.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.READ_SMS}, 0);
                    //stops the action from proceeding further as permission not granted yet
                    return;
                }

                Uri uri = Uri.parse("content://sms");
                String[] reqCols = new String[]{"date", "address", "body", "type"};
                ContentResolver cr = getContext().getContentResolver();

                String filter = "body LIKE ?";
                String data = etSMS.getText().toString();
                String[] filterArgs = {"%" + data + "%"};

                Cursor cursor = cr.query(uri, reqCols, filter, filterArgs, null);
                String smsBody = "";
                if (cursor.moveToFirst()) {
                    do {
                        long dateInMillis = cursor.getLong(0);
                        String date = (String) DateFormat
                                .format("dd MMM yyyy h:mm:ss aa", dateInMillis);
                        String address = cursor.getString(1);
                        String body = cursor.getString(2);
                        String type = cursor.getString(3);
                        if (type.equalsIgnoreCase("1")) {
                            type = "Inbox:";
                        }else {
                            type = "Sent:";
                        }
                        smsBody += type + " " + address + "\n at " + date
                                + "\n\"" + body + "\"\n\n";
                    } while (cursor.moveToNext());
                }tvSMS.setText(smsBody);
            }
        });
        return view;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 0: {
                //If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    //permission was granted, yay! Do the read SMS
                    //as if the btnRetrieve is clicked
                    btnRetrieveSMS.performClick();
                } else {
                    //permission denied... notify user
                    Toast.makeText(getContext(), "Permission not granted", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}