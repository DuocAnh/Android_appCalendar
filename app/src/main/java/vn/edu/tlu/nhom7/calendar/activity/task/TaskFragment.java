package vn.edu.tlu.nhom7.calendar.activity.task;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import vn.edu.tlu.nhom7.calendar.R;
import vn.edu.tlu.nhom7.calendar.activity.notification.NotificationHelper;
import vn.edu.tlu.nhom7.calendar.activity.task.CreateTaskActivity;
import vn.edu.tlu.nhom7.calendar.activity.task.ShowTaskActivity;
import vn.edu.tlu.nhom7.calendar.activity.task.UpdateTaskActivity;
import vn.edu.tlu.nhom7.calendar.adapter.TaskAdapter;
import vn.edu.tlu.nhom7.calendar.database.TaskDao;
import vn.edu.tlu.nhom7.calendar.database.TaskDaoImpl;
import vn.edu.tlu.nhom7.calendar.database.UserDaoImpl;
import vn.edu.tlu.nhom7.calendar.model.Task;

public class TaskFragment extends Fragment {
    public static final String CHANNEL_ID = "1";
    private List<Task> mListTask;
    private TaskAdapter taskAdapter;
    private RecyclerView rcvTask;
    private CalendarView calendar;
    private String dateSelected, idCurrentUser;
    private Button btnCreateTask;

    public TaskFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_task, container, false);

        initUi(rootView);

        mListTask = new ArrayList<>();
        taskAdapter = new TaskAdapter(mListTask, new TaskAdapter.IClickListener() {
            @Override
            public void onClickShowItem(Task task) {
                clickShowItem(task);
            }

            @Override
            public void onClickUpdateItem(Task task) {
                clickUpdateItem(task);
            }

            @Override
            public void onClickDeleteItem(Task task) {
                deleteTask(task);
            }
        });
        rcvTask.setAdapter(taskAdapter);

        UserDaoImpl userDao = UserDaoImpl.getInstance();
        idCurrentUser = userDao.getIdCurrentUser();

        long currentDateMillis = calendar.getDate();
        Calendar calendarInstance = Calendar.getInstance();
        calendarInstance.setTimeInMillis(currentDateMillis);
        int year = calendarInstance.get(Calendar.YEAR);
        int month = calendarInstance.get(Calendar.MONTH);
        int day = calendarInstance.get(Calendar.DAY_OF_MONTH);
        dateSelected = String.format("%02d/%02d/%d", day, month + 1, year);

        getListTasksOfDay(dateSelected, idCurrentUser);

        changeDay();

        btnCreateTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CreateTaskActivity.class);
                startActivity(intent);
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Task Channel";
            String description = "Channel for task notifications";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getActivity().getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }

        return rootView;
    }

    private void initUi(View rootView) {
        rcvTask = rootView.findViewById(R.id.taskRecycler);
        btnCreateTask = rootView.findViewById(R.id.btnCreateTask);
        calendar = rootView.findViewById(R.id.calendarView);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        rcvTask.setLayoutManager(linearLayoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
        rcvTask.addItemDecoration(dividerItemDecoration);
    }

    private void changeDay() {
        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                dateSelected = String.format("%02d/%02d/%d", dayOfMonth, month + 1, year);
                Log.d("TaskFragment", "Selected date: " + dateSelected);
                Log.d("TaskFragment2", "Selected date: " + idCurrentUser);
                getListTasksOfDay(dateSelected, idCurrentUser);
            }
        });
    }

    private void getListTasksOfDay(String dateSelected, String idCurrentUser) {
        TaskDaoImpl.getInstance().getTaskOfDay(dateSelected, idCurrentUser, new TaskDao.FirebaseCallback() {
            @Override
            public void onCallback(List<Task> mListTask) {
                Collections.sort(mListTask, new Comparator<Task>() {
                    @Override
                    public int compare(Task task1, Task task2) {
                        return task1.getStartTime().compareTo(task2.getStartTime());
                    }
                });
                taskAdapter.setData(mListTask);
            }
        });
    }

    private void clickShowItem(Task task) {
        Intent intent = new Intent(getActivity(), ShowTaskActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("object_task", task);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void clickUpdateItem(Task task) {
        Intent intent = new Intent(getActivity(), UpdateTaskActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("object_task", task);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void deleteTask(Task task) {
        new AlertDialog.Builder(getActivity())
                .setTitle(getString(R.string.app_name))
                .setMessage("Bạn có chắc chắn muốn xóa bản ghi này không?")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int id  = task.getId();
                        TaskDaoImpl.getInstance().deleteTask(id);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
        int id  = task.getId();
        NotificationHelper.cancelAlarm(getActivity(), id);
    }
}
