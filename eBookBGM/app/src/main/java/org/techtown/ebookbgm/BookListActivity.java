package org.techtown.ebookbgm;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BookListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book_item);
        List<BookDataStructure> bookDataStructures = new ArrayList<>();
        AssetManager assetManager = getAssets();
        String[] strings = new String[0];

        try {
            strings = assetManager.list("books");
        } catch (IOException e) {
            e.printStackTrace();
        }

        for(int i=0;i<strings.length;i++){

            int getId = getResources().getIdentifier(strings[i], "drawable", this.getPackageName());
            BookDataStructure bookDataStructure = new BookDataStructure(strings[i], getDrawable(getId));
            bookDataStructures.add(bookDataStructure);
            Log.d("Mybooklist", strings[i]);
        }


        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        CustomAdapter customAdapter = new CustomAdapter(bookDataStructures);
        recyclerView.setAdapter(customAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));



    }

}