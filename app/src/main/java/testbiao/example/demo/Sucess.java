package testbiao.example.demo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class Sucess extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sucess);

        Button lckbut = (Button) findViewById(R.id.lock);
        lckbut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(Sucess.this,Login.class);
                startActivity(it);
            }
        });
    }

    @Override
    public void onBackPressed() {
        return;
    }

}
