package org.techtown.ebookbgm;


import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {

    private List<BookDataStructure> bookDataStructures;

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;
        private final ImageView imageView;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View
            textView = (TextView) view.findViewById(R.id.book_title);
            imageView = (ImageView) view.findViewById(R.id.book_image);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(view.getContext(), ChapterList.class);
                    AssetManager am = view.getContext().getAssets();

                    try {
                        List<String> mapList = Arrays.asList(am.list("books/" + textView.getText().toString()));

                        if (mapList.contains("chapterlist.txt")) {
                            intent.putExtra("bookname", textView.getText().toString());
                            view.getContext().startActivity(intent);
                        }
                        else {
                            Toast.makeText(view.getContext(), "No book", Toast.LENGTH_SHORT).show();
                        }
                    } catch ( IOException ex){
                        ex.printStackTrace();
                    }


                }
            });
        }

        public TextView getTextView() {
            return textView;
        }
        public ImageView getImageView() {return imageView;}

    }

    public CustomAdapter(List<BookDataStructure> bookDataStructures) {
        this.bookDataStructures = bookDataStructures;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.book_list_recycler_view, viewGroup, false);

        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        BookDataStructure bookDataStructure = bookDataStructures.get(position);
        viewHolder.textView.setText(bookDataStructure.getBook_title());
        viewHolder.imageView.setImageDrawable(bookDataStructure.getDrawable());

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return bookDataStructures.size();
    }
}

