package org.techtown.ebookbgm;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ChapterList extends AppCompatActivity {

    final static String FILE_NAME = "aliceinwonderland_chapter_divided";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chapter_list);


        ListView listView = findViewById(R.id.listview);

        List<String> list = new ArrayList<>();

        AssetManager assetManager = getAssets();
        InputStream is = null;
        try {
            is = assetManager.open(FILE_NAME + "/chapterlist.txt");
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
                try {
                    readText(position+1);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

        });
    }

    private String readText(int input) throws IOException {
        AssetManager assetManager = getAssets();
        InputStream is = assetManager.open(FILE_NAME + "/chapter"+input +".txt");

        StringBuilder text = new StringBuilder();
        try{
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = br.readLine()) != null){
                text.append(line);
                text.append("\n");
            }
            br.close();
        } catch(IOException e){
            e.printStackTrace();
        }
        return text.toString();
    }
}