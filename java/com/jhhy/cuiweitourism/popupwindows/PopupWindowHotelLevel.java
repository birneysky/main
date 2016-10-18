package com.jhhy.cuiweitourism.popupwindows;


import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jhhy.cuiweitourism.R;
import com.jhhy.cuiweitourism.adapter.InnerTravelFirstListViewAdapter;
import com.jhhy.cuiweitourism.adapter.InnerTravelTripDaysListViewAdapter;
import com.jhhy.cuiweitourism.dialog.DatePickerActivity;
import com.jhhy.cuiweitourism.moudle.PriceArea;
import com.jhhy.cuiweitourism.net.utils.Consts;
import com.jhhy.cuiweitourism.net.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;


public class PopupWindowHotelLevel extends PopupWindow implements OnClickListener {

    private Activity mActivity;
    private String TAG = PopupWindowHotelLevel.class.getSimpleName();

    private TextView tvCancel; //取消
    private TextView tvConfirm; //确认
    private TextView tvClear; //清空

    private RadioButton rbAll;
    private RadioButton rbPrice;
    private RadioButton rbComfort;
    private RadioButton rbHighGrade;
    private RadioButton rbLuxury;

//    private RadioGroup radioGroup;

//    private boolean isClear;

    private boolean commit;
    private int starLevel;
    private int priceMin;
    private int priceMax;

    public PopupWindowHotelLevel(Activity activity, View parent) {
        super(activity);
        mActivity = activity;
        View view = View.inflate(activity, R.layout.popup_hotel_level_comment, null);
        view.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.fade_ins));
        LinearLayout ll_popup = (LinearLayout) view.findViewById(R.id.ll_popup);
        ll_popup.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.push_bottom_in_2));

        setWidth(LayoutParams.MATCH_PARENT);
        setHeight(LayoutParams.MATCH_PARENT);
        setBackgroundDrawable(new BitmapDrawable());
        setFocusable(true);
        setOutsideTouchable(true);
        setContentView(view);
        showAtLocation(parent, Gravity.BOTTOM, 0, 0);
        update();

        initView(view);
        addListener();
    }

    /**
     * 第二次进入窗口，要显示之前选择的数ju
     */
    public void refreshView(int starLevel, int priceMin, int priceMax){
        this.starLevel = starLevel;
        this.priceMax = priceMax;
        this.priceMin = priceMin;

        if (starLevel == 5){

        }else if (starLevel == 4){
            rbLuxury.setChecked(false);
        }else if (starLevel == 3){
            rbHighGrade.setChecked(false);
            rbLuxury.setChecked(false);
        }else if (starLevel == 2){
            rbComfort.setChecked(false);
            rbHighGrade.setChecked(false);
            rbLuxury.setChecked(false);
        }else if (starLevel == 1){
            rbPrice.setChecked(false);
            rbComfort.setChecked(false);
            rbHighGrade.setChecked(false);
            rbLuxury.setChecked(false);
        }
    }



    private void initView(View view) {
        tvCancel = (TextView) view.findViewById(R.id.title_main_tv_left_location);
        tvConfirm = (TextView) view.findViewById(R.id.title_main_iv_right_telephone);
        tvClear = (TextView) view.findViewById(R.id.tv_clear);

        rbAll = (RadioButton) view.findViewById(        R.id.rb_level_all);
        rbPrice = (RadioButton) view.findViewById(      R.id.rb_level_price);
        rbComfort = (RadioButton) view.findViewById(    R.id.rb_level_comfort);
        rbHighGrade = (RadioButton) view.findViewById(  R.id.rb_level_high_grade);
        rbLuxury = (RadioButton) view.findViewById(     R.id.rb_level_luxury);

//        radioGroup = (RadioGroup) view.findViewById(R.id.radio_group_hotel_level);

        rbAll.setChecked(true);
        rbPrice.setChecked(true);
        rbComfort.setChecked(true);
        rbHighGrade.setChecked(true);
        rbLuxury.setChecked(true);
    }

    private void addListener() {
        tvCancel.setOnClickListener(this);
        tvConfirm.setOnClickListener(this);
        tvClear.setOnClickListener(this);

        rbAll.setOnClickListener(this);
        rbPrice.setOnClickListener(this);
        rbComfort.setOnClickListener(this);
        rbHighGrade.setOnClickListener(this);
        rbLuxury.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rb_level_all:
                boolean checkAll = rbAll.isChecked();
                starLevel = 1;
                if (checkAll) {
                    rbPrice.setChecked(false);
                    rbComfort.setChecked(false);
                    rbHighGrade.setChecked(false);
                    rbLuxury.setChecked(false);
                }
                break;
            case R.id.rb_level_price:
                boolean checkPrice = rbPrice.isChecked();
                starLevel = 2;
                if (checkPrice){
                    rbComfort.setChecked(false);
                    rbHighGrade.setChecked(false);
                    rbLuxury.setChecked(false);
                }else{
                    rbAll.setChecked(true);
                    rbPrice.setChecked(true);
                }
                break;
            case R.id.rb_level_comfort:
                boolean checkComfort = rbComfort.isChecked();
                starLevel = 3;
                if (checkComfort){
                    rbHighGrade.setChecked(false);
                    rbLuxury.setChecked(false);
                }else{
                    rbAll.setChecked(true);
                    rbPrice.setChecked(true);
                    rbComfort.setChecked(true);
                }
                break;
            case R.id.rb_level_high_grade:
                boolean checkGrade = rbHighGrade.isChecked();
                starLevel = 4;
                if (checkGrade){
                    rbLuxury.setChecked(false);
                }else{
                    rbAll.setChecked(true);
                    rbPrice.setChecked(true);
                    rbComfort.setChecked(true);
                    rbHighGrade.setChecked(true);
                }
                break;
            case R.id.rb_level_luxury:
                boolean checkLuxury = rbLuxury.isChecked();
                starLevel = 5;
                if (checkLuxury){
                } else{
                    rbAll.setChecked(true);
                    rbPrice.setChecked(true);
                    rbComfort.setChecked(true);
                    rbHighGrade.setChecked(true);
                    rbLuxury.setChecked(true);
                }
                break;
            case R.id.title_main_tv_left_location: //取消
                commit = false;
                dismiss();
                break;
            case R.id.title_main_iv_right_telephone: //确定
                commit = true;
                dismiss();
                break;
            case R.id.tv_clear: //清空
                clear();
                break;
        }
    }

    private void clear() {
        rbPrice.setChecked(false);
        rbComfort.setChecked(false);
        rbHighGrade.setChecked(false);
        rbLuxury.setChecked(false);
        starLevel = 1;
    }

    public void setCommit(boolean flag) {
        commit = flag;
    }

    public boolean getCommit(){
        return commit;
    }

    public int getStarLevel(){
        return starLevel;
    }

    public int getPriceMin(){
        return priceMin;
    }

    public int getPriceMax() {
        return priceMax;
    }
}