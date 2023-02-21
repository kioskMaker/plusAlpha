package org.techtown.ebookbgm;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;

import org.checkerframework.checker.units.qual.A;
import org.checkerframework.checker.units.qual.C;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoadActivity extends AppCompatActivity {

    String PARENT_FILE_PATH = "books/";
    String BOOK_NAME;
    final static String INTERNAL_STORAGE_FILE_PATH = "/data/data/org.techtown.ebookbgm/files/";
    final static String CHAPTER_EMOTION_LIST_FILE_NAME = "chapterEmotionList";
    ArrayList<String[]> emotion_line_list = new ArrayList<String[]>();
    ArrayList<EmotionPointInfo> emotionPointInfos = new ArrayList<EmotionPointInfo>();
    TextView textView_analysis;
    private ProgressDialog progressDialog = null;
    int chapter;
    int chapter_num;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);

        Intent intent = getIntent();
        BOOK_NAME = intent.getExtras().getString("bookname");
        chapter = intent.getExtras().getInt("chapter");

        AssetManager assetManager = getAssets();
        try {
            chapter_num = assetManager.list("books/" + BOOK_NAME + "/chapter_en").length;
        } catch (IOException e) {
            e.printStackTrace();
        }


        textView_analysis = findViewById(R.id.text_analysis);

        Button button_analysis = findViewById(R.id.button_analysis);
        button_analysis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String path = INTERNAL_STORAGE_FILE_PATH + BOOK_NAME +"/";
                File chapterEmotionListFile = new File(path+CHAPTER_EMOTION_LIST_FILE_NAME + chapter +".txt");
                if(ChapterEmotionListExist()){
                    Log.d("Myhttp", "ChapterEmotionListExist success");
                    try {
                        BufferedReader bufferedReader = new BufferedReader(new FileReader(chapterEmotionListFile));
                        String line;
                        while((line = bufferedReader.readLine()) != null){
                            String[] strings = line.split(",");
                            if(Integer.parseInt(strings[0]) == chapter){
                                EmotionPointInfo info = new EmotionPointInfo(chapter, strings[1], Integer.parseInt(strings[2]), Integer.parseInt(strings[3]));
                                emotionPointInfos.add(info);
                            }
                        }
                        bufferedReader.close();
                        textView_analysis.setText(checkEmoInfos());

                    } catch (FileNotFoundException e) {
                        Log.d("Myhttp", "Button file not found");
                        e.printStackTrace();
                    } catch (IOException e) {
                        Log.d("Myhttp", "Button IOException");
                        e.printStackTrace();
                    }
                }
                else {
                    try {
                        Log.d("Myhttp", "ChapterEmotionListExist failed");
                        setProgressBar();
                        readText10line(chapter);
                    } catch (IOException e) {
                        Log.d("Myhttp", "Button IOException");
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void readText10line(int chapter) throws IOException {
        AssetManager assetManager = getAssets();
        InputStream is = assetManager.open(PARENT_FILE_PATH + BOOK_NAME + "/chapter_en/ch" + chapter + "_eng.txt");
        StringBuilder text = new StringBuilder();
        int count = 0;
        ArrayList<String> temp = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = br.readLine()) != null) {
                if (count == 10) {
                    temp.add(text.toString());
                    text = new StringBuilder();
                    count = 0;

                }
                text.append(line);
                text.append("\n");
                count++;
            }
            br.close();
            temp.add(text.toString());

        } catch (IOException e) {
            Log.d("Myhttp", "readtext10line IOException");
            e.printStackTrace();
        }
        Log.d("Myhttp", "chapter : " + chapter + " temp size : " + temp.size());
        RapidAPIonThread(chapter, temp, 0);
    }

    private void RapidAPIonThread(int chapter, ArrayList<String> text, int point){
        new Thread(()->{
            try {
                RapidAPI(chapter, text, point);
            } catch (IOException e) {
                e.printStackTrace();
                Log.d("Myhttp", "RapidAPI IOException");
            } catch (JSONException e) {
                e.printStackTrace();
                Log.d("Myhttp", "RapidAPI JSONException");
            }
        }).start();

    }

    private void RapidAPI(int chapter, ArrayList<String> text, int point) throws IOException, JSONException {
        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("text", text.get(point))
                .build();

        Request request = new Request.Builder()
                .url("https://twinword-emotion-analysis-v1.p.rapidapi.com/analyze/")
                .post(body)
                .addHeader("content-type", "application/x-www-form-urlencoded")
                .addHeader("X-RapidAPI-Key", "501446e123mshee70949ebe79345p10c707jsn24ee4321df32")
                .addHeader("X-RapidAPI-Host", "twinword-emotion-analysis-v1.p.rapidapi.com")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                call.cancel();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseToStr = response.body().string();
                Log.d("Myhttp", responseToStr);
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(responseToStr);
                } catch (JSONException e) {
                    Log.d("Myhttp", "JSONException 3");
                    e.printStackTrace();
                }
                try {
                    if(jsonObject.get("emotions_detected") != null){
                        emotion_line_list.add(toStringArray((JSONArray)jsonObject.get("emotions_detected")));
                        String str = "";
                        for(int i=0;i<emotion_line_list.get(emotion_line_list.size()-1).length;i++){
                            str = str + emotion_line_list.get(emotion_line_list.size()-1)[i] + ", ";

                        }
                        Log.d("Myhttp", "" + (emotion_line_list.size()-1) + " : " + str);
                    }
                } catch (JSONException e) {
                    Log.d("Myhttp", "JSONException 2");
                    e.printStackTrace();
                }
                //TO-DO 수정
                if(text.size() > point+1){
                    RapidAPIonThread(chapter, text, point+1);
                }
                else {
                    getEmotionPoint();
                    organizeEmotionPointInfos();
                    writeChapterEmotionList();
                    progressDialog.dismiss();
                }

            }
        });

    }

    private void writeChapterEmotionList() throws IOException {
        String path = INTERNAL_STORAGE_FILE_PATH + BOOK_NAME +"/";
        File file = new File(path);
        if(!file.exists()){
            file.mkdirs();
        }
        File chapterEmotionListFile = new File(path+CHAPTER_EMOTION_LIST_FILE_NAME+chapter+".txt");
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(path+CHAPTER_EMOTION_LIST_FILE_NAME+chapter+".txt"));
        String str = "";
        for(int i=0;i<emotionPointInfos.size();i++){
            EmotionPointInfo info = emotionPointInfos.get(i);
            str = str + info.getChapter() + "," + info.getEmotion_type() + "," + info.getStart() + "," + info.getEnd() + "\n";
        }
        bufferedWriter.write(str);
        Log.d("Myhttp", str);
        bufferedWriter.close();


    }
    private Boolean ChapterEmotionListExist(){
        String path = INTERNAL_STORAGE_FILE_PATH + BOOK_NAME +"/";
        File file = new File(path);
        if(file.exists()) {
            File file2 = new File(path+CHAPTER_EMOTION_LIST_FILE_NAME + chapter + ".txt");
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

            return false;
        }
    }

    private void getEmotionPoint(){
        for(int i=0;i<emotion_line_list.size();i++){
            for(int j=0;j<emotion_line_list.get(i).length;j++){
                f(i, emotion_line_list.get(i)[j], 0 );
            }
        }
    }

    private void f(int x, String emotion_str, int count){
        Boolean hit = false;
        if(x == emotion_line_list.size()){
            if(count >= 2){
                EmotionPointInfo emotionPointInfo = new EmotionPointInfo(chapter, emotion_str, x-count, x-1);
                emotionPointInfos.add(emotionPointInfo);
            }
            return;
        }

        if(emotion_line_list.get(x).length == 0){
            if(count >= 2){
                EmotionPointInfo emotionPointInfo = new EmotionPointInfo(chapter, emotion_str, x-count, x-1);
                emotionPointInfos.add(emotionPointInfo);
            }
            return ;
        }

        for(int i=0;i<emotion_line_list.get(x).length;i++){
            String emo_list_strarr_point = emotion_line_list.get(x)[i];
            if(emo_list_strarr_point.equals(emotion_str)){
                f(x+1, emotion_str, count+1);
                hit = true;
            }
        }
        if(!hit){
            if(count >= 2){
                EmotionPointInfo emotionPointInfo = new EmotionPointInfo(chapter, emotion_str, x-count, x-1);
                emotionPointInfos.add(emotionPointInfo);
            }
        }
        return;
    }

    public static String[] toStringArray(JSONArray array) {
        if(array==null)
            return new String[0];

        String[] arr=new String[array.length()];
        for(int i=0; i<arr.length; i++) {
            arr[i]=array.optString(i);
        }
        return arr;
    }
    private void organizeEmotionPointInfos(){
        for(int i=0;i<emotionPointInfos.size();i++){
            for(int j=i+1;j<emotionPointInfos.size();j++){
                EmotionPointInfo Info1 = emotionPointInfos.get(i);
                EmotionPointInfo Info2 = emotionPointInfos.get(j);
                if(Info1.getEmotion_type().equals(Info2.getEmotion_type()) && Info1.getEnd() == Info2.getEnd() && Info1.getStart() < Info2.getStart()){
                    emotionPointInfos.remove(Info2);
                }
            }
        }
    }
    private String checkEmoInfos(){
        String str = "";
        for(int i=0;i<emotionPointInfos.size();i++){
            EmotionPointInfo temp = emotionPointInfos.get(i);
            str = str + "Emotion_type : " + temp.getEmotion_type() + " START : " + temp.getStart() + " END : " + temp.getEnd() + "\n";
        }
        return str;
    }
    private void setProgressBar(){
        progressDialog = new ProgressDialog(this, android.R.style.Theme_Material_Dialog_Alert);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);      // Style - 원 모양 설정
        progressDialog.setMessage("Loading...");                           // Message - 표시할 텍스트
        progressDialog.setCanceledOnTouchOutside(false);                    // 터치시 Canceled 막기
        progressDialog.setIndeterminate(true);
        progressDialog.show();
    }


}