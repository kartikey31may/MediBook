package com.kartikey.medibook.Activity;



import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;

import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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

public class ActivityAppointmentSignUp extends AppCompatActivity implements View.OnClickListener {

    private ProgressBar progressBar;
    private static TextView dob;
    private EditText username, name, password, password2, mobile;
    private FirebaseDatabase mDatabase;
    private FirebaseAuth mAuth;
    private Button button;
    private RadioGroup gender;

    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_sign_up);

        progressBar = findViewById(R.id.register_progressbar);

        mDatabase = FirebaseDatabase.getInstance();
        dob=findViewById(R.id.register_dob);
        dob.setOnClickListener(this);
        username = findViewById(R.id.register_username);
        username.setText("");
        name = findViewById(R.id.register_fullname);
        name.setText("");
        password=findViewById(R.id.register_password_1);
        password.setText("");
        password2=findViewById(R.id.register_password_2);
        password2.setText("");
        mAuth = FirebaseAuth.getInstance();
        mobile=findViewById(R.id.register_mobile);
        gender=findViewById(R.id.gender_grp);
        button = findViewById(R.id.register_button);
        button.setOnClickListener(this);
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
            case R.id.register_button: register();
            break;
            case R.id.register_dob: calender();
            break;
        }

    }

    private void calender() {
            DialogFragment newFragment = new DatePickerFragment();
            newFragment.show(getSupportFragmentManager(), "datePicker");

    }

    private void register(){
        final String susername, sname, spassword, spassword2, mmobile, mdob, mgender;
        boolean condition=true;
        susername = username.getText().toString();
        mmobile = mobile.getText().toString();
        mdob = dob.getText().toString();
        RadioButton act = findViewById(gender.getCheckedRadioButtonId());
        mgender = act.getText().toString();
        sname = name.getText().toString();
        spassword = password.getText().toString();
        spassword2 = password2.getText().toString();


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
                                String patient_id;

                                FirebaseUser user = mAuth.getCurrentUser();
                                mDatabase = FirebaseDatabase.getInstance();
                                patient_id=user.getUid();
                                DatabaseReference mRef = mDatabase.getReference("Patient");
                                mRef.child(patient_id).child("Name").setValue(sname);
                                mRef.child(patient_id).child("Username").setValue(susername);
                                mRef.child(patient_id).child("DOB").setValue(mdob);
                                mRef.child(patient_id).child("Gender").setValue(mgender);
                                mRef.child(patient_id).child("Mobile").setValue(mmobile);
                                DatabaseReference mRefappointment = mDatabase.getReference("Appointment");
                                mRefappointment.child(patient_id).child("Appointment").setValue(null);

                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(ActivityAppointmentSignUp.this, "Successful registration", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(ActivityAppointmentSignUp.this, ActivityAppointmentLoggedIn.class);
                                startActivity(intent);


                            } else {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(ActivityAppointmentSignUp.this, "Failed", Toast.LENGTH_SHORT).show();
                                username.setText("");
                                password.setText("");
                                password2.setText("");

                            }
                        }

                    });
        }
    }

}
