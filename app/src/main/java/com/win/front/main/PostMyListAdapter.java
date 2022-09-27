package com.win.front.main;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.win.front.R;
import com.win.front.dto.CommentDeleteDTO;
import com.win.front.retorfit.RetrofitAPI;
import com.win.front.retorfit.RetrofitClient;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Iterator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostMyListAdapter extends BaseAdapter{
    ArrayList<PostListItem> items = new ArrayList<>();
    ImageView list_item_menu;
    private PostDeleteListener listener;

    private RetrofitClient retrofitClient;      // retrofit2 객체 참조 변수
    private RetrofitAPI retrofitAPI;            // retrofit2 api 객체 참조 변수

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public PostListItem getItem(int i) {
        return items.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View converView, ViewGroup viewGroup) {
        Context context = viewGroup.getContext();

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        converView = inflater.inflate(R.layout.post_fragment_post_my_list_item, viewGroup, false);

        TextView tv_title = (TextView) converView.findViewById(R.id.my_post_view_title) ;
        TextView tv_nickname = (TextView) converView.findViewById(R.id.my_post_view_nickname) ;
        TextView tv_date = (TextView) converView.findViewById(R.id.my_post_view_date) ;
        TextView tv_contents = (TextView) converView.findViewById(R.id.my_post_view_contents) ;

        PostListItem myItem = getItem(i);

        tv_title.setText(myItem.getTitle());
        tv_nickname.setText(myItem.getNickname());
        tv_date.setText(myItem.getDate());
        tv_contents.setText(myItem.getContents());

        if (myItem.getImageURI() != null) {
            ImageView tv_imageView = (ImageView) converView.findViewById(R.id.my_post_view_image);
            tv_imageView.setImageBitmap(byteArrayToBitmap(myItem.getImageURI()));

            ViewGroup.LayoutParams params = (ViewGroup.LayoutParams) tv_imageView.getLayoutParams();
            params.width = 1050;
            params.height = 800;

            tv_imageView.setLayoutParams(params);
        }

        // ... 버튼
        list_item_menu = converView.findViewById(R.id.my_list_item_menu);
        list_item_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopup(view, getItem(i).getPost_number());
            }
        });

        return converView;
    }

    public void addItem(String title, String nickname, String date, String contents, byte[] imageUri, String post_number) {

        PostListItem mItem = new PostListItem();

        mItem.setTitle(title);
        mItem.setNickname(nickname);
        mItem.setDate(date);
        mItem.setContents(contents);
        mItem.setImageURI(imageUri);
        mItem.setPost_number(post_number);

        items.add(mItem);
    }

    // Byte를 Bitmap으로 변환
    public Bitmap byteArrayToBitmap( byte[] byteArray ) {
        byte[] decodedImageBytes = Base64.decode(byteArray, Base64.DEFAULT);
        ByteArrayInputStream inStream = new ByteArrayInputStream(decodedImageBytes);

        Bitmap bitmap = BitmapFactory.decodeStream(inStream);

        return bitmap;
    }

    // 팝업 보여주기
    public void showPopup(View v, String post_number) {
        PopupMenu popup = new PopupMenu(v.getContext(), v);
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.list_item_menu_box_amend:
                        System.out.println("수정합니다." + post_number);
                        return true;

                    case R.id.list_item_menu_box_delete:
                        System.out.println("삭제합니다 : " + post_number);

                        retrofitClient = RetrofitClient.getInstance();
                        retrofitAPI = RetrofitClient.getRetrofitInterface();

                        retrofitAPI.getDeletePostResponse(post_number).enqueue(new Callback<Boolean>() {
                            @Override
                            public void onResponse(Call<Boolean> call, Response<Boolean> response) {

                                for (Iterator<PostListItem> iterator = items.iterator(); iterator.hasNext();) {
                                    PostListItem item = iterator.next();
                                    if (item.getPost_number().equals(post_number)) {
                                        iterator.remove();
                                    }
                                }

                                Toast myToast = Toast.makeText(v.getContext(),"포스트가 삭제되었습니다.", Toast.LENGTH_SHORT);
                                myToast.show();
                                listener.deletePost();
                            }
                            @Override
                            public void onFailure(Call<Boolean> call, Throwable t) {
                                System.out.println("에러 : " + t.getMessage());
                            }
                        });

                        return true;
                    default:
                        return false;
                }
            }
        });

        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.list_item_menu_drop_box, popup.getMenu());
        popup.show();
    }

    public void setlistener(PostDeleteListener postDeleteListener) {
        this.listener = postDeleteListener;
    }
}
