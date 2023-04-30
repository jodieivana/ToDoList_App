package com.uasmobile.todolist_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.uasmobile.todolist_app.adapter.TodoAdapter;
import com.uasmobile.todolist_app.model.Todo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import io.realm.exceptions.RealmPrimaryKeyConstraintException;

public class MainActivity extends AppCompatActivity {

    private RecyclerView listViewToDo;
    private TodoAdapter tasksAdapter;

    private List<Todo> taskList;

    SwipeRefreshLayout swipeRefreshLayout;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        String date_n = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(new Date());

        TextView date  = (TextView) findViewById(R.id.tanggalBiru);
        date.setText(date_n);

        //config Realm
        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder().allowWritesOnUiThread(true).deleteRealmIfMigrationNeeded().build();
        Realm.setDefaultConfiguration(config);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("title", "Coding");
        editor.apply();

        FloatingActionButton fab= (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(MainActivity.this, AddActivity.class);

                startActivity(intent);
            }
        });

        Realm realm = Realm.getDefaultInstance();

//        realm.executeTransaction(new Realm.Transaction() {
//            @Override
//            public void execute(Realm realm) {
//                realm.deleteAll();
//            }
//        });

        RealmResults<Todo> todos = realm.where(Todo.class).findAll();

        List<Todo> taskList = realm.copyFromRealm(todos);
        taskList.sort(new Comparator<Todo>() {
            @Override
            public int compare(Todo todo, Todo t1) {
//                return todo.getDate().compareTo(t1.getDate());
                String[] date1 = todo.getTotalDate().split("-");
                String[] date2 = t1.getTotalDate().split("-");
                int min1 = Integer.parseInt(date1[4]);
                int min2 = Integer.parseInt(date2[4]);
                int hour1 = Integer.parseInt(date1[3]);
                int hour2 = Integer.parseInt(date2[3]);
                int year1 = Integer.parseInt(date1[2]);
                int year2 = Integer.parseInt(date2[2]);
                int month1 = Integer.parseInt(date1[1]);
                int month2 = Integer.parseInt(date2[1]);
                int day1 = Integer.parseInt(date1[0]);
                int day2 = Integer.parseInt(date2[0]);

                if (year1 != year2) {
                    return year1 - year2;
                } else if (month1 != month2) {
                    return month1 - month2;
                } else if (day1 != day2) {
                    return day1 - day2;
                } else if (hour1 != hour2) {
                    return hour1 - hour2;
                } else {
                    return min1 - min2;
                }

            }
        });
//        taskList = new ArrayList<>();

        listViewToDo = findViewById(R.id.listViewToDo);
        listViewToDo.setLayoutManager(new LinearLayoutManager(this));
        tasksAdapter = new TodoAdapter(this);
        listViewToDo.setAdapter(tasksAdapter);

//        taskList = new ArrayList<Todo>();
//        taskList.addAll(realm.copyFromRealm(todos));


        ItemTouchHelper itemTouchHelper = new
                ItemTouchHelper(new RecyclerItemTouchHelper(tasksAdapter, (ArrayList) taskList));
        itemTouchHelper.attachToRecyclerView(listViewToDo);

//        Collections.reverse(taskList);
        tasksAdapter.setTasks(taskList);
        realm.close();

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                tasksAdapter.refreshData();
                swipeRefreshLayout.setRefreshing(false);
            }
        });


    }

    public void tambahDataBuku() {
        Realm realm = Realm.getDefaultInstance();
        //penyimpanan data

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                try{
                    realm.deleteAll();

                    Todo todo1 = realm.createObject(Todo.class, "12345");
                    todo1.setTitle("Coding");
                    todo1.setDescription("UAS Mobile");
                    todo1.setDate("19-04-23");
                    todo1.setPrio("High");

                }catch(RealmPrimaryKeyConstraintException e) {
                    Log.d("TAG", "PrimaryKey Sudah Ada"+e.getMessage().toString());
                }
            }
        });
        realm.close();
    }
}