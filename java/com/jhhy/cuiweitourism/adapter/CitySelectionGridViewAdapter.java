package com.jhhy.cuiweitourism.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jhhy.cuiweitourism.R;
import com.jhhy.cuiweitourism.net.models.ResponseModel.TrainStationInfo;

import java.util.List;

/**
 * Created by jiahe008 on 2016/8/23.
 */
public class CitySelectionGridViewAdapter extends MyBaseAdapter {

    public CitySelectionGridViewAdapter(Context ct, List list) {
        super(ct, list);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_gridview_city_selection, null);
            holder.tvCityName = (TextView) convertView.findViewById(R.id.tv_item_gridview_city_selection);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        TrainStationInfo info = (TrainStationInfo) getItem(position);
        holder.tvCityName.setText(info.getName());
        return convertView;
    }

    class ViewHolder {
        TextView tvCityName;
    }
}
