package com.win.front.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.tabs.TabLayout;
import com.win.front.R;
import com.win.front.find_id_pw_pack.FindIdFragment;
import com.win.front.find_id_pw_pack.FindPwFragment;

public class PostFragment extends Fragment {

    TabLayout tabLayout;
    Fragment postFragment = PostListFragment.newInstance();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.post_fragment, container, false);

        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.post_frame, postFragment).commit();

        tabLayout = v.findViewById(R.id.post_tab);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();

                Fragment selected = null;
                if(position == 0){
                    selected = PostListFragment.newInstance();

                }else if (position == 1){
                    selected = PostMyPostFragment.newInstance();
                }

                getChildFragmentManager().beginTransaction().replace(R.id.post_frame, selected).commit();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


        return v;
    }
}
