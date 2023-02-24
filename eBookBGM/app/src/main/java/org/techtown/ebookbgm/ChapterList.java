package org.techtown.ebookbgm;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ChapterList extends AppCompatActivity {

    String BOOK_NAME;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chapter_list);

        Toolbar mToolbar = findViewById(R.id.toolbar2);
        setSupportActionBar(mToolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false); //기본 타이틀 보여줄지 말지 설정
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼, 디폴트로 true만 해도 백버튼이 생김

        Intent intent = getIntent();
        BOOK_NAME = intent.getExtras().getString("bookname");

        ListView listView = findViewById(R.id.listview);

        List<String> list = new ArrayList<>();

        AssetManager assetManager = getAssets();
        InputStream is = null;
        try {
            is = assetManager.open("books/" + BOOK_NAME + "/chapterlist.txt");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try{
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = br.readLine()) != null){
                list.add(line);
            }
            br.close();
        } catch(IOException e){
            e.printStackTrace();
        }


        ArrayAdapter arrayAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1,list);
        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), PageMainActivity.class);
                intent.putExtra("bookname", BOOK_NAME);
                intent.putExtra("chapter", position+1);
                startActivity(intent);
            }

        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{ //toolbar의 back키 눌렀을 때 동작
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

}