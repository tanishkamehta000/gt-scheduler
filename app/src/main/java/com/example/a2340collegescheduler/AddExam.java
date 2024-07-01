package com.example.a2340collegescheduler;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import androidx.appcompat.app.AppCompatActivity;
import com.google.gson.Gson;

import java.util.Calendar;

public class AddExam extends AppCompatActivity {

    private EditText editExamName, editLocation, editExamDate, editExamTime;
    private Spinner spinnerExamAMPM;
    private Button addExamButton;
    private String originalExamName = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_exam);

        initializeUI();
        setupDatePicker();
        setupSaveButton();

        if (getIntent().getBooleanExtra("editMode", false)) {
            originalExamName = getIntent().getStringExtra("examName");
            loadExamDetails(originalExamName);
        }
    }

    private void loadExamDetails(String examName) {
        SharedPreferences prefs = getSharedPreferences("Exams", MODE_PRIVATE);
        String json = prefs.getString(examName, null);
        if (json != null) {
            Gson gson = new Gson();
            Exam exam = gson.fromJson(json, Exam.class);

            editExamName.setText(exam.getName());
            editLocation.setText(exam.getLocation());
            editExamDate.setText(exam.getDate());
        }
    }

    private void initializeUI() {
        editExamName = findViewById(R.id.editExamName);
        editLocation = findViewById(R.id.editLocation);
        editExamDate = findViewById(R.id.editExamDate);
        editExamTime = findViewById(R.id.editExamTime);
        spinnerExamAMPM = findViewById(R.id.spinnerExamAMPM);
        addExamButton = findViewById(R.id.addExamButton);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.ampm_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerExamAMPM.setAdapter(adapter);
    }

    private void setupDatePicker() {
        editExamDate.setOnClickListener(view -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(AddExam.this,
                    (datePicker, selectedYear, selectedMonth, selectedDay) -> {
                        String formattedDate = String.format("%d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay);
                        editExamDate.setText(formattedDate);
                    }, year, month, day);
            datePickerDialog.show();
        });
    }

    private void setupSaveButton() {
        addExamButton.setOnClickListener(view -> saveExam());
    }

    private void saveExam() {
        String name = editExamName.getText().toString().trim();
        String location = editLocation.getText().toString().trim();
        String date = editExamDate.getText().toString().trim();
        String time = editExamTime.getText().toString().trim();
        String amPm = spinnerExamAMPM.getSelectedItem().toString();

        Exam exam = new Exam(name, location, date, time, amPm);
        saveExamToSharedPreferences(exam);
    }

    private void saveExamToSharedPreferences(Exam exam) {
        SharedPreferences prefs = getSharedPreferences("Exams", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(exam);
        if (originalExamName != null && !originalExamName.equals(exam.getName())) {
            editor.remove(originalExamName);
        }
        editor.putString(exam.getName(), json);
        editor.apply();

        finish();
    }
}
