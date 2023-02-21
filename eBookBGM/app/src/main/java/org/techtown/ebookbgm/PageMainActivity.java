package org.techtown.ebookbgm;

import androidx.appcompat.app.AppCompatActivity;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class PageMainActivity extends FragmentActivity {
    private ViewPager pagesView;
    final static String FILE_NAME = "aliceinwonderland_chapter_divided";

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
            }
        });

    }
    private String readText(int input) throws IOException {
        AssetManager assetManager = getAssets();
        InputStream is = assetManager.open(FILE_NAME + "/alice_ch6.txt");

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

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
    }

    private void hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    // Shows the system bars by removing all the flags
    // except for the ones that make the content appear under the system bars.
    private void showSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

}