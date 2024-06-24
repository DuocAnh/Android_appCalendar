package vn.edu.tlu.nhom7.calendar.database;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import vn.edu.tlu.nhom7.calendar.model.Task;

public class TaskDaoImpl implements TaskDao {
    private FirebaseFirestore db;
    private CollectionReference tasksCollection;
    private static TaskDaoImpl instance;

    public TaskDaoImpl() {
        db = FirebaseFirestore.getInstance();
        tasksCollection = db.collection("Tasks");
    }

    public static TaskDaoImpl getInstance() {
        synchronized (TaskDaoImpl.class) {
            if (instance == null) {
                instance = new TaskDaoImpl();
            }
            return instance;
        }
    }

    public void getTaskOfDay(String date, String idUser, final FirebaseCallback callback) {
        tasksCollection.whereEqualTo("date", date)
                .whereEqualTo("idCurrentUser", idUser)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w("getTaskOfDay", "Listen failed.", e);
                            return;
                        }

                        List<Task> dbListTask = new ArrayList<>();
                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            Task task = documentSnapshot.toObject(Task.class);
                            dbListTask.add(task);
                        }
                        callback.onCallback(dbListTask);
                    }
                });
    }

    @Override
    public void createTask(Task task) {
        String taskId = String.valueOf(task.getId());
        tasksCollection.document(taskId).set(task)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
    }

    @Override
    public void updateTask(int taskId, Task task) {
        String taskIdStr = String.valueOf(taskId);
        tasksCollection.document(taskIdStr).set(task)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
    }

    @Override
    public void deleteTask(int taskId) {
        String taskIdStr = String.valueOf(taskId);
        tasksCollection.document(taskIdStr).delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
    }

    public void fetchLastTaskId(final LastTaskIdCallback callback) {
        tasksCollection.orderBy("id", Query.Direction.DESCENDING).limit(1)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            DocumentSnapshot lastDocument = queryDocumentSnapshots.getDocuments().get(0);
                            String lastTaskId = lastDocument.getId();
                            callback.onCallback(Integer.parseInt(lastTaskId));
                        } else {
                            callback.onCallback(0); // No documents found
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
    }

    public void isTaskExists(Task task, final TaskExistsCallback callback) {
        tasksCollection.whereEqualTo("taskName", task.getTaskName())
                .whereEqualTo("taskDescription", task.getTaskDescription())
                .whereEqualTo("date", task.getDate())
                .whereEqualTo("startTime", task.getStartTime())
                .whereEqualTo("endTime", task.getEndTime())
                .whereEqualTo("alarmTime", task.getAlarmTime())
                .whereEqualTo("color", task.getColor())
                .whereEqualTo("location", task.getLocation())
                .whereEqualTo("idCurrentUser", task.getIdCurrentUser())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                            Task existingTask = documentSnapshot.toObject(Task.class);
                            callback.onCallback(existingTask);
                        } else {
                            callback.onCallback(null);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        callback.onCallback(null);
                    }
                });
    }
}