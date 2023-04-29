package com.uasmobile.todolist_app;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.uasmobile.todolist_app.model.Todo;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.UUID;


import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.exceptions.RealmPrimaryKeyConstraintException;

public class AddActivity extends AppCompatActivity {

    EditText edtTitle, edtDesc, edtPrio;

    TextView edtDeadline;
    TextView edtTime;
    Button btnSaveEdit;

    String Title = "";
    String Desc = "";
    String Date = "";
    String Time = "";
    String totalDate = "";
    String Id= "";
    String Prio = "";

    DatePickerDialog.OnDateSetListener setListener;
    TimePickerDialog.OnTimeSetListener timeSetListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        getSupportActionBar().setTitle("Add");

        edtTitle = (EditText) findViewById(R.id.edtTitle);
        edtDesc = (EditText) findViewById(R.id.edtDesc);
        edtDeadline = (TextView) findViewById(R.id.edtDeadline);
        edtTime = (TextView) findViewById(R.id.edtTime);
        btnSaveEdit = (Button) findViewById(R.id.btnSaveEdit);
        edtPrio = (EditText) findViewById(R.id.edtPrio);


//        final String value =
//                ((RadioButton)findViewById(radioGroup.getCheckedRadioButtonId()))
//                        .getText().toString();

        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int date = calendar.get(Calendar.DATE);
        final int hour = calendar.get(Calendar.HOUR_OF_DAY);
        final int minute = calendar.get(Calendar.MINUTE);
        edtPrio.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Prio = charSequence.toString().toLowerCase();
                if (!Prio.equals("low") && !Prio.equals("high") && !Prio.equals("medium"))  {
                    edtPrio.setError("Only 'Low' or 'Medium' or 'High' are allowed.");
                    btnSaveEdit.setEnabled(false);
                } else {
                    edtPrio.setError(null);
                    btnSaveEdit.setEnabled(true); // enable the submit button
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        edtDeadline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        AddActivity.this, android.R.style.Theme_Light,setListener,year,month,date);
                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                datePickerDialog.show();
            }
        });

        edtTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        AddActivity.this, android.R.style.Theme_Light, timeSetListener, hour, minute, true);

                timePickerDialog.show();
            }
        });



        setListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int date) {
                Calendar selectedCalendar = Calendar.getInstance();
                selectedCalendar.set(year, month, date);
                if (selectedCalendar.before(Calendar.getInstance())) {

                    Toast.makeText(AddActivity.this, "Please select a future date", Toast.LENGTH_SHORT).show();

                    year = Calendar.getInstance().get(Calendar.YEAR);
                    month = Calendar.getInstance().get(Calendar.MONTH);
                    date = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
                }
                month = month + 1;
                Date = date + "-" + month + "-" + year;
                edtDeadline.setText(Date);

                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month - 1);
                calendar.set(Calendar.DAY_OF_MONTH, date);
            }
        };

        timeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                Time = String.format("%02d:%02d", hourOfDay, minute);

                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);

                // Get the current date and time
                Calendar now = Calendar.getInstance();

                // Check if the selected time is in the past
                if (calendar.before(now)) {
                    Toast.makeText(AddActivity.this, "Cannot select past time", Toast.LENGTH_SHORT).show();
                } else {
                    edtTime.setText(Time);

                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy-HH-mm");
                    totalDate = dateFormat.format(calendar.getTime());
                }
            }
        };



        btnSaveEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Title = (String) edtTitle.getText().toString();
                Desc = (String) edtDesc.getText().toString();
                Date = (String) edtDeadline.getText().toString();
                Time = (String) edtTime.getText().toString();
                Prio = (String) edtPrio.getText().toString();

                Id = UUID.randomUUID().toString();
                Log.d("TAG", "Title: " + Title + ", Desc: " + Desc + ", Date: " + Date+ ", Time: " + Time+ ",ID: " + Id);
                tambahData(Title, Desc, Date, Time, Id, Prio);

                Intent i = new Intent(AddActivity.this, MainActivity.class);
                finish();
                overridePendingTransition(0, 0);
                startActivity(i);
                overridePendingTransition(0, 0);
            }
        });

    }

    public void tambahData(String Title, String Desc, String Date, String Time, String Id, String Prio) {
        Realm realm = Realm.getDefaultInstance();
        //penyimpanan data

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {


                try{
                    Log.d("TAG", "Title" + Title + "Desc" + Desc + "Date" + Date+ "Time" + Time + Id);
                    Todo todo1 = realm.createObject(Todo.class, Id);
                    todo1.setTitle(Title);
                    todo1.setDescription(Desc);
                    todo1.setDate(Date);
                    todo1.setTotalDate(totalDate);
                    todo1.setTime(Time);
                    todo1.setPrio(Prio);
                    finish();
                }catch(RealmPrimaryKeyConstraintException e) {
                    Log.d("TAG", "PrimaryKey Sudah Ada"+e.getMessage().toString());
                }
            }
        });
        realm.close();
    }
}