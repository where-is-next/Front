package com.win.front.main;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Base64;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.win.front.R;
import com.win.front.domain.Comment;
import com.win.front.domain.Location;
import com.win.front.dto.AllPostDTO;
import com.win.front.dto.CommentDTO;
import com.win.front.dto.PostDTO;
import com.win.front.retorfit.RetrofitAPI;
import com.win.front.retorfit.RetrofitClient;

import net.daum.mf.map.api.MapPoint;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostAllView extends AppCompatActivity {

    String selected_number;

    ImageView post_all_view_back_view;

    TextView post_all_view_title;
    TextView post_all_view_nickname;
    TextView post_all_view_date;
    TextView comment_text;

    LinearLayout post_all_view_input_images;

    private RetrofitClient retrofitClient;      // retrofit2 객체 참조 변수
    private RetrofitAPI retrofitAPI;            // retrofit2 api 객체 참조 변수

    AllPostDTO selected_info;
    List<AllPostDTO> allPost;

    EditText post_commnet;

    private String userId;
    private SharedPreferences sp;

    PostAllViewCommentAdapter postAllViewCommentAdapter;
    ListView comment_listView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_fragment_view_post);

        Intent intent = getIntent();

        selected_number = intent.getStringExtra("selected_number");
        sp = getSharedPreferences("UserInfo", MODE_PRIVATE);
        userId = sp.getString("userId", "");

        post_all_view_title = findViewById(R.id.post_all_view_title);
        post_all_view_nickname = findViewById(R.id.post_all_view_nickname);
        post_all_view_date = findViewById(R.id.post_all_view_date);
        comment_text = findViewById(R.id.comment_text);

        post_all_view_input_images = findViewById(R.id.post_all_view_input_images);

        // 모든 포스트 정보 및 선택한 정보를 셋팅
        setAllPostInfo();

        // 키보드 내리기 위한 매니저
        InputMethodManager mInputMethodManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);

        // 댓글
        post_commnet = findViewById(R.id.post_commnet);
        post_commnet.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionSearch, KeyEvent keyEvent) {
                switch (actionSearch) {
                    case EditorInfo.IME_ACTION_DONE:

                        String inputComment = post_commnet.getText().toString();

                        if (inputComment.equals("")) {
                            Toast.makeText(PostAllView.this, "댓글을 입력해주세요.", Toast.LENGTH_LONG).show();
                        }
                        else {
                            mInputMethodManager.hideSoftInputFromWindow(post_commnet.getWindowToken(), 0); // 검색을 누르면 키보드 내림
                            post_commnet.setText(null);

                            CommentDTO commentDTO = new CommentDTO();

                            retrofitClient = RetrofitClient.getInstance();
                            retrofitAPI = RetrofitClient.getRetrofitInterface();

                            retrofitAPI.getNickNameResponse(userId).enqueue(new Callback<String>() {
                                @Override
                                public void onResponse(Call<String> call, Response<String> response) {
                                    String nickname = response.body().replaceAll("\"", "");

                                    commentDTO.setPost_number(selected_number);
                                    commentDTO.setComment_nickname(nickname);
                                    commentDTO.setComment_contents(inputComment);

                                    // 댓글 셋팅
                                    retrofitAPI.getAddCommentResponse(commentDTO).enqueue(new Callback<Long>() {
                                        @Override
                                        public void onResponse(Call<Long> call, Response<Long> response) {
                                            Long input = response.body();
                                            postAllViewCommentAdapter.addItem(input, commentDTO.getPost_number(), commentDTO.getComment_nickname(),
                                                    commentDTO.getComment_contents());

                                            comment_listView.setAdapter(postAllViewCommentAdapter);
                                            setListViewHeightBasedOnChildren(comment_listView);
                                        }
                                        @Override
                                        public void onFailure(Call<Long> call, Throwable t) {
                                            System.out.println("에러 : " + t.getMessage());
                                        }
                                    });
                                }

                                @Override
                                public void onFailure(Call<String> call, Throwable t) {
                                }
                            });
                        }
                        break;
                }
                return true;
            }
        });

        // 뒤로 돌아가기
        post_all_view_back_view = findViewById(R.id.post_all_view_back_view);
        post_all_view_back_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // 댓글 리스트
        comment_listView = findViewById(R.id.comment_listView);
        comment_listView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                comment_listView.requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
    }

    // 모든 포스트 정보를 가져오는 함수
    public void setAllPostInfo() {
        retrofitClient = RetrofitClient.getInstance();
        retrofitAPI = RetrofitClient.getRetrofitInterface();

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

                setPostInfo();
            }
            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                System.out.println("에러 : " + t.getMessage());
            }
        });
    }

    // 선택한 포스트의 정보를 셋팅하는 함수
    private void setPostInfo() {
        for (AllPostDTO allPostDTO : allPost) {
            if (allPostDTO.getNumber() == Long.parseLong(selected_number)) {
                selected_info = allPostDTO;
                break;
            }
        }

        // 댓글 수 셋팅
        retrofitClient = RetrofitClient.getInstance();
        retrofitAPI = RetrofitClient.getRetrofitInterface();

        retrofitAPI.getPostCommentCntResponse(Long.toString(selected_info.getNumber())).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                comment_text.setText("댓글 " + response.body().replaceAll("\"", ""));
            }
            @Override
            public void onFailure(Call<String> call, Throwable t) {
                System.out.println("에러 : " + t.getMessage());
            }
        });

        setAllVIewImages();
    }

    // 이미지 및 contents 셋팅 함수
    private void setAllVIewImages() {
        for (int i = 0; i < selected_info.getAllImages().size(); i++) {
            ImageView imageView = new ImageView(post_all_view_input_images.getContext());

            imageView.setImageBitmap(byteArrayToBitmap(selected_info.getAllImages().get(i)));
            imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.width = 1000;
            params.height = 800;

            imageView.setLayoutParams(params);

            post_all_view_input_images.addView(imageView);
        }

        // 타이틀, 닉네임, 날짜 셋팅
        post_all_view_title.setText(selected_info.getTitle());
        post_all_view_nickname.setText(selected_info.getNickname());
        post_all_view_date.setText(selected_info.getDate());

        // 컨텐츠 셋팅
        TextView post_view_contents = new TextView(post_all_view_input_images.getContext());
        post_view_contents.setText(selected_info.getContents());
        post_view_contents.setTextSize(15);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.topMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, getResources().getDisplayMetrics());

        post_view_contents.setLayoutParams(params);

        post_all_view_input_images.addView(post_view_contents);

        // 댓글 셋팅
        setAllComment();
    }

    // 댓글을 셋팅하는 함수
    private void setAllComment() {
        postAllViewCommentAdapter = new PostAllViewCommentAdapter();
        postAllViewCommentAdapter.setlistener(listener);

        retrofitClient = RetrofitClient.getInstance();
        retrofitAPI = RetrofitClient.getRetrofitInterface();

        retrofitAPI.getAllCommentResponse(selected_number).enqueue(new Callback<List<Comment>>() {
            @Override
            public void onResponse(Call<List<Comment>> call, Response<List<Comment>> response) {
                List<Comment> body = response.body();

                if (!body.isEmpty()) {
                    for (Comment comment : body) {
                        PostAllViewCommentItem postAllViewCommentItem = new PostAllViewCommentItem();
                        postAllViewCommentItem.setComment_number(comment.getNumber());
                        postAllViewCommentItem.setComment_post_number(comment.getNumber_post());
                        postAllViewCommentItem.setComment_nickname(comment.getNickname());
                        postAllViewCommentItem.setComment_contents(comment.getContents());

                        postAllViewCommentAdapter.addItem(postAllViewCommentItem.getComment_number(),
                                postAllViewCommentItem.getComment_post_number(),
                                postAllViewCommentItem.getComment_nickname(),
                                postAllViewCommentItem.getComment_contents());
                    }

                    comment_listView.setAdapter(postAllViewCommentAdapter);
                    setListViewHeightBasedOnChildren(comment_listView);
                }
            }
            @Override
            public void onFailure(Call<List<Comment>> call, Throwable t) {
                System.out.println("에러 : " + t.getMessage());
            }
        });

    }

    // Byte를 Bitmap으로 변환
    public Bitmap byteArrayToBitmap(byte[] byteArray ) {
        byte[] decodedImageBytes = Base64.decode(byteArray, Base64.DEFAULT);
        ByteArrayInputStream inStream = new ByteArrayInputStream(decodedImageBytes);

        Bitmap bitmap = BitmapFactory.decodeStream(inStream);

        return bitmap;
    }

    // 리스트 뷰 크기 조절
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.AT_MOST);

        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    // 삭제했을 때의 리스너
    CommentDeleteListener listener = new CommentDeleteListener() {
        @Override
        public void commentDelete() {
            postAllViewCommentAdapter.notifyDataSetChanged();
            comment_listView.setAdapter(postAllViewCommentAdapter);
            setListViewHeightBasedOnChildren(comment_listView);
        }
    };
}
