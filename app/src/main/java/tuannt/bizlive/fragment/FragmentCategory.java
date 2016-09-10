package tuannt.bizlive.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;

import java.util.ArrayList;

import tuannt.bizlive.activity.MainActivity;
import tuannt.bizlive.activity.R;
import tuannt.bizlive.model.Zone;

/**
 * Created by Administrator on 11/17/2015.
 */
//public class FragmentCategory extends Fr {
//}
public class FragmentCategory extends Fragment {
    // Scrolling scrolling;

    ObservableScrollViewCallbacks scrollListener;


    public FragmentCategory() {
       // this.arrDataZone = dataZone;
        //this.scrollListener = scrollListener;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        // Log.d("OnCreat4", "CategoryListFragment");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view;
        view = inflater.inflate(R.layout.fragmentcategory, container,
                false);
        FragmentTransaction transaction = getFragmentManager()
                .beginTransaction();

        transaction.replace(R.id.category_fragment, new TabCategoryFragment());

        transaction.commit();

        return view;

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        scrollListener = (ObservableScrollViewCallbacks) activity;
    }
}