package org.techtown.ebookbgm;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Handler;
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
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
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

    final static String FILE_NAME = "aliceinwonderland_chapter_divided";
    final static String INTERNAL_STORAGE_FILE_PATH = "/data/data/org.techtown.ebookbgm/files/";
    final static String CHAPTER_EMOTION_LIST_FILE_NAME = "chapterEmotionList.txt";
    ArrayList<String[]> emotion_line_list = new ArrayList<String[]>();
    ArrayList<EmotionPointInfo> emotionPointInfos = new ArrayList<EmotionPointInfo>();
    TextView textView_analysis;
    private ProgressDialog progressDialog = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);

        textView_analysis = findViewById(R.id.text_analysis);

        Button button_analysis = findViewById(R.id.button_analysis);
        button_analysis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String path = INTERNAL_STORAGE_FILE_PATH +FILE_NAME+"/";
                File chapterEmotionListFile = new File(path+CHAPTER_EMOTION_LIST_FILE_NAME);
                if(ChapterEmotionListExist()){
                    Log.d("Myhttp", "ChapterEmotionListExist success");
                    try {
                        BufferedReader bufferedReader = new BufferedReader(new FileReader(chapterEmotionListFile));
                        String line;
                        while((line = bufferedReader.readLine()) != null){
                            String[] strings = line.split(",");
                            EmotionPointInfo info = new EmotionPointInfo(strings[0], Integer.parseInt(strings[1]), Integer.parseInt(strings[2]));
                            emotionPointInfos.add(info);
                        }
                        textView_analysis.setText(checkEmoInfos());
                        bufferedReader.close();

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
                        readText10line(6);
                    } catch (IOException e) {
                        Log.d("Myhttp", "Button IOException");
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void readText10line(int input) throws IOException {
        AssetManager assetManager = getAssets();
        InputStream is = assetManager.open(FILE_NAME + "/chapter_en/chapter"+input+"_en.txt");

        StringBuilder text = new StringBuilder();
        int count = 0;
        int tempcount = 0;
        try{
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = br.readLine()) != null){
                if(count == 10){
                    tempcount++;
                    final String temptext = text.toString();
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            RapidAPIonThread(temptext);
                        }
                    }, 1000 * tempcount);
                    text = new StringBuilder();
                    count = 0;

                }
                text.append(line);
                text.append("\n");
                count++;
            }
            br.close();
            tempcount++;
            final String temptext = text.toString();
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    RapidAPIonThread(temptext);
                }
            }, 1000 * tempcount);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    getEmotionPoint();
                    organizeEmotionPointInfos();
                    try {
                        writeChapterEmotionList();
                    } catch (IOException e) {
                        Log.d("Myhttp", "writeChapterEmotionList IOException");
                        e.printStackTrace();
                    }
                    textView_analysis.setText(checkEmoInfos());
                    progressDialog.dismiss();
                }
            }, 1000 * tempcount + 5000);


        } catch(IOException e){
            Log.d("Myhttp", "readtext10line IOException");
            e.printStackTrace();
        }
    }

    private void RapidAPIonThread(String text){
        new Thread(()->{
            try {
                RapidAPI(text);
            } catch (IOException e) {
                e.printStackTrace();
                Log.d("Myhttp", "RapidAPI IOException");
            } catch (JSONException e) {
                e.printStackTrace();
                Log.d("Myhttp", "RapidAPI JSONException");
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
        String responseToStr =  response.body().string();
        Log.d("Myhttp", responseToStr);
        JSONObject jsonObject = new JSONObject(responseToStr);
        if(jsonObject.get("emotions_detected") != null){
            emotion_line_list.add(toStringArray((JSONArray)jsonObject.get("emotions_detected")));
            String str = "";
            for(int i=0;i<emotion_line_list.get(emotion_line_list.size()-1).length;i++){
                str = str + emotion_line_list.get(emotion_line_list.size()-1)[i] + ", ";
            }
            Log.d("Myhttp", "" + (emotion_line_list.size()-1) + " : " + str);

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
        String str = "";
        for(int i=0;i<emotionPointInfos.size();i++){
            EmotionPointInfo info = emotionPointInfos.get(i);
            str = str + info.getEmotion_type() + "," + info.getStart() + "," + info.getEnd() + "\n";
        }
        bufferedWriter.write(str);
        bufferedWriter.close();

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
                EmotionPointInfo emotionPointInfo = new EmotionPointInfo(emotion_str, x-count, x-1);
                emotionPointInfos.add(emotionPointInfo);
            }
            return;
        }

        if(emotion_line_list.get(x).length == 0){
            if(count >= 2){
                EmotionPointInfo emotionPointInfo = new EmotionPointInfo(emotion_str, x-count, x-1);
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
                EmotionPointInfo emotionPointInfo = new EmotionPointInfo(emotion_str, x-count, x-1);
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