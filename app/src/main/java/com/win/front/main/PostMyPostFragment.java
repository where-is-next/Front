package com.win.front.main;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
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

public class PostMyPostFragment extends Fragment {

    public static PostMyPostFragment newInstance() {
        return new PostMyPostFragment();
    }

    ImageView my_post_add_btn;
    private RetrofitClient retrofitClient;      // retrofit2 객체 참조 변수
    private RetrofitAPI retrofitAPI;            // retrofit2 api 객체 참조 변수

    List<AllPostDTO> myAllPost;

    LinearLayout my_post_list_layout;

    ListView my_listView;
    PostMyListAdapter postMyListAdapter;

    SearchView my_post_search_text;

    private SharedPreferences sp;
    String userId;

    String selected_number;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.post_fragment_my_post, container, false);

        sp = this.getActivity().getSharedPreferences("UserInfo", MODE_PRIVATE);
        userId = sp.getString("userId", "");

        // 포스트 추가 버튼
        my_post_add_btn = rootView.findViewById(R.id.my_post_add_btn);
        my_post_add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), PostAddActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });

        my_post_list_layout = rootView.findViewById(R.id.my_post_list_layout);
        my_listView = (ListView) rootView.findViewById(R.id.my_listView);

        // 리스트 아이템 클릭 시 뷰로 보여줌
        my_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                PostListItem selectedItem = (PostListItem) adapterView.getAdapter().getItem(i);

                // 포스트 넘버가 같은 allPostDTO를 가져옴
                for (AllPostDTO selected : myAllPost) {
                    if (selected.getNumber() == Long.parseLong(selectedItem.getPost_number())) {
                        Long number = selected.getNumber();
                        selected_number = Long.toString(number);
                        break;
                    }
                }

                Intent intent = new Intent(getActivity(), PostAllView.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                intent.putExtra("selected_number", selected_number);
                startActivity(intent);
            }
        });

        // 포스트를 저장
        setAllPost();

        // 포스트 검색검색
        my_post_search_text = rootView.findViewById(R.id.my_post_search_text);
        my_post_search_text.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (myAllPost != null) {
                    postMyListAdapter = new PostMyListAdapter();

                    for (AllPostDTO temp_allPostItem : myAllPost) {
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

                            postMyListAdapter.addItem(postListItem.getTitle(), postListItem.getNickname(), postListItem.getDate(),
                                    postListItem.getContents(), postListItem.getImageURI(), postListItem.getPost_number());
                        }
                    }

                    my_listView.setAdapter(postMyListAdapter);
                }

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
                    TextView textViewNm = new TextView(my_post_list_layout.getContext());
                    textViewNm.setText("존재하는 포스트가 없습니다.");
                    textViewNm.setTextSize(16);//텍스트 크기

                    LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT
                            ,LinearLayout.LayoutParams.WRAP_CONTENT);

                    param.gravity = Gravity.CENTER;
                    param.topMargin = 800;
                    textViewNm.setLayoutParams(param);
                    my_post_list_layout.addView(textViewNm);
                }

                // 포스트가 존재하므로 가져온다.
                else {
                    retrofitAPI.getMyPostResponse(userId).enqueue(new Callback<JsonArray>() {
                        @Override
                        public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                            JsonArray jsonArray = response.body();

                            myAllPost = new ArrayList<>();

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

                                myAllPost.add(input);
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
        postMyListAdapter = new PostMyListAdapter();
        postMyListAdapter.setlistener(listener);

        Collections.reverse(myAllPost);

        for (AllPostDTO allPostDTO : myAllPost) {
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

            postMyListAdapter.addItem(postListItem.getTitle(), postListItem.getNickname(), postListItem.getDate(),
                    postListItem.getContents(), postListItem.getImageURI(), postListItem.getPost_number());
        }

        my_listView.setAdapter(postMyListAdapter);
    }


    // 삭제, 수정에 관한 리스너
    PostAmendDeleteListener listener = new PostAmendDeleteListener() {
        @Override
        public void deletePost() {
            postMyListAdapter.notifyDataSetChanged();
            my_listView.setAdapter(postMyListAdapter);
        }

        @Override
        public void amendPost(String post_number) {
            Intent intent = new Intent(getActivity(), PostAmendView.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            intent.putExtra("post_number", post_number);
            startActivity(intent);
        }
    };
}
