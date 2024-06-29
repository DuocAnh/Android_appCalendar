package vn.edu.tlu.nhom7.calendar.activity.user;

import android.content.Intent;
import android.util.Log;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import java.util.HashMap;
import java.util.Map;

import vn.edu.tlu.nhom7.calendar.R;
import vn.edu.tlu.nhom7.calendar.activity.MainActivity;

public class Sign_Up extends AppCompatActivity {

    TextView tv_Name, tv_Email, tv_Password, messes, messes1, messes2;
    EditText edt_Name, edt_Email, edt_Password;
    ImageButton img_Showpass, callback;
    Button btn_SignUp;
    boolean isPasswordVisible = false;
    private FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    String userID;

    private static final String TAG = "Sign_Up Verification Email Sent : ";

    private void Mapping(){
        tv_Name = findViewById(R.id.tv_nameSignUp);
        tv_Email = findViewById(R.id.tv_emailSignup);
        tv_Password = findViewById(R.id.tv_passwordSignUp);
        edt_Name = findViewById(R.id.edt_nameSignUp);
        edt_Email = findViewById(R.id.edt_emailSignUp);
        edt_Password = findViewById(R.id.edt_passwordSignUp);
        img_Showpass = findViewById(R.id.img_Showpass);
        btn_SignUp = findViewById(R.id.btn_SignUp);
        messes = findViewById(R.id.tv_message);
        messes1 = findViewById(R.id.tv_message1);
        messes2 = findViewById(R.id.tv_message2);
        callback = findViewById(R.id.img_backSignup);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Mapping();

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        if (firebaseAuth.getCurrentUser() != null){
            Toast.makeText(this, "Dang ki roi", Toast.LENGTH_LONG).show();
        }

        int tv = ContextCompat.getColor(this,R.color.star_color);
        int tvColor = tv_Email.getCurrentTextColor();
        String Name = edt_Name.getText().toString().trim();


        callback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Sign_Up.this,MainSignUp.class));
            }
        });

        edt_Name.setOnFocusChangeListener((v, hasFocus) -> {
            if(hasFocus) {
                tv_Name.setTextColor(tv);
            }else {
                tv_Name.setTextColor(tvColor);
            }
        });
        edt_Email.setOnFocusChangeListener((v, hasFocus) -> {
            if(hasFocus) {
                tv_Email.setTextColor(tv);
            }else {
                tv_Email.setTextColor(tvColor);
            }
        });
        edt_Password.setOnFocusChangeListener((v, hasFocus) -> {
            if(hasFocus) {
                tv_Password.setTextColor(tv);
            }else {
                tv_Password.setTextColor(tvColor);
            }
        });

        img_Showpass.setOnClickListener(v -> {
            if (isPasswordVisible) {
                edt_Password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                img_Showpass.setImageResource(R.drawable.not_eye);
                isPasswordVisible = false;
            } else {
                edt_Password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                img_Showpass.setImageResource(R.drawable.eye);
                isPasswordVisible = true;
            }
            edt_Password.setSelection(edt_Password.getText().length());
        });

        edt_Name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() < 6){
                    messes.setVisibility(TextView.VISIBLE);
                    messes.setTextColor(getResources().getColor(R.color.messes_err));
                    messes.setText("* Minimum six characters");
                }
                else {
                    messes.setVisibility(TextView.VISIBLE);
                    messes.setTextColor(getResources().getColor(R.color.messes_done));
                    messes.setText("* Valid");
                }
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        edt_Email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() < 6){
                    messes1.setVisibility(TextView.VISIBLE);
                    messes1.setTextColor(getResources().getColor(R.color.messes_err));
                    messes1.setText("* Minimum six characters");
                }
                else {
                    messes1.setVisibility(TextView.VISIBLE);
                    messes1.setTextColor(getResources().getColor(R.color.messes_done));
                    messes1.setText("* Valid");
                }
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        edt_Password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() < 6){
                    messes2.setVisibility(TextView.VISIBLE);
                    messes2.setTextColor(getResources().getColor(R.color.messes_err));
                    messes2.setText("* Minimum six characters");
                }
                else {
                    messes2.setVisibility(TextView.VISIBLE);
                    messes2.setTextColor(getResources().getColor(R.color.messes_done));
                    messes2.setText("* Valid");
                }
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        btn_SignUp.setOnClickListener(v -> {
            String email = edt_Email.getText().toString().trim();
            String password = edt_Password.getText().toString().trim();
            String name = edt_Name.getText().toString();

            if(edt_Email.length() >= 6 && edt_Password.length() >=6 ){
                firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            // xac thu email chinh chu
                            FirebaseUser  firebaseUser = firebaseAuth.getCurrentUser();
                            firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(Sign_Up.this, "Verification Email Has been Sent.", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG,"onFailure: Email not sent " + e.getMessage());
                                }
                            });

                            Toast.makeText(Sign_Up.this, "User created", Toast.LENGTH_SHORT).show();
                            userID = firebaseAuth.getCurrentUser().getUid();
                            DocumentReference documentReference = firebaseFirestore.collection("users").document(userID);
                            Map<String,Object> user = new HashMap<>();
                            user.put("fName",name);
                            user.put("fEmail",email);
                            user.put("fPassword",password);
                            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(Sign_Up.this, "onSuccess: user Profile is created for " + userID, Toast.LENGTH_LONG).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(Sign_Up.this, "onFailure" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });

                            startActivity(new Intent(Sign_Up.this, MainActivity.class));

                        }else {
                            Toast.makeText(Sign_Up.this, "Error \n" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }else{
                Toast.makeText(this, "Review the character count conditions", Toast.LENGTH_SHORT).show();
            }
        });
    }
}