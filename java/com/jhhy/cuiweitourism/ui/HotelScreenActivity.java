package com.jhhy.cuiweitourism.ui;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.jhhy.cuiweitourism.R;
import com.jhhy.cuiweitourism.adapter.HotelSortListAdapter;
import com.jhhy.cuiweitourism.adapter.InnerTravelTripDaysListViewAdapter;
import com.jhhy.cuiweitourism.net.biz.HotelActionBiz;
import com.jhhy.cuiweitourism.net.models.ResponseModel.FetchError;
import com.jhhy.cuiweitourism.net.models.ResponseModel.GenericResponseModel;
import com.jhhy.cuiweitourism.net.models.ResponseModel.HotelScreenBrandResponse;
import com.jhhy.cuiweitourism.net.models.ResponseModel.HotelScreenFacilities;
import com.jhhy.cuiweitourism.net.models.ResponseModel.HoutelPropertiesInfo;
import com.jhhy.cuiweitourism.net.netcallback.BizGenericCallback;
import com.jhhy.cuiweitourism.net.utils.LogUtil;
import com.jhhy.cuiweitourism.utils.LoadingIndicator;
import com.just.sun.pricecalendar.ToastCommon;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

public class HotelScreenActivity extends AppCompatActivity implements View.OnClickListener {

    private String TAG = "HotelScreenActivity";

    private ImageView ivTitleLeft;
    private TextView tvTitle;
    private ActionBar actionBar;

    private ListView listViewFirst;
    private ListView listViewSecond;
    private LinearLayout layoutCommit;
    private Button btnCommit;

    private ArrayList<String> screenProperties = new ArrayList<>();

    private HotelSortListAdapter adapterFirst;
    private HotelSortListAdapter adapterSecondBrand;
    private HotelSortListAdapter adapterSecondFacility;

