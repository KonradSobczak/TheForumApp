package com.example.theforumapp.Fragments;


import android.app.Activity;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import static android.app.Activity.RESULT_OK;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.theforumapp.Constants;
import com.example.theforumapp.SampleAdminReceiver;
import com.example.theforumapp.Model.User;
import com.example.theforumapp.R;
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
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.samsung.android.knox.EnterpriseDeviceManager;
import com.samsung.android.knox.application.ApplicationPolicy;
import com.samsung.android.knox.custom.CustomDeviceManager;
import com.samsung.android.knox.custom.SettingsManager;
import com.samsung.android.knox.license.KnoxEnterpriseLicenseManager;
import com.samsung.android.knox.restriction.RestrictionPolicy;

import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {

    CircleImageView image_profile;
    TextView username;

    DatabaseReference reference;
    FirebaseUser fuser;

    StorageReference storageReference;
    private static final int IMAGE_REQUEST = 1;
    private static final int DEVICE_ADMIN_ADD_RESULT_ENABLE = 1;
    private Uri imageUri;
    private StorageTask uploadTask;
    private DevicePolicyManager mDPM;
    private ComponentName mDeviceAdmin;
    private TextView adminStatus;
    private Button adminToggle;
    private Button activateLicense;
    private Button deactivateLicense;
    private Button setAdbStateBtn;
    private Button setUsbDebugging;


    public ProfileFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        image_profile = view.findViewById(R.id.profile_image);
        username = view.findViewById(R.id.username);
        mDPM = (DevicePolicyManager) getContext().getSystemService(Context.DEVICE_POLICY_SERVICE);
        mDeviceAdmin = new ComponentName(getContext(), SampleAdminReceiver.class);
        storageReference = FirebaseStorage.getInstance().getReference("uploads");

        fuser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
        adminStatus = view.findViewById(R.id.adminStatus);
        setAdbStateBtn = view.findViewById(R.id.enableAdbBtn);
        setUsbDebugging = view.findViewById(R.id.setUsbDebugging);
        adminToggle = view.findViewById(R.id.adminToggle);
        activateLicense = view.findViewById(R.id.activateLicensee);
          setAdbStateBtn.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View view) {
                  enableAdbState();
              }
          });
          setUsbDebugging.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View view) {
                  setUsbDebugging();
              }
          });
        deactivateLicense = view.findViewById(R.id.deactivateLicense);
        adminToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleAdmin();
            }
        });

        activateLicense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activateLicense();
            }
        });

        deactivateLicense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deactivateLicense();
            }
        });

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                username.setText(user.getUsername());
                if (user.getImageURL().equals("default")) {
                    if (user.getAccess().equals("customer")) {
                        image_profile.setImageResource(R.drawable.customer_logo);
                    } else {
                        image_profile.setImageResource(R.drawable.knox_logo);
                    }
                } else {
                    if (isAdded()) {
                        Glide.with(Objects.requireNonNull(getActivity()).getApplicationContext()).load(user.getImageURL()).into(image_profile);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        image_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImage();
                Log.d("UPLOADING", "here4");
            }
        });


        return view;
    }

    private void openImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMAGE_REQUEST);
    }

    private String getFileExtenstion(Uri uri) {
        ContentResolver contentResolver = getContext().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadImage() {
        final ProgressDialog pd = new ProgressDialog(getContext());
        pd.setMessage("Uploading");
        pd.show();
        if (imageUri != null) {
            final StorageReference fileReference = storageReference.child(System.currentTimeMillis() + "." + getFileExtenstion(imageUri));

            uploadTask = fileReference.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        String mUri = downloadUri.toString();

                        reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("imageURL", mUri);
                        reference.updateChildren(map);

                        pd.dismiss();
                    } else {
                        Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }
            });
        } else {
            Toast.makeText(getContext(), "No image selected", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == DEVICE_ADMIN_ADD_RESULT_ENABLE) {
            switch (resultCode) {
                // End user cancels the request
                case Activity.RESULT_CANCELED:
                    Toast.makeText(getContext(), getString(R.string.admin_cancelled), Toast.LENGTH_SHORT).show();
                    break;
                // End user accepts the request
                case Activity.RESULT_OK:
                    Toast.makeText(getContext(), "getString(R.string.admin_enabled", Toast.LENGTH_SHORT).show();
                    adminStatus.setText("Admin activated");
                    activateLicense.setClickable(true);
                    deactivateLicense.setClickable(true);
                    break;
            }
        }

        if (requestCode == IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            imageUri = data.getData();

            if (uploadTask != null && uploadTask.isInProgress()) {
                Toast.makeText(getContext(), "Upload in progress", Toast.LENGTH_SHORT).show();
            } else {
                uploadImage();
            }
        }
    }

    private void toggleAdmin() {
        boolean adminState = mDPM.isAdminActive(mDeviceAdmin);
        if (adminState) {
            Toast.makeText(getContext(), getResources().getString(R.string.deactivating_admin), Toast.LENGTH_SHORT).show();
            // Deactivate application as device administrator
            mDPM.removeActiveAdmin(new ComponentName(getContext(), SampleAdminReceiver.class));
            adminStatus.setText("No admin");
        } else {
            try {
                Toast.makeText(getContext(), getResources().getString(R.string.activating_admin), Toast.LENGTH_SHORT).show();
                // Ask the user to add a new device administrator to the system
                Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mDeviceAdmin);
                // Start the add device administrator activity
                startActivityForResult(intent, DEVICE_ADMIN_ADD_RESULT_ENABLE);

            } catch (Exception e) {
                Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void activateLicense() {

        // Instantiate the EnterpriseLicenseManager class to use the activateLicense method
        KnoxEnterpriseLicenseManager klmManager = KnoxEnterpriseLicenseManager.getInstance(getContext());

        try {
            // KPE License Activation TODO Add license key to Constants.java
            klmManager.activateLicense(Constants.KPE_LICENSE_KEY);
            Toast.makeText(getContext(), getResources().getString(R.string.activate_license_progress), Toast.LENGTH_SHORT).show();
            ;

        } catch (Exception e) {
            Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    private void deactivateLicense() {

        // Instantiate the EnterpriseLicenseManager class to use the activateLicense method
        KnoxEnterpriseLicenseManager klmManager = KnoxEnterpriseLicenseManager.getInstance(getContext());

        try {
            // KPE License Activation TODO Add license key to Constants.java
            klmManager.deActivateLicense(Constants.KPE_LICENSE_KEY);
            Toast.makeText(getContext(), getResources().getString(R.string.activate_license_progress), Toast.LENGTH_SHORT).show();
            ;

        } catch (Exception e) {
            Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    private void refreshButtons() {
        boolean adminState = mDPM.isAdminActive(mDeviceAdmin);

        if (!adminState) {
            adminStatus.setText("No admin");
            activateLicense.setClickable(false);
            deactivateLicense.setClickable(false);

        } else {
            adminStatus.setText("Admin activated");
            activateLicense.setClickable(true);
            deactivateLicense.setClickable(true);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshButtons();
    }

    private void enableAdbState() {
        try {
            CustomDeviceManager cdm = CustomDeviceManager.getInstance();
            SettingsManager kcsm = cdm.getSettingsManager();
            kcsm.setAdbState(true);
        } catch (SecurityException e) {
            Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
        }

    }

    private void setUsbDebugging() {
        EnterpriseDeviceManager edm = EnterpriseDeviceManager.getInstance(getContext());
        RestrictionPolicy restrictionPolicy = edm.getRestrictionPolicy();
        try {
            restrictionPolicy.setUsbDebuggingEnabled(true);
        } catch (SecurityException e) {
            Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
        }


    }
}
