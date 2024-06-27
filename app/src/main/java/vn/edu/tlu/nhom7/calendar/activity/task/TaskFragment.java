package vn.edu.tlu.nhom7.calendar.activity.task;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import vn.edu.tlu.nhom7.calendar.R;
import vn.edu.tlu.nhom7.calendar.activity.notification.NotificationHelper;
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
    private MaterialCalendarView calendar;
    private String dateSelected, idCurrentUser;
    private Button btnCreateTask;
    private CalendarDay selectedDate;
    private ImageView gifImageView;

    public TaskFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
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

        getDate();

        calendar.setDateSelected(selectedDate, true);

        calendar.addDecorator(new DayViewDecorator() {
            @Override
            public boolean shouldDecorate(CalendarDay day) {
                return day.equals(selectedDate);
            }

            @Override
            public void decorate(DayViewFacade view) {
                GradientDrawable drawable = new GradientDrawable();
                drawable.setColor(Color.parseColor("#6750a4"));
                drawable.setCornerRadius(8);
                view.setBackgroundDrawable(drawable);
                view.addSpan(new ForegroundColorSpan(Color.WHITE));
            }
        });

        getListTasksOfDay(dateSelected, idCurrentUser);

        changeDay();

        btnCreateTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CreateTaskActivity.class);
                intent.putExtra("key_createTask", dateSelected);
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
        gifImageView = rootView.findViewById(R.id.gifImageView);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        rcvTask.setLayoutManager(linearLayoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
        rcvTask.addItemDecoration(dividerItemDecoration);
    }

    private void getDate() {
        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra("key_date")) {
            dateSelected = intent.getExtras().getString("key_date");
            selectedDate = CalendarDay.from(
                    Integer.parseInt(dateSelected.substring(6)),
                    Integer.parseInt(dateSelected.substring(3, 5)) - 1,
                    Integer.parseInt(dateSelected.substring(0, 2))
            );
            intent.removeExtra("key_date");
        } else {
            Calendar calendarInstance = Calendar.getInstance();
            int year = calendarInstance.get(Calendar.YEAR);
            int month = calendarInstance.get(Calendar.MONTH);
            int day = calendarInstance.get(Calendar.DAY_OF_MONTH);
            dateSelected = String.format("%02d/%02d/%d", day, month + 1, year);
            selectedDate = CalendarDay.today();
        }
    }

    private void changeDay() {
        calendar.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                int year = date.getYear();
                int month = date.getMonth() + 1;
                int day = date.getDay();
                dateSelected = String.format("%02d/%02d/%d", day, month, year);
                Log.d("TaskFragment", "Selected date: " + dateSelected);

                selectedDate = date;
                calendar.invalidateDecorators();

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

                // Show or hide the GIF based on the task list
                if (mListTask == null || mListTask.isEmpty()) {
                    gifImageView.setVisibility(View.VISIBLE);
                    rcvTask.setVisibility(View.GONE);
                } else {
                    gifImageView.setVisibility(View.GONE);
                    rcvTask.setVisibility(View.VISIBLE);
                }
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
