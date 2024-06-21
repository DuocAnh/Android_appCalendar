package vn.edu.tlu.nhom7.calendar.activity.task;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import vn.edu.tlu.nhom7.calendar.R;
import vn.edu.tlu.nhom7.calendar.adapter.TaskAdapter;
import vn.edu.tlu.nhom7.calendar.database.TaskDao;
import vn.edu.tlu.nhom7.calendar.database.TaskDaoImpl;
import vn.edu.tlu.nhom7.calendar.model.Task;

public class TaskActivity extends AppCompatActivity {
    private List<Task> mListTask;
    private TaskAdapter taskAdapter;
    private RecyclerView rcvTask;
    private CalendarView calendar;
    private String dateSelected;
    private Button btnCreateTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_task);

        initUi();

        mListTask = new ArrayList<>();
        taskAdapter = new TaskAdapter(mListTask, new TaskAdapter.IClickListener() {
            @Override
            public void onClickShowItem(Task task) {
                ClickShowItem(task);
            }

            @Override
            public void onClickUpdateItem(Task task) {
                ClickUpdateItem(task);
            }

            @Override
            public void onClickDeleteItem(Task task) {
                deleteTask(task);
            }
        });
        rcvTask.setAdapter(taskAdapter);

        long currentDateMillis = calendar.getDate();
        Calendar calendarInstance = Calendar.getInstance();
        calendarInstance.setTimeInMillis(currentDateMillis);
        int year = calendarInstance.get(Calendar.YEAR);
        int month = calendarInstance.get(Calendar.MONTH);
        int day = calendarInstance.get(Calendar.DAY_OF_MONTH);
        dateSelected = String.format("%02d/%02d/%d", day, month + 1, year);

        getListTasksOfDay(dateSelected);

        changeDay();

        btnCreateTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TaskActivity.this, CreateTaskActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initUi() {
        rcvTask = findViewById(R.id.taskRecycler);
        btnCreateTask = findViewById(R.id.btnCreateTask);
        calendar = findViewById(R.id.calendarView);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rcvTask.setLayoutManager(linearLayoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        rcvTask.addItemDecoration(dividerItemDecoration);
    }

    private void changeDay() {
        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                dateSelected = String.format("%02d/%02d/%d", dayOfMonth, month + 1, year);
                Log.d("TaskActivity", "Selected date: " + dateSelected);
                getListTasksOfDay(dateSelected);
            }
        });
    }

    private void getListTasksOfDay(String dateSelected) {
        TaskDaoImpl.getInstance().getTaskOfDay(dateSelected, new TaskDao.FirebaseCallback() {
            @Override
            public void onCallback(List<Task> mListTask) {
                taskAdapter.setData(mListTask);
            }
        });
    }

    private void ClickShowItem(Task task) {
        Intent intent = new Intent(this, ShowTaskActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("object_task", task);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void ClickUpdateItem(Task task) {
        Intent intent = new Intent(this, UpdateTaskActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("object_task", task);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void deleteTask(Task task) {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.app_name))
                .setMessage("Bạn có chắc chắn muốn xóa bản ghi này không?")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int id  = task.getId();
                        TaskDaoImpl.getInstance().deleteTask(id);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}