package com.example.theforumapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.OpenableColumns;
import android.renderscript.Sampler;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.theforumapp.Model.Url;
import com.example.theforumapp.Model.User;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.samsung.android.knox.EnterpriseDeviceManager;
import com.samsung.android.knox.deviceinfo.DeviceInventory;
import com.samsung.android.knox.license.KnoxEnterpriseLicenseManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class CreateThreadActivity extends AppCompatActivity {

    TextView username;
    FirebaseUser fUser;
    DatabaseReference reference;
    EditText issueTitle;
    EditText deviceModel;
    EditText currentBinary;
    Spinner issueType;
    Spinner occurence;
    Spinner engineerSpinner;
    Button attachBtn1;
    Button attachBtn2;
    TextView autoFillBtn;
    Button attachBtn3;
    Uri uri1;
    Uri uri2;
    LoadingDialog dialog;
    Uri uri3;
    TextView attachment1;
    TextView attachment2;
    TextView attachment3;
    EditText salesCode;
    EditText serialNumber;
    EditText osVersion;
    EditText issuedescription;
    Button createThreadBtn;
    Uri downloadUri1;
    Uri downloadUri2;
    Uri downloadUri3;
    String engineerId;
    ArrayList<String> downloadUris;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_thread);
        username = findViewById(R.id.username);
        username.setText("Thread creation");
        deviceModel = findViewById(R.id.deviceModelText);
        currentBinary = findViewById(R.id.currentBinaryText);
        issueType = findViewById(R.id.issueTypeSpinner);
        occurence = findViewById(R.id.occurenceSpinner);
        osVersion = findViewById(R.id.osEdit);
        attachBtn1 = findViewById(R.id.attachBtn1);
        salesCode = findViewById(R.id.salesCodeEdit);
        serialNumber = findViewById(R.id.serialNumberEdit);
        autoFillBtn = findViewById(R.id.autoFillBtn);
        attachBtn2 = findViewById(R.id.attachBtn2);
        attachBtn3 = findViewById(R.id.attachBtn3);
        dialog = new LoadingDialog(this);
        attachment1 = findViewById(R.id.attachment1);
        engineerSpinner = findViewById(R.id.engineerSpinner);
        attachment2 = findViewById(R.id.attachment2);
        attachment3 = findViewById(R.id.attachment3);
        downloadUris = new ArrayList<>();
        issuedescription = findViewById(R.id.issueDescriptionText);
        issueTitle = findViewById(R.id.issueTitleText);
        createThreadBtn = findViewById(R.id.createThreadButton);
        createThreadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (areFieldsEmpty()) {
                    Toast.makeText(CreateThreadActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                } else {

                    dialog.startDialog();
                    sendFileToFirebase(uri1);
                    sendFileToFirebase(uri2);
                    sendFileToFirebase(uri3);
                    String engineerName = engineerSpinner.getSelectedItem().toString();
                    getEngineerId(engineerName);

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            String title = issueTitle.getText().toString();
                            String model = deviceModel.getText().toString();
                            String binary = currentBinary.getText().toString();
                            String issue = issueType.getSelectedItem().toString();
                            String occ = occurence.getSelectedItem().toString();
                            String os = osVersion.getText().toString();
                            String sales = salesCode.getText().toString();
                            String serial = serialNumber.getText().toString();
                            String description = issuedescription.getText().toString();
                            String Uri1 = String.valueOf(downloadUri1);
                            String Uri2 = String.valueOf(downloadUri2);
                            String Uri3 = String.valueOf(downloadUri3);
                            createThread(title, model, binary, issue, occ, description, engineerId,os, sales, serial,  Uri1, Uri2, Uri3);
                        }
                    }, 30000);
                }
            }
        });
        attachBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkForPermission(1);
            }
        });
        attachBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkForPermission(2);
            }
        });
        attachBtn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkForPermission(3);
            }
        });
        autoFillBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                autoFillData();
            }
        });
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        populateSpinner();
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });
        fUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    private void autoFillData() {
        currentBinary.setText(Build.VERSION.INCREMENTAL);
        disableEditText(currentBinary);
        osVersion.setText(Build.VERSION.RELEASE);
        disableEditText(osVersion);
        deviceModel.setText(Build.MODEL);
        disableEditText(deviceModel);
        try {
            EnterpriseDeviceManager edm = EnterpriseDeviceManager.getInstance(this);
            DeviceInventory deviceInventory = edm.getDeviceInventory();String serialNo = deviceInventory.getSerialNumber();
            if (serialNo!=null) {
                serialNumber.setText(serialNo);
                disableEditText(serialNumber);
            }
            String sales = deviceInventory.getSalesCode();
            if (sales!=null) {
                salesCode.setText(sales);
                disableEditText(salesCode);
            }
        } catch (Exception e) {
            Toast.makeText(this, "Please activate knox license for Sales Code and Serial Number", Toast.LENGTH_SHORT).show();
        }


    }

    private void createThread(String issueTitle, String deviceModel, String binary, String issueType, String occurence, String description, String engineerId, String osVersion, String salesCode, String serialNumber, String downloadUri1, String downloadUri2, String downloadUri3) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String customerId = firebaseUser.getUid();
        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference();
        HashMap<String, Object> hashMap = new HashMap<>();
        String id = UUID.randomUUID().toString();
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



        Task createThread = reference1.child("Threads").push().setValue(hashMap);
        createThread.addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                deleteUrlsFromDataBase();
            }
        });
        createThread.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(CreateThreadActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void populateSpinner() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        final List<String> engineers = new ArrayList<>();
        reference.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot engineerSnapshot : dataSnapshot.getChildren()) {
                    User engineer = engineerSnapshot.getValue(User.class);
                    if (engineer.getAccess().equals("engineer")) {
                        engineers.add(engineer.getUsername());
                    }
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(CreateThreadActivity.this, android.R.layout.simple_spinner_item, engineers);
                adapter.setDropDownViewResource(android.R.layout.simple_selectable_list_item);
                engineerSpinner.setAdapter(adapter);
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 9 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            uploadFile(requestCode);
        } else {
        }
    }

    private void checkForPermission(int attachment) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            uploadFile(attachment);
        } else {
            ActivityCompat.requestPermissions(CreateThreadActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, attachment);
        }


    }

    private void uploadFile(int attachment) {
        Intent intent = new Intent("com.sec.android.app.myfiles.PICK_DATA");
        intent.putExtra("CONTENT_TYPE", "*/*");
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        startActivityForResult(intent, attachment);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            uri1 = data.getData();
            String fileName = queryName(getContentResolver(), uri1);
            attachment1.setText(fileName);


        } else if (requestCode == 2 && resultCode == RESULT_OK && data != null) {
            uri2 = data.getData();
            String fileName = queryName(getContentResolver(), uri2);
            attachment2.setText(fileName);

        } else if (requestCode == 3 && resultCode == RESULT_OK && data != null) {
            uri3 = data.getData();
            String fileName = queryName(getContentResolver(), uri3);
            attachment3.setText(fileName);

        } else {
            Toast.makeText(this, "Please select a file", Toast.LENGTH_SHORT).show();

        }
    }

    private String queryName(ContentResolver resolver, Uri uri) {
        Cursor returnCursor =
                resolver.query(uri, null, null, null, null);
        assert returnCursor != null;
        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        returnCursor.moveToFirst();
        String name = returnCursor.getString(nameIndex);
        returnCursor.close();
        return name;
    }

    private void sendFileToFirebase(final Uri uri) {
        final StorageReference storage = FirebaseStorage.getInstance().getReference();
        final String fileName = queryName(getContentResolver(), uri);
        final StorageReference fileRefenence = storage.child("Uploads").child(fileName);
        fileRefenence.putFile(uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                getDownloadUrl(uri);

            }
        });
    }

    private void getDownloadUrl(Uri uri) {
        final StorageReference storage = FirebaseStorage.getInstance().getReference();
        String fileName = queryName(getContentResolver(), uri);
        final StorageReference fileReference = storage.child("Uploads").child(fileName);
        UploadTask uploadTask = fileReference.putFile(uri);
        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return fileReference.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference();
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("url", downloadUri.toString());
                    reference1.child("Uploads").push().setValue(map);
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            getUrls();
                        }
                    },3000);
                } else {
                }
            }
        });

}

    private void getUrls() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Uploads");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Url url = snapshot.getValue(Url.class);
                    Log.d("PARSING ISSUE: ", url.getUrl());
                    if (!downloadUris.contains(url.getUrl())) {
                        downloadUris.add(url.getUrl());
                    }
                }


            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        if (downloadUris.size() == 3) {
            Log.d("PARSING ISSUE: ", String.valueOf(downloadUris.size()));
            downloadUri1 = Uri.parse(downloadUris.get(0));
            downloadUri2 = Uri.parse(downloadUris.get(1));
            downloadUri3 = Uri.parse(downloadUris.get(2));
        }

    }

    private void deleteUrlsFromDataBase() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Uploads");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Url url = snapshot.getValue(Url.class);
                    if (url.getUrl().equals(String.valueOf(downloadUri1)) || url.getUrl().equals(String.valueOf(downloadUri2)) || url.getUrl().equals(String.valueOf(downloadUri3))) {
                        snapshot.getRef().setValue(null);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        dialog.dismissDialog();
        startActivity(new Intent(CreateThreadActivity.this, MainActivity.class));
    }

    class LoadingDialog {
        private Activity activity;
        private AlertDialog dialog;

        LoadingDialog(Activity activity) {
            this.activity = activity;
        }

        void startDialog() {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            LayoutInflater inflater = activity.getLayoutInflater();
            builder.setView(inflater.inflate(R.layout.dialog, null));
            builder.setCancelable(false);
            dialog = builder.create();
            dialog.show();

        }

        void dismissDialog() {
            dialog.dismiss();
        }

    }

    private void getEngineerId(final String engineerName) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    if (user.getUsername().equals(engineerName)) {
                        engineerId = user.getId();

                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void disableEditText(EditText editText) {
        editText.setFocusable(false);
        editText.setEnabled(false);
        editText.setCursorVisible(false);
        editText.setKeyListener(null);
    }

    private boolean areFieldsEmpty() {
        boolean value;
        value = attachment1.getText().toString().matches("No attachment")
                || attachment2.getText().toString().matches("No attachment")
                || attachment3.getText().toString().matches("No attachment")
                || issueTitle.getText().toString().matches("")
                || serialNumber.getText().toString().matches("")
                || currentBinary.getText().toString().matches("")
                || salesCode.getText().toString().matches("")
                || osVersion.getText().toString().matches("")
                || issuedescription.getText().toString().matches("");
        Log.d("ARE EMPTY", String.valueOf(value));

        return value;
    }



    }