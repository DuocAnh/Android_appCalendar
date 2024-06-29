package vn.edu.tlu.nhom7.calendar.activity.user;


import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import vn.edu.tlu.nhom7.calendar.R;
import vn.edu.tlu.nhom7.calendar.activity.MainActivity;
import vn.edu.tlu.nhom7.calendar.activity.task.ShowTaskActivity;
import vn.edu.tlu.nhom7.calendar.activity.task.TaskFragment;
import vn.edu.tlu.nhom7.calendar.database.UserDaoImpl;

public class Login extends AppCompatActivity {

    EditText edt_Email , edt_Password;
    TextView tv_Email, tv_Password, tv_ForPassword, tv_messes, tv_messes1;

    Switch sw_Remmember;
    ImageButton imgbtn_Facebook, imbtn_Google, imgbtn_Twitter, showPasswork, back;
    Button btn_Login;
    LinearLayout ll_Email, ll_passWord;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    private static final  int RC_SIGN_IN = 40;
    GoogleSignInClient googleSignInClient;
    ProgressDialog progressDialog;
    boolean isPasswordVisible = false;


    private void mapping(){
        edt_Email = findViewById(R.id.edt_emailLogin);
        edt_Password = findViewById(R.id.edt_passwordLogin);
        tv_Email = findViewById(R.id.tv_emailLogin);
        tv_Password = findViewById(R.id.tv_passwordLogin);
        tv_ForPassword = findViewById(R.id.tv_forgotPassword);
        sw_Remmember = findViewById(R.id.sw_Login);
        imgbtn_Facebook = findViewById(R.id.imbtn_facebook);
        imbtn_Google = findViewById(R.id.imbtn_google);
        imgbtn_Twitter = findViewById(R.id.imbtn_twitter);
        btn_Login = findViewById(R.id.btn_Login);
        tv_messes = findViewById(R.id.tv_message);
        tv_messes1 = findViewById(R.id.tv_message1);
        ll_Email = findViewById(R.id.ll_email);
        ll_passWord = findViewById(R.id.ll_Remmember);
        showPasswork = findViewById(R.id.img_Eye);
        back = findViewById(R.id.img_back);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        UserDaoImpl userDao = UserDaoImpl.getInstance();
        String idCurrentUser = userDao.getIdCurrentUser();
        Log.d("Iduser", idCurrentUser);
        if(idCurrentUser == "0"){

        } else {
            Intent intent = new Intent(Login.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

// Face book
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this.getApplication());

        mapping();

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();


        // google
        progressDialog = new ProgressDialog(Login.this);
        progressDialog.setTitle("Creating account");
        progressDialog.setMessage("We are creating your account");

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this,gso);

        imbtn_Google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });


        int tv = ContextCompat.getColor(this,R.color.star_color);
        int tvColor = tv_Email.getCurrentTextColor();
        int paddingBottomPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics());
        int marginTop = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,48, getResources().getDisplayMetrics());


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this,MainSignUp.class));
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

        edt_Email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() < 6){
                    tv_messes.setVisibility(TextView.VISIBLE);
                    ll_Email.setPadding(ll_Email.getPaddingLeft(), ll_Email.getPaddingTop(), ll_Email.getPaddingRight(), paddingBottomPx);
                    tv_messes.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.messes_err));
                    tv_messes.setText("* Minimum six characters");
                }
                else {
                    tv_messes.setVisibility(TextView.VISIBLE);
                    ll_Email.setPadding(ll_Email.getPaddingLeft(), ll_Email.getPaddingTop(), ll_Email.getPaddingRight(), paddingBottomPx);
                    tv_messes.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.messes_done));
                    tv_messes.setText("* Valid");
                }
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        edt_Password.addTextChangedListener(new TextWatcher() {
            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) ll_passWord.getLayoutParams();
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() < 6) {
                    layoutParams.setMargins(0, marginTop, 0, 0);
                    ll_passWord.setLayoutParams(layoutParams);
                    tv_messes1.setVisibility(TextView.VISIBLE);
                    tv_messes1.setText("* Minimum six characters");
                    tv_messes1.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.messes_err));

                } else {
                    layoutParams.setMargins(0, marginTop, 0, 0);
                    ll_passWord.setLayoutParams(layoutParams);
                    tv_messes1.setVisibility(TextView.VISIBLE);
                    tv_messes1.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.messes_done));
                    tv_messes1.setText("* Valid");
                }
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        showPasswork.setOnClickListener(v -> {
            if (isPasswordVisible) {
                edt_Password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                showPasswork.setImageResource(R.drawable.not_eye);
                isPasswordVisible = false;
            } else {
                edt_Password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                showPasswork.setImageResource(R.drawable.eye);
                isPasswordVisible = true;
            }
            edt_Password.setSelection(edt_Password.getText().length());
        });


        tv_ForPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                View dialogView = LayoutInflater.from(v.getContext()).inflate(R.layout.dialog_reset_password, null);

                Dialog dialog = new Dialog(v.getContext());
                dialog.setContentView(dialogView);
                Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);

                EditText resetMail = dialogView.findViewById(R.id.etResetMail);
                Button btnCancel = dialogView.findViewById(R.id.btnDialogCancel);
                Button btnSubmit = dialogView.findViewById(R.id.btnDialogSubmit);

                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                btnSubmit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String mail = resetMail.getText().toString().trim();

                        if (TextUtils.isEmpty(mail) || !Patterns.EMAIL_ADDRESS.matcher(mail).matches()) {
                            Toast.makeText(Login.this, "Please enter a valid email address.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        firebaseAuth.fetchSignInMethodsForEmail(mail).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                            @Override
                            public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                                if (task.isSuccessful()) {
                                    firebaseAuth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(Login.this, "Reset Link Sent To Your Email.", Toast.LENGTH_LONG).show();
                                            dialog.dismiss();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(Login.this, "Error! Reset Link is Not Sent: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                } else{
                                    Toast.makeText(Login.this, "Error checking email existence: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
                dialog.show();
            }
        });

        btn_Login.setOnClickListener(v -> {
            String email = edt_Email.getText().toString().trim();
            String password = edt_Password.getText().toString().trim();

            if(email.length() >= 6 && password.length() >= 6) {
                if (sw_Remmember.isChecked()) {
                    firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), "Login Successfully", Toast.LENGTH_SHORT).show();

                                FirebaseUser user = firebaseAuth.getCurrentUser();
                                assert user != null;
                                String userID = user.getUid();

                                // Update Password on FirebaseStore
                                DocumentReference documentReference = firebaseFirestore.collection("users").document(userID);
                                documentReference.update("fPassword", password)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.d("Messes notice update password firebase", "Password updated in Firestore");
                                                startActivity(new Intent(Login.this, MainActivity.class));
                                                finish();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(Login.this, "Error updating password: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            } else {
                                Log.d("","ERROR LOGIN");
                                Toast.makeText(Login.this, "Email or Password is Incorrect", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(Login.this, "Please enable the switch to proceed", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), "Review the character count conditions", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void signIn(){
        Intent intent = googleSignInClient.getSignInIntent();
        startActivityForResult(intent,RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN){
            Task<GoogleSignInAccount>task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuth(account.getIdToken());
            } catch (ApiException e) {
                e.printStackTrace();
                Log.d("Login to google", "Failed to sign in " + e.getMessage() );
            }
        }
    }

    private void firebaseAuth(String idToken) {
        AuthCredential authCredential = GoogleAuthProvider.getCredential(idToken, null);

        firebaseAuth.signInWithCredential(authCredential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

                        if (firebaseUser != null) {
                            String userID = firebaseUser.getUid();
                            FirebaseFirestore db = FirebaseFirestore.getInstance();

                            DocumentReference docRef = db.collection("users").document(userID);

                            docRef.get().addOnCompleteListener(docTask -> {
                                if (docTask.isSuccessful()) {
                                    DocumentSnapshot document = docTask.getResult();
                                    if (document.exists()) {
                                        Toast.makeText(Login.this, "Login success", Toast.LENGTH_LONG).show();
                                        startActivity(new Intent(Login.this, UserProfile.class));
                                    } else {
                                        Toast.makeText(Login.this, "Account is not registered", Toast.LENGTH_LONG).show();
                                        startActivity(new Intent(Login.this,MainSignUp.class));
                                    }
                                } else {
                                    Toast.makeText(Login.this, "Failed to check if user exists: " + Objects.requireNonNull(docTask.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    } else {
                        Toast.makeText(Login.this, "Authentication failed: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}