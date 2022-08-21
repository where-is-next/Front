package com.example.front.find_id_pw_pack;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Dimension;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.front.R;

public class find_id_finish_fragment extends Fragment {

    public static find_id_finish_fragment newInstance() {
        return new find_id_finish_fragment();
    }

    TextView searchedId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.find_id_finish_fragment, container, false);

        searchedId = rootView.findViewById(R.id.searched_id);
        if (getArguments() != null)
        {
            String id = getArguments().getString("search_id"); // 프래그먼트1에서 받아온 값 넣기
            searchedId.setText(id);
        }

        return rootView;
    }
}
