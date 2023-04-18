package com.uasmobile.todolist_app;

import static java.security.AccessController.getContext;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

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
import android.widget.ImageButton;
import android.widget.TextView;

import com.uasmobile.todolist_app.adapter.TodoAdapter;
import com.uasmobile.todolist_app.model.Todo;

import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import io.realm.Realm;
import io.realm.exceptions.RealmPrimaryKeyConstraintException;

public class EditActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TodoAdapter adapter;
    private List<Todo> todoList;

    EditText edtTitlee, edtDesce;

    TextView edtDeadlinee;
    Button btnSaveEdite;

    String Title = "";
    String Desc = "";
    String Date = "";
    String Id= "";

    DatePickerDialog.OnDateSetListener setListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("Edit");

        setContentView(R.layout.activity_edit);
        getIntent().getStringExtra("title");
        getIntent().getStringExtra("date");
        getIntent().getStringExtra("id");

        edtDesce = (EditText) findViewById(R.id.edtDesce);
        edtTitlee = (EditText) findViewById(R.id.edtTitlee);
        edtDeadlinee = (TextView) findViewById(R.id.edtDeadlinee);
        btnSaveEdite = (Button) findViewById(R.id.btnSaveEdite);

        edtTitlee.setText(getIntent().getStringExtra("title"));
        edtDesce.setText(getIntent().getStringExtra("desc"));
        edtDeadlinee.setText(getIntent().getStringExtra("date"));

        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int date = calendar.get(Calendar.DATE);

        edtDeadlinee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        EditActivity.this, android.R.style.Theme_Light,setListener,year,month,date);
                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                datePickerDialog.show();
            }
        });

        setListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int date) {
                month = month + 1;
                Date = date + "-" + month + "-" + year;
                edtDeadlinee.setText(Date);

                Id = UUID.randomUUID().toString();
            }
        };


        btnSaveEdite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Title = (String) edtTitlee.getText().toString();
                Desc = (String) edtDesce.getText().toString();
                Date = (String) edtDeadlinee.getText().toString();
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
