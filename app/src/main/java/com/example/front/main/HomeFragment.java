package com.example.front.main;
import static android.content.Context.MODE_PRIVATE;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.front.R;
import com.example.front.SignIn;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.kakao.sdk.user.UserApiClient;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class HomeFragment extends Fragment{

    GoogleSignInOptions gso;
    GoogleSignInClient gsc;

    Button signOut;
    Button kakao_sign_out;

    private SharedPreferences sp;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.home_fragment, container, false);
        sp = getActivity().getSharedPreferences("UserInfo", MODE_PRIVATE);

        signOut = v.findViewById(R.id.sign_out);

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(getActivity(), gso);

        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sign_out();
            }
        });

        kakao_sign_out = v.findViewById(R.id.kakao_sign_out);
        kakao_sign_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                kakao_out();
            }
        });

        return v;
    }

    private void sign_out() {
        gsc.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                logout_alertDialog();
            }
        });
    }

    private void kakao_out() {
        UserApiClient.getInstance().logout(new Function1<Throwable, Unit>() {
            @Override
            public Unit invoke(Throwable throwable) {
                logout_alertDialog();
                return null;
            }
        });
    }

    public void logout_alertDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setCancelable(false);
        alert.setTitle("알림");
        alert.setMessage("로그아웃되었습니다.");
        alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getActivity().finish();
                Intent intent = new Intent(getActivity(), SignIn.class);
                startActivity(intent);
            }
        });
        alert.show();
    }
}
