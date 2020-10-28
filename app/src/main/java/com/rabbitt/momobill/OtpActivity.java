package com.rabbitt.momobill;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class OtpActivity extends AppCompatActivity {

    private static final String TAG = "maluOTP";
    private FirebaseAuth mAuth;
    private String verificationId, phone_no;
    ProgressDialog loading;
    EditText otp_txt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        otp_txt = findViewById(R.id.otpTxt);
        Intent i = getIntent();
        phone_no = i.getStringExtra("phone_no");
        Log.i(TAG, "onCreate: "+phone_no);
        mAuth = FirebaseAuth.getInstance();
        sendVerificationCode(phone_no);
    }

    //Sending verification code to the mobile phone
    private void sendVerificationCode(String number) {
        Log.i(TAG, "sendVerificationCode: "+number);
        loading = ProgressDialog.show(this, "Registering", "Please wait...we will automatically verify your OTP", false, true);
        PhoneAuthProvider.getInstance().verifyPhoneNumber(number, 60, TimeUnit.SECONDS, TaskExecutors.MAIN_THREAD, mCallBack);
    }

    private void verifyCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithCredential(credential);
    }

    //Signin process is now initiated
    private void signInWithCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    String currentuser = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
                    Log.i(TAG, "signInWithCredential: " + currentuser);
                    loading.dismiss();
                    goHomePage(currentuser);
                } else {
                    loading.dismiss();
                    Toast.makeText(OtpActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void goHomePage(String currentuser) {
        Intent intent = new Intent (OtpActivity.this, SignUpActivity.class);
        intent.putExtra("phone", phone_no);
        startActivity(intent);
        finish();
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
            mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verificationId = s;
            Log.i(TAG, "onCodeSent: "+verificationId);
        }

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();
            if(code != null){
                otp_txt.setText(code);
                verifyCode(code);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Log.i(TAG, "onVerificationFailed: "+e.getMessage());
            Toast.makeText(OtpActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    };

    public void submit_click(View view) {

    }
}