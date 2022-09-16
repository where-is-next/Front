package com.win.front.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.win.front.R;
import com.win.front.dto.AllPostDTO;
import com.win.front.dto.PostDTO;
import com.win.front.find_id_pw_pack.FindIdFinishFragment;
import com.win.front.retorfit.RetrofitAPI;
import com.win.front.retorfit.RetrofitClient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.zip.CheckedOutputStream;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostListFragment extends Fragment {

    public static PostListFragment newInstance() {
        return new PostListFragment();
    }

    ImageView post_add_btn;
    private RetrofitClient retrofitClient;      // retrofit2 객체 참조 변수
    private RetrofitAPI retrofitAPI;            // retrofit2 api 객체 참조 변수

    List<AllPostDTO> allPost;

    LinearLayout linearLayout;

    ListView listView;
    PostListAdapter postListAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.post_fragment_post_list, container, false);

        post_add_btn = rootView.findViewById(R.id.post_add_btn);
        post_add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), PostAddActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });

        linearLayout = rootView.findViewById(R.id.post_list_layout);
        listView = (ListView) rootView.findViewById(R.id.listView);

        // 포스트를 저장
        setAllPost();

        return rootView;
    }

    // 모든 포스트 정보를 가져오는 함수
    public void setAllPost() {

        retrofitClient = RetrofitClient.getInstance();
        retrofitAPI = RetrofitClient.getRetrofitInterface();

        // 포스트가 있는지 체크
        retrofitAPI.isPostResponse().enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {

                // 포스트가 존재하지 않는다면
                if (!response.body()) {
                    TextView textViewNm = new TextView(linearLayout.getContext());
                    textViewNm.setText("존재하는 포스트가 없습니다.");
                    textViewNm.setTextSize(16);//텍스트 크기

                    LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT
                            ,LinearLayout.LayoutParams.WRAP_CONTENT);

                    param.gravity = Gravity.CENTER;
                    param.topMargin = 800;
                    linearLayout.addView(textViewNm, param);
                }

                // 포스트가 존재하므로 가져온다.
                else {
                    retrofitAPI.getAllPostResponse().enqueue(new Callback<List<AllPostDTO>>() {
                        @Override
                        public void onResponse(Call<List<AllPostDTO>> call, Response<List<AllPostDTO>> response) {
                            allPost = response.body();
                            setAllPostList();
                        }
                        @Override
                        public void onFailure(Call<List<AllPostDTO>> call, Throwable t) {
                        }
                    });
                }
            }
            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
            }
        });
    }

    public void setAllPostList() {
        postListAdapter = new PostListAdapter();

        Collections.reverse(allPost);

        for (AllPostDTO allPostDTO : allPost) {
            PostListItem postListItem = new PostListItem();

            postListItem.setTitle(allPostDTO.getTitle());
            postListItem.setNickname(allPostDTO.getNickname());
            postListItem.setDate(allPostDTO.getDate());
            postListItem.setContents(allPostDTO.getContents());

            if (!allPostDTO.getImages().isEmpty()) {
                postListItem.setImageURI(allPostDTO.getImages().get(0));
            }
            else {
                postListItem.setImageURI("");
            }

            postListAdapter.addItem(postListItem.getTitle(), postListItem.getNickname(), postListItem.getDate(), postListItem.getContents(), postListItem.getImageURI());
        }

        listView.setAdapter(postListAdapter);
    }
}
