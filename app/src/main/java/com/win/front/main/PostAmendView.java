package com.win.front.main;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.win.front.R;
import com.win.front.dto.AllPostDTO;
import com.win.front.dto.PostDTO;
import com.win.front.dto.PostUpdateDTO;
import com.win.front.retorfit.RetrofitAPI;
import com.win.front.retorfit.RetrofitClient;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostAmendView extends AppCompatActivity {

    Button post_amend_picture_btn;
    Button post_amend_complete_btn;
    Button post_amend_cancle_btn;

    EditText post_amend_title;
    EditText post_amend_contents;
    Uri uri;

    private String userId;
    private SharedPreferences sp;

    private RetrofitClient retrofitClient;      // retrofit2 객체 참조 변수
    private RetrofitAPI retrofitAPI;            // retrofit2 api 객체 참조 변수

    String post_number;

    ProgressDialog dialog;  // 프로그레스 다이얼로그

    ImageView post_amend_back_view;

    ArrayList<Bitmap> PostAmendImage;
    List<AllPostDTO> allPostArr;
    AllPostDTO selected_info_post;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_fragment_amend_post_activity);

        PostAmendImage = new ArrayList<>();
        dialog = new ProgressDialog(this);

        Intent intent = getIntent();

        post_number = intent.getStringExtra("post_number");
        sp = getSharedPreferences("UserInfo", MODE_PRIVATE);
        userId = sp.getString("userId", "");

        // 뒤로 돌아가기
        post_amend_back_view = findViewById(R.id.post_amend_back_view);
        post_amend_back_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // 제목
        post_amend_title = findViewById(R.id.post_amend_title);
        post_amend_title.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == event.KEYCODE_ENTER)
                    return true;
                return false;
            }
        });

        // 내용
        post_amend_contents = findViewById(R.id.post_amend_contents);
        post_amend_contents.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == event.KEYCODE_ENTER)
                    return true;
                return false;
            }
        });

        // 사진 추가
        post_amend_picture_btn = findViewById(R.id.post_amend_picture_btn);
        post_amend_picture_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityResult.launch(intent);
            }
        });

        // 취소버튼
        post_amend_cancle_btn = findViewById(R.id.post_amend_cancle_btn);
        post_amend_cancle_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // 완료 버튼
        post_amend_complete_btn = findViewById(R.id.post_amend_complete_btn);
        post_amend_complete_btn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                String input_title = post_amend_title.getText().toString();
                String input_contents = post_amend_contents.getText().toString();

                if (input_title.equals("")) {
                    custom_dialog_one_text_check("제목을 입력해주세요.");
                } else if (input_contents.equals("")) {
                    custom_dialog_one_text_check("내용을 입력해주세요.");
                } else {
                    // 현재 날짜 구하기
                    LocalDate now = LocalDate.now();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
                    String formatedNow = now.format(formatter);


                    amendPost(input_title, input_contents, formatedNow);
                }
            }
        });

        // 선택한 포스트를 불러오는 함수
        settingPost();

    }

    // 선택한 포스트를 불러오는 함수
    private void settingPost() {
        retrofitClient = RetrofitClient.getInstance();
        retrofitAPI = RetrofitClient.getRetrofitInterface();

        retrofitAPI.getAllPostResponse().enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                JsonArray jsonArray = response.body();

                allPostArr = new ArrayList<>();

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

                    allPostArr.add(input);
                }

                settingPostInfo();
            }
            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                System.out.println("에러 : " + t.getMessage());
            }
        });
    }

    // 선택한 포스트의 정보를 셋팅하는 함수
    private void settingPostInfo() {
        for (AllPostDTO allPostDTO : allPostArr) {
            if (allPostDTO.getNumber() == Long.parseLong(post_number)) {
                selected_info_post = allPostDTO;
                break;
            }
        }

        settingPostContents();
    }

    // 이미지 및 contents 셋팅 함수
    private void settingPostContents() {
        // 제목 및 컨텐츠 셋팅
        post_amend_title.setText(selected_info_post.getTitle());
        post_amend_contents.setText(selected_info_post.getContents());

        for (int i = 0; i < selected_info_post.getAllImages().size(); i++) {
            LinearLayout picture_layout = (LinearLayout) findViewById(R.id.amend_picture_layout);

            ImageView imageView = new ImageView(picture_layout.getContext());
            Bitmap bitmap = byteArrayToBitmap(selected_info_post.getAllImages().get(i));
            imageView.setImageBitmap(bitmap);

            CardView cardView = new CardView(picture_layout.getContext());
            cardView.addView(imageView);
            cardView.setRadius(10);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 80, getResources().getDisplayMetrics());
            params.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 80, getResources().getDisplayMetrics());
            params.leftMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15, getResources().getDisplayMetrics());
            params.gravity = Gravity.CENTER;

            PostAmendImage.add(bitmap);

            picture_layout.addView(cardView,params);

            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    BitmapDrawable dr = (BitmapDrawable) imageView.getDrawable();
                    Bitmap tempBitmap = dr.getBitmap();

                    PostAmendImage.remove(tempBitmap);
                    picture_layout.removeView(cardView);
                }
            });
        }
    }

    // 갤러리에서 이미지 선택하여 저장 및 View Control
    ActivityResultLauncher<Intent> startActivityResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if( result.getResultCode() == RESULT_OK && result.getData() != null){

                        uri = result.getData().getData();

                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);

                            LinearLayout picture_layout = (LinearLayout) findViewById(R.id.amend_picture_layout);
                            ImageView imageView = new ImageView(picture_layout.getContext());
                            imageView.setImageBitmap(bitmap);

                            CardView cardView = new CardView(picture_layout.getContext());
                            cardView.addView(imageView);
                            cardView.setRadius(10);

                            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            param.width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 80, getResources().getDisplayMetrics());
                            param.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 80, getResources().getDisplayMetrics());
                            param.leftMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15, getResources().getDisplayMetrics());
                            param.gravity = Gravity.CENTER;

                            PostAmendImage.add(bitmap);

                            picture_layout.addView(cardView,param);
                            cardView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    BitmapDrawable dr = (BitmapDrawable) imageView.getDrawable();
                                    Bitmap tempBitmap = dr.getBitmap();

                                    PostAmendImage.remove(tempBitmap);
                                    picture_layout.removeView(cardView);
                                }
                            });

                        }catch (FileNotFoundException e){
                            e.printStackTrace();
                        }catch (IOException e){
                            e.printStackTrace();
                        }
                    }
                }
            });

    // 포스트 수정
    private void amendPost(String input_title, String input_contents, String formatedNow) {
        retrofitClient = RetrofitClient.getInstance();
        retrofitAPI = RetrofitClient.getRetrofitInterface();

        ArrayList<byte[]> input_image = new ArrayList<>();

        if (!PostAmendImage.isEmpty()) {
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);  // 다이얼로그 스타일 설정
            dialog.setCancelable(false);    // 화면 밖 터치해도 종료되지 않게
            dialog.setMessage("Loading ...");   // 메세지
            dialog.show();  // 다이얼로그 시작

            for (int i = 0; i < PostAmendImage.size(); i++) {
                Bitmap resizeBitmap = resize(PostAmendImage.get(i)); //사이즈 조정
                byte[] image = bitmapToByteArray(resizeBitmap);

                input_image.add(image);
            }
        }

        retrofitAPI.getNickNameResponse(userId).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                String nickname = response.body().replaceAll("\"", "");

                PostUpdateDTO postUpdateDTO = new PostUpdateDTO(post_number, userId, nickname, formatedNow, input_title, input_contents, input_image);

                // 포스트를 저장
                retrofitAPI.amendPostMethod(postUpdateDTO).enqueue(new Callback<Boolean>() {
                    @Override
                    public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                        if (response.body()) {
                            dialog.dismiss();
                            custom_dialog_one_text_finish("글이 수정되었습니다.");
                        }
                    }
                    @Override
                    public void onFailure(Call<Boolean> call, Throwable t) {
                    }
                });
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
            }
        });
    }

    //비트맵을 바이너리 바이트배열로 바꾸어주는 메서드
    public byte[] bitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }

    // Byte를 Bitmap으로 변환
    public Bitmap byteArrayToBitmap( byte[] byteArray ) {
        byte[] decodedImageBytes = Base64.decode(byteArray, Base64.DEFAULT);
        ByteArrayInputStream inStream = new ByteArrayInputStream(decodedImageBytes);

        Bitmap bitmap = BitmapFactory.decodeStream(inStream);

        return bitmap;
    }

    // 비트맵 사이즈 조정
    private Bitmap resize(Bitmap bm){
        bm = Bitmap.createScaledBitmap(bm, 1050, 800, true);

        return bm;
    }

    // 커스텀 다이얼로그 : one_text, 등록 완료용
    public void custom_dialog_one_text_finish(String message) {
        LayoutInflater inflater= getLayoutInflater();
        View view = inflater.inflate(R.layout.custom_alert_dialog_one_text, null);
        ((TextView)view.findViewById(R.id.first_text)).setText(message);

        AlertDialog alert = new AlertDialog.Builder(this)
                .setView(view)
                .setCancelable(false)
                .create();

        alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alert.requestWindowFeature(Window.FEATURE_NO_TITLE);

        view.findViewById(R.id.alert_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alert.dismiss();
                finish();
            }
        });

        alert.show();

        WindowManager.LayoutParams params = alert.getWindow().getAttributes();
        params.width = 900;
        alert.getWindow().setAttributes(params);
    }

    // 커스텀 다이얼로그 : one_text, 널값 확인용
    public void custom_dialog_one_text_check(String message) {
        LayoutInflater inflater= getLayoutInflater();
        View view = inflater.inflate(R.layout.custom_alert_dialog_one_text, null);
        ((TextView)view.findViewById(R.id.first_text)).setText(message);

        AlertDialog alert = new AlertDialog.Builder(this)
                .setView(view)
                .setCancelable(false)
                .create();

        alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alert.requestWindowFeature(Window.FEATURE_NO_TITLE);

        view.findViewById(R.id.alert_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alert.dismiss();
            }
        });

        alert.show();

        WindowManager.LayoutParams params = alert.getWindow().getAttributes();
        params.width = 900;
        alert.getWindow().setAttributes(params);
    }
}
