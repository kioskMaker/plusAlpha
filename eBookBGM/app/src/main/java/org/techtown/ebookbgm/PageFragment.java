package org.techtown.ebookbgm;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

<<<<<<< Updated upstream
public class PageFragment extends Fragment {
=======
public class PageFragment extends AppCompatActivity {
>>>>>>> Stashed changes
    private final static String PAGE_TEXT = "PAGE_TEXT";

    public static PageFragment newInstance(CharSequence pageText) {
        PageFragment frag = new PageFragment();
        Bundle args = new Bundle();
        args.putCharSequence(PAGE_TEXT, pageText);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        CharSequence text = getArguments().getCharSequence(PAGE_TEXT);
        TextView pageView = (TextView) inflater.inflate(R.layout.page, container, false);
        pageView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.text_size));
        pageView.setText(text);
        return pageView;
    }
}