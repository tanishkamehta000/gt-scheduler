package com.example.a2340collegescheduler;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

public class AddToDo extends AppCompatActivity {

    private EditText editTaskDescription;
    private Button btnSaveTask;
    private String originalTaskDescription = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_task);

        editTaskDescription = findViewById(R.id.editTaskDescription);
        btnSaveTask = findViewById(R.id.btnSaveTask);

        if (getIntent().hasExtra("editMode")) {
            originalTaskDescription = getIntent().getStringExtra("taskDescription");
            editTaskDescription.setText(originalTaskDescription);
        }

        btnSaveTask.setOnClickListener(view -> saveTask());
    }

    private void saveTask() {
        String taskDescription = editTaskDescription.getText().toString().trim();

        SharedPreferences prefs = getSharedPreferences("ToDoTasks", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();

        if (originalTaskDescription != null && !originalTaskDescription.isEmpty()) {
            editor.remove(originalTaskDescription);
        }

        ToDoTask task = new ToDoTask(taskDescription);
        String json = gson.toJson(task);
        editor.putString(taskDescription, json);
        editor.apply();

        finish();
    }
}
