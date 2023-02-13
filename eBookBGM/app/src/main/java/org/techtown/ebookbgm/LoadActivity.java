package org.techtown.ebookbgm;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.Map;

public class LoadActivity extends AppCompatActivity {

    EmotionClassifier emotionClassifier;
    String str = "It's amazing! said Alice.";
    String[] emotion = {"anger", "boredom", "empty", "enthusiasm", "fear", "fun", "happiness",
            "hate" ,"love", "neutral", "relief", "sadness", "surprise", "worry"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);

        emotionClassifier = new EmotionClassifier(this);
        try {
            emotionClassifier.init();
        } catch (IOException ioe){
            Log.d("LoadActivity", "failed to init Classifier", ioe);
        }

        Button button_analysis = findViewById(R.id.button_analysis);
        TextView textView_analysis = findViewById(R.id.text_analysis);

        button_analysis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, Float> ans = emotionClassifier.classify(str);
                String ans_str = "";
                for(int i=0;i<emotion.length;i++){
                    ans_str = ans_str + emotion[i] + " : " + ans.get(emotion[i]) + "\n";
                }
                textView_analysis.setText(ans_str);
            }
        });

    }
}