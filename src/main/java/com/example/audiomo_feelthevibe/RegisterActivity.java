package com.example.audiomo_feelthevibe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
  TextView alreadyHaveaccount;
  EditText inputEmail,inputPassword,inputConformPassword,inputusername;
  Button btnRegister;
  String emailPattern="[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
  ProgressDialog progressDialog;
  String userID;


  FirebaseFirestore fstore;
  FirebaseAuth mAuth;
  FirebaseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        alreadyHaveaccount=findViewById(R.id.alreadyHaveaccount);

        inputEmail=findViewById(R.id.inputemail);
        inputPassword=findViewById(R.id.inputpassword);
        inputConformPassword=findViewById(R.id.inputpassword2);
        btnRegister=findViewById(R.id.btnregister);
        progressDialog=new ProgressDialog(this);

        mAuth=FirebaseAuth.getInstance();
        mUser=mAuth.getCurrentUser();
        fstore=FirebaseFirestore.getInstance();
//        userID=mAuth.getCurrentUser().getUid();


     inputusername=findViewById(R.id.inputuser);

        alreadyHaveaccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this,MainActivity.class));
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PerforAuth();
                peruser();
            }
        });

    }

    private void peruser() {
        String username=inputusername.getText().toString();
//        userID=mAuth.getCurrentUser().getUid();
        String mail=inputEmail.getText().toString();
        DocumentReference documentReference=fstore.collection("users").document(mail);
        final Map<String,Object> user=new HashMap<>();
        user.put("username",username);
        //user.put("Email",mail);
        documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(RegisterActivity.this, "Username Register", Toast.LENGTH_SHORT).show();
            }
        });



    }


    private void PerforAuth() {

        String email=inputEmail.getText().toString();
        String password=inputPassword.getText().toString();
        String confirmPassword=inputConformPassword.getText().toString();

        if(!email.matches(emailPattern))
        {
            inputEmail.setError("Enter correct email");
            inputEmail.requestFocus();
        }
        else if(password.isEmpty() || password.length()<6)
        {
            inputPassword.setError("Enter proper Password");
        }
        else if(!password.equals(confirmPassword))
        {
            inputConformPassword.setError("Password not match field");
        }
        else
        {
            progressDialog.setMessage("Please wait while Registration.....");
            progressDialog.setTitle("Registration");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        progressDialog.dismiss();
                        sendUserToNextActivity();
                        Toast.makeText(RegisterActivity.this,"Registration Succesful",Toast.LENGTH_SHORT).show();
                    }
                    else {
                        progressDialog.dismiss();
                        Toast.makeText(RegisterActivity.this,""+task.getException(),Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void sendUserToNextActivity() {
        Intent intent=new Intent(RegisterActivity.this,HomeActvity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}