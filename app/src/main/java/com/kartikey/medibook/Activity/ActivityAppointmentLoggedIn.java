package com.kartikey.medibook.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kartikey.medibook.Modal.Appointment;
import com.kartikey.medibook.Modal.Patient;
import com.kartikey.medibook.R;

import org.w3c.dom.Text;

import java.util.Calendar;

public class ActivityAppointmentLoggedIn extends AppCompatActivity implements View.OnClickListener{
    private AlertDialog.Builder builder;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mApp;
    private TextView textView;
    private FirebaseAuth mAuth;
    public String name, dob, mobile, gender, username;
    private ProgressBar progressBar;
    public Button btn;
    private RecyclerView mAppointment;
    private String patient_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_logged_in);

        builder = new AlertDialog.Builder(this);

        mDatabase = FirebaseDatabase.getInstance();

        textView = findViewById(R.id.logged_textview);

        mAuth = FirebaseAuth.getInstance();

        btn = findViewById(R.id.btn_logged_log_out);
        btn.setOnClickListener(this);
        btn=findViewById(R.id.btn_logged_appointmnet);
        btn.setOnClickListener(this);

        progressBar=findViewById(R.id.logged_progressbar);
        progressBar.setVisibility(View.VISIBLE);

        FirebaseUser user = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance();
         patient_id=user.getUid();
        mApp=FirebaseDatabase.getInstance().getReference("Appointment").child(patient_id);

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
                onStart();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mAppointment=findViewById(R.id.logged_recyclerview);
        mAppointment.setLayoutManager(new LinearLayoutManager(this));

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btn_logged_log_out:
                Intent i = new Intent(ActivityAppointmentLoggedIn.this,ActivityMenu.class);
                mAuth.signOut();
                finish();
                startActivity(i);
                Toast.makeText(ActivityAppointmentLoggedIn.this, "Signed Out", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_logged_appointmnet:
                Intent intent = new Intent(ActivityAppointmentLoggedIn.this, ActivityAppointmentForm.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<Appointment, AppointmentViewHolder>firebaseRecyclerAdapter= new FirebaseRecyclerAdapter<Appointment, AppointmentViewHolder>
                (Appointment.class, R.layout.appointment_row, AppointmentViewHolder.class, mApp) {
            @Override
            protected void populateViewHolder(AppointmentViewHolder viewHolder, Appointment model, final int position) {
                final String appointment = getRef(position).getKey();
                final String mSpecialist = model.Specialist;
                final String mStatus=model.Status;
                String s = "Appointment_id : "+appointment+"\nDate :"+model.Date+"\nSpecialist :"+model.Specialist+"\nProblem : "+model.Problem+"\nSession :"+model.Session+"\nStatus :"+model.Status;

                if(mStatus.equals("Approved"))
                {viewHolder.setBackground(1);}
                if(mStatus.equals("Disapproved"))
                {viewHolder.setBackground(3);}
                if(mStatus.equals("Appointed"))
                {viewHolder.setBackground(2);}

                viewHolder.setText(s);

                AppointmentViewHolder.btn2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mStatus.equals("Appointed")){
                            AlertDialog.Builder mBuilder = new AlertDialog.Builder(ActivityAppointmentLoggedIn.this);
                            View mView = getLayoutInflater().inflate(R.layout.dialogbox, null);
                            final EditText mSuggestion = mView.findViewById(R.id.suggestion);
                            final RatingBar mRatingBar = mView.findViewById(R.id.suggestion_rating);

                            Button sbtn = mView.findViewById(R.id.dialog_submit);
                            sbtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    final String sug, rat;

                                    rat = mRatingBar.getNumStars()+"";
                                    sug = mSuggestion.getText().toString();
                                    DatabaseReference refrat = FirebaseDatabase.getInstance().getReference("Rating");
                                    refrat.child(patient_id).child(appointment).child("Rating").setValue(rat);
                                    refrat.child(patient_id).child(appointment).child("Suggestion").setValue(sug);
                                    Toast.makeText(ActivityAppointmentLoggedIn.this, "Suggestion saved", Toast.LENGTH_SHORT).show();
                                    getRef(position).removeValue();
                                    DatabaseReference mRef = mDatabase.getReference("Specialist").child(mSpecialist).child(appointment);
                                    mRef.removeValue();
                                    finish();
                                    startActivity(getIntent());
                                }
                            });
                            mBuilder.setView(mView);
                            mBuilder.setCancelable(false);

                            AlertDialog dialog = mBuilder.create();
                            dialog.show();
                        }
                            else{
                        Toast.makeText(ActivityAppointmentLoggedIn.this, "Deleted Appointment:"+appointment, Toast.LENGTH_SHORT).show();
                        getRef(position).removeValue();
                        DatabaseReference mRef = mDatabase.getReference("Specialist").child(mSpecialist).child(appointment);
                        mRef.removeValue();
                        finish();
                        startActivity(getIntent());
                        }
                    }
                });
            }
        };

        mAppointment.setAdapter(firebaseRecyclerAdapter);

    }

    public static class AppointmentViewHolder extends RecyclerView.ViewHolder{
        View mView;
         static Button btn2;
         TextView text;
         LinearLayout ll;

        public AppointmentViewHolder(View itemView) {
            super(itemView);
            mView=itemView;
            btn2= itemView.findViewById(R.id.delete);
            text = mView.findViewById(R.id.appointment_info);
            ll=mView.findViewById(R.id.ll);

        }

        public void setBackground(int a){
            if(a==1)
                text.setBackgroundResource(R.drawable.background_approved);
            else if(a==2)
                text.setBackgroundResource(R.drawable.background_completed);
            else if(a==3)
                text.setBackgroundResource(R.drawable.background_disapproved);


        }
        public void setTextColor(int a){
            text.setTextColor(Color.parseColor("#689F38"));
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
                        Intent intent = new Intent(ActivityAppointmentLoggedIn.this,ActivityMenu.class);
                        mAuth.signOut();
                        finish();
                        startActivity(intent);
                        Toast.makeText(ActivityAppointmentLoggedIn.this, "Signed Out", Toast.LENGTH_SHORT).show();
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
