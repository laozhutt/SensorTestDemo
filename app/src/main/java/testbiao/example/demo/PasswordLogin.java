package testbiao.example.demo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class PasswordLogin extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pass_login);

        Button logbt = (Button) findViewById(R.id.pslogin);
        logbt.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                login();
            }
        });


    }
    @Override
    public void onBackPressed() {
        return;
    }

    private void login() {
        String user = ((TextView) findViewById(R.id.user_tx)).getText().toString();
        String pass = ((TextView) findViewById(R.id.pass_tx)).getText().toString();

        if (user.equals("admin") && pass.equals("admin")) {
            Intent it = new Intent(PasswordLogin.this, Sucess.class);
            startActivity(it);
        } else {
            Intent it = new Intent(PasswordLogin.this, Fail.class);
            startActivity(it);
        }
    }

}
