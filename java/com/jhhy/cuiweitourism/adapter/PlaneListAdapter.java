package com.jhhy.cuiweitourism.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jhhy.cuiweitourism.R;
import com.jhhy.cuiweitourism.net.models.ResponseModel.PlaneTicketCityInfo;
import com.jhhy.cuiweitourism.net.models.ResponseModel.PlaneTicketInfoOfChina;
import com.jhhy.cuiweitourism.net.models.ResponseModel.PlaneTicketInternationalInfo;
import com.jhhy.cuiweitourism.net.utils.LogUtil;
import com.jhhy.cuiweitourism.ui.PlaneListActivity;
import com.jhhy.cuiweitourism.ui.PlaneListInternationalActivity;
import com.jhhy.cuiweitourism.utils.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by jiahe008 on 2016/10/26.
 */
public class PlaneListAdapter extends MyBaseAdapter {

    private String TAG = "PlaneListAdapter";
    private int type; //1：国内机票  2：国外机票
    private int singleType = -1; //1:单程 0:往返
    private int priceType; //2:票价+税费；1:含税票价

    private PlaneTicketCityInfo fromCity;
    private PlaneTicketCityInfo toCity;

    public PlaneListAdapter(Context ct, List list, PlaneTicketCityInfo fromCity, PlaneTicketCityInfo toCity, int type){
        super(ct, list);
        this.fromCity = fromCity;
        this.toCity = toCity;
        this.type = type;
    }

    public void setPriceType(int priceType){
        this.priceType = priceType;
        notifyDataSetChanged();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder = null;
        if (view == null){
            holder = new ViewHolder();
            view = LayoutInflater.from(context).inflate(R.layout.item_plane_list, null);
            holder.tvStartTime = (TextView) view.findViewById(R.id.tv_plane_start_time);
            holder.tvFromAirport = (TextView) view.findViewById(R.id.tv_plane_from_airport);
            holder.tvArrivalTime = (TextView) view.findViewById(R.id.tv_plane_arrival_time);
            holder.tvArrivalAirport = (TextView) view.findViewById(R.id.tv_plane_arrival_airport);
            holder.tvTicketPrice = (TextView) view.findViewById(R.id.tv_plane_ticket_price);
            holder.tvPlaneClass = (TextView) view.findViewById(R.id.tv_plane_ticket_class);
            holder.tvPlaneInfo = (TextView) view.findViewById(R.id.tv_plane_info);
            holder.tvArrivalType = (TextView) view.findViewById(R.id.tv_plane_arrival_type);
            holder.tvConsumingTime = (TextView) view.findViewById(R.id.tv_plane_consuming_time);
//            holder.tvTicketNum = (TextView) view.findViewById(R.id.tv_plane_ticket_number);
            view.setTag(holder);
        }else{
            holder = (ViewHolder) view.getTag();
        }
        if (type == 1) {
            PlaneTicketInfoOfChina.FlightInfo flight = (PlaneTicketInfoOfChina.FlightInfo) getItem(i);
            if (flight != null) {
                holder.tvStartTime.setText(String.format("%s:%s", flight.depTime.substring(0, 2), flight.depTime.substring(2)));
                holder.tvFromAirport.setText(fromCity.getAirportname());
                holder.tvArrivalTime.setText(String.format("%s:%s", flight.arriTime.substring(0, 2), flight.arriTime.substring(2)));
                holder.tvArrivalAirport.setText(toCity.getAirportname());
                ArrayList<PlaneTicketInfoOfChina.SeatItemInfo> seatItems = flight.getSeatItems();
                Collections.sort(seatItems);
                PlaneTicketInfoOfChina.SeatItemInfo seat = seatItems.get(0);
                holder.tvTicketPrice.setText(String.format("￥%s", seat.getParPrice()));
                holder.tvPlaneClass.setText(seat.getSeatMsg());
                holder.tvPlaneInfo.setText(String.format("%s %s", seat.getFlightNo(), flight.planeType));
            }
        }else if (type == 2){
            PlaneTicketInternationalInfo.PlaneTicketInternationalF flight = (PlaneTicketInternationalInfo.PlaneTicketInternationalF) getItem(i);
//            if () //单程
            if (flight != null){
                PlaneTicketInternationalInfo.PlaneTicketInternationalFS s1 = flight.S1;
                holder.tvArrivalType.setVisibility(View.VISIBLE);
                holder.tvConsumingTime.setVisibility(View.VISIBLE);
                holder.tvStartTime.setText(s1.fromTime);
                holder.tvArrivalTime.setText(s1.toTime);
                if ("0".equals(s1.transferFrequency)) {
                    holder.tvArrivalType.setText(context.getString(R.string.plane_flight_single)); //直达
                }else{
                    holder.tvArrivalType.setText(context.getString(R.string.plane_flight_unsingle)); //中转
                }
                holder.tvConsumingTime.setText(Utils.getDiffMinuteStr(String.format("%s %s", s1.fromDate, s1.fromTime), String.format("%s %s", s1.toDate, s1.toTime))); //TODO 耗时
                holder.tvFromAirport.setText(String.format("%s%s", PlaneListInternationalActivity.info.P.get(s1.fromAirportCode).fullName, s1.fromAirportName)); //起飞机场/航站楼
                holder.tvArrivalAirport.setText(String.format("%s%s", PlaneListInternationalActivity.info.P.get(s1.toAirportCode).fullName, s1.toAirportName)); //起飞机场/航站楼

                PlaneTicketInternationalInfo.PlaneTicketInternationalHF hf = PlaneListInternationalActivity.info.HMap.get(flight.F);
                if (priceType == 1){ //含税总价
                    holder.tvTicketPrice.setText(String.format("￥%s", hf.cabin.totalFare.taxTotal));
                    holder.tvPlaneClass.setText(context.getString(R.string.plane_flight_tax));
                } else { //税价+票价
                    holder.tvTicketPrice.setText(String.format("￥%s", hf.cabin.baseFare.faceValueTotal)); //票面价 ; 含税总价：hf.cabin.totalFare.taxTotal
                    holder.tvPlaneClass.setText(String.format("税费：￥%s", hf.cabin.passengerType.taxTypeCodeMap.get("XT").price)); //税费xxx; 含税总价；
                }
                String[] cabinTypes = hf.cabin.passengerType.airportCabinType.split(",");
                StringBuffer sb = new StringBuffer();
                for (String cabinType1 : cabinTypes) {
                    sb.append(PlaneListInternationalActivity.info.R.get(cabinType1)).append("|");
                }
                String cabinType =  sb.toString().substring(0, sb.length()-1);
                holder.tvPlaneInfo.setText(String.format("%s%s | %s", s1.flightInfos.get(0).flightNumberCheck, PlaneListInternationalActivity.info.J.get(s1.flightInfos.get(0).flightTypeCheck).typeName, cabinType)); //飞机/舱位 MU5003空客A320(J)|经济舱(R)
            }
        }
        return view;
    }

    class ViewHolder{
        private TextView tvStartTime;
        private TextView tvFromAirport;

        private TextView tvArrivalTime;
        private TextView tvArrivalAirport;

        private TextView tvTicketPrice;
        private TextView tvPlaneClass;  //舱位

        private TextView tvPlaneInfo;   //飞机信息
//        private TextView tvTicketNum;   //机票数量

        private TextView tvArrivalType; //直达、中转
        private TextView tvConsumingTime; //耗时
    }
}
