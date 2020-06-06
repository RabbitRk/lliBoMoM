package com.rabbitt.momobill;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class OtpActivity extends AppCompatActivity {

    private static final String TAG = "maluOTP";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);
        Intent i = getIntent();
        String phone_no = i.getStringExtra("phone_no");
        Log.i(TAG, "onCreate: "+phone_no);
//        sendVerificationCode(phone_no);
    }

//    //Sending verification code to the mobile phone
//    private void sendVerificationCode(String number) {
//        Log.i(TAG, "sendVerificationCode: "+number);
//        loading = ProgressDialog.show(this, "Registering", "Please wait...we will automatically verify your OTP", false, true);
//        PhoneAuthProvider.getInstance().verifyPhoneNumber(number, 60, TimeUnit.SECONDS, TaskExecutors.MAIN_THREAD, mCallBack);
//    }
//
//    private void verifyCode(String code) {
//        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
//        signInWithCredential(credential);
//    }
//
//    //Signin process is now initiated
//    private void signInWithCredential(PhoneAuthCredential credential) {
//        mAuth.signInWithCredential(credential).addOnCompleteListener(task -> {
//            if(task.isSuccessful()){
//                String currentuser = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
//                Log.i(TAG, "signInWithCredential: "+currentuser);
//                loading.dismiss();
//                getUserId(currentuser);
//            }
//            else{
//                loading.dismiss();
//                Toast.makeText(OTPActivity.this,task.getException().getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
//    }

//    //It initiates the Phone Authentication process
//    /*
//     * The callback function will handle the rest of the process
//     * */
//    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
//            mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
//
//        @Override
//        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
//            super.onCodeSent(s, forceResendingToken);
//            verificationId = s;
//            Log.i(TAG, "onCodeSent: "+verificationId);
//        }
//
//        @Override
//        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
//            String code = phoneAuthCredential.getSmsCode();
//            if(code != null){
//                otp_txt.setText(code);
//                verifyCode(code);
//            }
//        }
//
//        @Override
//        public void onVerificationFailed(FirebaseException e) {
//            Log.i(TAG, "onVerificationFailed: "+e.getMessage());
//            Toast.makeText(OTPActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
//        }
//    };

}