package com.jhhy.cuiweitourism.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.jhhy.cuiweitourism.R;

/**
 * 退改签说明
 */
public class PlaneChangeBackActivity extends BaseActionBarActivity {

    private TextView tvRefundTicket; //退票条件
    private TextView tvChangeTicket; //改签条件

    private String refund;
    private String change;
    private String notice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_plane_change_back);
        getData();
        super.onCreate(savedInstanceState);
    }

    private void getData() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null){
            refund = bundle.getString("refund");
            change = bundle.getString("change");
            notice = bundle.getString("notice"); //不能改签
        }
    }

    @Override
    protected void setupView() {
        super.setupView();
        tvTitle.setText(getString(R.string.plane_flight_change_ticket_title));
        tvRefundTicket = (TextView) findViewById(R.id.tv_plane_ticket_refund);
        tvChangeTicket = (TextView) findViewById(R.id.tv_plane_ticket_change);
        tvRefundTicket.setText(refund);
        tvChangeTicket.setText(change);
    }

    @Override
    protected void addListener() {
        super.addListener();

    }
}
