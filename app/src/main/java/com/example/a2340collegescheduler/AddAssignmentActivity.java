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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class AddAssignmentActivity extends AppCompatActivity {

    private EditText editAssignmentTitle, editDueDate, editDueTime;
    private Spinner spinnerAMPM, spinnerAssociatedClass;
    private Button addAssignmentButton;
    private String originalAssignmentTitle = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_assignments);

        initializeUI();
        setupDatePicker();
        setupSaveButton();
        populateClassSpinner();

        if (getIntent().getBooleanExtra("editMode", false)) {
            originalAssignmentTitle = getIntent().getStringExtra("assignmentTitle");
            loadAssignmentDetails(originalAssignmentTitle);
        }
    }

    private void loadAssignmentDetails(String assignmentTitle) {
        SharedPreferences prefs = getSharedPreferences("Assignments", MODE_PRIVATE);
        String json = prefs.getString(assignmentTitle, null);
        if (json != null) {
            Gson gson = new Gson();
            Assignment assignment = gson.fromJson(json, Assignment.class);

            editAssignmentTitle.setText(assignment.getTitle());
            editDueDate.setText(assignment.getDueDate());
            editDueTime.setText(assignment.getDueTime());

        }
    }

    private void initializeUI() {
        editAssignmentTitle = findViewById(R.id.editAssignmentTitle);
        editDueDate = findViewById(R.id.editDueDate);
        editDueTime = findViewById(R.id.editAssignmentTime);
        spinnerAssociatedClass = findViewById(R.id.spinnerAssociatedClass);
        spinnerAMPM = findViewById(R.id.spinnerAMPM);
        addAssignmentButton = findViewById(R.id.addAssignmentButton);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.ampm_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAMPM.setAdapter(adapter);
    }

    private void setupDatePicker() {
        editDueDate.setOnClickListener(view -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(AddAssignmentActivity.this,
                    (datePicker, selectedYear, selectedMonth, selectedDay) -> {
                        String formattedDate = String.format("%d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay);
                        editDueDate.setText(formattedDate);
                    }, year, month, day);
            datePickerDialog.show();
        });
    }

    private void populateClassSpinner() {
        SharedPreferences prefs = getSharedPreferences("ClassDetails", MODE_PRIVATE);
        Map<String, ?> allEntries = prefs.getAll();
        List<String> classNames = new ArrayList<>();
        Gson gson = new Gson();

        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            String json = (String) entry.getValue();
            ClassDetails classDetails = gson.fromJson(json, ClassDetails.class);
            classNames.add(classDetails.getCourseName());
        }

        Spinner spinnerAssociatedClass = findViewById(R.id.spinnerAssociatedClass);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, classNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAssociatedClass.setAdapter(adapter);
    }


    private void setupSaveButton() {
        addAssignmentButton.setOnClickListener(view -> {
            saveAssignment();
        });
    }

    private void saveAssignment() {
        String title = editAssignmentTitle.getText().toString().trim();
        String dueDate = editDueDate.getText().toString().trim();
        String dueTime = editDueTime.getText().toString().trim();
        String amPm = spinnerAMPM.getSelectedItem().toString();
        String associatedClass = spinnerAssociatedClass.getSelectedItem().toString().trim();

        Assignment assignment = new Assignment(title, dueDate, dueTime, amPm, associatedClass);
        saveAssignmentToSharedPreferences(assignment);
    }

    private void saveAssignmentToSharedPreferences(Assignment assignment) {
        SharedPreferences prefs = getSharedPreferences("Assignments", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(assignment);

        if (originalAssignmentTitle != null && !originalAssignmentTitle.equals(assignment.getTitle())) {
            editor.remove(originalAssignmentTitle);
        }
        editor.putString(assignment.getTitle(), json);
        editor.apply();

        finish();
    }

}
