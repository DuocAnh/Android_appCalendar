package vn.edu.tlu.nhom7.calendar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainSignUp extends AppCompatActivity {

    Button btn_Facebook,btn_PhoneNumber;
    ImageButton btn_Google;
    TextView btn_Login;

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
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Mapping();

        btn_Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainSignUp.this,Login.class);
                startActivity(intent);
            }
        });
    }
}