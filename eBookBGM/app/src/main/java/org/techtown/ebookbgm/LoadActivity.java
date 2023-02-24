package org.techtown.ebookbgm;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;

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

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoadActivity extends AppCompatActivity {

    String BOOK_NAME = "aliceinwonderland";
    final static String INTERNAL_STORAGE_FILE_PATH = "/data/data/org.techtown.ebookbgm/files/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);

        String path = INTERNAL_STORAGE_FILE_PATH + BOOK_NAME + "/";
        File file = new File(path);
        Button button_analysis = findViewById(R.id.button_analysis);
        button_analysis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( file.exists() ){ //파일존재여부확인

                    if(file.isDirectory()){ //파일이 디렉토리인지 확인

                        File[] files = file.listFiles();

                        for( int i=0; i<files.length; i++){
                            if( files[i].delete() ){
                                Log.d("Mypager", "내부 파일삭제 성공");
                            }else{
                                Log.d("Mypager", "내부 파일삭제 실패");
                            }
                        }

                    }
                    if(file.delete()){
                        Log.d("Mypager", "파일삭제 성공");
                    }else{
                        Log.d("Mypager", "파일삭제 실패");
                    }

                }else{
                    System.out.println("파일이 존재하지 않습니다.");
                }
            }
        });

    }




}