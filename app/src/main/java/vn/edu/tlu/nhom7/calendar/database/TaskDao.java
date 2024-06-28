package vn.edu.tlu.nhom7.calendar.database;


import java.text.ParseException;
import java.util.List;

import vn.edu.tlu.nhom7.calendar.model.Task;

public interface TaskDao {
    void getAllTasks(String idUser, getAllTasksCallBack callback);
    void getTaskOfDay(String date, String idUser, FirebaseCallback callback);
    void createTask(Task task);
    void updateTask(int taskId, Task task);
    void deleteTask(int taskId);
    void fetchLastTaskId(LastTaskIdCallback callback);
    void isTaskExists(Task task, TaskExistsCallback callback);
    public interface getAllTasksCallBack {
        void onCallback(List<Task> taskList);
    }
    public interface FirebaseCallback {
        void onCallback(List<Task> taskList);
    }
    public interface LastTaskIdCallback {
        void onCallback(int lastTaskId);
    }
    public interface TaskExistsCallback {
        void onCallback(Task task);
    }
}