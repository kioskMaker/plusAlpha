package org.techtown.ebookbgm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;


import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.os.Bundle;

import android.text.TextPaint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class PageMainActivity extends AppCompatActivity implements View.OnClickListener{
    private ClickableViewPager pagesView;
    String FILE_NAME = "books/";
    String BOOK_NAME = null;
    int sentence_num;
    int CHAPTER;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page_main);

        Toolbar toolbar = findViewById(R.id.toolbar);

        pagesView = (ClickableViewPager) findViewById(R.id.pages);

        Intent intent = getIntent();
        BOOK_NAME = intent.getExtras().getString("bookname");
        CHAPTER = intent.getExtras().getInt("chapter");
        TextView toolbar_title = findViewById(R.id.chapter_number);
        toolbar_title.setText("제" + CHAPTER + "장");


        setSupportActionBar(toolbar);
        getSupportActionBar().hide();
        getSupportActionBar().setTitle(null);


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_menu);
        bottomNavigationView.setVisibility(View.GONE);

        pagesView.setOnViewPagerClickListener(new ClickableViewPager.OnClickListener() {
            @Override
            public void onViewPagerClick(ViewPager viewPager) {
                Log.d("Mypager", "click");
                if(getSupportActionBar().isShowing()){
                    bottomNavigationView.setVisibility(View.GONE);
                    getSupportActionBar().hide();
                } else{
                    getSupportActionBar().show();
                    bottomNavigationView.setVisibility(View.VISIBLE);
                }


            }
        });


        String str = null;
        try {
            str = readText(CHAPTER);
        } catch (IOException e) {
            Log.d("Mypager", "onCreate IOException");
            e.printStackTrace();
        }
        String finalStr = str;

        // to get ViewPager width and height we have to wait global layout
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

                for(int i=0;i<textPagerAdapter.getCount();i++){
                    String str = textPagerAdapter.getPageTexts(i).toString();
                    if(str.length() >= 2){
                        if(str.substring(str.length()-2).equals("\n")){
                            String[] strings = str.split("\n");
                            sentence_num = strings.length;
                        }
                        else{
                            String[] strings = str.split("\n");
                            sentence_num = strings.length-1;
                        }
                    }
                    Log.d("Mypager", ""+sentence_num);
                }

            }
        });
        BottomNavigationView bottom_menu = findViewById(R.id.bottom_menu);

// 하단바를 눌렀을 때 프래그먼트가 변경되게 함

        bottom_menu.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                Intent intent = new Intent(getApplicationContext(), PageMainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                switch (item.getItemId()) {
                    case R.id.back:
                        if(CHAPTER == 1) {
                            Toast.makeText(getApplicationContext(), "First Chapter", Toast.LENGTH_LONG).show();
                            break;
                        }
                        intent.putExtra("bookname", BOOK_NAME);
                        intent.putExtra("chapter", CHAPTER-1);
                        startActivity(intent);
                        finish();
                        break;
                    case R.id.front:
                        AssetManager as = getAssets();
                        int MAX_CHAPTER;
                        try {
                            MAX_CHAPTER = as.list(FILE_NAME + BOOK_NAME + "/chapter_en").length;
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        if(CHAPTER == MAX_CHAPTER) {
                            Toast.makeText(getApplicationContext(), "Last Chapter", Toast.LENGTH_LONG).show();
                            break;
                        }
                        intent.putExtra("bookname", BOOK_NAME);
                        intent.putExtra("chapter", CHAPTER+1);
                        startActivity(intent);
                        finish();
                        break;
                }
                return true;
            }
        });



    }
    private String readText(int input) throws IOException {
        AssetManager assetManager = getAssets();
        InputStream is = assetManager.open(FILE_NAME + BOOK_NAME + "/ch" + input + ".txt");

        StringBuilder text = new StringBuilder();
        try{
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = br.readLine()) != null){
                text.append("   " + line);
                text.append("\n");
            }
            br.close();
        } catch(IOException e){
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
}