    private int type; //7：位置区域
    private HotelSortListAdapter adapterSecondLocation; //位置区域
    private int businessDistrictPosition; //商圈
    private int districtPosition; //行政区
    private int viewSpot; //景点

//    private int hotelBrandPosition; //酒店品牌
//    private int hotelFacilityPosition; //设施服务
    private HashSet<Object> facilitySet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //获取ActionBar对象
        actionBar =  getSupportActionBar();
        //自定义一个布局，并居中
        actionBar.setDisplayShowCustomEnabled(true);
        View v = LayoutInflater.from(getApplicationContext()).inflate(R.layout.title_tab1_inner_travel, null);
        actionBar.setCustomView(v, new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT)); //自定义ActionBar布局);
        actionBar.setElevation(0); //删除自带阴影

        setContentView(R.layout.activity_select_all_country_area);
        getData();
        setupView();
        refreshView();
        addListener();
    }

    private void getData() {
        Intent intent = getIntent();
        if (intent != null){
            Bundle bundle = intent.getExtras();
            if (bundle != null){
                type = bundle.getInt("type");
                if (7 == type){
                    businessDistrictPosition = bundle.getInt("businessDistrictPosition");
                    districtPosition = bundle.getInt("districtPosition");
                    viewSpot = bundle.getInt("viewSpot");
                }else{
//                    hotelBrand = bundle.getInt("");
//                    hotelFacility = bundle.getInt("");
                }
                LogUtil.e(TAG, "type = " + type);
            }
        }
    }

    private void setupView() {
        tvTitle = (TextView) actionBar.getCustomView().findViewById(R.id.tv_title_inner_travel);
        tvTitle.setText(getString(R.string.hotel_screen));
        ivTitleLeft = (ImageView) actionBar.getCustomView().findViewById(R.id.title_main_tv_left_location);

        listViewFirst = (ListView) findViewById(R.id.list_father);
        listViewSecond = (ListView) findViewById(R.id.list_son);

        layoutCommit = (LinearLayout) findViewById(R.id.layout_hotel_commit);
        layoutCommit.setVisibility(View.VISIBLE);
        btnCommit = (Button) findViewById(R.id.btn_commit_screen);
    }

    private void addListener() {
        ivTitleLeft.setOnClickListener(this);
        btnCommit.setOnClickListener(this);
        listViewFirst.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                refreshSecondListView(i);
            }
        });

        listViewSecond.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                int firstPosition = adapterFirst.getSelection();
                if (7 == type){
                    if (0 == firstPosition){
                        businessDistrictPosition = i;
                    }else if (1 == firstPosition){
                        districtPosition = i;
                    }else if (2 == firstPosition){
                        viewSpot = i;
                    }
                    adapterSecondLocation.setSelection(i);
                    adapterSecondLocation.notifyDataSetChanged();
                } else {
                    if (0 == firstPosition) {
                        adapterSecondBrand.setSelection(i);
                        adapterSecondBrand.notifyDataSetChanged();
                    } else if (1 == firstPosition) {
                        if (facilitySet.contains(i)){
                            facilitySet.remove(i);
                        }else {
                            facilitySet.add(i);
                        }
                        adapterSecondFacility.setSelections(facilitySet);
                        adapterSecondFacility.notifyDataSetChanged();
                    }
                }
            }
        });
    }

    private void refreshSecondListView(int position) {
        adapterFirst.setSelection(position);
        adapterFirst.notifyDataSetChanged();
        if (7 == type){
            if (0 == position){
                adapterSecondLocation.setData(HotelMainActivity.listBusinessDistrict);
                adapterSecondLocation.setSelection(businessDistrictPosition);
            }else if (1 == position){
                adapterSecondLocation.setData(HotelMainActivity.listDistrict);
                adapterSecondLocation.setSelection(districtPosition);
            }else if (2 == position){
                adapterSecondLocation.setData(HotelMainActivity.listViewSpot);
                adapterSecondLocation.setSelection(viewSpot);
            }
            listViewSecond.setAdapter(adapterSecondLocation);
        } else {
            if (0 == position) { //酒店品牌
                adapterSecondBrand.setData(HotelMainActivity.listBrand);
                adapterSecondBrand.setType(2);
                listViewSecond.setAdapter(adapterSecondBrand);
            } else if (1 == position) { //设施服务
                adapterSecondFacility.setData(HotelMainActivity.facilities);
                adapterSecondFacility.setType(3);
                adapterSecondFacility.setSelections(facilitySet);
                listViewSecond.setAdapter(adapterSecondFacility);
            }
        }
    }

    private void refreshView() {
        if (type == 7){
            screenProperties.add("商圈");
            screenProperties.add("行政区");
            screenProperties.add("景点");
        } else {
            screenProperties.add("酒店品牌");
            screenProperties.add("设施服务");
            facilitySet = new HashSet<>();
            facilitySet.add(0);
        }
        adapterFirst = new HotelSortListAdapter(getApplicationContext(), screenProperties);
        adapterFirst.setSelection(0);
        adapterFirst.setType(1);
        listViewFirst.setAdapter(adapterFirst);

        if (7 ==  type){
            adapterSecondLocation = new HotelSortListAdapter(getApplicationContext(), HotelMainActivity.listBusinessDistrict);
            adapterSecondLocation.setType(type);
            adapterSecondLocation.setSelection(businessDistrictPosition);
            listViewSecond.setAdapter(adapterSecondLocation);
        }else {
            adapterSecondBrand = new HotelSortListAdapter(getApplicationContext(), HotelMainActivity.listBrand);
            adapterSecondFacility = new HotelSortListAdapter(getApplicationContext(), HotelMainActivity.facilities);
            adapterSecondBrand.setType(2);
            adapterSecondBrand.setSelection(0);
            listViewSecond.setAdapter(adapterSecondBrand);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.title_main_tv_left_location:
                finish();
                break;
            case R.id.btn_commit_screen:
                commitScreen();
                break;
        }
    }

    private void commitScreen() {
        int firstSelection = adapterFirst.getSelection();
        LogUtil.e(TAG, "firstSelection = " + firstSelection);
        Intent data = new Intent();
        Bundle bundle = new Bundle();
        if (7 == type){
            bundle.putInt("businessDistrictPosition", businessDistrictPosition);
            bundle.putInt("districtPosition", districtPosition);
            bundle.putInt("viewSpot", viewSpot);
        }else{
            int brandPosition = adapterSecondBrand.getSelection();
//            int facilityPosition = adapterSecondFacility.getSelection();
            String brandName = "";
            if (0 != brandPosition){
                brandName = HotelMainActivity.listBrand.get(brandPosition).getID();
            }
            String facilityName = "";
            if (facilitySet.contains(0)){
                facilityName = "";
            }else {
                facilityName = Arrays.toString(facilitySet.toArray()).replace(" ", "").replace("[", "").replace("]", "");
            }
            bundle.putString("brandName", brandName);
            bundle.putString("facilityName", facilityName);
            LogUtil.e(TAG, "brandName = " + brandName +", facilityName = " + facilityName);
        }
        data.putExtras(bundle);
        setResult(RESULT_OK, data);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        TAG = null;
        ivTitleLeft = null;
        tvTitle = null;
        actionBar = null;
        listViewFirst = null;
        listViewSecond = null;
        layoutCommit = null;
        btnCommit = null;
        if (screenProperties != null){
            screenProperties.clear();
            screenProperties = null;
        }
        adapterFirst = null;
        adapterSecondBrand = null;
        adapterSecondFacility = null;
        adapterSecondLocation = null;
    }
}
