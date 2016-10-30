package com.example.ytseitkin.tojewlist;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.util.Calendar;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ButtonBarLayout;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import org.w3c.dom.Text;

public class SetupReminder extends AppCompatActivity {

    private Activity that = this;

    private TextView dateView;
    private TextView timeView;
    private Button setDate;
    private Button setTime;

    int year,day,month,minute,hour;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_reminder);

        Button alarm = (Button) findViewById(R.id.createAlarm);

        dateView = (TextView) findViewById(R.id.date);

        timeView = (TextView) findViewById(R.id.timeText);

        setDate = (Button) findViewById(R.id.setDate);
        setTime = (Button) findViewById(R.id.setTime);

        TextView text = (TextView)findViewById(R.id.details);

        text.setText(text.getText().toString() + getIntent().getExtras().getString("name"));

        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(this);

        sharedPreferences.edit().putString("details",getIntent().getExtras().getString("name"));

        setDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDate();
            }
        });

        setTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setTime();
            }
        });

        alarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(that , AlarmReceiver.class);
                AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
                PendingIntent pendingIntent = PendingIntent.getService(that, 0, myIntent, 0);

                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.MINUTE, minute);
                calendar.set(Calendar.HOUR, hour);
                calendar.add(Calendar.DAY_OF_MONTH, day);
                calendar.add(Calendar.MONTH, month);
                calendar.add(Calendar.YEAR, year);

                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 1000*60*60*24 , pendingIntent);
            }
        });

        setDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDate();
            }
        });

        setTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                setTime();

            }
        });
    }


    public void setDate() {
        showDialog(999);
    }

    public void setTime() {
        showDialog(888);

    }

    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == 999) {
            return new DatePickerDialog(this, myDateListener, year, month, day);
        }
        else if (id == 888){
            return new TimePickerDialog(this, myTimeListener, hour, minute, false);
        }

        return null;
    }

    private DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
            // TODO Auto-generated method stub
            // arg1 = year
            // arg2 = month
            // arg3 = day
            showDate(arg1, arg2+1, arg3);
        }
    };

    private TimePickerDialog.OnTimeSetListener myTimeListener = new TimePickerDialog.OnTimeSetListener(){
        @Override
        public void onTimeSet(TimePicker timePicker, int i, int i1) {

            showTime(i,i1);
        }
    };

    private void showDate(int year, int month, int day) {
        dateView.setText(new StringBuilder().append(day).append("/")
                .append(month).append("/").append(year));
    }

    private void showTime(int hour,int minute){
        timeView.setText((minute<10) ? hour+ ":0" + minute : hour + ":" + minute);
    }
}
