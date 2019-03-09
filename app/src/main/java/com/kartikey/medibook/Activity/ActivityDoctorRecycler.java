package com.kartikey.medibook.Activity;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kartikey.medibook.Adapter.DoctorViewHolder;
import com.kartikey.medibook.Modal.DoctorAppointment;
import com.kartikey.medibook.Modal.Patient;
import com.kartikey.medibook.R;

import java.util.Calendar;

public class ActivityDoctorRecycler extends AppCompatActivity {

    private FirebaseDatabase mDatabase;
    private DatabaseReference mDocApp;
    private ProgressBar progressBar;
    public String name, dob, mobile, gender, username, aa, bb;
    private TextView textView;
    Button approve, disapprove, appointed;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_recycler);
        final String specialist = getIntent().getExtras().getString("Specialist");
        final String patient_id = getIntent().getExtras().getString("patient_id");
        final String appointment = getIntent().getExtras().getString("Appointment_id");
        aa = specialist+"\n"+patient_id+"\n"+appointment;
        textView=findViewById(R.id.doctor_recycler);

        progressBar=findViewById(R.id.recycler_progressbar);
        mDatabase=FirebaseDatabase.getInstance();
        mDocApp = FirebaseDatabase.getInstance().getReference("Specialist").child(specialist).child(appointment);
        progressBar.setVisibility(View.VISIBLE);

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

                aa = "Name\t\t\t\t\t\t\t\t\t\t:\t"+name+"\nUsername\t\t\t\t\t\t:\t"+username+"\nDate of Birth\t\t\t\t:\t"+dob+"\nAge\t\t\t\t\t\t\t\t\t\t\t:\t"+age+"\nGender\t\t\t\t\t\t\t\t\t:\t"+gender+"\nMobile\t\t\t\t\t\t\t\t\t:\t"+mobile;
                mDocApp.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        String Date = dataSnapshot.child("Date").getValue(String.class);
                        String Problem= dataSnapshot.child("Problem").getValue(String.class);
                        String Session = dataSnapshot.child("Session").getValue(String.class);
                        String Status = dataSnapshot.child("Status").getValue(String.class);

                        bb = "\nAppointment_id\t\t:\t"+appointment+"\nDate\t\t\t\t\t\t\t\t\t\t\t:\t"+Date+"\nProblem\t\t\t\t\t\t\t\t:\t"+Problem+"\nSession\t\t\t\t\t\t\t\t:\t"+Session+"\nStatus\t\t\t\t\t\t\t\t\t:\t"+Status;
                        progressBar.setVisibility(View.GONE);
                        String t=aa+""+"\n"+""+bb;
                        textView.setText(t);
                        progressBar.setVisibility(View.GONE);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        approve=findViewById(R.id.approve);
        disapprove=findViewById(R.id.disapprove);
        appointed=findViewById(R.id.appointed);
        approve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ActivityDoctorRecycler.this, "Appointment Approved:"+appointment, Toast.LENGTH_SHORT).show();
                DatabaseReference r = mDatabase.getReference("Specialist").child(specialist).child(appointment).child("Status");
                r.setValue("Approved");
                DatabaseReference ref = mDatabase.getReference("Appointment").child(patient_id).child(appointment).child("Status");
                ref.setValue("Approved");
                finish();
                startActivity(getIntent());
            }
        });
        disapprove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ActivityDoctorRecycler.this, "Appointment Disapproved:"+appointment, Toast.LENGTH_SHORT).show();
                DatabaseReference r = mDatabase.getReference("Specialist").child(specialist).child(appointment);
                r.removeValue();
                DatabaseReference ref = mDatabase.getReference("Appointment").child(patient_id).child(appointment).child("Status");
                ref.setValue("Disapproved");
                finish();
            }
        });
        appointed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ActivityDoctorRecycler.this, "Appointed:"+appointment, Toast.LENGTH_SHORT).show();
                DatabaseReference r = mDatabase.getReference("Specialist").child(specialist).child(appointment);
                r.removeValue();
                DatabaseReference ref = mDatabase.getReference("Appointment").child(patient_id).child(appointment).child("Status");
                ref.setValue("Appointed");
                finish();
            }
        });
    }

}
