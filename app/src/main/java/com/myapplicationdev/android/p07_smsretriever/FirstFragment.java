package com.myapplicationdev.android.p07_smsretriever;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
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


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FirstFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FirstFragment extends Fragment {

    Button btnRetrieveNum, btnSendEmail;
    TextView tvSMSNum;
    EditText etNum;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FirstFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FirstFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FirstFragment newInstance(String param1, String param2) {
        FirstFragment fragment = new FirstFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_first, container, false);

        tvSMSNum = view.findViewById(R.id.tvSMSNum);
        btnRetrieveNum = view.findViewById(R.id.btnRetSMSNum);
        etNum = view.findViewById(R.id.etNum);
        btnSendEmail = view.findViewById(R.id.btnSendEmail);

        btnRetrieveNum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int permissionCheck = PermissionChecker.checkSelfPermission(getContext(), Manifest.permission.READ_SMS);

                if (permissionCheck != PermissionChecker.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_SMS},0);
                    //stops the action from proceeding further as permission not
                    //granted yet
                    return;
                }

                //Create all messages URI
                Uri uri = Uri.parse("content://sms");

                //The columns we want
                //date is when the message took place
                //address is the number of the other party
                //body is the message content
                //type 1 is received, type 2 is sent
                String[] reqCols = new String[]{"date", "address", "body", "type"};

                //Get Content Resolver object from which to
                //query the content provider
                ContentResolver cr = getActivity().getContentResolver();

                //The filter String
                String filter = "address LIKE ? ";
                //The matches for the ?
                String num = etNum.getText().toString();
                String[] filterArgs = {"%" + num + "%"};

                //Fetch SMS Message from Built-in Content Provider
                Cursor cursor = cr.query(uri, reqCols, filter, filterArgs,null);
                String smsBody = "";
                if (cursor.moveToFirst()) {
                    do {
                        long dateInMillis = cursor.getLong(0);
                        String date = (String) DateFormat.format("dd MMM yyyy h:mm:ss aa", dateInMillis);
                        String address = cursor.getString(1);
                        String body = cursor.getString(2);
                        String type = cursor.getString(3);

                        if (type.equalsIgnoreCase("1")) {
                            type = "Inbox: ";
                        } else {
                            type = "Sent: ";
                        }
                        smsBody += type + " " + address + "\n at " + date + "\n\"" + body + "\"\n\n";
                    } while (cursor.moveToNext());
                }
                tvSMSNum.setText(smsBody);
            }
        });

        btnSendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Uri uri = Uri.parse("content://sms");

                String[] reqCols = new String[]{"body"};

                ContentResolver cr = getActivity().getContentResolver();

                //The filter String
                String filter = "address LIKE ? ";
                //The matches for the ?
                String num = etNum.getText().toString();
                String[] filterArgs = {"%" + num + "%"};

                //Fetch SMS Message from Built-in Content Provider
                Cursor cursor = cr.query(uri, reqCols, filter, filterArgs,null);
                String smsBody = "";
                if (cursor.moveToFirst()) {
                    do {
                        String body = cursor.getString(0);

                        smsBody += "" + body + "\n\n";
                    } while (cursor.moveToNext());
                }
                // The action you want this intent to do;
                // ACTION_SEND is used to indicate sending text
                Intent email = new Intent(Intent.ACTION_SEND);
                // Put essentials like email address, subject & body text
                email.putExtra(Intent.EXTRA_EMAIL,
                        new String[]{"19029489@myrp.edu.sg"});
                email.putExtra(Intent.EXTRA_SUBJECT,
                        "My SMS Content");
                email.putExtra(Intent.EXTRA_TEXT,
                        smsBody);
                // This MIME type indicates email
                email.setType("message/rfc822");
                // createChooser shows user a list of app that can handle
                // this MIME type, which is, email
                startActivity(Intent.createChooser(email,
                        "Choose an Email client :"));
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
                    btnRetrieveNum.performClick();
                } else {
                    //permission denied... notify user
                    Toast.makeText(getContext(), "Permission not granted", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}