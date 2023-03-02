package org.techtown.ebookbgm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;


import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;

import android.os.Handler;
import android.os.Message;
import android.text.TextPaint;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.material.bottomnavigation.BottomNavigationView;

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
import java.util.HashMap;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PageMainActivity extends AppCompatActivity implements View.OnClickListener {
    private ClickableViewPager pagesView;
    private static final int FADEIN_MSG = 101;
    private static final int FADEOUT_MSG = 102;
    private static final int FADEOUTIN_MSG = 103;
    String FILE_NAME = "books/";
    String BOOK_NAME = null;
    int sentence_num = 0;
    int CHAPTER;
    String PARENT_FILE_PATH = "books/";
    final static String INTERNAL_STORAGE_FILE_PATH = "/data/data/org.techtown.ebookbgm/files/";
    final static String CHAPTER_EMOTION_LIST_FILE_NAME = "chapterEmotionList";
    ArrayList<String[]> emotion_line_list = new ArrayList<String[]>();
    ArrayList<EmotionPointInfo> emotionPointInfos = new ArrayList<EmotionPointInfo>();
    private ProgressDialog progressDialog = null;
    HashMap<Integer, Pair<String, Integer>> emoLine_dir = new HashMap<>();
    HashMap<Integer, Pair<String, Integer>> emoPage_dir = new HashMap<>();
    float volume = 0f;
    MediaPlayer mediaPlayer;
    Boolean isInit = false;
    int NUM = 1;
    Pair<String, Integer> prev_music;
    ArrayList<Integer> pages_sentences = new ArrayList<>();
    final int[] endPoint = new int[1];
    int FADE_DELAY = 100;
    MusicHandler musicHandler = new MusicHandler(this, new MediaPlayer(), new MediaPlayer());
    Boolean isMusicOn = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        endPoint[0] = -999;
        pagesView = (ClickableViewPager) findViewById(R.id.pages);
        Intent intent = getIntent();
        BOOK_NAME = intent.getExtras().getString("bookname");
        CHAPTER = intent.getExtras().getInt("chapter");
        TextView toolbar_title = findViewById(R.id.chapter_number);
        toolbar_title.setText("제" + CHAPTER + "장");
        Switch musicbutton = findViewById(R.id.musicOn);
        musicbutton.setChecked(true);
        musicbutton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isMusicOn = isChecked;
                if(!isMusicOn){
                    try {
                        musicHandler.crossFade("null",1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });


        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().hide();
        getSupportActionBar().setTitle(null);


//        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_menu);
//        bottomNavigationView.setVisibility(View.GONE);
//
        pagesView.setOnViewPagerClickListener(new ClickableViewPager.OnClickListener() {
            @Override
            public void onViewPagerClick(ViewPager viewPager) {
                Log.d("Mypager", "click");
                if (getSupportActionBar().isShowing()) {
//                    bottomNavigationView.setVisibility(View.GONE);
                    getSupportActionBar().hide();
                } else {
                    getSupportActionBar().show();
//                    bottomNavigationView.setVisibility(View.VISIBLE);
                }
            }
        });

        // to get ViewPager width and height we have to wait global layout
        pagerSetting();

        // 하단바를 눌렀을 때 프래그먼트가 변경되게 함
//        setBottomNavigationView();

        // load emotion point info
        loadEmotionPointInfos();

        // music player init

        // page 변경 시 동작

    }

    private String readText(int input) throws IOException {
        AssetManager assetManager = getAssets();
        InputStream is = assetManager.open(FILE_NAME + BOOK_NAME + "/ch" + input + ".txt");

        StringBuilder text = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = br.readLine()) != null) {
                text.append("   " + line);
                text.append("\n");
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return text.toString();
    }


    public void onClickBack(View v) {
        Log.d("Mypager", "clicking");
    }

    @Override
    public void onClick(View v) {

    }

