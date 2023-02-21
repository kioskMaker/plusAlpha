package org.techtown.ebookbgm;



import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.List;

public class TextPagerAdapter extends FragmentPagerAdapter {

    private final List<CharSequence> pageTexts;

    public TextPagerAdapter(FragmentManager fm, List<CharSequence> pageTexts) {
        super(fm);
        this.pageTexts = pageTexts;
    }

    @Override
    public Fragment getItem(int i) {
        return PageFragment.newInstance(pageTexts.get(i));
    }

    @Override
    public int getCount() {
        return pageTexts.size();
    }

    public CharSequence getPageTexts(int i){
        return pageTexts.get(i);
    }
}