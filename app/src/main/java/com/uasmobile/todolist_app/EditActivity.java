package com.uasmobile.todolist_app;

import static java.security.AccessController.getContext;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

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
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.uasmobile.todolist_app.adapter.TodoAdapter;
import com.uasmobile.todolist_app.model.Todo;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import io.realm.Realm;
import io.realm.exceptions.RealmPrimaryKeyConstraintException;

public class EditActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TodoAdapter adapter;
    private List<Todo> todoList;

    EditText edtTitlee, edtDesce, edtPrioe;

    TextView edtDeadlinee, edtTimee;
    Button btnSaveEdite;



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
        getSupportActionBar().setTitle("Edit");

        setContentView(R.layout.activity_edit);
        getIntent().getStringExtra("title");
        getIntent().getStringExtra("date");
        getIntent().getStringExtra("time");
        getIntent().getStringExtra("id");
        getIntent().getStringExtra("prio");

        edtDesce = (EditText) findViewById(R.id.edtDesce);
        edtTitlee = (EditText) findViewById(R.id.edtTitlee);
        edtDeadlinee = (TextView) findViewById(R.id.edtDeadlinee);
        edtTimee = (TextView) findViewById(R.id.edtTimee);
        edtPrioe = (EditText) findViewById(R.id.edtPrioe);
        btnSaveEdite = (Button) findViewById(R.id.btnSaveEdite);

        edtTitlee.setText(getIntent().getStringExtra("title"));
        edtDesce.setText(getIntent().getStringExtra("desc"));
        edtDeadlinee.setText(getIntent().getStringExtra("date"));
        edtTimee.setText(getIntent().getStringExtra("time"));
        edtPrioe.setText(getIntent().getStringExtra("prio"));

        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int date = calendar.get(Calendar.DATE);
        final int hour = calendar.get(Calendar.HOUR_OF_DAY);
        final int minute = calendar.get(Calendar.MINUTE);

        edtPrioe.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Prio = charSequence.toString().toLowerCase();
                if (!Prio.equals("low") && !Prio.equals("high") && !Prio.equals("medium"))  {
                    edtPrioe.setError("Only 'Low' or 'Medium' or 'High' are allowed.");
                    btnSaveEdite.setEnabled(false);
                } else {
                    edtPrioe.setError(null);
                    btnSaveEdite.setEnabled(true); // enable the submit button
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        edtDeadlinee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        EditActivity.this, android.R.style.Theme_Light,setListener,year,month,date);
                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                datePickerDialog.show();
            }
        });

        edtTimee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        EditActivity.this, android.R.style.Theme_Light, timeSetListener, hour, minute, true);

                timePickerDialog.show();
            }
        });

        setListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int date) {
                Calendar selectedCalendar = Calendar.getInstance();
                selectedCalendar.set(year, month, date);
                if (selectedCalendar.before(Calendar.getInstance())) {

                    Toast.makeText(EditActivity.this, "Please select a future date", Toast.LENGTH_SHORT).show();

                    year = Calendar.getInstance().get(Calendar.YEAR);
                    month = Calendar.getInstance().get(Calendar.MONTH);
                    date = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
                }
                month = month + 1;
                Date = date + "-" + month + "-" + year;
                edtDeadlinee.setText(Date);

                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month - 1);
                calendar.set(Calendar.DAY_OF_MONTH, date);

//                Id = UUID.randomUUID().toString();
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
                    Toast.makeText(EditActivity.this, "Cannot select past time", Toast.LENGTH_SHORT).show();
                } else {
                    edtTimee.setText(Time);

                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy-HH-mm");
                    totalDate = dateFormat.format(calendar.getTime());
                }
            }
        };


        btnSaveEdite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Title = (String) edtTitlee.getText().toString();
                Desc = (String) edtDesce.getText().toString();
                Date = (String) edtDeadlinee.getText().toString();
                Time = (String) edtTimee.getText().toString();
                Prio = (String) edtPrioe.getText().toString();
                String Id = getIntent().getStringExtra("id"); // get the ID of the Todo object

                Log.d("TAG", "Title: " + Title + ", Desc: " + Desc + ", Date: " + Date + ",ID: " + Id);
//                TodoAdapter todo = new TodoAdapter();

                Realm realm = Realm.getDefaultInstance();
                //update data

                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        try{
                            Log.d("TAG", "Title: " + Title + ", Desc: " + Desc + ", Date: " + Date + ", ID: " + Id);
                            Todo todo1 = realm.where(Todo.class).equalTo("id",Id).findFirst();
                            todo1.setTitle(Title);
                            todo1.setDescription(Desc);
                            todo1.setDate(Date);
                            todo1.setTime(Time);
                            todo1.setTotalDate(totalDate);
                            todo1.setPrio(Prio);
                        }catch(RealmPrimaryKeyConstraintException e) {
                            Log.d("TAG", "PrimaryKey Sudah Ada"+e.getMessage().toString());
                        }
                    }
                });
                realm.close();

                Intent i = new Intent(EditActivity.this, MainActivity.class);
                finish();
                overridePendingTransition(0, 0);
                startActivity(i);
                overridePendingTransition(0, 0);

                finish();
            }
        });

    }


//    public void tambahData(String Title, String Desc, String Date, String Id) {
//        Realm realm = Realm.getDefaultInstance();
//        //penyimpanan data
//
//        realm.executeTransaction(new Realm.Transaction() {
//            @Override
//            public void execute(Realm realm) {
//
//
//                try{
//                    Log.d("TAG", "Title" + Title + "Desc" + Desc + "Date" + Date+ Id);
//                    Todo todo1 = realm.createObject(Todo.class, Id);
//                    todo1.setTitle(Title);
//                    todo1.setDescription(Desc);
//                    todo1.setDate(Date);
//                    finish();
//                }catch(RealmPrimaryKeyConstraintException e) {
//                    Log.d("TAG", "PrimaryKey Sudah Ada"+e.getMessage().toString());
//                }
//            }
//        });
//        realm.close();
//    }
}
