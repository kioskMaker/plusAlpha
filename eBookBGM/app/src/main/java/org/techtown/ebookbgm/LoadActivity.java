package org.techtown.ebookbgm;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import okhttp3.MediaType;

import org.json.JSONException;
import org.tensorflow.lite.Interpreter;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class LoadActivity extends AppCompatActivity {

    EmotionClassifier emotionClassifier;
    String str1 = "it gets better. without explanation; you just wake up one morning and youâ€™re just happy, totally and utterly elated.";
    String str2 = "i am not at all happy";
    String str = "";
    final static String FILE_NAME = "aliceinwonderland_chaper_divided";
    Interpreter interpreter;
    String responseString;
    @Override
    public void finish() {
        super.finish();
        interpreter.close();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);

        emotionClassifier = new EmotionClassifier(this);
        try {
            emotionClassifier.init();
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        //emotion analysis button
        Button button_analysis = findViewById(R.id.button_analysis);
        TextView textView_analysis = findViewById(R.id.text_analysis);

        button_analysis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Pair[] emotion_result = emotionClassifier.classify(str);
                String str = "";
                float max_emotion = 0;
                int max_position = 0;
                for(int i=0;i<emotion_result.length;i++){
                    str = str + emotion_result[i].first + " : " + emotion_result[i].second + "\n";
                    if(max_emotion < (float)emotion_result[i].second){
                        max_position = i;
                        max_emotion = (float)emotion_result[i].second;
                    }
                }
                str = str + "MAX = " + emotion_result[max_position].first + " : " + emotion_result[max_position].second;
                textView_analysis.setText(str);
                Toast.makeText(getApplicationContext(), "success", Toast.LENGTH_LONG).show();


            }
        });

        //set test String
        TextView example = findViewById(R.id.example);

        Button example1 = findViewById(R.id.example1);
        example1.setText(str1);
        example1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                str = str1;
                example.setText(str);
            }
        });
        Button example2 = findViewById(R.id.example2);
        example2.setText(str2);
        example2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                str = str2;
                example.setText(str);
            }
        });

        RapidAPIonThread(str1);

    }

    private void RapidAPIonThread(String text){
        new Thread(()->{
            try {
                RapidAPI(text);
            } catch (IOException e) {
                e.printStackTrace();
                Log.d("http", "RapidAPI exception");
            }
        }).start();
    }

    private void RapidAPI(String text) throws IOException {
        OkHttpClient client = new OkHttpClient();

        MediaType mediaType = MediaType.parse("application/json");
        String value = "{\r\n    \"sentence\": \"" + text + "\"\r\n}";
        RequestBody body = RequestBody.create(mediaType, value);
        Request request = new Request.Builder()
                .url("https://emodex-emotions-analysis.p.rapidapi.com/rapidapi/emotions")
                .post(body)
                .addHeader("content-type", "application/json")
                .addHeader("X-RapidAPI-Key", "501446e123mshee70949ebe79345p10c707jsn24ee4321df32")
                .addHeader("X-RapidAPI-Host", "emodex-emotions-analysis.p.rapidapi.com")
                .build();

        Response response = client.newCall(request).execute();
        if(response.isSuccessful()){
            responseString = response.body().string();
            Log.d("http", responseString);
            response.body().close();
        }
    }





}