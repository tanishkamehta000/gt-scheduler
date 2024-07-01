package com.example.a2340collegescheduler;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import com.google.gson.Gson;
import androidx.appcompat.app.AppCompatActivity;

public class AddClass extends AppCompatActivity {
    private EditText courseText, timeText, instructor;
    private CheckBox monday, tuesday, wednesday, thursday, friday, saturday, sunday;
    private Spinner amPm;
    private Button addButton;
    private String originalCourseName = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_classes);

        courseText = findViewById(R.id.editCourseName);
        timeText = findViewById(R.id.editTime);
        instructor = findViewById(R.id.editInstructor);
        monday = findViewById(R.id.checkMonday);
        tuesday = findViewById(R.id.checkTuesday);
        wednesday = findViewById(R.id.checkWednesday);
        thursday = findViewById(R.id.checkThursday);
        friday = findViewById(R.id.checkFriday);
        saturday = findViewById(R.id.checkSaturday);
        sunday = findViewById(R.id.checkSunday);
        amPm = findViewById(R.id.spinnerAMPM);
        addButton = findViewById(R.id.addClassButton);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.ampm_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        amPm.setAdapter(adapter);

        addButton.setOnClickListener(v -> {
            saveClass();
            finish();
        });

        if (getIntent().getBooleanExtra("editMode", false)) {
            originalCourseName = getIntent().getStringExtra("courseName");
            loadClassDetailsForEdit(originalCourseName);
        }
    }

    private void loadClassDetailsForEdit(String courseName) {
        SharedPreferences prefs = getSharedPreferences("ClassDetails", MODE_PRIVATE);
        String json = prefs.getString(courseName, null);
        if (json != null) {
            Gson gson = new Gson();
            ClassDetails classDetails = gson.fromJson(json, ClassDetails.class);

            courseText.setText(classDetails.getCourseName());
            instructor.setText(classDetails.getInstructor());
        }
    }

    private void saveClass() {
        String newCourseName = courseText.getText().toString().trim();
        String time = timeText.getText().toString().trim() + " " + amPm.getSelectedItem().toString();
        String instructorName = instructor.getText().toString().trim();
        boolean[] days = {
                monday.isChecked(),
                tuesday.isChecked(),
                wednesday.isChecked(),
                thursday.isChecked(),
                friday.isChecked(),
                saturday.isChecked(),
                sunday.isChecked()
        };

        ClassDetails classDetails = new ClassDetails(newCourseName, time, instructorName, days);

        SharedPreferences prefs = getSharedPreferences("ClassDetails", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(classDetails);


        if (originalCourseName != null && !originalCourseName.equals(newCourseName)) {
            editor.remove(originalCourseName);
        }
        editor.putString(newCourseName, json);
        editor.apply();
        finish();
    }

    private void clearFields() {
        courseText.setText("");
        timeText.setText("");
        instructor.setText("");
        monday.setChecked(false);
        tuesday.setChecked(false);
        wednesday.setChecked(false);
        thursday.setChecked(false);
        friday.setChecked(false);
        saturday.setChecked(false);
        sunday.setChecked(false);
        amPm.setSelection(0);
    }
}
