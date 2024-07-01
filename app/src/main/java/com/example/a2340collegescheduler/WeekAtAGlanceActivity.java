package com.example.a2340collegescheduler;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class WeekAtAGlanceActivity extends AppCompatActivity {

    private ListView listViewAssignments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.week_at_a_glance);

        listViewAssignments = findViewById(R.id.listViewAssignments);

        Button addClassButton = findViewById(R.id.addClassButton);
        addClassButton.setOnClickListener(view -> {
            Intent intent = new Intent(WeekAtAGlanceActivity.this, AddClass.class);
            startActivity(intent);
        });

        Button btnAddAssignment = findViewById(R.id.btnAddAssignment);
        btnAddAssignment.setOnClickListener(view -> {
            Intent intent = new Intent(WeekAtAGlanceActivity.this, AddAssignmentActivity.class);
            startActivity(intent);
        });

        Button btnAddExam = findViewById(R.id.btnAddExam);
        btnAddExam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(WeekAtAGlanceActivity.this, AddExam.class);
                startActivity(intent);
            }
        });

        Button btnAddTask = findViewById(R.id.btnAddTask);
        btnAddTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(WeekAtAGlanceActivity.this, AddToDo.class);
                startActivity(intent);
            }
        });

        populateWeekView();
        populateListView();
    }






    private void populateWeekView() {
        TableLayout weekTable = findViewById(R.id.weekTable);

        List<ClassDetails> classes = retrieveClasses();

        for (int i = weekTable.getChildCount() - 1; i > 0; i--) {
            weekTable.removeViewAt(i);
        }

        for (int i = 0; i < classes.size(); i++) {
            ClassDetails classDetail = classes.get(i);
            TableRow row = new TableRow(this);
            row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
            row.setPadding(0, 0, 0, 20);

            row.setTag(classDetail.getCourseName());


            row.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    String courseName = (String) v.getTag();
                    confirmAndDeleteClass(courseName);
                    return true;
                }
            });

            for (int j = 0; j < 7; j++) {
                TextView dayView = new TextView(this);
                dayView.setPadding(8, 8, 8, 8);
                dayView.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1f));

                if (classDetail.getDays()[j]) {
                    String displayText = classDetail.getCourseName() + "\n" + classDetail.getTime() + "\nInstructor: " + classDetail.getInstructor();
                    dayView.setText(displayText);
                } else {
                    dayView.setText("");
                }
                row.addView(dayView);
            }

            weekTable.addView(row);
        }
    }



    private void populateListView() {
        List<Assignment> assignments = retrieveAssignments();
        List<Exam> exams = retrieveExams();
        List<ToDoTask> toDoTasks = retrieveToDoTasks();
        List<String> listViewItems = new ArrayList<>();
        List<String> itemTypes = new ArrayList<>();
        for (ToDoTask task : toDoTasks) {
            listViewItems.add("Task: " + task.getTaskDescription());
            itemTypes.add("Task");
        }

        if (!toDoTasks.isEmpty()) {
            listViewItems.add("------ Assignments & Exams ------");
            itemTypes.add("Divider");
        }

        for (Assignment assignment : assignments) {
            String detail = "Assignment: " + assignment.getTitle() + " - " + assignment.getAssociatedClass() +
                    " - Due: " + assignment.getDueDate() + " " + assignment.getDueTime() + " " + assignment.getAmPm();
            listViewItems.add(detail);
            itemTypes.add("Assignment");
        }

        for (Exam exam : exams) {
            String detail = "Exam: " + exam.getName() + " - " + exam.getLocation() +
                    " - Date: " + exam.getDate() + " " + exam.getTime() + " " + exam.getAmPm();
            listViewItems.add(detail);
            itemTypes.add("Exam");
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listViewItems);
        listViewAssignments.setAdapter(adapter);

        listViewAssignments.setOnItemLongClickListener((parent, view, position, id) -> {
            String itemType = itemTypes.get(position);
            if ("Assignment".equals(itemType)) {
                String assignmentTitle = listViewItems.get(position).split(" - ")[0].replace("Assignment: ", "").trim();
                confirmAndDeleteAssignment(assignmentTitle);
            } else if ("Exam".equals(itemType)) {
                String examName = listViewItems.get(position).split(" - ")[0].replace("Exam: ", "").trim();
                confirmAndEditOrDeleteExam(examName);
            } else if ("Task".equals(itemType)) {
                String taskDescription = listViewItems.get(position).replace("Task: ", "").trim();
                confirmAndEditOrDeleteTask(taskDescription);
            }
            return true;
        });
    }








    private List<ClassDetails> retrieveClasses() {
        SharedPreferences prefs = getSharedPreferences("ClassDetails", MODE_PRIVATE);
        Map<String, ?> allEntries = prefs.getAll();
        List<ClassDetails> classDetailsList = new ArrayList<>();
        Gson gson = new Gson();

        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            String json = entry.getValue().toString();
            Type type = new TypeToken<ClassDetails>(){}.getType();
            ClassDetails classDetails = gson.fromJson(json, type);
            classDetailsList.add(classDetails);
        }

        return classDetailsList;
    }

    private List<Assignment> retrieveAssignments() {
        SharedPreferences prefs = getSharedPreferences("Assignments", MODE_PRIVATE);
        Map<String, ?> allEntries = prefs.getAll();
        List<Assignment> assignments = new ArrayList<>();
        Gson gson = new Gson();

        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            String json = entry.getValue().toString();
            Type type = new TypeToken<Assignment>(){}.getType();
            Assignment assignment = gson.fromJson(json, type);
            assignments.add(assignment);
        }

        return assignments;
    }

    private List<Exam> retrieveExams() {
        SharedPreferences prefs = getSharedPreferences("Exams", MODE_PRIVATE);
        Map<String, ?> allEntries = prefs.getAll();
        List<Exam> exams = new ArrayList<>();
        Gson gson = new Gson();

        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            String json = entry.getValue().toString();
            Type type = new TypeToken<Exam>(){}.getType();
            Exam exam = gson.fromJson(json, type);
            exams.add(exam);
        }

        return exams;
    }

    private List<ToDoTask> retrieveToDoTasks() {
        SharedPreferences prefs = getSharedPreferences("ToDoTasks", MODE_PRIVATE);
        Map<String, ?> allEntries = prefs.getAll();
        List<ToDoTask> toDoTasks = new ArrayList<>();
        Gson gson = new Gson();

        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            String json = entry.getValue().toString();
            Type type = new TypeToken<ToDoTask>(){}.getType();
            ToDoTask task = gson.fromJson(json, type);
            toDoTasks.add(task);
        }

        return toDoTasks;
    }




    private void confirmAndDeleteClass(String courseName) {
        String[] options = {"Edit", "Delete"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Option");
        builder.setItems(options, (dialog, which) -> {
            if (which == 0) {
                Intent intent = new Intent(WeekAtAGlanceActivity.this, AddClass.class);
                intent.putExtra("courseName", courseName);
                intent.putExtra("editMode", true);
                startActivity(intent);
            } else if (which == 1) {
                deleteClass(courseName);
            }
        });
        builder.show();
    }


    private void confirmAndDeleteAssignment(String assignmentTitle) {
        String[] options = {"Edit", "Delete"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Option");
        builder.setItems(options, (dialog, which) -> {
            if (which == 0) {
                Intent intent = new Intent(WeekAtAGlanceActivity.this, AddAssignmentActivity.class);
                intent.putExtra("assignmentTitle", assignmentTitle);
                intent.putExtra("editMode", true);
                startActivity(intent);
            } else if (which == 1) {
                deleteAssignment(assignmentTitle);
            }
        });
        builder.show();
    }

    private void confirmAndEditOrDeleteExam(String examName) {
        String[] options = {"Edit", "Delete"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Option");
        builder.setItems(options, (dialog, which) -> {
            if (which == 0) {
                Intent intent = new Intent(WeekAtAGlanceActivity.this, AddExam.class);
                intent.putExtra("examName", examName);
                intent.putExtra("editMode", true);
                startActivity(intent);
            } else if (which == 1) {
                deleteExam(examName);
            }
        });
        builder.show();
    }

    private void confirmAndEditOrDeleteTask(String taskDescription) {
        String[] options = {"Edit", "Delete"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Option");
        builder.setItems(options, (dialog, which) -> {
            if (which == 0) {
                Intent intent = new Intent(WeekAtAGlanceActivity.this, AddToDo.class);
                intent.putExtra("taskDescription", taskDescription);
                intent.putExtra("editMode", true);
                startActivity(intent);
            } else if (which == 1) {
                deleteTask(taskDescription);
            }
        });
        builder.show();
    }









    private void deleteClass(String courseName) {
        SharedPreferences prefs = getSharedPreferences("ClassDetails", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(courseName);
        editor.apply();
        populateWeekView();
    }

    private void deleteAssignment(String assignmentTitle) {
        SharedPreferences prefs = getSharedPreferences("Assignments", MODE_PRIVATE);
        if (prefs.contains(assignmentTitle)) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.remove(assignmentTitle);
            editor.apply();

            populateListView();
        }
    }

    private void deleteExam(String examName) {
        SharedPreferences prefs = getSharedPreferences("Exams", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(examName);
        editor.apply();

        populateListView();
    }

    private void deleteTask(String taskDescription) {
        SharedPreferences prefs = getSharedPreferences("ToDoTasks", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(taskDescription);
        editor.apply();

        populateListView();
    }



    @Override
    protected void onResume() {
        super.onResume();
        populateWeekView();
        populateListView();
    }


}
