package com.win.front.main;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.win.front.R;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;

public class PostMyListAdapter extends BaseAdapter{
    ArrayList<PostListItem> items = new ArrayList<>();
    ImageView list_item_menu;

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
        if (converView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            converView = inflater.inflate(R.layout.post_fragment_post_my_list_item, viewGroup, false);
        }

        TextView tv_title = (TextView) converView.findViewById(R.id.post_view_title) ;
        TextView tv_nickname = (TextView) converView.findViewById(R.id.post_view_nickname) ;
        TextView tv_date = (TextView) converView.findViewById(R.id.post_view_date) ;
        TextView tv_contents = (TextView) converView.findViewById(R.id.post_view_contents) ;

        PostListItem myItem = getItem(i);

        tv_title.setText(myItem.getTitle());
        tv_nickname.setText(myItem.getNickname());
        tv_date.setText(myItem.getDate());
        tv_contents.setText(myItem.getContents());

        if (myItem.getImageURI() != null) {
            ImageView tv_imageView = (ImageView) converView.findViewById(R.id.post_view_image);
            tv_imageView.setImageBitmap(byteArrayToBitmap(myItem.getImageURI()));

            ViewGroup.LayoutParams params = (ViewGroup.LayoutParams) tv_imageView.getLayoutParams();
            params.width = 1050;
            params.height = 800;

            tv_imageView.setLayoutParams(params);
        }

        // ... 버튼
        list_item_menu = converView.findViewById(R.id.list_item_menu);
        list_item_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopup(view);
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
    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(v.getContext(), v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.list_item_menu_drop_box, popup.getMenu());
        popup.show();
    }
}