//    private void setBottomNavigationView() {
//        BottomNavigationView bottom_menu = findViewById(R.id.bottom_menu);
//        bottom_menu.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
//            @Override
//            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//
//                Intent intent = new Intent(getApplicationContext(), PageMainActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                switch (item.getItemId()) {
//                    case R.id.back:
//                        if (CHAPTER == 1) {
//                            Toast.makeText(getApplicationContext(), "First Chapter", Toast.LENGTH_LONG).show();
//                            break;
//                        }
//                        intent.putExtra("bookname", BOOK_NAME);
//                        intent.putExtra("chapter", CHAPTER - 1);
//                        startActivity(intent);
//                        finish();
//                        break;
//                    case R.id.front:
//                        AssetManager as = getAssets();
//                        int MAX_CHAPTER;
//                        try {
//                            MAX_CHAPTER = as.list(FILE_NAME + BOOK_NAME + "/chapter_en").length;
//                        } catch (IOException e) {
//                            throw new RuntimeException(e);
//                        }
//                        if (CHAPTER == MAX_CHAPTER) {
//                            Toast.makeText(getApplicationContext(), "Last Chapter", Toast.LENGTH_LONG).show();
//                            break;
//                        }
//                        intent.putExtra("bookname", BOOK_NAME);
//                        intent.putExtra("chapter", CHAPTER + 1);
//                        startActivity(intent);
//                        finish();
//                        break;
//                }
//                return true;
//            }
//        });
//    }

    private void loadEmotionPointInfos() {
        String path = INTERNAL_STORAGE_FILE_PATH + BOOK_NAME + "/";
        File chapterEmotionListFile = new File(path + CHAPTER_EMOTION_LIST_FILE_NAME + CHAPTER + ".txt");
        if (ChapterEmotionListExist()) {
            Log.d("Myhttp", "ChapterEmotionListExist success");
            try {
                BufferedReader bufferedReader = new BufferedReader(new FileReader(chapterEmotionListFile));
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    String[] strings = line.split(",");
                    if (Integer.parseInt(strings[0]) == CHAPTER) {
                        emoLine_dir.put(Integer.parseInt(strings[2]), new Pair<>(strings[1], Integer.parseInt(strings[3])));
                    }
                }
                bufferedReader.close();
                Log.d("Mypager", checkEmoInfos());

            } catch (FileNotFoundException e) {
                Log.d("Myhttp", "Button file not found");
                e.printStackTrace();
            } catch (IOException e) {
                Log.d("Myhttp", "Button IOException");
                e.printStackTrace();
            }
        } else {
            try {
                Log.d("Myhttp", "ChapterEmotionListExist failed");
                setProgressBar();
                readText30line(CHAPTER);
            } catch (IOException e) {
                Log.d("Myhttp", "Button IOException");
                e.printStackTrace();
            }
        }
    }

    private void readText30line(int chapter) throws IOException {
        AssetManager assetManager = getAssets();
        InputStream is = assetManager.open(PARENT_FILE_PATH + BOOK_NAME + "/chapter_en/ch" + chapter + "_eng.txt");
        StringBuilder text = new StringBuilder();
        int count = 0;
        ArrayList<String> temp = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = br.readLine()) != null) {
                if (count == 30) {
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
            Log.d("Myhttp", "readtext30line IOException");
            e.printStackTrace();
        }
        Log.d("Myhttp", "chapter : " + chapter + " temp size : " + temp.size());
        RapidAPIonThread(chapter, temp, 0);
    }

    private void RapidAPIonThread(int chapter, ArrayList<String> text, int point) {
        new Thread(() -> {
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
                    if (jsonObject.get("emotions_detected") != null) {
                        emotion_line_list.add(toStringArray((JSONArray) jsonObject.get("emotions_detected")));
                        String str = "";
                        for (int i = 0; i < emotion_line_list.get(emotion_line_list.size() - 1).length; i++) {
                            str = str + emotion_line_list.get(emotion_line_list.size() - 1)[i] + ", ";

                        }
                        Log.d("Myhttp", "" + (emotion_line_list.size() - 1) + " : " + str);
                    }
                } catch (JSONException e) {
                    Log.d("Myhttp", "JSONException 2");
                    e.printStackTrace();
                }
                //TO-DO 수정
                if (text.size() > point + 1) {
                    RapidAPIonThread(chapter, text, point + 1);
                } else {
                    getEmotionPoint();
                    organizeEmotionPointInfos();
                    emotionLineMapping();
                    writeChapterEmotionList();
                    Intent intent = new Intent(getApplicationContext(), PageMainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("bookname", BOOK_NAME);
                    intent.putExtra("chapter", CHAPTER);
                    startActivity(intent);
                    finish();
                    progressDialog.dismiss();

                }

            }
        });

    }

    private void writeChapterEmotionList() throws IOException {
        String path = INTERNAL_STORAGE_FILE_PATH + BOOK_NAME + "/";
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        File chapterEmotionListFile = new File(path + CHAPTER_EMOTION_LIST_FILE_NAME + CHAPTER + ".txt");
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(path + CHAPTER_EMOTION_LIST_FILE_NAME + CHAPTER + ".txt"));
        String str = "";
        for (int key : emoLine_dir.keySet()) {
            str = str + CHAPTER + "," + emoLine_dir.get(key).first + "," + key + "," + emoLine_dir.get(key).second + "\n";
        }
        bufferedWriter.write(str);
        Log.d("Myhttp", str);
        bufferedWriter.close();
    }

    private Boolean ChapterEmotionListExist() {
        String path = INTERNAL_STORAGE_FILE_PATH + BOOK_NAME + "/";
        File file = new File(path);
        if (file.exists()) {
            File file2 = new File(path + CHAPTER_EMOTION_LIST_FILE_NAME + CHAPTER + ".txt");
            if (file2.exists()) {
                Log.d("Myhttp", "file exist");
                return true;
            } else {
                Log.d("Myhttp", "file not exist");
                return false;
            }
        } else {

            return false;
        }
    }

    private void getEmotionPoint() {
        for (int i = 0; i < emotion_line_list.size(); i++) {
            for (int j = 0; j < emotion_line_list.get(i).length; j++) {
                f(i, emotion_line_list.get(i)[j], 0);
            }
        }
    }

    private void f(int x, String emotion_str, int count) {
        Boolean hit = false;
        if (x == emotion_line_list.size()) {
            if (count >= 2) {
                EmotionPointInfo emotionPointInfo = new EmotionPointInfo(CHAPTER, emotion_str, x - count, x - 1);
                emotionPointInfos.add(emotionPointInfo);
            }
            return;
        }

        if (emotion_line_list.get(x).length == 0) {
            if (count >= 2) {
                EmotionPointInfo emotionPointInfo = new EmotionPointInfo(CHAPTER, emotion_str, x - count, x - 1);
                emotionPointInfos.add(emotionPointInfo);
            }
            return;
        }

        for (int i = 0; i < emotion_line_list.get(x).length; i++) {
            String emo_list_strarr_point = emotion_line_list.get(x)[i];
            if (emo_list_strarr_point.equals(emotion_str)) {
                f(x + 1, emotion_str, count + 1);
                hit = true;
            }
        }
        if (!hit) {
            if (count >= 2) {
                EmotionPointInfo emotionPointInfo = new EmotionPointInfo(CHAPTER, emotion_str, x - count, x - 1);
                emotionPointInfos.add(emotionPointInfo);
            }
        }
        return;
    }

    public static String[] toStringArray(JSONArray array) {
        if (array == null)
            return new String[0];

        String[] arr = new String[array.length()];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = array.optString(i);
        }
        return arr;
    }

    private void organizeEmotionPointInfos() {
        for (int i = 0; i < emotionPointInfos.size(); i++) {
            for (int j = i + 1; j < emotionPointInfos.size(); j++) {
                EmotionPointInfo Info1 = emotionPointInfos.get(i);
                EmotionPointInfo Info2 = emotionPointInfos.get(j);
                if (Info1.getEmotion_type().equals(Info2.getEmotion_type()) && Info1.getEnd() == Info2.getEnd() && Info1.getStart() < Info2.getStart()) {
                    emotionPointInfos.remove(Info2);
                }
            }
        }
    }

    private String checkEmoInfos() {
        String str = "";
        for (int key : emoPage_dir.keySet()) {
            str = str + "Emotion_type : " + emoPage_dir.get(key).first + " START : " + key + " END : " + emoPage_dir.get(key).second + "\n";
        }
        return str;
    }

    private void setProgressBar() {
        progressDialog = new ProgressDialog(this, android.R.style.Theme_Material_Dialog_Alert);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);      // Style - 원 모양 설정
        progressDialog.setMessage("Loading...");                           // Message - 표시할 텍스트
        progressDialog.setCanceledOnTouchOutside(false);                    // 터치시 Canceled 막기
        progressDialog.setIndeterminate(true);
        progressDialog.show();
    }

    private void emotionLineMapping() {
        for (int i = 0; i < emotionPointInfos.size(); i++) {
            EmotionPointInfo info = emotionPointInfos.get(i);
            if (emoLine_dir.get(info.getStart()) != null) {
                if (emoLine_dir.get(info.getStart()).second + 2 <= info.getEnd()) {
                    emoLine_dir.put(emoLine_dir.get(info.getStart()).second + 1, new Pair<>(info.getEmotion_type(), info.getEnd()));
                } else if (emoLine_dir.get(info.getStart()).second >= info.getEnd() + 2) {
                    emoLine_dir.put(info.getEnd() + 1, new Pair<>(emoLine_dir.get(info.getStart()).first, emoLine_dir.get(info.getStart()).second));
                    emoLine_dir.put(info.getStart(), new Pair<>(info.getEmotion_type(), info.getEnd()));
                } else if (emoLine_dir.get(info.getStart()).second == info.getEnd() + 1) {

                } else if (emoLine_dir.get(info.getStart()).second + 1 == info.getEnd()) {
                    emoLine_dir.put(info.getStart(), new Pair<>(info.getEmotion_type(), info.getEnd()));
                } else if (emoLine_dir.get(info.getStart()).second == info.getEnd()) {

                }
            } else {
                emoLine_dir.put(info.getStart(), new Pair<>(info.getEmotion_type(), info.getEnd()));
            }
        }
    }


    private void lineToPage() {
        for (int key : emoLine_dir.keySet()) {
            int start = -1;
            int end = -1;

            for (int i = 0; i < pages_sentences.size(); i++) {
                if ((key * 30) <= pages_sentences.get(i)) {
                    start = i;
                    break;
                }
            }
            for (int i = 0; i < pages_sentences.size(); i++) {
                if ((emoLine_dir.get(key).second * 30) <= pages_sentences.get(i)) {
                    end = i;
                    break;
                }
            }
            emoPage_dir.put(start, new Pair<>(emoLine_dir.get(key).first, end));
        }
    }

    private void pagerSetting() {
        String str = null;
        try {
            str = readText(CHAPTER);
        } catch (IOException e) {
            Log.d("Mypager", "onCreate IOException");
            e.printStackTrace();
        }
        String finalStr = str;

        pagesView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                PageSplitter pageSplitter = new PageSplitter(pagesView.getWidth(), pagesView.getHeight(), 1, 0);
                TextPaint textPaint = new TextPaint();
                textPaint.setTextSize(getResources().getDimension(R.dimen.text_size));
                textPaint.setColor(Color.BLACK);
                pageSplitter.append(finalStr, textPaint);

                pagesView.setAdapter(new TextPagerAdapter(getSupportFragmentManager(), pageSplitter.getPages()));
                pagesView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                TextPagerAdapter textPagerAdapter = (TextPagerAdapter) pagesView.getAdapter();

                for (int i = 0; i < textPagerAdapter.getCount(); i++) {
                    String str = textPagerAdapter.getPageTexts(i).toString();
                    if (str.length() >= 2) {
                        if (str.substring(str.length() - 2).equals("\n")) {
                            String[] strings = str.split("\n");
                            sentence_num += strings.length;
                        } else {
                            String[] strings = str.split("\n");
                            sentence_num += strings.length - 1;
                        }
                        pages_sentences.add(sentence_num);
                    }
                    Log.d("Mypager", "" + sentence_num);
                }
                lineToPage();

                Log.d("Mypager", checkEmoInfos());
                if (emoPage_dir.get(0) != null) {
                    //Music start
                    endPoint[0] = emoPage_dir.get(0).second;

                    try {
                        musicHandler.crossFade(emoPage_dir.get(0).first, new Random().nextInt(getAssets().list("musics/"+emoPage_dir.get(0).first).length) +1);
                        Toast.makeText(getApplicationContext(), "bgm playing...", Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        Log.d("Mypager", "musicHandler IOE");
                        e.printStackTrace();
                    }
                }
            }
        });
        pagesView.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Log.d("Mypager", "position : " + position);
                if (emoPage_dir.get(position) != null && isMusicOn) {
                    // music start
                    try {
                        musicHandler.crossFade(emoPage_dir.get(position).first,new Random().nextInt(getAssets().list("musics/"+emoPage_dir.get(position).first).length) +1);
                        Toast.makeText(getApplicationContext(), "bgm playing...", Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    endPoint[0] = emoPage_dir.get(position).second;
                    emoPage_dir.remove(position);

                } else if (position == endPoint[0] + 1 && isMusicOn) {
                    // music stop
                    try {
                        musicHandler.crossFade("null",1);
                        Toast.makeText(getApplicationContext(), "bgm stop...", Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        musicHandler.reset();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d("Mypager", "menu clicking");
        switch (item.getItemId()){
            case android.R.id.home:{ //toolbar의 back키 눌렀을 때 동작
                finish();
                return  true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
