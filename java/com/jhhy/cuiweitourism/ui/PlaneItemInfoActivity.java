package com.jhhy.cuiweitourism.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.jhhy.cuiweitourism.R;
import com.jhhy.cuiweitourism.adapter.PlaneItemInfoListAdapter;
import com.jhhy.cuiweitourism.adapter.PlaneListAdapter;
import com.jhhy.cuiweitourism.net.models.ResponseModel.PlaneTicketCityInfo;
import com.jhhy.cuiweitourism.net.models.ResponseModel.PlaneTicketInfoOfChina;
import com.jhhy.cuiweitourism.utils.ToastUtil;
import com.jhhy.cuiweitourism.utils.Utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PlaneItemInfoActivity extends BaseActionBarActivity {

    private TextView tvFromAirport; //起飞机场
    private TextView tvToAirport;   //降落机场

    private TextView tvStartTime;   //起飞时间
    private TextView tvArrivalTime; //降落时间

    private TextView tvStartDate;   //起飞日期
    private TextView tvArrivalDate; //降落日期

    private TextView tvTimeConsuming; //耗时
    private TextView tvPlaneInfo;   //飞机信息，餐饮

    private PullToRefreshListView pullListView;
    private ListView listView;
    private List<PlaneTicketInfoOfChina.SeatItemInfo> list;
    private PlaneItemInfoListAdapter adapter;

    private PlaneTicketInfoOfChina.FlightInfo flight; //航班信息
    private PlaneTicketCityInfo fromCity; //出发城市
    private PlaneTicketCityInfo toCity; //到达城市
    private String dateFrom; //出发日期


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_plane_item_info);
        getData();
        super.onCreate(savedInstanceState);
    }

    private void getData() {
        Bundle bundle = getIntent().getExtras();
        flight = (PlaneTicketInfoOfChina.FlightInfo) bundle.getSerializable("flight");
        fromCity = (PlaneTicketCityInfo) bundle.getSerializable("fromCity");
        toCity = (PlaneTicketCityInfo) bundle.getSerializable("toCity");
        dateFrom = bundle.getString("dateFrom");
        list = flight.getSeatItems();
    }

    @Override
    protected void setupView() {
        super.setupView();

        tvTitle.setText(String.format("%s—%s", fromCity.getName(), toCity.getName()));

        tvFromAirport = (TextView) findViewById(R.id.tv_plane_start_station);
        tvToAirport = (TextView) findViewById(R.id.tv_plane_end_station);
        tvStartTime = (TextView) findViewById(R.id.tv_plane_start_time);
        tvArrivalTime = (TextView) findViewById(R.id.tv_plane_end_time);
        tvStartDate = (TextView) findViewById(R.id.tv_plane_start_date);
        tvArrivalDate = (TextView) findViewById(R.id.tv_plane_arrival_time);
        tvTimeConsuming = (TextView) findViewById(R.id.tv_plane_order_time_consuming);
        tvPlaneInfo = (TextView) findViewById(R.id.tv_plane_order_plane_date);

        tvFromAirport.setText(fromCity.getAirportname());
        tvToAirport.setText(toCity.getAirportname());
        tvStartTime.setText(String.format("%s:%s", flight.depTime.substring(0, 2), flight.depTime.substring(2)));
        tvArrivalTime.setText(String.format("%s:%s", flight.arriTime.substring(0, 2), flight.arriTime.substring(2)));
        tvStartDate.setText(dateFrom);
        tvArrivalDate.setText(Utils.getDateStrYMDE(flight.param1));
        tvTimeConsuming.setText(Utils.getDiffMinute(flight.depTime, flight.arriTime));
        String info = String.format("%s | %s %s", flight.flightNo, flight.planeType, flight.meal.equals("true")?"有餐饮":"无餐饮");
        SpannableStringBuilder sb = new SpannableStringBuilder(info);
        ForegroundColorSpan mealSpan = new ForegroundColorSpan(getResources().getColor(R.color.colorMeal));
        sb.setSpan(mealSpan, info.lastIndexOf(" ") + 1, info.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        tvPlaneInfo.setText(sb);

        pullListView = (PullToRefreshListView) findViewById(R.id.list_plane_detail);
        //这几个刷新Label的设置
        pullListView.getLoadingLayoutProxy().setLastUpdatedLabel(Utils.getCurrentTime());
        pullListView.getLoadingLayoutProxy().setPullLabel("PULLLABLE");
        pullListView.getLoadingLayoutProxy().setRefreshingLabel("refreshingLabel");
        pullListView.getLoadingLayoutProxy().setReleaseLabel("releaseLabel");

        //上拉、下拉设定
        pullListView.setMode(PullToRefreshBase.Mode.DISABLED);
        listView = pullListView.getRefreshableView();

        //上拉、下拉监听函数
        pullListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {

            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {

            }
        });

        adapter = new PlaneItemInfoListAdapter(getApplicationContext(), list) {
            @Override
            public void onItemTextViewClick(int position, View textView, int id) {
                itemViewClick(position, id);
            }
        };
        listView.setAdapter(adapter);

    }

    //退改签说明，预定
    /**
     * @param position 位置
     * @param id 控件Id
     */
    private void itemViewClick(int position, int id) {
        switch (id){
            case R.id.tv_plane_ticket_refund: //退改签说明

                break;
            case R.id.btn_plane_ticket_reserve: //预定
                reserveTicker(position);
                break;
        }
    }

    private void reserveTicker(int position) {
        Intent intent = new Intent(getApplicationContext(), PlaneEditOrderActivity.class);
        Bundle bundle = new Bundle();
//        PlaneTicketInfoOfChina.SeatItemInfo seat = list.get(position);
        bundle.putSerializable("flight", flight);
        bundle.putInt("positionSeat", position);
        bundle.putString("dateFrom", dateFrom);
        bundle.putSerializable("fromCity", fromCity);
        bundle.putSerializable("toCity", toCity);
        intent.putExtras(bundle);
        startActivityForResult(intent, EDIT_PLANE_ORDER); //查看某趟列车
    }

    private int EDIT_PLANE_ORDER = 9632; //编辑机票订单

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (RESULT_OK == resultCode){
            if (requestCode == EDIT_PLANE_ORDER){ //机票订单成功

            }
        }
    }

    @Override
    protected void addListener() {
        super.addListener();

    }
}
