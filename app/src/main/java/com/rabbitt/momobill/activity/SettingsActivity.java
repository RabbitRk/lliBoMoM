package com.rabbitt.momobill.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.gcacace.signaturepad.views.SignaturePad;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rabbitt.momobill.R;
import com.rabbitt.momobill.prefsManager.PrefsManager;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.acl.Owner;

import static com.rabbitt.momobill.prefsManager.PrefsManager.KEY;
import static com.rabbitt.momobill.prefsManager.PrefsManager.OWNER;
import static com.rabbitt.momobill.prefsManager.PrefsManager.PREF_NAME;
import static com.rabbitt.momobill.prefsManager.PrefsManager.USER_EMAIL;
import static com.rabbitt.momobill.prefsManager.PrefsManager.USER_NAME;
import static com.rabbitt.momobill.prefsManager.PrefsManager.USER_PHONE;

public class SettingsActivity extends AppCompatActivity {


    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 200;
    private static final int IMAGE_PICK_GALLERY_CODE = 1000;
    private static final int IMAGE_PICK_CAMERA_CODE = 2000;

    Uri imageUri, resultUri;
    String[] cameraPermission;
    String[] storagePermission;

    String key;
    Bitmap signBitmap;
    ImageView imageView;
    int i=0;
    public String[] image,imgPath;

    FloatingActionButton newImageBtn;

    File myDir;


    FirebaseStorage storage;
    StorageReference mStorageRef,logoRef,signRef;
    SharedPreferences preference,checkPref;
    SharedPreferences.Editor prefEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        preference = getSharedPreferences(PrefsManager.USER_PREF,0);
        checkPref = getSharedPreferences(PREF_NAME,0);
        prefEditor = preference.edit();

        key=preference.getString(KEY,"No Email Found");

        storage= FirebaseStorage.getInstance();
        mStorageRef=storage.getReference();

        myDir = new File(Environment.getExternalStorageDirectory() + "/Santha Agencies");

        if (!myDir.exists()) {
            myDir.mkdir();
        }



        cameraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
    }



    public void updateEmail(View view) {

        String email = preference.getString(USER_EMAIL,"No Email Found");

        Toast.makeText(this, email, Toast.LENGTH_SHORT).show();

        final AlertDialog dialogBuilder = new AlertDialog.Builder(this).create();
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.change_email_dialog,null);

        TextView currentEmailTxt,newEmailTxt;
        final EditText currentEmail,newEmail;
        Button saveBtn,cancelBtn;
        currentEmailTxt=dialogView.findViewById(R.id.current_email_txt);
        newEmailTxt=dialogView.findViewById(R.id.new_email_txt);
        newEmail=dialogView.findViewById(R.id.new_email);
        currentEmail=dialogView.findViewById(R.id.current_email);
        saveBtn=dialogView.findViewById(R.id.update_btn);
        currentEmailTxt.setText("Current Email id");
        newEmailTxt.setText("New Email id");
        currentEmail.setText(email);

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String mail = newEmail.getText().toString();
                String child;
                prefEditor.putString(USER_EMAIL,mail);
                prefEditor.commit();

                if(checkPref.getBoolean(OWNER,false))
                    child = "Owner";
                else
                    child = "Employee";

                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Agency");
                reference.child(child).child(key).child("email").setValue(mail);

                dialogBuilder.dismiss();

            }
        });

        cancelBtn=dialogView.findViewById(R.id.cancel_dialog_btn);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogBuilder.dismiss();
            }
        });
        dialogBuilder.setView(dialogView);
        dialogBuilder.show();

    }

    public void updatePhone(View view) {

        String email = preference.getString(USER_PHONE,"No Phone Found");


        final AlertDialog dialogBuilder = new AlertDialog.Builder(this).create();
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.change_email_dialog,null);

        TextView currentPhoneTxt,newPhoneTxt;
        final EditText currentPhone,newPhone;
        Button saveBtn,cancelBtn;
        currentPhoneTxt=dialogView.findViewById(R.id.current_email_txt);
        newPhoneTxt=dialogView.findViewById(R.id.new_email_txt);
        newPhone=dialogView.findViewById(R.id.new_email);
        currentPhone=dialogView.findViewById(R.id.current_email);
        saveBtn=dialogView.findViewById(R.id.update_btn);
        currentPhoneTxt.setText("Current Phone");
        newPhoneTxt.setText("New Phone");
        currentPhone.setText(email);

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String phone = newPhone.getText().toString();
                String child;
                prefEditor.putString(USER_PHONE,phone);
                prefEditor.commit();

                if(checkPref.getBoolean(OWNER,false))
                    child = "Owner";
                else
                    child = "Employee";

                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Agency");
                reference.child(child).child(key).child("phone").setValue(phone);

                dialogBuilder.dismiss();

            }
        });

        cancelBtn=dialogView.findViewById(R.id.cancel_dialog_btn);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogBuilder.dismiss();
            }
        });
        dialogBuilder.setView(dialogView);
        dialogBuilder.show();

    }

    public void updateLogo(View view) {
        if (!checkCameraPermission())
            requestCameraPermission();
        if (!checkStoragePermission())
            requestStoragePermission();
        String[] items = {" Camera", " Gallery"};
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Select Image");
        dialog.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                if (i == 0) {
                    pickCamera();
                }
                if (i == 1) {
                    pickGallery();
                }
            }
        });
        dialog.create().show();

    }

    private void pickGallery() {

        //Intent to pick image from gallery
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_GALLERY_CODE);
    }

    private void requestStoragePermission() {

        ActivityCompat.requestPermissions(this, storagePermission, STORAGE_REQUEST_CODE);
    }

    private boolean checkStoragePermission() {

        boolean storResult = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);

        return storResult;
    }

    private void pickCamera() {

        //Intent to capture image from camera
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Pic");//Title of picture
        values.put(MediaStore.Images.Media.DESCRIPTION, "Image to text");//desc
        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(cameraIntent, IMAGE_PICK_CAMERA_CODE);
    }

    private void requestCameraPermission() {

        ActivityCompat.requestPermissions(this, cameraPermission, CAMERA_REQUEST_CODE);
    }

    private boolean checkCameraPermission() {

        boolean camResult = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean storResult = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);

        return camResult && storResult;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (resultCode == RESULT_OK) {
            if (requestCode == IMAGE_PICK_CAMERA_CODE) {
                //Got Image from Camera now crop it
                CropImage.activity(imageUri).setGuidelines(CropImageView.Guidelines.ON).start(this);
            }
            if (requestCode == IMAGE_PICK_GALLERY_CODE) {
                imageUri = intent.getData();
                //Got Image from Gallery now crop it
                CropImage.activity(imageUri).setGuidelines(CropImageView.Guidelines.ON).start(this);
            }
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            final ProgressDialog progressDialog = ProgressDialog.show(this,"","Uploading...",true);
            CropImage.ActivityResult result = CropImage.getActivityResult(intent);

            if (resultCode == RESULT_OK) {

                resultUri = result.getUri();
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resultUri);
                    String imageName=resultUri.getLastPathSegment().substring(resultUri.getLastPathSegment().lastIndexOf("/")+1);
                    File f=new File(myDir,"Logo");
                    FileOutputStream stream=new FileOutputStream(f);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);

                    ByteArrayOutputStream outputStream=new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG,0,outputStream);
                    logoRef = mStorageRef.child("Logo/"+key);

                    logoRef.putBytes(outputStream.toByteArray()).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(SettingsActivity.this, "Success", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(SettingsActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                        }
                    });


                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
            else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception e = result.getError();
                Toast.makeText(this, "Error is" + e, Toast.LENGTH_SHORT).show();
            }
        }
    }


    public void getSign(View view) {

        final AlertDialog dialogBuilder = new AlertDialog.Builder(this).create();
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.signature_dialog,null);
        final SignaturePad signaturePad=dialogView.findViewById(R.id.signature_pad);
        Button save,clr,cancel;
        save = dialogView.findViewById(R.id.save_sign);
        clr = dialogView.findViewById(R.id.clear_sign);
        cancel = dialogView.findViewById(R.id.cancel_sign);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ProgressDialog progressDialog = ProgressDialog.show(SettingsActivity.this, "", "Saving...", true);
                signBitmap = signaturePad.getSignatureBitmap();
                try {
                    File f = new File(myDir, "Signature.jpg");
                    FileOutputStream stream = new FileOutputStream(f);
                    signBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);

                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    signBitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
                    signRef = mStorageRef.child("Signature/" + key);

                    signRef.putBytes(outputStream.toByteArray()).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(SettingsActivity.this, "Success", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(SettingsActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(SettingsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        clr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signaturePad.clearView();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogBuilder.dismiss();
            }
        });


        dialogBuilder.setView(dialogView);
        dialogBuilder.show();
    }

}