package org.techtown.ebookbgm;

import android.graphics.drawable.Drawable;
import android.widget.ImageView;

public class BookDataStructure {

    private String book_title;
    private Drawable drawable;

    public BookDataStructure(String book_title, Drawable drawable){
        this.book_title = book_title;
        this.drawable = drawable;
    }

    public String getBook_title() {
        return book_title;
    }

    public void setBook_title(String book_title) {
        this.book_title = book_title;
    }

    public Drawable getDrawable() {
        return drawable;
    }

    public void setDrawable(Drawable drawable) {
        this.drawable = drawable;
    }

}
