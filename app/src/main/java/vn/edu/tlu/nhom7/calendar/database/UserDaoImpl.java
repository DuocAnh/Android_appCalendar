package vn.edu.tlu.nhom7.calendar.database;

import com.google.firebase.auth.FirebaseAuth;

public class UserDaoImpl implements UserDao {
    private FirebaseAuth firebaseAuth;
    private static UserDaoImpl instance;

    private UserDaoImpl() {
        firebaseAuth = FirebaseAuth.getInstance();
    }

    public static UserDaoImpl getInstance() {
        synchronized (UserDaoImpl.class) {
            if (instance == null) {
                instance = new UserDaoImpl();
            }
            return instance;
        }
    }

    @Override
    public String getIdCurrentUser() {
        if (firebaseAuth.getCurrentUser() != null) {
            return firebaseAuth.getCurrentUser().getUid();
        } else {
            return "0";
        }
    }
}
