package com.kartikey.medibook.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Toast;

import com.kartikey.medibook.R;

public class ActivityMenu extends AppCompatActivity implements View.OnClickListener{
    private AlertDialog.Builder builder;

    CardView card;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        card=findViewById(R.id.appointment_form);
        card.setOnClickListener(this);
        card=findViewById(R.id.doctor);
        card.setOnClickListener(this);
        card=findViewById(R.id.help);
        card.setOnClickListener(this);


        builder = new AlertDialog.Builder(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.appointment_form:
                Intent intent = new Intent(ActivityMenu.this, ActivityAppointmentLogin.class);
                startActivity(intent);
                break;
            case R.id.doctor:
                Intent i = new Intent(ActivityMenu.this, ActivityDoctorLogin.class);
                startActivity(i);
                break;
            case R.id.help:
                Intent i2 = new Intent(ActivityMenu.this, ActivityHelp.class);
                startActivity(i2);                break;
        }
    }

    public void onBackPressed() {
        builder.setMessage("Do you want to exit.?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                        System.exit(0);
                        Toast.makeText(ActivityMenu.this, "Exit", Toast.LENGTH_SHORT).show();
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
