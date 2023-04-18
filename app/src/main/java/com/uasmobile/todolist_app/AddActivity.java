package com.uasmobile.todolist_app;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.uasmobile.todolist_app.model.Todo;

import java.util.Calendar;
import java.util.UUID;


import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.exceptions.RealmPrimaryKeyConstraintException;

public class AddActivity extends AppCompatActivity {

    EditText edtTitle, edtDesc;

    TextView edtDeadline;
    Button btnSaveEdit;

    String Title = "";
    String Desc = "";
    String Date = "";
    String Id= "";

    DatePickerDialog.OnDateSetListener setListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        edtTitle = (EditText) findViewById(R.id.edtTitle);
        edtDesc = (EditText) findViewById(R.id.edtDesc);
        edtDeadline = (TextView) findViewById(R.id.edtDeadline);
        btnSaveEdit = (Button) findViewById(R.id.btnSaveEdit);

        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int date = calendar.get(Calendar.DATE);

        edtDeadline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        AddActivity.this, android.R.style.Theme_Light,setListener,year,month,date);
                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                datePickerDialog.show();
            }
        });

        setListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int date) {
                month = month + 1;
                Date = date + "-" + month + "-" + year;
                edtDeadline.setText(Date);
            }
        };


        btnSaveEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Title = (String) edtTitle.getText().toString();
                Desc = (String) edtDesc.getText().toString();
                Date = (String) edtDeadline.getText().toString();
                Id = UUID.randomUUID().toString();
                Log.d("TAG", "Title: " + Title + ", Desc: " + Desc + ", Date: " + Date+ ",ID: " + Id);
                tambahData(Title, Desc, Date, Id);

                Intent i = new Intent(AddActivity.this, MainActivity.class);
                finish();
                overridePendingTransition(0, 0);
                startActivity(i);
                overridePendingTransition(0, 0);
            }
        });

    }

    public void tambahData(String Title, String Desc, String Date, String Id) {
        Realm realm = Realm.getDefaultInstance();
        //penyimpanan data

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {


                try{
                    Log.d("TAG", "Title" + Title + "Desc" + Desc + "Date" + Date+ Id);
                    Todo todo1 = realm.createObject(Todo.class, Id);
                    todo1.setTitle(Title);
                    todo1.setDescription(Desc);
                    todo1.setDate(Date);
                    finish();
                }catch(RealmPrimaryKeyConstraintException e) {
                    Log.d("TAG", "PrimaryKey Sudah Ada"+e.getMessage().toString());
                }
            }
        });
        realm.close();
    }
}