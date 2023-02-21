package org.techtown.ebookbgm;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // 원하는 Activity 실행
        Button buttonPaPagoAPI = findViewById(R.id.button_PapagoAPI);
        buttonPaPagoAPI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PapagoAPI.class);
                startActivity(intent);
            }
        });

        Button buttonLoadActivity = findViewById(R.id.button_LoadActivity);
        buttonLoadActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LoadActivity.class);
                startActivity(intent);
            }
        });
        Button button_startactivity = findViewById(R.id.button_startactivity);
        button_startactivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), startactivity.class);
                startActivity(intent);
            }
        });

        Button buttonPageActivity = findViewById(R.id.button_pageActivity);
        buttonPageActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PageMainActivity.class);
                startActivity(intent);
            }
        });

    }
}