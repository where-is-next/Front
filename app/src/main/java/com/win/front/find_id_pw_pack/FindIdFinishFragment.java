package com.win.front.find_id_pw_pack;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.win.front.R;

public class FindIdFinishFragment extends Fragment {

    public static FindIdFinishFragment newInstance() {
        return new FindIdFinishFragment();
    }

    TextView searchedId;
    Button login;

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

        login = rootView.findViewById(R.id.find_id_finish_btn_login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((FindIdPw)getActivity()).finish();
            }
        });

        return rootView;
    }
}
