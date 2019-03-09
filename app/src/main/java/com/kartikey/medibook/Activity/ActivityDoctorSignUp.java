package com.kartikey.medibook.Activity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kartikey.medibook.R;

import java.util.Calendar;

public class ActivityDoctorSignUp extends AppCompatActivity implements View.OnClickListener {

    private ProgressBar progressBar;
    private static TextView dob;
    private EditText username, name, password, password2, mobile, security;
    private FirebaseDatabase mDatabase;
    private FirebaseAuth mAuth;
    private Button button;
    private RadioGroup gender;
    private Spinner spinner;
    public String specialist;
    private String Security_code="1234";

    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_sign_up);

        progressBar = findViewById(R.id.doctor_progressbar);
        spinner=findViewById(R.id.doctor_specialist);
        mDatabase = FirebaseDatabase.getInstance();
        dob=findViewById(R.id.doctor_dob);
        dob.setOnClickListener(this);
        username = findViewById(R.id.doctor_username);
        username.setText("");
        name = findViewById(R.id.doctor_fullname);
        name.setText("");
        password=findViewById(R.id.doctor_password_1);
        password.setText("");
        password2=findViewById(R.id.doctor_password_2);
        password2.setText("");
        security=findViewById(R.id.doctor_security);
        security.setText("");
        mAuth = FirebaseAuth.getInstance();
        mobile=findViewById(R.id.doctor_mobile);
        gender=findViewById(R.id.gender_grp);
        button = findViewById(R.id.doctor_button);
        button.setOnClickListener(this);

        final ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.specialist));
        spinner.setAdapter(spinnerAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                specialist = spinnerAdapter.getItem(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {


        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            DatePickerDialog dialogDatePicker = new DatePickerDialog(getActivity(), this, year, month, day);
            dialogDatePicker.getDatePicker().setSpinnersShown(true);
            dialogDatePicker.getDatePicker().setCalendarViewShown(false);
            return dialogDatePicker;
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            month++;
            if(month<10){
                dob.setText(day+"/0"+month+"/"+year);
            }else
                dob.setText(day+"/"+month+"/"+year);

        }


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.doctor_button: register();
                break;
            case R.id.doctor_dob: calender();
                break;
        }

    }

    private void calender() {
        DialogFragment newFragment = new ActivityDoctorSignUp.DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");

    }

    private void register(){
        final String susername, sname, spassword, spassword2, mmobile, mdob, mgender, mspecialist, msecurity;
        boolean condition=true;
        susername = username.getText().toString();
        mmobile = mobile.getText().toString();
        mdob = dob.getText().toString();
        RadioButton act = findViewById(gender.getCheckedRadioButtonId());
        mgender = act.getText().toString();
        sname = name.getText().toString();
        spassword = password.getText().toString();
        spassword2 = password2.getText().toString();
        msecurity = security.getText().toString();
        mspecialist=specialist;

        if(msecurity!=Security_code){
            security.setHint("Invalid Security Code");
            condition=false;
        }

        if(susername.equals("")){
            username.setHint("Enter a valid Email");
            condition=false;
        }
        if(mmobile.equals("")||mmobile.length()>10){
            mobile.setHint("Enter a valid Mobile Number");
            condition=false;
        }
        if(!mdob.contains("/")){
            dob.setText("Pick a valid Date");
            condition=false;
        }
        if(!(susername.contains("@")&&susername.contains("."))){
            username.setText("");
            username.setHint("Enter a valid Email");
            condition=false;
        }
        if(!susername.equals("")){
            if(!(Character.isAlphabetic(susername.charAt(0))||Character.isDigit(susername.charAt(0)))){
                username.setText("");
                username.setHint("Enter a valid Email");
                condition=false;
            }
        }
        if(sname.equals("")){
            name.setHint("Enter a valid Name");
            condition=false;
        }
        if(spassword.equals("")){
            password.setHint("Enter a valid Password");
            condition=false;
        }
        if(spassword2.equals("")){
            password2.setHint("Enter a valid Password");
            condition=false;
        }
        if(!spassword.equals(spassword2)){
            password.setText("");
            password2.setText("");
            password.setHint("Password did not Match");
            condition=false;
        }
        if(condition){
            progressBar.setVisibility(View.VISIBLE);
            mAuth.createUserWithEmailAndPassword(susername, spassword)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) {
                                String doctor_id;

                                FirebaseUser user = mAuth.getCurrentUser();
                                mDatabase = FirebaseDatabase.getInstance();
                                doctor_id=user.getUid();
                                DatabaseReference mRef = mDatabase.getReference("Doctor");
                                mRef.child(doctor_id).child("Name").setValue(sname);
                                mRef.child(doctor_id).child("Username").setValue(susername);
                                mRef.child(doctor_id).child("DOB").setValue(mdob);
                                mRef.child(doctor_id).child("Gender").setValue(mgender);
                                mRef.child(doctor_id).child("Mobile").setValue(mmobile);
                                mRef.child(doctor_id).child("Specialist").setValue(mspecialist);

                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(ActivityDoctorSignUp.this, "Successful registration", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(ActivityDoctorSignUp.this, ActivityDoctorLogged.class);
                                startActivity(intent);


                            } else {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(ActivityDoctorSignUp.this, "Failed", Toast.LENGTH_SHORT).show();
                                username.setText("");
                                password.setText("");
                                password2.setText("");

                            }
                        }

                    });
        }
    }

}
