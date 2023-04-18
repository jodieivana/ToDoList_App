package com.uasmobile.todolist_app.adapter;

import android.content.Context;
import android.content.Intent;
import android.icu.text.CaseMap;
import android.os.Parcel;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.uasmobile.todolist_app.MainActivity;
import com.uasmobile.todolist_app.R;
import com.uasmobile.todolist_app.model.Todo;

import java.util.List;

import io.realm.Realm;
import io.realm.exceptions.RealmPrimaryKeyConstraintException;

public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.ViewHolder>{

    private List<Todo> todoList;
    private MainActivity activity;

    public TodoAdapter(MainActivity activity){
        this.activity = activity;
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
        holder.task.setChecked(toBoolean(item.getStatus()));
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

    public void editItem (String Id, String Title, String Desc, String Date){
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
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        CheckBox task;
        TextView isi,desc,date;

        ViewHolder(View view){
            super(view);
            task = view.findViewById(R.id.todoCheckbox);
            isi = view.findViewById(R.id.todoIsi);
            desc = view.findViewById(R.id.todoDesc);
            date = view.findViewById(R.id.todoDate);
        }
    }

}
