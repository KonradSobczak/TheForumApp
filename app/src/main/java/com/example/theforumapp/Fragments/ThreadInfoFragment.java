package com.example.theforumapp.Fragments;


import android.Manifest;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.webkit.URLUtil;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.theforumapp.CreateThreadActivity;
import com.example.theforumapp.MainActivity;
import com.example.theforumapp.Model.Thread;
import com.example.theforumapp.Model.User;
import com.example.theforumapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.io.File;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 */
public class ThreadInfoFragment extends Fragment {

    private Button resolveThreadBtn;
    private String id;
    private String issueTitle;
    private String deviceModel;
    private String binary;
    private String issueType;
    private String occurence;
    private String description;
    private String customerId;
    private String engineerId;
    private String osVersion;
    private String downloadUri1;
    private String downloadUri2;
    private String downloadUri3;
    private String engineerName;
    private String customerName;
    private String resolution;
    private String salesCode;
    private String serialNumber;
    private TextView titleText;
    private TextView deviceModelText;
    private TextView binaryText;
    private TextView issueTypeText;
    private TextView occurenceText;
    private TextView descriptionText;
    private TextView customerText;
    private TextView engineerText;
    private TextView osVersionText;
    private TextView downloadUri1Text;
    private TextView downloadUri2Text;
    private TextView downloadUri3Text;
    private TextView serialNumberText;
    private TextView salesCodeText;


    public ThreadInfoFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_thread_info, container, false);
        id = getArguments().getString("thread");
        bindViews(view);
        getTicketInformation();


        resolveThreadBtn = view.findViewById(R.id.resolveThread);
        resolveThreadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showResolutionDialog();
            }
        });

        downloadUri1Text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    downloadFile(downloadUri1Text.getText().toString());
                } else {
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
                }
            }
        });

        downloadUri2Text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    downloadFile(downloadUri2Text.getText().toString());
                } else {requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
                }
            }
        });

        downloadUri3Text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    downloadFile(downloadUri3Text.getText().toString());
                } else {
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
                }
            }
        });



        return view;
    }


    private void showResolutionDialog() {

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_spinner, null);
        mBuilder.setTitle("Resolution");
        final Spinner spinner = dialogView.findViewById(R.id.resolutionSpinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, getResources().getStringArray( R.array.resolution));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter( adapter);
        mBuilder.setPositiveButton("Resolve", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                resolution = spinner.getSelectedItem().toString();
                archieveThread(id,issueTitle,deviceModel,binary,issueType,occurence,description,engineerId,customerId, osVersion, salesCode, serialNumber, downloadUri1,downloadUri2,downloadUri3, resolution, customerName, engineerName);


            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        mBuilder.setView(dialogView);
        AlertDialog dialog = mBuilder.create();
        dialog.show();
    }

    private void getTicketInformation() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Threads");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Thread thread = snapshot.getValue(Thread.class);
                    if (thread.getId().equals(id)) {
                        Log.d("CHECKING ID", id);
                        issueTitle = thread.getIssueTitle();
                        binary = thread.getBinary();
                        issueType = thread.getIssueType();
                        occurence = thread.getOccurrence();
                        description = thread.getDescription();
                        customerId = thread.getCustomerId();
                        deviceModel = thread.getDeviceModel();
                        engineerId = thread.getEngineerId();
                        osVersion = thread.getOsVersion();
                        downloadUri1 = thread.getDownloadUri1();
                        downloadUri2 = thread.getDownloadUri2();
                        downloadUri3 = thread.getDownloadUri3();
                        salesCode = thread.getSalesCode();
                        serialNumber = thread.getSerialNumber();
                        getCustomerandUser();

                        binaryText.setText(binary);
                        titleText.setText(issueTitle);
                        issueTypeText.setText(issueType);
                        descriptionText.setText(description);
                        occurenceText.setText(occurence);
                        deviceModelText.setText(deviceModel);
                        osVersionText.setText(osVersion);
                        downloadUri1Text.setText(downloadUri1);
                        downloadUri2Text.setText(downloadUri2);
                        downloadUri3Text.setText(downloadUri3);
                        salesCodeText.setText(salesCode);
                        serialNumberText.setText(serialNumber);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void showInformation() {

    }

    private void archieveThread(String id, String issueTitle, String deviceModel, String binary, String issueType, String occurence, String description, String engineerId, String customerId, String osVersion, String salesCode, String serialNumber, String downloadUri1, String downloadUri2, String downloadUri3, String resolution, String customerName, String engineerName) {
        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("id", id);
        hashMap.put("issueTitle", issueTitle);
        hashMap.put("deviceModel", deviceModel);
        hashMap.put("binary", binary);
        hashMap.put("issueType", issueType);
        hashMap.put("occurrence", occurence);
        hashMap.put("description", description);
        hashMap.put("customerId", customerId);
        hashMap.put("engineerId", engineerId);
        hashMap.put("osVersion", osVersion);
        hashMap.put("salesCode", salesCode);
        hashMap.put("serialNumber", serialNumber);
        hashMap.put("downloadUri1", downloadUri1);
        hashMap.put("downloadUri2", downloadUri2);
        hashMap.put("downloadUri3", downloadUri3);
        hashMap.put("resolution", resolution);
        hashMap.put("customerName", customerName);
        hashMap.put("engineerName", engineerName);

        Task createThread = reference1.child("Archieved Threads").push().setValue(hashMap);
        createThread.addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                Toast.makeText(getContext(), "It worked!", Toast.LENGTH_SHORT).show();
                deleteThreadFromCurrentThreads();
            }
        });
    }

    private void deleteThreadFromCurrentThreads() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Threads");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Thread thread = snapshot.getValue(Thread.class);
                    if (thread.getId().equals(id)) {
                        snapshot.getRef().removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                getContext().startActivity(new Intent(getContext(), MainActivity.class));

                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void bindViews(View view) {
        titleText = view.findViewById(R.id.titleText);
        deviceModelText = view.findViewById(R.id.modelText);
        binaryText = view.findViewById(R.id.binaryText);
        issueTypeText = view.findViewById(R.id.typeText);
        occurenceText = view.findViewById(R.id.occurenceText);
        descriptionText = view.findViewById(R.id.descriptionText);
        customerText = view.findViewById(R.id.customerText);
        engineerText = view.findViewById(R.id.engineerText);
        osVersionText = view.findViewById(R.id.osText);
        downloadUri1Text = view.findViewById(R.id.attachment1Text);
        downloadUri2Text = view.findViewById(R.id.attachment2Text);
        downloadUri3Text = view.findViewById(R.id.attachment3Text);
        serialNumberText = view.findViewById(R.id.serialText);
        salesCodeText = view.findViewById(R.id.salesText);
    }

    private void getCustomerandUser() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    if (user.getId().equals(customerId)) {
                        customerName = user.getUsername();
                        customerText.setText(customerName);
                    } else if(user.getId().equals(engineerId)) {
                        engineerName = user.getUsername();
                        engineerText.setText(engineerName);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void downloadFile(String url) {
        DownloadManager downloadManager = (DownloadManager) getContext().getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(url);
        String fileExtenstion = MimeTypeMap.getFileExtensionFromUrl(url);
        String fileName = URLUtil.guessFileName(String.valueOf(uri), null, fileExtenstion);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setTitle("Downloading")
                .setDescription("Downloading attachment")
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
        downloadManager.enqueue(request);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        } else {
        }
    }

}
