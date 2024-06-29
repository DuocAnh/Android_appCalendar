package vn.edu.tlu.nhom7.calendar.activity.user;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

import java.util.Objects;

import vn.edu.tlu.nhom7.calendar.R;
import vn.edu.tlu.nhom7.calendar.activity.MainActivity;

public class UserProfile extends AppCompatActivity {

    TextView tvName, tvEmail, tvPassword, tvmessageVerification, tvresetPasswordYP,tvaddImageYP;
    ImageView imageUser;
    Button btnlogOut, btnmessageVerification;
    ImageButton btnimgback;

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    StorageReference storageReference;
    FirebaseUser user;
    String userID;

    private void mapping(){
        tvName = findViewById(R.id.tvNameUser);
        tvEmail = findViewById(R.id.tvEmailUser);
        tvPassword = findViewById(R.id.tvPasswordUser);
        tvmessageVerification = findViewById(R.id.tvMessageVerification);
        btnmessageVerification = findViewById(R.id.btnMessageVerification);
        tvresetPasswordYP = findViewById(R.id.tv_resetPasswordYP);
        imageUser = findViewById(R.id.imageUser);
        tvaddImageYP = findViewById(R.id.tv_addImageYP);
        btnlogOut = findViewById(R.id.btnlogOutUser);
        btnimgback = findViewById(R.id.img_buttonback);

    }
    private static final String TAG = "User_Profile Verification Email Sent : ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_profile);

// Face bôk
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this.getApplication());


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mapping();

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

//        Intent intent = getIntent();
//        String userID = intent.getStringExtra("userID");

        Intent intent = getIntent();
        String photoUrl = intent.getStringExtra("photoUrl");

        if (photoUrl != null) {
            Glide.with(this)
                    .load(photoUrl)
                    .into(imageUser);
        } else {
            Toast.makeText(this, "No profile photo available", Toast.LENGTH_SHORT).show();
        }

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

        // update password btn reset password to firebase and mapping password profile user
        DocumentReference documentReference = firebaseFirestore.collection("users").document(userID);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value != null && value.exists()) {
                    tvName.setText(value.getString("fName"));
                    tvEmail.setText(value.getString("fEmail"));
                    tvPassword.setText(value.getString("fPassword"));
                } else {
                    Log.d("ERROR UPDATE password in userprofile", "error email or password");
                }
            }
        });

        // Reset password
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

        // add Image
        tvaddImageYP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(openGalleryIntent, 1000);
            }
        });

        // store Image User
        StorageReference profileRef = storageReference.child("users/"+firebaseAuth.getCurrentUser().getUid()+"/profile.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(imageUser);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("Error Image pull","no image or pull image errored");
            }
        });

        // Logout

        btnimgback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });
        btnlogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();

                GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(getApplicationContext(), GoogleSignInOptions.DEFAULT_SIGN_IN);
                googleSignInClient.signOut().addOnCompleteListener(UserProfile.this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // Hiển thị thông báo đăng xuất thành công
                        Toast.makeText(getApplicationContext(), "Logout Success", Toast.LENGTH_SHORT).show();

                        // Chuyển hướng người dùng đến MainSignUp activity
                        startActivity(new Intent(getApplicationContext(), MainSignUp.class));
                        finish();
                    }
                });
            }
        });

    }

    // up and down Image User

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000 && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            if (imageUri != null) {
                uploadImageToFirebase(imageUri);

            } else {
                Toast.makeText(this, "Failed to retrieve image URI", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void uploadImageToFirebase(Uri imageUri) {
        // upload image to firebase
        StorageReference fileRef = storageReference.child("users/" + Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid() + "/profile.jpg");
        fileRef.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
            Toast.makeText(UserProfile.this, "Image uploaded", Toast.LENGTH_LONG).show();
            fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                Picasso.get().load(uri).into(imageUser);
                // Optionally save the download URL to the user's profile in Firestore
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                String userID = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
                DocumentReference docRef = db.collection("users").document(userID);
                docRef.update("profileImageUrl", uri.toString())
                        .addOnSuccessListener(aVoid -> Toast.makeText(UserProfile.this, "Profile image URL saved", Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e -> Toast.makeText(UserProfile.this, "Failed to save profile image URL", Toast.LENGTH_SHORT).show());
            });
        }).addOnFailureListener(e -> Toast.makeText(UserProfile.this, "Image not uploaded", Toast.LENGTH_SHORT).show());
    }

}

