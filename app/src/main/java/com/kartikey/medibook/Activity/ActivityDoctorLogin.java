package com.kartikey.medibook.Activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.kartikey.medibook.R;

public class ActivityDoctorLogin extends AppCompatActivity implements View.OnClickListener{

    private EditText username, password;
    private Button btn;
    private FirebaseAuth mAuth;
    private String mpassword, musername;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_login);
        btn=findViewById(R.id.btn_doctor_signup);
        btn.setOnClickListener(this);
        btn=findViewById(R.id.btn_doctor_login);
        btn.setOnClickListener(this);
        progressBar=findViewById(R.id.doctor_login_progressbar);
        mAuth = FirebaseAuth.getInstance();


        username = findViewById(R.id.doctor_username);
        username.setText("");
        password = findViewById(R.id.doctor_password);
        password.setText("");


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_doctor_signup:
                Intent intent = new Intent(ActivityDoctorLogin.this, ActivityDoctorSignUp.class);
                startActivity(intent);
                break;
            case R.id.btn_doctor_login: login();
                break;
        }
    }

    public void login(){

        boolean condition = true;

        if(username.getText().toString().equals("")){
            username.setText("");
            username.setHint("Enter Valid Username");
            condition = false;
        }
        if(password.getText().toString().equals("")){
            password.setText("");
            password.setHint("Enter Valid Password");
            condition = false;
        }

        if(condition){
            progressBar.setVisibility(View.VISIBLE);
            mpassword=password.getText().toString();
            musername=username.getText().toString();
            mAuth.signInWithEmailAndPassword(musername, mpassword)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(ActivityDoctorLogin.this, "Login Successfull", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(ActivityDoctorLogin.this, ActivityDoctorLogged.class);
                                startActivity(intent);
                            }
                            else{
                                progressBar.setVisibility(View.GONE);
                                username.setText("");
                                password.setText("");
                                Toast.makeText(ActivityDoctorLogin.this, "Login Failed", Toast.LENGTH_SHORT).show();


                            }
                        }

                    });
        }


    }

}