package vn.edu.tlu.nhom7.calendar.activity.notification;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import vn.edu.tlu.nhom7.calendar.model.Task;

public class NotificationHelper {
    public static void setAlarm(Context context, Task task) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, NotificationReceiver.class);
        intent.putExtra("taskName", task.getTaskName());
        intent.putExtra("taskDescription", task.getTaskDescription());
        intent.putExtra("startTime", task.getStartTime());
        intent.putExtra("endTime", task.getEndTime());

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        String dateTime = task.getDate() + " " + task.getStartTime();
        try {
            Date date = sdf.parse(dateTime);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);

            switch (task.getAlarmTime()) {
                case "Thông báo đúng giờ":
                    // No adjustment needed
                    break;
                case "Thông báo trước 5 phút":
                    calendar.add(Calendar.MINUTE, -5);
                    break;
                case "Thông báo trước 15 phút":
                    calendar.add(Calendar.MINUTE, -15);
                    break;
                case "Thông báo trước 20 phút":
                    calendar.add(Calendar.MINUTE, -20);
                    break;
                case "Thông báo trước 30 phút":
                    calendar.add(Calendar.MINUTE, -30);
                    break;
                default:
                    Log.e("NotificationHelper", "Unexpected alarm time: " + task.getAlarmTime());
                    break;
            }

            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    context,
                    task.getId(),
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public static void cancelAlarm(Context context, int taskId) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                taskId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        alarmManager.cancel(pendingIntent);
    }
}
