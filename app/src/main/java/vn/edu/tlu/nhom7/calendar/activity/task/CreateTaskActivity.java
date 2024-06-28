package vn.edu.tlu.nhom7.calendar.activity.task;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.CountDownLatch;

import vn.edu.tlu.nhom7.calendar.R;
import vn.edu.tlu.nhom7.calendar.activity.MainActivity;
import vn.edu.tlu.nhom7.calendar.activity.notification.NotificationHelper;
import vn.edu.tlu.nhom7.calendar.database.TaskDao;
import vn.edu.tlu.nhom7.calendar.database.TaskDaoImpl;
import vn.edu.tlu.nhom7.calendar.database.UserDao;
import vn.edu.tlu.nhom7.calendar.database.UserDaoImpl;
import vn.edu.tlu.nhom7.calendar.model.Task;

public class CreateTaskActivity extends AppCompatActivity {
    private EditText etTaskName, etTaskDescription, etDate,
            etStartTime, etEndTime, etLocation;
    private Spinner spAlarmTime, spColor;
    private ImageView imageColor;
    private int id;
    private String idCurrentUser;
    private Button btnCreateTask;
    private ImageButton img_buttonback;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_task);

        initUi();

        etDate.setOnClickListener(view -> showDatePickerDialog(this, etDate));
        etStartTime.setOnClickListener(view -> showTimePickerDialog(this, etStartTime));
        etEndTime.setOnClickListener(view -> showTimePickerDialog(this, etEndTime));
        showAlarmTime();
        showColor();

        getNextId();
        UserDaoImpl userDao = UserDaoImpl.getInstance();
        idCurrentUser = userDao.getIdCurrentUser();

        setupBackButton();
        btnCreateTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTask();
            }
        });
    }

    private void initUi() {
        etTaskName = findViewById(R.id.etTaskName);
        etTaskDescription = findViewById(R.id.etTaskDescription);
        etDate = findViewById(R.id.etDate);
        etStartTime = findViewById(R.id.etStartTime);
        etEndTime = findViewById(R.id.etEndTime);
        spAlarmTime = findViewById(R.id.spAlarmTime);
        spColor = findViewById(R.id.spColor);
        etLocation = findViewById(R.id.etLocation);
        btnCreateTask = findViewById(R.id.btnCreateTask);
        imageColor = findViewById(R.id.imageColor);
        img_buttonback = findViewById(R.id.img_buttonback);

        etTaskName.setFilters(new InputFilter[] { new InputFilter.LengthFilter(100) });
        etTaskDescription.setFilters(new InputFilter[] { new InputFilter.LengthFilter(200) });
        etLocation.setFilters(new InputFilter[] { new InputFilter.LengthFilter(100) });

        Intent intent = getIntent();
        if (intent != null) {
            String date = intent.getStringExtra("key_createTask");
            if (date != null) {
                etDate.setText(date);
            }
        }
    }

    private void addTask() {
        String strTaskName = etTaskName.getText().toString().trim();
        String strTaskDescription = etTaskDescription.getText().toString().trim();
        String strDate = etDate.getText().toString().trim();
        String strStartTime = etStartTime.getText().toString().trim();
        String strEndTime = etEndTime.getText().toString().trim();
        String strAlarmTime = spAlarmTime.getSelectedItem().toString().trim();
        String strColor = spColor.getSelectedItem().toString().trim();
        String strLocation = etLocation.getText().toString().trim();

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        try {
            Date startTime = sdf.parse(strStartTime);
            Date endTime = sdf.parse(strEndTime);

            if (TextUtils.isEmpty(strTaskName) || TextUtils.isEmpty(strTaskDescription) || TextUtils.isEmpty(strDate) || TextUtils.isEmpty(strStartTime) || TextUtils.isEmpty(strEndTime) || TextUtils.isEmpty(strAlarmTime)) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ các trường", Toast.LENGTH_SHORT).show();
            } else if (startTime != null && endTime != null && !endTime.after(startTime)) {
                Toast.makeText(this, "Thời gian kết thúc phải sau thời gian bắt đầu", Toast.LENGTH_SHORT).show();
            } else {
                Task task = new Task(id, strTaskName, strTaskDescription, strDate, strStartTime, strEndTime, strAlarmTime, strColor, strLocation, idCurrentUser);

                TaskDaoImpl taskDao = TaskDaoImpl.getInstance();
                taskDao.isTaskExists(task, new TaskDao.TaskExistsCallback() {
                    @Override
                    public void onCallback(Task existingTask) {
                        if (existingTask != null) {
                            Toast.makeText(CreateTaskActivity.this, "Bản ghi đã tồn tại", Toast.LENGTH_SHORT).show();
                        } else {
                            taskDao.createTask(task);
                            Toast.makeText(CreateTaskActivity.this, "Thêm công việc thành công", Toast.LENGTH_SHORT).show();

                            NotificationHelper.setAlarm(CreateTaskActivity.this, task);

                            Intent intent = new Intent(CreateTaskActivity.this, MainActivity.class);
                            intent.putExtra("key_task", "task");
                            intent.putExtra("key_date", strDate);
                            startActivity(intent);
                            finish();
                        }
                    }
                });
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void getNextId() {
        TaskDaoImpl taskDao = TaskDaoImpl.getInstance();
        taskDao.fetchLastTaskId(new TaskDaoImpl.LastTaskIdCallback() {
            @Override
            public void onCallback(int lastTaskId) {
                id = lastTaskId + 1;
            }
        });
    }

    public static void showDatePickerDialog(Context context, final EditText dateEditText) {
        final Calendar c = Calendar.getInstance();
        String dateString = dateEditText.getText().toString();
        if (!dateString.isEmpty()) {
            try {
                String[] dateParts = dateString.split("/");
                int day = Integer.parseInt(dateParts[0]);
                int month = Integer.parseInt(dateParts[1]) - 1;
                int year = Integer.parseInt(dateParts[2]);
                c.set(year, month, day);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                (view, year1, monthOfYear, dayOfMonth) -> {
                    // Set the selected date on the EditText

                    String selectedDate = String.format("%02d/%02d/%d", dayOfMonth, monthOfYear + 1, year1);

                    dateEditText.setText(selectedDate);
                }, year, month, day);
        datePickerDialog.show();
    }

    public static void showTimePickerDialog(Context context, final EditText timeEditText) {
        final Calendar c = Calendar.getInstance();
        String timeString = timeEditText.getText().toString();
        if (!timeString.isEmpty()) {
            try {
                String[] timeParts = timeString.split(":");
                int hour = Integer.parseInt(timeParts[0]);
                int minute = Integer.parseInt(timeParts[1]);
                c.set(Calendar.HOUR_OF_DAY, hour);
                c.set(Calendar.MINUTE, minute);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(context,
                (view, hourOfDay, minute1) -> {
                    String selectedTime = String.format("%02d:%02d", hourOfDay, minute1);
                    timeEditText.setText(selectedTime);
                }, hour, minute, true);
        timePickerDialog.show();
    }

    private void showAlarmTime() {
        String[] alarmTime = {"Thông báo đúng giờ", "Thông báo trước 5 phút", "Thông báo trước 15 phút", "Thông báo trước 20 phút", "Thông báo trước 30 phút"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, alarmTime);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spAlarmTime.setAdapter(adapter);
    }

    private void showColor() {
        String[] colors = {"Công việc", "Học tập", "Giải trí", "Việc quan trọng"};
        int[] colorDrawables = {
                R.drawable.ic_task_cl_blue,
                R.drawable.ic_task_cl_green,
                R.drawable.ic_task_cl_yellow,
                R.drawable.ic_task_cl_red
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, colors);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spColor.setAdapter(adapter);

        spColor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                imageColor.setImageResource(colorDrawables[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }
    private void setupBackButton() {
        img_buttonback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}