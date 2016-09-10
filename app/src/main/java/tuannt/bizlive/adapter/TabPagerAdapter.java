package tuannt.bizlive.adapter;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;

import java.util.ArrayList;

import tuannt.bizlive.fragment.FragmentCategory;
import tuannt.bizlive.fragment.FragmentNew;
import tuannt.bizlive.fragment.FragmentHome;
import tuannt.bizlive.fragment.FragmentHot;
import tuannt.bizlive.model.JSONArrayData;
import tuannt.bizlive.model.Post;

/**
 * Created by Tuan on 10/1/2015.
 */
public class TabPagerAdapter extends FragmentStatePagerAdapter {

    static int mNumOfTabs = 4;
    Activity context;

    ObservableScrollViewCallbacks scrollListener;
    JSONArrayData data;
    ArrayList<Post> listHome = new ArrayList<>();
    ArrayList<Post> listLast = new ArrayList<>();
    ArrayList<Post> listHot = new ArrayList<>();
    ArrayList<Post> listHomeSub = new ArrayList<>();


    public TabPagerAdapter(FragmentManager fm, final Activity context) {
        super(fm);
        this.context = context;

    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                FragmentHome tab0 = new FragmentHome();
//
                return tab0;
            case 1:
                FragmentHot tabHot = new FragmentHot();
                return tabHot;
            case 2:

                FragmentNew tab1 = new FragmentNew();

//
                return tab1;

            case 3:
                FragmentCategory tab2 = new FragmentCategory();
                return tab2;


            default:
                return null;
        }
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_UNCHANGED;
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }

//	@Override


    @Override
    public void notifyDataSetChanged() {
        Log.d("LOGTAG", "pager change");
        super.notifyDataSetChanged();
    }
}
