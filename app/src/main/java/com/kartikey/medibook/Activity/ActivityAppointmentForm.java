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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kartikey.medibook.Modal.Patient;
import com.kartikey.medibook.R;

import java.util.Calendar;

public class ActivityAppointmentForm extends AppCompatActivity {

    private FirebaseDatabase mDatabase;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;
    private String specialist;
    private long app_id;
    public String appointment_id;
    private Spinner spinner;
    private static TextView mdob, textView;
    private RadioGroup session;
    private Button button;
    private EditText problem;
    public String name, dob, mobile, gender, username;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_form);

        spinner = findViewById(R.id.form_specialist);
        mdob=findViewById(R.id.form_app_ate);
        session=findViewById(R.id.form_session);
        button=findViewById(R.id.btn_form_appointmnet);
        problem=findViewById(R.id.form_problem);
        progressBar=findViewById(R.id.form_progressbar);
        textView=findViewById(R.id.form_textview);
        mDatabase = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        progressBar.setVisibility(View.VISIBLE);


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

        mdob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calender();
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bookAppointment();
            }
        });
        app_id=System.currentTimeMillis();
        appointment_id=""+app_id;

        String patient_id=user.getUid();
        DatabaseReference mRef = mDatabase.getReference("Patient").child(patient_id);
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int age, t, c, n;
                name = dataSnapshot.child("Name").getValue(String.class);
                username= dataSnapshot.child("Username").getValue(String.class);
                dob = dataSnapshot.child("DOB").getValue(String.class);
                n=dob.length();
                t=Integer.parseInt(dob.substring(n-4, n));
                final Calendar cal = Calendar.getInstance();
                c = cal.get(Calendar.YEAR);
                age = c-t;
                gender = dataSnapshot.child("Gender").getValue(String.class);
                mobile = dataSnapshot.child("Mobile").getValue(String.class);
                String text;
                text = "Name\t\t\t\t\t\t\t\t:\t"+name+"\nUsername\t\t\t\t:\t"+username+"\nDate of Birth\t\t:\t"+dob+"\nAge\t\t\t\t\t\t\t\t\t:\t"+age+"\nGender\t\t\t\t\t\t\t:\t"+gender+"\nMobile\t\t\t\t\t\t\t:\t"+mobile;
                textView.setText(text);
                Patient p = new Patient(name, dob, mobile, gender, username);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void bookAppointment() {
        String mproblem, date, sessionTime, mspecialist;
        RadioButton r;
        mproblem = problem.getText().toString();
        date = mdob.getText().toString();
        r = findViewById(session.getCheckedRadioButtonId());
        sessionTime=r.getText().toString();
        mspecialist= specialist;

        boolean condition = true;

        if(mproblem.equals("")){
            problem.setText("");
            problem.setHint("Enter Your Healtth Problem Briefly");
            condition = false;
        }
        if(!date.contains("/")){
            mdob.setText("Select a valid date");
            condition = false;
        }

        if(condition){
            progressBar.setVisibility(View.VISIBLE);
            FirebaseUser user = mAuth.getCurrentUser();
            mDatabase = FirebaseDatabase.getInstance();
            String patient_id=user.getUid();
            DatabaseReference mRef = mDatabase.getReference("Specialist").child(specialist).child(appointment_id);
            mRef.child("patient_id").setValue(patient_id);
            mRef.child("Date").setValue(date);
            mRef.child("Problem").setValue(mproblem);
            mRef.child("Session").setValue(sessionTime);
            mRef.child("Status").setValue("Not Approved");

            DatabaseReference mRefappointment = mDatabase.getReference("Appointment").child(patient_id).child(appointment_id);
            mRefappointment.child("Date").setValue(date);
            mRefappointment.child("Problem").setValue(mproblem);
            mRefappointment.child("Session").setValue(sessionTime);
            mRefappointment.child("Specialist").setValue(specialist);
            mRefappointment.child("Status").setValue("Not Approved");


            progressBar.setVisibility(View.GONE);
            Toast.makeText(ActivityAppointmentForm.this, "Successful Booked", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(ActivityAppointmentForm.this, ActivityAppointmentLoggedIn.class);
            startActivity(i);
        }


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

            DatePickerDialog dialogDatePicker = new DatePickerDialog(getActivity(), this, year, month, day);
            dialogDatePicker.getDatePicker().setSpinnersShown(true);
            dialogDatePicker.getDatePicker().setCalendarViewShown(false);
            dialogDatePicker.getDatePicker().setMaxDate(System.currentTimeMillis()+2592000000L);
            dialogDatePicker.getDatePicker().setMinDate(System.currentTimeMillis()+86400000);
            return dialogDatePicker;
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            month++;
            if(month<10){
                mdob.setText(day+"/0"+month+"/"+year);
            }else
                mdob.setText(day+"/"+month+"/"+year);

        }


    }

    private void calender() {
        DialogFragment newFragment = new ActivityAppointmentForm.DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");

    }
}
