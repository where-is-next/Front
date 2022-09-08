package com.win.front.main;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;

import com.win.front.R;
import com.win.front.domain.Location;
import com.win.front.AutoCompleteTextXBtn;
import com.win.front.retorfit.RetrofitAPI;
import com.win.front.retorfit.RetrofitClient;

import net.daum.mf.map.api.CalloutBalloonAdapter;
import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapFragment extends Fragment implements MapView.MapViewEventListener, MapView.POIItemEventListener {

    MapView mapView;

    private RetrofitClient retrofitClient;      // retrofit2 객체 참조 변수
    private RetrofitAPI retrofitAPI;            // retrofit2 api 객체 참조 변수

    List<Location> locationList = new ArrayList<>();   // 관광지 객체 저장 리스트
    List<String> locationNameList = new ArrayList<>(); // 관광지 이름 저장 리스트(자동완성을 위한 리스트)

    MapPoint MARKER_POINT;
    MapPOIItem marker;

    boolean searchFlag = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.map_fragment, container, false);

        // 지도 : MapView 생성 및 컨테이너 등록
        mapView = new MapView(getActivity());
        ViewGroup mapViewContainer = (ViewGroup) v.findViewById(R.id.map_view);

        // 커스텀 말풍선 어댑터
        mapView.setCalloutBalloonAdapter(new CustomCalloutBalloonAdapter());

        // 이벤트 리스너
        mapView.setPOIItemEventListener(this);

        // 지도 : 지도 띄우기
        mapViewContainer.addView(mapView);

        // 관광지 리스트 추가
        settingList();

        // 키보드 내리기 위한 매니저
        InputMethodManager mInputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

        // 텍스트 옆 아이콘
        Drawable searchIcon = DrawableCompat.wrap(ContextCompat.getDrawable((getContext()), R.drawable.search_icon));

        // 자동완성 텍스트 기능
        AutoCompleteTextXBtn autoCompleteTextView = (AutoCompleteTextXBtn) v.findViewById(R.id.autoCompleteText);

        autoCompleteTextView.setCompoundDrawablesWithIntrinsicBounds(searchIcon, null, null, null);

        autoCompleteTextView.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_dropdown_item_1line, locationNameList));
        autoCompleteTextView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionSearch, KeyEvent keyEvent) {
                switch (actionSearch) {
                    case EditorInfo.IME_ACTION_SEARCH:

                        String inputText = autoCompleteTextView.getText().toString();

                        if (inputText.equals("")) {
                            Toast.makeText(getActivity(), "검색어를 입력해주세요.", Toast.LENGTH_LONG).show();
                        }
                        else {
                            for (Location lo : locationList) {
                                if (inputText.equals(lo.getName())) {
                                    mapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(
                                                    Double.parseDouble(lo.getLatitude()), Double.parseDouble(lo.getLongitude())),
                                            2, true);

                                    mapView.zoomIn(true);

                                    mInputMethodManager.hideSoftInputFromWindow(autoCompleteTextView.getWindowToken(), 0); // 검색을 누르면 키보드 내림
                                    searchFlag = true;
                                }
                            }

                            if (!searchFlag) {
                                Toast.makeText(getActivity(), "현재 서비스 지역이 아닙니다.", Toast.LENGTH_LONG).show();
                            }
                            searchFlag = false;
                        }
                        break;
                }
                return true;
            }
        });

        return v;
    }

    // 관광지 리스트 추가 함수
    private void settingList() {
        //retrofit 생성
        retrofitClient = RetrofitClient.getInstance();
        retrofitAPI = RetrofitClient.getRetrofitInterface();

        //저장된 데이터와 함께 api에서 정의한 getLoginResponse 함수를 실행한 후 응답을 받음
        retrofitAPI.getLocationResponse().enqueue(new Callback<List<Location>>() {
            @Override
            public void onResponse(Call<List<Location>> call, Response<List<Location>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Location> tempList = response.body();

                    if (locationNameList.isEmpty()) {
                        for (Location lo : tempList) {
                            locationNameList.add(lo.getName().replaceAll("\"",""));
                        }
                    }

                    if (locationList.isEmpty()) {
                        for (Location temp : tempList) {
                            locationList.add(new Location(temp.getName().replaceAll("\"",""),
                                    temp.getLatitude().replaceAll("\"",""),
                                    temp.getLongitude().replaceAll("\"",""),
                                    temp.getAddress().replaceAll("\"",""),
                                    temp.getUrl().replaceAll("\"","")));
                        }
                    }

                    settingMaker();
                }
            }

            @Override
            public void onFailure(Call<List<Location>> call, Throwable t) {
                alertDialog("관광지 정보를 불러올 수 없습니다. 다시 시도해주세요");
            }
        });
    }

    // 지도 : 마커 찍기
    public void settingMaker() {
        for (Location lo : locationList) {
            MARKER_POINT = MapPoint.mapPointWithGeoCoord(Double.parseDouble(lo.getLatitude()), Double.parseDouble(lo.getLongitude()));
            marker = new MapPOIItem();
            marker.setItemName(lo.getName());
            marker.setTag(0);
            marker.setMapPoint(MARKER_POINT);
            marker.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
            marker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.
            mapView.addPOIItem(marker);
        }
    }


    // 다이얼로그 함수
    public void alertDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(false);
        builder.setTitle("알림")
                .setMessage(message)
                .setPositiveButton("확인", null)
                .create()
                .show();
    }

    @Override
    public void onPOIItemSelected(MapView mapView, MapPOIItem mapPOIItem) {
    }

    // 말풍선 클릭시 발생하는 이벤트
    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem) {
        StringBuffer sb = new StringBuffer();
        for (Location lo : locationList) {
            if (lo.getName().equals(mapPOIItem.getItemName())) {
                sb.append(lo.getUrl());
                break;
            }
        }
        Intent urlintent = new Intent(Intent.ACTION_VIEW, Uri.parse(sb.toString()));
        startActivity(urlintent);
    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem, MapPOIItem.CalloutBalloonButtonType calloutBalloonButtonType) {

    }

    @Override
    public void onDraggablePOIItemMoved(MapView mapView, MapPOIItem mapPOIItem, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewInitialized(MapView mapView) {

    }

    @Override
    public void onMapViewCenterPointMoved(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewZoomLevelChanged(MapView mapView, int i) {

    }

    @Override
    public void onMapViewSingleTapped(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDoubleTapped(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewLongPressed(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDragStarted(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDragEnded(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewMoveFinished(MapView mapView, MapPoint mapPoint) {

    }

    // 커스텀 말풍선 클래스
    class CustomCalloutBalloonAdapter implements CalloutBalloonAdapter {

        View mCalloutBalloon = getLayoutInflater().inflate(R.layout.balloon_layout, null);

        @Override
        public View getCalloutBalloon(MapPOIItem mapPOIItem) {
            for (Location lo : locationList) {
                if (lo.getName().equals(mapPOIItem.getItemName())) {
                    ((TextView) mCalloutBalloon.findViewById(R.id.ball_tv_name)).setText(lo.getName());
                    ((TextView) mCalloutBalloon.findViewById(R.id.ball_tv_address)).setText(lo.getAddress());
                    break;
                }
            }
            return mCalloutBalloon;
        }

        @Override
        public View getPressedCalloutBalloon(MapPOIItem mapPOIItem) {
            return mCalloutBalloon;
        }
    }
}
