package vn.edu.tlu.nhom7.calendar;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.auth.User;

public class UserProfile extends AppCompatActivity {

    TextView tvName, tvEmail, tvPassword, tvmessageVerification, tvresetPasswordYP;
    ImageView imageUser;
    Button btnlogOut, btnmessageVerification;

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    FirebaseUser user;

    String userID;
    private static final String TAG = "User_Profile Verification Email Sent : ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tvName = findViewById(R.id.tvNameUser);
        tvEmail = findViewById(R.id.tvEmailUser);
        tvPassword = findViewById(R.id.tvPasswordUser);
        tvmessageVerification = findViewById(R.id.tvMessageVerification);
        btnmessageVerification = findViewById(R.id.btnMessageVerification);
        tvresetPasswordYP = findViewById(R.id.tv_resetPasswordYP);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        userID = firebaseAuth.getCurrentUser().getUid();

        user = firebaseAuth.getCurrentUser();

        if (!user.isEmailVerified()){
            tvmessageVerification.setVisibility(View.VISIBLE);
            btnmessageVerification.setVisibility(View.VISIBLE);

            btnmessageVerification.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FirebaseUser  user = firebaseAuth.getCurrentUser();
                    user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(UserProfile.this, "Verification Email Has been Sent.", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG,"onFailure: Email not sent " + e.getMessage());
                        }
                    });
                }
            });
        }

        DocumentReference documentReference = firebaseFirestore.collection("users").document(userID);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value != null && value.exists()) {
                    tvName.setText(value.getString("fName"));
                    tvEmail.setText(value.getString("fEmail"));
                    tvPassword.setText(value.getString("fPassword"));
                } else {
                    Toast.makeText(UserProfile.this, "Document does not exist!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        tvresetPasswordYP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                View dialogView = LayoutInflater.from(v.getContext()).inflate(R.layout.dialog_reset_password1, null);

                Dialog dialog = new Dialog(v.getContext());
                dialog.setContentView(dialogView);
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

                final EditText resetPassword = dialogView.findViewById(R.id.etResetMail);
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
                        String newPassword = resetPassword.getText().toString().trim();

                        if (TextUtils.isEmpty(newPassword)) {
                            Toast.makeText(UserProfile.this, "Please enter a new password.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        user.updatePassword(newPassword).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                // Update the password in Firestore
                                DocumentReference documentReference = firebaseFirestore.collection("users").document(userID);
                                documentReference.update("fPassword", newPassword).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(UserProfile.this, "Password Successfully Updated.", Toast.LENGTH_SHORT).show();
                                        tvPassword.setText(newPassword);
                                        dialog.dismiss();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(UserProfile.this, "Password Update in Firestore Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(UserProfile.this, "Password Reset Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
                dialog.show();
            }
        });

        btnlogOut = findViewById(R.id.btnlogOutUser);
        btnlogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getApplicationContext(), MainSignUp.class));
                Toast.makeText(getApplicationContext(), "Logout Success", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}

