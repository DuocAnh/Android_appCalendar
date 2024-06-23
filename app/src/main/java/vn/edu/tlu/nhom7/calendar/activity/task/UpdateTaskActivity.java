package vn.edu.tlu.nhom7.calendar.activity.task;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import vn.edu.tlu.nhom7.calendar.R;
import vn.edu.tlu.nhom7.calendar.database.TaskDaoImpl;
import vn.edu.tlu.nhom7.calendar.model.Task;

public class UpdateTaskActivity extends AppCompatActivity {
    private EditText etTaskName, etTaskDescription, etDate,
            etStartTime, etEndTime, etLocation;
    private Spinner spAlarmTime, spColor;
    private ImageView imageColor;
    private int id;
    private Button btnUpdateTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_update_task);

        initUi();

        etDate.setOnClickListener(view -> CreateTaskActivity.showDatePickerDialog(this, etDate));
        etStartTime.setOnClickListener(view -> CreateTaskActivity.showTimePickerDialog(this,etStartTime));
        etEndTime.setOnClickListener(view -> CreateTaskActivity.showTimePickerDialog(this,etEndTime));
        showAlarmTime();
        showColor();

        getTask();

        btnUpdateTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTask();
            }
        });

    }

    public void initUi() {
        etTaskName = findViewById(R.id.etTaskName);
        etTaskDescription = findViewById(R.id.etTaskDescription);
        etDate = findViewById(R.id.etDate);
        etStartTime = findViewById(R.id.etStartTime);
        etEndTime = findViewById(R.id.etEndTime);
        etLocation = findViewById(R.id.etLocation);
        spAlarmTime = findViewById(R.id.spAlarmTime);
        spColor = findViewById(R.id.spColor);
        btnUpdateTask = findViewById(R.id.btnUpdateTask);
        imageColor = findViewById(R.id.imageColor);
    }

    private void getTask() {
        Task task = (Task) getIntent().getExtras().get("object_task");
        if (task != null) {
            id = task.getId();
            etTaskName.setText(task.getTaskName());
            etTaskDescription.setText(task.getTaskDescription());
            etDate.setText(task.getDate());
            etStartTime.setText(task.getStartTime());
            etEndTime.setText(task.getEndTime());
            etLocation.setText(task.getLocation());

            String[] alarmTimeOp = {"Thông báo đúng giờ", "Thông báo trước 5 phút", "Thông báo trước 15 phút", "Thông báo trước 20 phút", "Thông báo trước 30 phút"};
            for (int i = 0; i < alarmTimeOp.length; i++) {
                if (alarmTimeOp[i].equals(task.getAlarmTime())) {
                    spAlarmTime.setSelection(i);
                    break;
                }
            }
            String[] colors = {"Xanh dương", "Xanh lục", "Vàng", "Đỏ"};
            int[] colorDrawables = {
                    R.drawable.ic_task_cl_blue,
                    R.drawable.ic_task_cl_green,
                    R.drawable.ic_task_cl_yellow,
                    R.drawable.ic_task_cl_red
            };
            for (int i = 0; i < colors.length; i++) {
                if (colors[i].equals(task.getColor())) {
                    spColor.setSelection(i);
                    imageColor.setImageResource(colorDrawables[i]);
                    break;
                }
            }
        }
    }

    private void editTask() {
        String strTaskName = etTaskName.getText().toString().trim();
        String strTaskDescription = etTaskDescription.getText().toString().trim();
        String strDate = etDate.getText().toString().trim();
        String strStartTime = etStartTime.getText().toString().trim();
        String strEndTime = etEndTime.getText().toString().trim();
        String strAlarmTime = spAlarmTime.getSelectedItem().toString().trim();
        String strColor = spColor.getSelectedItem().toString().trim();
        String strLocation = etLocation.getText().toString().trim();

        if (TextUtils.isEmpty(strTaskName) || TextUtils.isEmpty(strTaskDescription) || TextUtils.isEmpty(strDate) || TextUtils.isEmpty(strStartTime) || TextUtils.isEmpty(strEndTime) || TextUtils.isEmpty(strAlarmTime)) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ các trường", Toast.LENGTH_SHORT).show();
        }else {
            Task task = new Task(id, strTaskName, strTaskDescription, strDate, strStartTime, strEndTime, strAlarmTime, strColor, strLocation);
            TaskDaoImpl db = new TaskDaoImpl();
            db.updateTask(id, task);

            Toast.makeText(this, "Sửa thông tin công việc thành công", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(UpdateTaskActivity.this, TaskActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void showAlarmTime() {
        String[] alarmTime = {"Thông báo đúng giờ", "Thông báo trước 5 phút", "Thông báo trước 15 phút", "Thông báo trước 20 phút", "Thông báo trước 30 phút"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, alarmTime);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spAlarmTime.setAdapter(adapter);
    }

    private void showColor() {
        String[] colors = {"Xanh dương", "Xanh lục", "Vàng", "Đỏ"};
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
}