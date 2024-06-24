package vn.edu.tlu.nhom7.calendar.database;


import java.util.List;

import vn.edu.tlu.nhom7.calendar.model.Task;

public interface TaskDao {
    void getTaskOfDay(String date, String idUser, FirebaseCallback callback);
    void createTask(Task task);
    void updateTask(int taskId, Task task);
    void deleteTask(int taskId);
    void fetchLastTaskId(LastTaskIdCallback callback);
    void isTaskExists(Task task, TaskExistsCallback callback);

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