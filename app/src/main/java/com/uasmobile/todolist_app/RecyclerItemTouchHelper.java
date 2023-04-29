package com.uasmobile.todolist_app;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.renderscript.Sampler;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.uasmobile.todolist_app.adapter.TodoAdapter;
import com.uasmobile.todolist_app.model.Todo;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class RecyclerItemTouchHelper extends ItemTouchHelper.SimpleCallback {

    private TodoAdapter adapter;

    private List<Todo> todoList;

    private ArrayList taskList;

    public RecyclerItemTouchHelper(TodoAdapter adapter, ArrayList taskList){
        super(0,ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        this.adapter = adapter;
        this.taskList = taskList;
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target){
        return false;
    }

    @Override
    public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction){
        final int position = viewHolder.getBindingAdapterPosition();
        String id = adapter.getItem(position).getId();
        String title = adapter.getItem(position).getTitle();
        String desc = adapter.getItem(position).getDescription();
        String date = adapter.getItem(position).getDate();
        String time = adapter.getItem(position).getTime();
        String prio = adapter.getItem(position).getPrio();

        if(direction == ItemTouchHelper.LEFT){
            AlertDialog.Builder builder = new AlertDialog.Builder(adapter.getContext());
            builder.setTitle("Delete Task");
            builder.setMessage("Are you sure you want to delete this Task?");
            builder.setPositiveButton("Yes",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            adapter.deleteItem(id);
                            Realm realm = Realm.getDefaultInstance();
                            taskList.clear();
                            RealmResults<Todo> todos = realm.where(Todo.class).findAll();
                            taskList.addAll(realm.copyFromRealm(todos));
                            adapter.notifyDataSetChanged();
                        }
                    });
            builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    adapter.notifyItemChanged(viewHolder.getBindingAdapterPosition());
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
        else {
            adapter.editItem(id, title, desc, date, time, prio);
            Intent intent = new Intent(adapter.getContext(), EditActivity.class);
            intent.putExtra("position", position);
            intent.putExtra("id", id);

//            intent.putExtra("id", todo1.getId());
            intent.putExtra("title", adapter.getItem(position).getTitle());
            intent.putExtra("desc", adapter.getItem(position).getDescription());
            intent.putExtra("date", adapter.getItem(position).getDate());
            intent.putExtra("time", adapter.getItem(position).getTime());
            intent.putExtra("prio", adapter.getItem(position).getPrio());

            adapter.getContext().startActivity(intent);
        }

    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

        Drawable icon;
        ColorDrawable background;

        View itemView = viewHolder.itemView;
        int backgroundCornerOffset = 20;

        if (dX > 0) {
            icon = ContextCompat.getDrawable(adapter.getContext(), R.drawable.edit);
            background = new ColorDrawable(ContextCompat.getColor(adapter.getContext(), R.color.darkgrey));
        } else {
            icon = ContextCompat.getDrawable(adapter.getContext(), R.drawable.delete);
            background = new ColorDrawable(Color.RED);
        }

        assert icon != null;
        int iconMargin = (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
        int iconTop = itemView.getTop() + (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
        int iconBottom = iconTop + icon.getIntrinsicHeight();

        if (dX > 0) { // Swiping to the right
            int iconLeft = itemView.getLeft() + iconMargin;
            int iconRight = itemView.getLeft() + iconMargin + icon.getIntrinsicWidth();
            icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);

            background.setBounds(itemView.getLeft(), itemView.getTop(),
                    itemView.getLeft() + ((int) dX) + backgroundCornerOffset, itemView.getBottom());
        } else if (dX < 0) { // Swiping to the left
            int iconLeft = itemView.getRight() - iconMargin - icon.getIntrinsicWidth();
            int iconRight = itemView.getRight() - iconMargin;
            icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);

            background.setBounds(itemView.getRight() + ((int) dX) - backgroundCornerOffset,
                    itemView.getTop(), itemView.getRight(), itemView.getBottom());
        } else { // view is unSwiped
            background.setBounds(0, 0, 0, 0);
        }

        background.draw(c);
        icon.draw(c);
    }
}
