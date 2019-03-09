package com.kartikey.medibook.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import com.kartikey.medibook.Modal.DoctorAppointment;
import com.kartikey.medibook.R;

import java.util.Calendar;

public class ActivityDoctorLogged extends AppCompatActivity implements View.OnClickListener{
    private AlertDialog.Builder builder;
    private FirebaseDatabase mDatabase;
    public DatabaseReference mDocApp;
    private TextView textView;
    private FirebaseAuth mAuth;
    public String name, dob, mobile, gender, username, specialist="", extra;
    private ProgressBar progressBar;
    public Button btn;
    private RecyclerView mDoctorAppointment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_logged);

        builder = new AlertDialog.Builder(this);


        mDatabase = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();

        textView = findViewById(R.id.doctor_logged_textview);
        btn = findViewById(R.id.btn_logged_log_out);
        btn.setOnClickListener(this);


        progressBar=findViewById(R.id.logged_progressbar);
        progressBar.setVisibility(View.VISIBLE);


        FirebaseUser user = mAuth.getCurrentUser();
        String doctor_id=user.getUid();

        DatabaseReference mRef = mDatabase.getReference("Doctor").child(doctor_id);
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
                specialist = dataSnapshot.child("Specialist").getValue(String.class);
                String text;
                text = "Name\t\t\t\t\t\t\t\t:\t"+name+"\nUsername\t\t\t\t:\t"+username+"\nDate of Birth\t\t:\t"+dob+"\nAge\t\t\t\t\t\t\t\t\t:\t"+age+"\nGender\t\t\t\t\t\t\t:\t"+gender+"\nMobile\t\t\t\t\t\t\t:\t"+mobile+"\nSpecialist\t\t\t\t:\t"+specialist;
                textView.setText(text);
                onStart();
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mDocApp=mDatabase.getReference("Specialist").child(specialist);
        onStart();


    }

    @Override
    protected void onStart() {
        super.onStart();
        mDocApp=mDatabase.getReference("Specialist").child(specialist);
        mDoctorAppointment=findViewById(R.id.doctor_logged_recycler);
        mDoctorAppointment.setLayoutManager(new LinearLayoutManager(this));


        FirebaseRecyclerAdapter<DoctorAppointment, DoctorAppointmentViewHolder>firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<DoctorAppointment, DoctorAppointmentViewHolder>
                (DoctorAppointment.class, R.layout.doctor_row, DoctorAppointmentViewHolder.class, mDocApp) {
            @Override
            protected void populateViewHolder(DoctorAppointmentViewHolder viewHolder, DoctorAppointment model, final int position) {
                final String appointment = getRef(position).getKey();final String mStatus=model.Status;
                final String patient_id = model.patient_id;
                final String nStatus = model.Status;
                String s = "Appointment_id : "+appointment+"\nDate :"+model.Date+"\nProblem : "+model.Problem+"\nSession :"+model.Session+"\nStatus :"+model.Status;

               /* if(nStatus.equals("Approved"))
                {viewHolder.setBackground(1);}*/

                viewHolder.setText(s);

                DoctorAppointmentViewHolder.text.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                       final Intent intent = new Intent(ActivityDoctorLogged.this,ActivityDoctorRecycler.class);
                       final Bundle b =new Bundle();
                         getRef(position).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                extra = dataSnapshot.child("patient_id").getValue(String.class);

                                b.putString("patient_id", extra);
                                b.putString("Specialist",getRef(position).getParent().getKey());
                                b.putString("Appointment_id", getRef(position).getKey());
                                intent.putExtras(b);

                                startActivity(intent);

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    }
                });
            }
        };

        mDoctorAppointment.setAdapter(firebaseRecyclerAdapter);
    }


    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btn_logged_log_out:
                mAuth.signOut();
                finish();
                break;

        }
    }

    public static class DoctorAppointmentViewHolder extends RecyclerView.ViewHolder{
        View mView;
        static TextView text;

        public DoctorAppointmentViewHolder(View itemView) {
            super(itemView);
            mView=itemView;
            text = mView.findViewById(R.id.doctor_appointment_info);

        }
        public void setBackground(int a) {
                text.setBackgroundResource(R.drawable.background_approved);

        }

        public void setText(String s){
            text.setText(s);
        }


    }

    public void onBackPressed() {
        builder.setMessage("Do you want to exit.?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(ActivityDoctorLogged.this,ActivityMenu.class);
                        mAuth.signOut();
                        finish();
                        startActivity(intent);
                        Toast.makeText(ActivityDoctorLogged.this, "Signed Out", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert = builder.create();
        alert.setTitle("Alert!!");
        alert.show();
    }
}

