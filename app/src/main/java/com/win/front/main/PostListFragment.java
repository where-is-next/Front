package com.win.front.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.win.front.R;
import com.win.front.dto.AllPostDTO;
import com.win.front.retorfit.RetrofitAPI;
import com.win.front.retorfit.RetrofitClient;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

    SearchView post_search_text;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.post_fragment_post_list, container, false);

        // 포스트 추가 버튼
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

        // 리스트 아이템 클릭 시 뷰로 보여줌
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                PostListItem selectedItem = (PostListItem) adapterView.getAdapter().getItem(i);
            }
        });

        // 포스트를 저장
        setAllPost();

        // 포스트 검색검색
        post_search_text = rootView.findViewById(R.id.post_search_text);
        post_search_text.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                postListAdapter = new PostListAdapter();

                for (AllPostDTO temp_allPostItem : allPost) {
                    if (temp_allPostItem.getTitle().contains(s) || temp_allPostItem.getContents().contains(s)) {
                        PostListItem postListItem = new PostListItem();

                        postListItem.setTitle(temp_allPostItem.getTitle());
                        postListItem.setNickname(temp_allPostItem.getNickname());
                        postListItem.setDate(temp_allPostItem.getDate());
                        postListItem.setContents(temp_allPostItem.getContents());
                        postListItem.setPost_number(Long.toString(temp_allPostItem.getNumber()));

                        if (!temp_allPostItem.getAllImages().isEmpty()) {
                            postListItem.setImageURI(temp_allPostItem.getAllImages().get(0));
                        }
                        else {
                            postListItem.setImageURI(null);
                        }

                        postListAdapter.addItem(postListItem.getTitle(), postListItem.getNickname(), postListItem.getDate(),
                                postListItem.getContents(), postListItem.getImageURI(), postListItem.getPost_number());
                    }
                }

                listView.setAdapter(postListAdapter);
                return false;
            }
        });

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
                    textViewNm.setLayoutParams(param);
                    linearLayout.addView(textViewNm);
                }

                // 포스트가 존재하므로 가져온다.
                else {
                    retrofitAPI.getAllPostResponse().enqueue(new Callback<JsonArray>() {
                        @Override
                        public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                            JsonArray jsonArray = response.body();

                            allPost = new ArrayList<>();

                            for (int i = 0; i < jsonArray.size(); i++) {
                                AllPostDTO input = new AllPostDTO();
                                JsonObject object = (JsonObject) jsonArray.get(i);

                                Long number = Long.parseLong(object.get("number").getAsString());
                                String id = object.get("id").getAsString();
                                String nickname = object.get("nickname").getAsString();
                                String date = object.get("date").getAsString();
                                String title = object.get("title").getAsString();
                                String contents = object.get("contents").getAsString();

                                ArrayList<byte[]> allImages = new ArrayList<>();
                                JsonArray tempImageArr = (JsonArray) object.get("allImages");
                                for (int j = 0; j < tempImageArr.size(); j++) {
                                    JsonElement jsonElement = tempImageArr.get(j);
                                    byte[] bytes = new Gson().toJson(jsonElement).getBytes(StandardCharsets.UTF_8);

                                    allImages.add(bytes);
                                }

                                input.setNumber(number);
                                input.setId(id);
                                input.setNickname(nickname);
                                input.setDate(date);
                                input.setTitle(title);
                                input.setContents(contents);
                                input.setAllImages(allImages);

                                allPost.add(input);
                            }

                            setAllPostList();
                        }
                        @Override
                        public void onFailure(Call<JsonArray> call, Throwable t) {
                            System.out.println("에러 : " + t.getMessage());
                        }
                    });
                }
            }
            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                System.out.println("서버통신 실패");
            }
        });
    }

    // 포스트를 리스트에 셋팅
    public void setAllPostList() {
        postListAdapter = new PostListAdapter();

        Collections.reverse(allPost);

        for (AllPostDTO allPostDTO : allPost) {
            PostListItem postListItem = new PostListItem();

            postListItem.setTitle(allPostDTO.getTitle());
            postListItem.setNickname(allPostDTO.getNickname());
            postListItem.setDate(allPostDTO.getDate());
            postListItem.setContents(allPostDTO.getContents());
            postListItem.setPost_number(Long.toString(allPostDTO.getNumber()));

            if (!allPostDTO.getAllImages().isEmpty()) {
                postListItem.setImageURI(allPostDTO.getAllImages().get(0));
            }
            else {
                postListItem.setImageURI(null);
            }

            postListAdapter.addItem(postListItem.getTitle(), postListItem.getNickname(), postListItem.getDate(),
                    postListItem.getContents(), postListItem.getImageURI(), postListItem.getPost_number());
        }

        listView.setAdapter(postListAdapter);
    }
}
