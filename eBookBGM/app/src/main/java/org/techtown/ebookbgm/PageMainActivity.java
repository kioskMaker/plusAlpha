package org.techtown.ebookbgm;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;


import android.content.res.AssetManager;
import android.graphics.Color;
import android.os.Bundle;

import android.text.TextPaint;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class PageMainActivity extends FragmentActivity{
    private ViewPager pagesView;
    final static String FILE_NAME = "books/aliceinwonderland";
    int sentence_num;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page_main);

        pagesView = (ViewPager) findViewById(R.id.pages);


        String str = null;
        try {
            str = readText(1);
        } catch (IOException e) {
            Log.d("Mypagesplitter", "onCreate IOException");
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
                    if(str.substring(str.length()-2).equals("\n")){
                        String[] strings = str.split("\n");
                        sentence_num = strings.length;
                    }
                    else{
                        String[] strings = str.split("\n");
                        sentence_num = strings.length-1;
                    }
                    Log.d("Mypager", ""+sentence_num);
                }

            }
        });

    }
    private String readText(int input) throws IOException {
        AssetManager assetManager = getAssets();
        InputStream is = assetManager.open(FILE_NAME + "/ch" + input + ".txt");

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



}