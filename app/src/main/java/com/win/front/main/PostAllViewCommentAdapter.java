package com.win.front.main;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
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

import java.util.ArrayList;
import java.util.Iterator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostAllViewCommentAdapter extends BaseAdapter {
    ArrayList<PostAllViewCommentItem> items = new ArrayList<>();
    ImageView list_item_menu;

    private RetrofitClient retrofitClient;      // retrofit2 객체 참조 변수
    private RetrofitAPI retrofitAPI;            // retrofit2 api 객체 참조 변수

    private String userId;
    private SharedPreferences sp;

    private CommentDeleteListener listener;

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public PostAllViewCommentItem getItem(int i) {
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
            converView = inflater.inflate(R.layout.post_fragment_view_post_comment_item, viewGroup, false);
        }

        sp = converView.getContext().getSharedPreferences("UserInfo", MODE_PRIVATE); // 로그인한 유저 ID를 담고 있는 변수
        userId = sp.getString("userId", "");

        TextView tv_comment_nickname = (TextView) converView.findViewById(R.id.comment_nickname);
        TextView tv_comment_contents = (TextView) converView.findViewById(R.id.comment_contents);

        PostAllViewCommentItem myItem = getItem(i);

        tv_comment_nickname.setText(myItem.getComment_nickname());
        tv_comment_contents.setText(myItem.getComment_contents());

        // ... 버튼
        list_item_menu = converView.findViewById(R.id.comment_list_item_menu);
        list_item_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopup(view, getItem(i).getComment_number());
            }
        });

        return converView;
    }

    public void addItem(Long number, String post_number, String nickname, String contents) {

        PostAllViewCommentItem mItem = new PostAllViewCommentItem();

        mItem.setComment_number(number);
        mItem.setComment_post_number(post_number);
        mItem.setComment_nickname(nickname);
        mItem.setComment_contents(contents);
        items.add(mItem);
    }

    // 팝업 보여주기
    public void showPopup(View v, Long selected_comment_number) {
        PopupMenu popup = new PopupMenu(v.getContext(), v);
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.comment_menu_drop_box_delete:
                        System.out.println("로그인한 아이디 : " + userId);
                        System.out.println("댓글 넘버 : " + selected_comment_number);

                        CommentDeleteDTO commentDeleteDTO = new CommentDeleteDTO();
                        commentDeleteDTO.setNumber(selected_comment_number);
                        commentDeleteDTO.setId(userId);

                        retrofitClient = RetrofitClient.getInstance();
                        retrofitAPI = RetrofitClient.getRetrofitInterface();

                        retrofitAPI.getDeleteCommentResponse(commentDeleteDTO).enqueue(new Callback<Boolean>() {
                            @Override
                            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                                // 삭제를 선택한 댓글이 내가 쓴 댓글이 아닐경우
                                if (!response.body()) {
                                    Toast myToast = Toast.makeText(v.getContext(),"권한이 없습니다.", Toast.LENGTH_SHORT);
                                    myToast.show();
                                }

                                // 삭제를 선택한 댓글이 내가 쓴 댓글일 경우
                                else {
                                    Toast myToast = Toast.makeText(v.getContext(),"삭제하였습니다.", Toast.LENGTH_SHORT);
                                    myToast.show();

                                    for (Iterator<PostAllViewCommentItem> iterator = items.iterator(); iterator.hasNext();) {
                                        PostAllViewCommentItem item = iterator.next();
                                        if (item.getComment_number().equals(selected_comment_number)) {
                                            iterator.remove();
                                        }
                                    }
                                    listener.commentDelete();
                                }
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
        inflater.inflate(R.menu.comment_menu_drop_box, popup.getMenu());
        popup.show();
    }

    public void setlistener(CommentDeleteListener commentDeleteListener) {
        this.listener = commentDeleteListener;
    }
}
