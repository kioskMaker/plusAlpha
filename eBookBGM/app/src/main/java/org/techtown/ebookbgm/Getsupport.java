package org.techtown.ebookbgm;

import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

public class Getsupport extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_getsupport2);

        RelativeLayout relativeLayout = (RelativeLayout)findViewById(R.id.layout);

                relativeLayout.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View view){
                        if(getSupportActionBar().isShowing()){
                            getSupportActionBar().hide();
                        } else{
                            getSupportActionBar().show();
                        }
                    }
                });
    }
}