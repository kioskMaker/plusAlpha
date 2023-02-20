package org.techtown.ebookbgm;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;

import org.checkerframework.checker.units.qual.A;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.tensorflow.lite.Interpreter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;

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
    final static String INTERNAL_STORAGE_FILE_PATH = "/data/data/org.techtown.ebookbgm/files/";
    final static String CHAPTER_EMOTION_LIST_FILE_NAME = "chapterEmotionList.txt";
    String responseString;
    ArrayList<String[]> emotion_line_list = new ArrayList<String[]>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);

        try {
            writeChapterEmotionList();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Button button_analysis = findViewById(R.id.button_analysis);
        button_analysis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String path = INTERNAL_STORAGE_FILE_PATH +FILE_NAME+"/";
                File chapterEmotionListFile = new File(path+CHAPTER_EMOTION_LIST_FILE_NAME);
                if(chapterEmotionListFile.exists()){
                    Toast.makeText(getApplicationContext(), "ok", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void readText10line(int input) throws IOException {
        AssetManager assetManager = getAssets();
        InputStream is = assetManager.open(FILE_NAME + "/chapter_en/chapter"+input+"_en.txt");

        StringBuilder text = new StringBuilder();
        int count = 0;
        try{
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = br.readLine()) != null){
                if(count == 10){
                    RapidAPIonThread(text.toString());
                    text = new StringBuilder();
                    count = 0;
                }
                text.append(line);
                text.append("\n");
                count++;
            }
            br.close();
            RapidAPIonThread(text.toString());

        } catch(IOException e){
            e.printStackTrace();
        }
    }

    private void RapidAPIonThread(String text){
        new Thread(()->{
            try {
                RapidAPI(text);
            } catch (IOException e) {
                e.printStackTrace();
                Log.d("Myhttp", "RapidAPI exception");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void RapidAPI(String text) throws IOException, JSONException {
        OkHttpClient client = new OkHttpClient();

        RequestBody body = new FormBody.Builder()
                .add("text", text)
                .build();

        Request request = new Request.Builder()
                .url("https://twinword-emotion-analysis-v1.p.rapidapi.com/analyze/")
                .post(body)
                .addHeader("content-type", "application/x-www-form-urlencoded")
                .addHeader("X-RapidAPI-Key", "501446e123mshee70949ebe79345p10c707jsn24ee4321df32")
                .addHeader("X-RapidAPI-Host", "twinword-emotion-analysis-v1.p.rapidapi.com")
                .build();
        Response response = client.newCall(request).execute();
        Log.d("Myhttp", response.body().string());
        JSONObject jsonObject = new JSONObject(response.body().string());
        if(jsonObject.get("emotions_detected") != null){
            emotion_line_list.add((String[]) jsonObject.get("emotions_detected"));
        }
        else {
            Log.d("Myhttp", "response emotion_detected is null");
        }

    }

    private void writeChapterEmotionList() throws IOException {
        String path = INTERNAL_STORAGE_FILE_PATH +FILE_NAME+"/";
        File file = new File(path);
        if(!file.exists()){
            file.mkdirs();
        }

        File chapterEmotionListFile = new File(path+CHAPTER_EMOTION_LIST_FILE_NAME);
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(path+CHAPTER_EMOTION_LIST_FILE_NAME));
        bufferedWriter.write("1");

    }
    private Boolean ChapterEmotionListExist(){
        String path = INTERNAL_STORAGE_FILE_PATH +FILE_NAME+"/";
        File file = new File(path);
        if(file.exists()) {
            File file2 = new File(path+CHAPTER_EMOTION_LIST_FILE_NAME);
            if(file2.exists()){
                Log.d("Myhttp", "file exist");
                return true;
            }
            else {
                Log.d("Myhttp", "file not exist");
                return false;
            }
        }
        else {
            Log.d("Myhttp", "dic not exist");
            return false;
        }
    }

}