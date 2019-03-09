package com.kartikey.medibook.Modal;

public class DoctorAppointment {

    public String  Date, Problem, Session, Status, patient_id;

    public DoctorAppointment(String mpatient_id, String date, String problem, String session, String status) {
        Date = date;
        Problem = problem;
        Session = session;
        Status = status;
        patient_id = mpatient_id;

    }
    public DoctorAppointment(){

    }
}
