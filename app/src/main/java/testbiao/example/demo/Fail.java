package testbiao.example.demo;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;

public class Fail extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fail);

        Button fautobut = (Button) findViewById(R.id.fail_auto);
        Button fpassbut = (Button) findViewById(R.id.fail_pass);

        fautobut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent (Fail.this, Login.class);
                startActivity(it);
            }
        });

        fpassbut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent (Fail.this, PasswordLogin.class);
                startActivity(it);
            }
        });
    }

    @Override
    public void onBackPressed() {
        return;
    }

}
