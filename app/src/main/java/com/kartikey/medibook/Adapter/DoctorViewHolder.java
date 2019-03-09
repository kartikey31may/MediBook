package com.kartikey.medibook.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.kartikey.medibook.R;

public class DoctorViewHolder extends RecyclerView.ViewHolder {

    public View mView;
    public static Button btn;

    public DoctorViewHolder(View itemView) {
        super(itemView);
        mView=itemView;
        btn=mView.findViewById(R.id.approve);
    }

    public void setDetails(Context ctx, String s){

        TextView text = mView.findViewById(R.id.doctor_appointment_info);
        text.setText(s);
    }
}
