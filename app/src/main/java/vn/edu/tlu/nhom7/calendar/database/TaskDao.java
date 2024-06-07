package vn.edu.tlu.nhom7.calendar.database;


import java.util.List;

import vn.edu.tlu.nhom7.calendar.model.Task;

public interface TaskDao {
    void getTaskOfDay(String date, FirebaseCallback callback);
    void createTask(Task task);
    void updateTask(int taskId, Task task);
    void deleteTask(int taskId);

    interface FirebaseCallback {
        void onCallback(List<Task> taskList);
    }
    public interface LastTaskIdCallback {
        void onCallback(int lastTaskId);
    }
}