package com.uasmobile.todolist_app.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.icu.text.CaseMap;
import android.os.Parcel;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.uasmobile.todolist_app.MainActivity;
import com.uasmobile.todolist_app.R;
import com.uasmobile.todolist_app.model.Todo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.realm.Realm;
import io.realm.exceptions.RealmPrimaryKeyConstraintException;

public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.ViewHolder>{

    private List<Todo> todoList;
    private MainActivity activity;
    private SharedPreferences prefs;

    public TodoAdapter(MainActivity activity){
        this.activity = activity;
        this.prefs = activity.getSharedPreferences("my_prefs", Context.MODE_PRIVATE);
    }

    protected TodoAdapter(Parcel in) {
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_listviewtodo, parent, false);
        return new ViewHolder(itemView);

    }

    public void onBindViewHolder (ViewHolder holder, int position){
        Todo item = todoList.get(position);
        holder.isi.setText(item.getTitle());
        holder.desc.setText(item.getDescription());
        holder.date.setText(item.getDate());
        holder.time.setText(item.getTime());

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault());
            Date date = sdf.parse(item.getDate() + " " + item.getTime());

            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            if (cal.before(Calendar.getInstance())) {
                holder.date.setTextColor(ContextCompat.getColor(activity, R.color.red));
                holder.time.setTextColor(ContextCompat.getColor(activity, R.color.red));
            } else {
                holder.date.setTextColor(ContextCompat.getColor(activity, R.color.darkgrey));
                holder.time.setTextColor(ContextCompat.getColor(activity, R.color.darkgrey));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String prioText = item.getPrio();
        String capitalizedPrioText = prioText.substring(0, 1).toUpperCase() + prioText.substring(1).toLowerCase();
        holder.prio.setText(capitalizedPrioText);

        String priority = item.getPrio().toLowerCase();
        switch (priority) {
            case "high":
                holder.circlePrio.setColorFilter(ContextCompat.getColor(activity, R.color.red));
                break;
            case "medium":
                holder.circlePrio.setColorFilter(ContextCompat.getColor(activity, R.color.orange));
                break;
            case "low":
                holder.circlePrio.setColorFilter(ContextCompat.getColor(activity, R.color.green));
                break;
            default:
                holder.circlePrio.setColorFilter(ContextCompat.getColor(activity, R.color.darkgrey));
        }

        boolean isChecked = prefs.getBoolean("checkbox_state_" + item.getId(), false);
        holder.task.setChecked(isChecked);

        holder.task.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                prefs.edit().putBoolean("checkbox_state_" + item.getId(), isChecked).apply();
                if (isChecked) {
                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault());
                        Date date = sdf.parse(item.getDate() + " " + item.getTime());
                        if (date.before(Calendar.getInstance().getTime())) {
                            deleteItem(item.getId());
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }

    public void refreshData() {
        Realm realm = Realm.getDefaultInstance();
        List<Todo> todoList = realm.where(Todo.class).findAll();
        setTasks(todoList);
    }

    private boolean toBoolean(int n){
        return n!=0;
    }

    public int getItemCount(){
        return todoList.size();
    }

    public Todo getItem(int position) {
        return todoList.get(position);
    }

    public void setTasks(List<Todo> todoList){
        this.todoList = todoList;
        notifyDataSetChanged();
    }

    public Context getContext() {return activity;}

    public void deleteItem(String Id){
        Realm realm = Realm.getDefaultInstance();
        //delete data

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                try{
                    Log.d("TAG",  "ID" + Id);
                    Todo todo1 = realm.where(Todo.class).equalTo("id",Id).findFirst();
                    todo1.deleteFromRealm();
                }catch(RealmPrimaryKeyConstraintException e) {
                    Log.d("TAG", "PrimaryKey Sudah Ada"+e.getMessage().toString());
                }
            }
        });
        realm.close();
    }

    public void editItem (String Id, String Title, String Desc, String Date, String Time, String prio){
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
                    todo1.setDate(Time);
                    todo1.setPrio(prio);
                }catch(RealmPrimaryKeyConstraintException e) {
                    Log.d("TAG", "PrimaryKey Sudah Ada"+e.getMessage().toString());
                }
            }
        });
        realm.close();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        CheckBox task;
        TextView isi,desc,date, time ,prio;

        ImageView circlePrio;

        ViewHolder(View view){
            super(view);
            task = view.findViewById(R.id.todoCheckbox);
            isi = view.findViewById(R.id.todoIsi);
            desc = view.findViewById(R.id.todoDesc);
            date = view.findViewById(R.id.todoDate);
            time = view.findViewById(R.id.todoTime);
            prio = view.findViewById(R.id.todoPrio);
            circlePrio = view.findViewById(R.id.circlePrio);
        }
    }


}
