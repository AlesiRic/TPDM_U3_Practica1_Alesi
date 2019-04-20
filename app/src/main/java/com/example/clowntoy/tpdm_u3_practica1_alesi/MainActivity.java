package com.example.clowntoy.tpdm_u3_practica1_alesi;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button c1,c2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        c1=findViewById(R.id.Choice1);
        c2=findViewById(R.id.Choice2);
        c1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent act=new Intent(MainActivity.this,Main2Activity.class);
                startActivity(act);
            }
        });
        c2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent act=new Intent(MainActivity.this,Main3Activity.class);
                startActivity(act);
            }
        });
    }
}
