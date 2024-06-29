package vn.edu.tlu.nhom7.calendar.activity.user;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
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
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookActivity;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.security.Permission;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import vn.edu.tlu.nhom7.calendar.R;
import vn.edu.tlu.nhom7.calendar.activity.MainActivity;

public class MainSignUp extends AppCompatActivity {

    Button btn_PhoneNumber, btn_Facebook;
    ImageButton btn_Google;
    TextView btn_Login;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    StorageReference storageReference;
    ProgressDialog progressDialog;
    private static  final int RC_SIGN_IN = 40;
    GoogleSignInClient googleSignInClient;
    ShapeableImageView imageView;
    private CallbackManager mcallbackManager;
    private FirebaseAuth.AuthStateListener authStateListener;
    private AccessTokenTracker accessTokenTracker;




    private void Mapping(){
        btn_Facebook = findViewById(R.id.btn_Facebook_SU);
        btn_PhoneNumber = findViewById(R.id.btn_Phone_SU);
        btn_Google = findViewById(R.id.imbtn_google);
        btn_Google = findViewById(R.id.imbtn_google);
        btn_Login = findViewById(R.id.tv_Login);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main_sign_up);

/// Fcaebook
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this.getApplication());
        mcallbackManager = CallbackManager.Factory.create();

        Mapping();

        btn_Facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginManager.getInstance().logInWithReadPermissions(MainSignUp.this, Arrays.asList("email", "public_profile"));
            }
        });

        LoginManager.getInstance().registerCallback(mcallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
//                handleFacebookAccessToken(loginResult.getAccessToken());
                GraphRequest request =  GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        try {
                            String email = object.getString("email");
                            firebaseAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                                @Override
                                public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                                    if (task.getResult().getSignInMethods().isEmpty()){
                                        Toast.makeText(MainSignUp.this, "This is unergistered account please register", Toast.LENGTH_SHORT).show();
                                    }else {
                                        handleFacebookAccessToken(loginResult.getAccessToken());
                                    }
                                }
                            });
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
                Bundle parameters = new Bundle();
                parameters .putString("file","email,name");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        firebaseAuth =  FirebaseAuth.getInstance();
        firebaseFirestore =  FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        btn_Login.setOnClickListener(v -> {
            Intent intent = new Intent(MainSignUp.this,Login.class);
            startActivity(intent);
        });

        btn_PhoneNumber.setOnClickListener(v -> {
            Intent intent = new Intent(MainSignUp.this,Sign_Up.class);
            startActivity(intent);
        });

        progressDialog = new ProgressDialog(MainSignUp.this);
        progressDialog.setTitle("Creating account");
        progressDialog.setMessage("We are creating your account");

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this,gso);

        btn_Google.setOnClickListener(v -> signIn());

        // face checked ton tai
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null){
                    Intent intent = new Intent(MainSignUp.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }
        };
        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                if (currentAccessToken == null){
                    firebaseAuth.signOut();
                }
            }
        };

    }

    // Facebook


    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        firebaseAuth.removeAuthStateListener(authStateListener);
    }

    private void handleFacebookAccessToken(AccessToken accessToken) {
        final AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                   Intent intent = new Intent(MainSignUp.this, MainActivity.class);
                   intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
                } else {
                    Toast.makeText(MainSignUp.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainSignUp.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Google
    private void signIn() {
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
                Log.d("Sign Up to google", "Failed to Sign Up " + e.getMessage() );
            }
        }
    }

    private void firebaseAuth(String idToken) {
        AuthCredential authCredential = GoogleAuthProvider.getCredential(idToken, null);

        firebaseAuth.signInWithCredential(authCredential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

                        if (firebaseUser != null){
                            String userID = firebaseUser.getUid();
                            String displayName = firebaseUser.getDisplayName();
                            String email = firebaseUser.getEmail();
                            Uri photoUrl = firebaseUser.getPhotoUrl();

                            FirebaseFirestore db = FirebaseFirestore.getInstance();

                            DocumentReference docRef = db.collection("users").document(userID);

                            docRef.get().addOnCompleteListener(docTask -> {
                                if (docTask.isSuccessful()){
                                    DocumentSnapshot document = docTask.getResult();
                                    if (document.exists()){

                                        Toast.makeText(MainSignUp.this, "Account has been registered, Please Login with it  ", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(MainSignUp.this,Login.class));
                                    }else {
                                        Toast.makeText(MainSignUp.this, "Create account with google  successful", Toast.LENGTH_SHORT).show();

                                        Map<String, String> user = new HashMap<>();
                                        user.put("fName", displayName);
                                        user.put("fEmail", email);

                                        if (photoUrl != null) {
                                            // Download the image from photoUrl to a local file
                                            uploadImageToFirebase(photoUrl);
                                        } else {
                                            Toast.makeText(MainSignUp.this, "User does not have a profile photo", Toast.LENGTH_SHORT).show();
                                        }

                                        db.collection("users").document(userID)
                                                .set(user)
                                                .addOnSuccessListener(aVoid -> {
                                                    Toast.makeText(MainSignUp.this, "User profile is created for " + userID, Toast.LENGTH_SHORT).show();

                                                    Intent intent = new Intent(MainSignUp.this,UserProfile.class);
                                                    assert photoUrl != null;
                                                    intent.putExtra("photoUrl",photoUrl.toString());
                                                    startActivity(intent);
                                                })
                                                .addOnFailureListener(e -> {
                                                    Toast.makeText(MainSignUp.this, "Failed to create user profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                });
                                    }
                                } else {
                                    Toast.makeText(MainSignUp.this, "Failed to check if user exists: " + Objects.requireNonNull(docTask.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }else {
                            Toast.makeText(MainSignUp.this, "Authentication failed: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void uploadImageToFirebase(Uri photoUrl) {
        if (photoUrl != null) {
            String userID = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
            StorageReference fileRef = storageReference.child("users/" + userID + "/profile.jpg");

            Glide.with(this)
                    .asFile()
                    .load(photoUrl)
                    .into(new CustomTarget<File>() {
                        @Override
                        public void onResourceReady(@NonNull File resource, @Nullable Transition<? super File> transition) {
                            Uri fileUri = Uri.fromFile(resource);
                            try {
                                InputStream inputStream = new FileInputStream(resource);
                                fileRef.putStream(inputStream)
                                        .addOnSuccessListener(taskSnapshot -> {
                                            Toast.makeText(MainSignUp.this, "Image uploaded", Toast.LENGTH_SHORT).show();
                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(MainSignUp.this, "Failed to upload image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        });
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                                Toast.makeText(MainSignUp.this, "File not found: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                        }
                    });
        } else {
            Toast.makeText(MainSignUp.this, "User does not have a profile photo", Toast.LENGTH_SHORT).show();
        }
    }

}

