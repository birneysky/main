package com.jhhy.cuiweitourism.net.biz;

import android.content.Context;
import android.os.Handler;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;
import com.jhhy.cuiweitourism.net.models.FetchModel.PlaneTicketCityFetch;
import com.jhhy.cuiweitourism.net.models.FetchModel.PlaneTicketInfoForChinalRequest;
import com.jhhy.cuiweitourism.net.models.FetchModel.PlaneTicketInfoInternationalRequest;
import com.jhhy.cuiweitourism.net.models.ResponseModel.FetchError;
import com.jhhy.cuiweitourism.net.models.ResponseModel.FetchResponseModel;
import com.jhhy.cuiweitourism.net.models.ResponseModel.GenericResponseModel;
import com.jhhy.cuiweitourism.net.models.ResponseModel.PlaneTicketCityInfo;
import com.jhhy.cuiweitourism.net.models.ResponseModel.PlaneTicketInternationalInfo;
import com.jhhy.cuiweitourism.net.models.ResponseModel.PlaneTicketInfoOfChina;
import com.jhhy.cuiweitourism.net.netcallback.BizGenericCallback;
import com.jhhy.cuiweitourism.net.netcallback.FetchGenericCallback;
import com.jhhy.cuiweitourism.net.netcallback.FetchGenericResponse;
import com.jhhy.cuiweitourism.net.netcallback.HttpUtils;
import com.jhhy.cuiweitourism.net.utils.HanziToPinyin;
import com.jhhy.cuiweitourism.net.utils.LogUtil;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by birney on 2016-10-10.
 */
public class PlaneTicketActionBiz extends BasicActionBiz {

//    public PlaneTicketActionBiz(Context context, Handler handler) {
//        super(context, handler);
//    }

    public PlaneTicketActionBiz(){
    }

    /**
     * 飞机出发城市、到达城市
     */
    public void getPlaneTicketCityInfo(PlaneTicketCityFetch request, BizGenericCallback<ArrayList<PlaneTicketCityInfo>> callback){
        request.code = "Plane_flyarea";
        FetchGenericResponse<ArrayList<PlaneTicketCityInfo>> fetchGenericResponse = new FetchGenericResponse<ArrayList<PlaneTicketCityInfo>>(callback) {
            @Override
            public void onCompletion(FetchResponseModel response) {
                ArrayList<PlaneTicketCityInfo> array = parseJsonToObjectArray(response,PlaneTicketCityInfo.class);
                HanziToPinyin hzToPyConvertor = HanziToPinyin.getInstance();
                for (PlaneTicketCityInfo itemInfo : array){
                    HanziToPinyin.PinYinCollection collection = hzToPyConvertor.getPinYin(itemInfo.name);
                    itemInfo.fullPY = collection.fullPY;
                    itemInfo.shortPY = collection.shortPY;
                    itemInfo.headChar = String.valueOf(collection.headChar);
                }
                GenericResponseModel<ArrayList<PlaneTicketCityInfo>> returnModel = new GenericResponseModel<ArrayList<PlaneTicketCityInfo>>(response.head,array);
                this.bizCallback.onCompletion(returnModel);
            }

            @Override
            public void onError(FetchError error) {
                this.bizCallback.onError(error);
            }
        };
        HttpUtils.executeXutils(request,new FetchGenericCallback<>(fetchGenericResponse));
    }

    /**
     *  国内机票
     */

    public void planeTicketInfoInChina(PlaneTicketInfoForChinalRequest request, BizGenericCallback<PlaneTicketInfoOfChina> callback) {
        request.code = "Fly_index";
        FetchGenericResponse<PlaneTicketInfoOfChina> fetchResponse = new FetchGenericResponse<PlaneTicketInfoOfChina>(callback) {
            @Override
            public void onCompletion(FetchResponseModel response) {
                PlaneTicketInfoOfChina info = parseJsonToObject(response, PlaneTicketInfoOfChina.class);
                GenericResponseModel<PlaneTicketInfoOfChina> returnModel = new GenericResponseModel<PlaneTicketInfoOfChina>(response.head, info);
                this.bizCallback.onCompletion(returnModel);
            }

            @Override
            public void onError(FetchError error) {
                this.bizCallback.onError(error);
            }
        };

        HttpUtils.executeXutils(request, new FetchGenericCallback<>(fetchResponse));
    }

    /**
     *  国际机票(查询)
     */
    public void planeTicketInfoOfInternational(final PlaneTicketInfoInternationalRequest request, BizGenericCallback<PlaneTicketInternationalInfo> callback){
        request.code = "Plane_gjhb";
        FetchGenericResponse<PlaneTicketInternationalInfo> fetchGenericResponse = new FetchGenericResponse<PlaneTicketInternationalInfo>(callback) {
            @Override
            public void onCompletion(FetchResponseModel response) {
                final Gson gson = new Gson();
                final Type type = new TypeToken<HashMap<String, Object>>() {}.getType();
                final HashMap<String,Object> map = gson.fromJson(response.body,type);
                PlaneTicketInternationalInfo info = new PlaneTicketInternationalInfo();
                if (map.containsKey("P")){
                    LinkedTreeMap<String,LinkedTreeMap<String,ArrayList<String>>> content =  (LinkedTreeMap<String,LinkedTreeMap<String,ArrayList<String>>>)map.get("P");;
                    Iterator iter = content.entrySet().iterator();
                    ArrayList<PlaneTicketInternationalInfo.AirportInfo> aiports = new ArrayList<>();
                    while (iter.hasNext()) {
                        Map.Entry<String,ArrayList<String>> entry = (Map.Entry<String,ArrayList<String>>) iter.next();
                        ArrayList<String> value = entry.getValue();
                        PlaneTicketInternationalInfo.AirportInfo airportInfo = new PlaneTicketInternationalInfo.AirportInfo();
                        airportInfo.airportCode = entry.getKey();
                        airportInfo.abbreviation = value.get(0);
                        airportInfo.fullName = value.get(1);
                        airportInfo.city = value.get(2);
                        airportInfo.cityCode = value.get(3);
                        aiports.add(airportInfo);
                    }
                    info.P = aiports;
                }

                if (map.containsKey("J")){
                    LinkedTreeMap<String,LinkedTreeMap<String,ArrayList<String>>> content =  (LinkedTreeMap<String,LinkedTreeMap<String,ArrayList<String>>>)map.get("J");;
                    Iterator iter = content.entrySet().iterator();
                    Map<String, PlaneTicketInternationalInfo.AircraftTypeInfo> aircrafts = new HashMap<>();
                    while (iter.hasNext()) {
                        Map.Entry<String,ArrayList<String>> entry = (Map.Entry<String,ArrayList<String>>) iter.next();
                        ArrayList<String> value = entry.getValue();
                        PlaneTicketInternationalInfo.AircraftTypeInfo aircraft = new PlaneTicketInternationalInfo.AircraftTypeInfo();
                        aircraft.typeCode = entry.getKey();
                        aircraft.typeName = value.get(0);
                        aircraft.typeNum = value.get(1);
                        aircraft.airframe = value.get(2);
                        aircraft.minNum = value.get(3);
                        aircraft.maxNum = value.get(4);
                        aircrafts.put(entry.getKey(), aircraft);
                    }
                    info.J = aircrafts;
                }


                if(map.containsKey("A")) {
                    LinkedTreeMap<String, LinkedTreeMap<String, ArrayList<String>>> content = (LinkedTreeMap<String, LinkedTreeMap<String, ArrayList<String>>>) map.get("A");
                    Iterator iter = content.entrySet().iterator();
                    ArrayList<PlaneTicketInternationalInfo.AirlineCompanyInfo> airlineCompanyInfos = new ArrayList<>();
                    while (iter.hasNext()) {
                        Map.Entry<String, ArrayList<String>> entry = (Map.Entry<String, ArrayList<String>>) iter.next();
                        ArrayList<String> value = entry.getValue();
                        PlaneTicketInternationalInfo.AirlineCompanyInfo airlineCompany = new PlaneTicketInternationalInfo.AirlineCompanyInfo();
                        airlineCompany.airlineCompanyCode = entry.getKey();
                        airlineCompany.companyName = value.get(0);
                        airlineCompany.shortName = value.get(1);
                        airlineCompany.isOnlineCheckin = value.get(2);
                        airlineCompanyInfos.add(airlineCompany);
                    }
                    info.A = airlineCompanyInfos;
                }

                if (map.containsKey("R")){
                    LinkedTreeMap<String, LinkedTreeMap<String, String>> content = (LinkedTreeMap<String, LinkedTreeMap<String, String>>) map.get("R");
                    Iterator iter = content.entrySet().iterator();
//                    ArrayList<PlaneTicketInternationalInfo.AircraftCabinInfo> aircraftCabinInfos = new ArrayList<>();
                    Map<String, String> aircraftCabinInfos = new HashMap<>();
                    while (iter.hasNext()) {
                        Map.Entry<String, String> entry = (Map.Entry<String, String>) iter.next();
//                        PlaneTicketInternationalInfo.AircraftCabinInfo aircraftCabinInfo = new PlaneTicketInternationalInfo.AircraftCabinInfo();
//                        aircraftCabinInfo.levelCode = entry.getKey();
//                        aircraftCabinInfo.levelDescription = entry.getValue();
                        aircraftCabinInfos.put(entry.getKey(), entry.getValue());
                    }
                    info.R = aircraftCabinInfos;
                }

                if (map.containsKey("F")){
                    LinkedTreeMap<String, PlaneTicketInternationalInfo.PlaneTicketInternationalF> fMap = new LinkedTreeMap<>();
                    LinkedTreeMap<String, LinkedTreeMap<String, ArrayList<Object>>> content = (LinkedTreeMap<String, LinkedTreeMap<String, ArrayList<Object>>>) map.get("F"); //F1—>S1—>string
                    for (String fKey: content.keySet()){ //F1
                        PlaneTicketInternationalInfo.PlaneTicketInternationalF fn = new PlaneTicketInternationalInfo.PlaneTicketInternationalF();
                        LinkedTreeMap<String, ArrayList<Object>> fContent = content.get(fKey);
                        fn.F = fKey;
                        if (fContent.containsKey("S1")){ //S1
                            ArrayList sContent = fContent.get("S1");
                            ArrayList sContent1 = (ArrayList) sContent.get(0);
                            PlaneTicketInternationalInfo.PlaneTicketInternationalFS s1 = new PlaneTicketInternationalInfo.PlaneTicketInternationalFS();
                            s1.AVCode = String.valueOf(sContent1.get(0));
                            s1.planeInfoNode = String.valueOf(sContent1.get(1));
                            s1.fromAirportCode = String.valueOf(sContent1.get(2));
                            s1.fromAirportName = String.valueOf(sContent1.get(3));
                            s1.fromDate = String.valueOf(sContent1.get(4));
                            s1.fromTime = String.valueOf(sContent1.get(5));
                            s1.toAirportCode = String.valueOf(sContent1.get(6));
                            s1.toAirportName = String.valueOf(sContent1.get(7));
                            s1.toDate = String.valueOf(sContent1.get(8));
                            s1.toTime = String.valueOf(sContent1.get(9));
                            s1.transferFrequency = String.valueOf(sContent1.get(10));
                            s1.flightPeriodTotal = String.valueOf(sContent1.get(11));
                            s1.stopPeriod = String.valueOf(sContent1.get(12));
                            ArrayList s13 = (ArrayList) sContent1.get(13);
                            ArrayList frequencys = new ArrayList();
                            if (s13 != null && s13.size() != 0){
                                for (int i = 0; i < s13.size(); i++) {
                                    PlaneTicketInternationalInfo.Frequency frequency = new PlaneTicketInternationalInfo.Frequency();
                                    ArrayList frequencyStr = (ArrayList) s13.get(i);
                                    frequency.frequencyAirport = String.valueOf(frequencyStr.get(0));
                                    frequency.frequencyTerminal = String.valueOf(frequencyStr.get(1));
                                    frequencys.add(frequency);
                                }
                            }
                            s1.frequencys = frequencys;

                            ArrayList sContent2 = (ArrayList) sContent.get(1);
                            ArrayList<PlaneTicketInternationalInfo.FlightInfo> flightInfos = new ArrayList<>();
                            for (int i = 0; i < sContent2.size(); i++){
                                PlaneTicketInternationalInfo.FlightInfo flightInfo = new PlaneTicketInternationalInfo.FlightInfo();
                                ArrayList sContent2i = (ArrayList) sContent2.get(i);

                                flightInfo.airlineCompanyCheck = String.valueOf(sContent2i.get(0));
                                flightInfo.flightNumberCheck = String.valueOf(sContent2i.get(1));
                                flightInfo.fromAirportCodeCheck = String.valueOf(sContent2i.get(2));
                                flightInfo.toAirportCodeCheck = String.valueOf(sContent2i.get(3));
                                flightInfo.fromDateCheck = String.valueOf(sContent2i.get(4));
                                flightInfo.fromTimeCheck = String.valueOf(sContent2i.get(5));
                                flightInfo.toDateCheck = String.valueOf(sContent2i.get(6));
                                flightInfo.toTimeCheck = String.valueOf(sContent2i.get(7));
                                flightInfo.flightPeriod = String.valueOf(sContent2i.get(8));
                                flightInfo.flightTypeCheck = String.valueOf(sContent2i.get(9));

                                ArrayList stop = (ArrayList) sContent2i.get(10);
                                PlaneTicketInternationalInfo.Stopped stopped = new PlaneTicketInternationalInfo.Stopped();
                                if (stop != null && stop.size() != 0){
                                    stopped.stoppedTimes = String.valueOf(stop.get(0));
                                    stopped.stoppedAirport = String.valueOf(stop.get(1));
                                    stopped.stoppedPeriod = String.valueOf(stop.get(2));
                                }
                                flightInfo.stopped = stopped;
                                flightInfo.fromTerminal = String.valueOf(sContent2i.get(11));
                                flightInfo.toTermianl = String.valueOf(sContent2i.get(12));
                                flightInfo.meals = String.valueOf(sContent2i.get(13));
                                flightInfo.mileage = String.valueOf(sContent2i.get(14));
                                flightInfo.aircraftCabin = String.valueOf(sContent2i.get(15));
                                flightInfo.fromWeekday = String.valueOf(sContent2i.get(16));
                                flightInfo.entertainment = String.valueOf(sContent2i.get(17));
                                flightInfo.electronicTicketIdentification = String.valueOf(sContent2i.get(18));
                                flightInfo.shareFlightNumber = String.valueOf(sContent2i.get(19));

                                flightInfos.add(flightInfo);
                            }
                            s1.flightInfos = flightInfos;
                            fn.S1 = s1;
                        }else if (fContent.containsKey("S2")){
                            PlaneTicketInternationalInfo.PlaneTicketInternationalFS s2 = new PlaneTicketInternationalInfo.PlaneTicketInternationalFS();
                            ArrayList sContent = fContent.get("S2");

                            ArrayList sContent1 = (ArrayList) sContent.get(0);
                            s2.AVCode = String.valueOf(sContent1.get(0));
                            s2.planeInfoNode = String.valueOf(sContent1.get(1));
                            s2.fromAirportCode = String.valueOf(sContent1.get(2));
                            s2.fromAirportName = String.valueOf(sContent1.get(3));
                            s2.fromDate = String.valueOf(sContent1.get(4));
                            s2.fromTime = String.valueOf(sContent1.get(5));
                            s2.toAirportCode = String.valueOf(sContent1.get(6));
                            s2.toAirportName = String.valueOf(sContent1.get(7));
                            s2.toDate = String.valueOf(sContent1.get(8));
                            s2.toTime = String.valueOf(sContent1.get(9));
                            s2.transferFrequency = String.valueOf(sContent1.get(10));
                            s2.flightPeriodTotal = String.valueOf(sContent1.get(11));
                            s2.stopPeriod = String.valueOf(sContent1.get(12));
                            ArrayList s13 = (ArrayList) sContent1.get(13);
                            ArrayList frequencys = new ArrayList();
                            if (s13 != null && s13.size() != 0){
                                for (int i = 0; i < s13.size(); i++) {
                                    PlaneTicketInternationalInfo.Frequency frequency = new PlaneTicketInternationalInfo.Frequency();
                                    ArrayList frequencyStr = (ArrayList) s13.get(i);
                                    frequency.frequencyAirport = String.valueOf(frequencyStr.get(0));
                                    frequency.frequencyTerminal = String.valueOf(frequencyStr.get(1));
                                    frequencys.add(frequency);
                                }
                            }
                            s2.frequencys = frequencys;

                            ArrayList sContent2 = (ArrayList) sContent.get(1);
                            ArrayList<PlaneTicketInternationalInfo.FlightInfo> flightInfos = new ArrayList<>();
                            for (int i = 0; i < sContent2.size(); i++){
                                PlaneTicketInternationalInfo.FlightInfo flightInfo = new PlaneTicketInternationalInfo.FlightInfo();
                                ArrayList sContent2i = (ArrayList) sContent2.get(i);

                                flightInfo.airlineCompanyCheck = String.valueOf(sContent2i.get(0));
                                flightInfo.flightNumberCheck = String.valueOf(sContent2i.get(1));
                                flightInfo.fromAirportCodeCheck = String.valueOf(sContent2i.get(2));
                                flightInfo.toAirportCodeCheck = String.valueOf(sContent2i.get(3));
                                flightInfo.fromDateCheck = String.valueOf(sContent2i.get(4));
                                flightInfo.fromTimeCheck = String.valueOf(sContent2i.get(5));
                                flightInfo.toDateCheck = String.valueOf(sContent2i.get(6));
                                flightInfo.toTimeCheck = String.valueOf(sContent2i.get(7));
                                flightInfo.flightPeriod = String.valueOf(sContent2i.get(8));
                                flightInfo.flightTypeCheck = String.valueOf(sContent2i.get(9));

                                ArrayList stop = (ArrayList) sContent2i.get(10);
                                PlaneTicketInternationalInfo.Stopped stopped = new PlaneTicketInternationalInfo.Stopped();
                                if (stop != null && stop.size() != 0){
                                    stopped.stoppedTimes = String.valueOf(stop.get(0));
                                    stopped.stoppedAirport = String.valueOf(stop.get(1));
                                    stopped.stoppedPeriod = String.valueOf(stop.get(2));
                                }
                                flightInfo.stopped = stopped;
                                flightInfo.fromTerminal = String.valueOf(sContent2i.get(11));
                                flightInfo.toTermianl = String.valueOf(sContent2i.get(12));
                                flightInfo.meals = String.valueOf(sContent2i.get(13));
                                flightInfo.mileage = String.valueOf(sContent2i.get(14));
                                flightInfo.aircraftCabin = String.valueOf(sContent2i.get(15));
                                flightInfo.fromWeekday = String.valueOf(sContent2i.get(16));
                                flightInfo.entertainment = String.valueOf(sContent2i.get(17));
                                flightInfo.electronicTicketIdentification = String.valueOf(sContent2i.get(18));
                                flightInfo.shareFlightNumber = String.valueOf(sContent2i.get(19));

                                flightInfos.add(flightInfo);
                            }
                            s2.flightInfos = flightInfos;
                            fn.S2 = s2;
                        }
                        fMap.put(fKey, fn);
                    }
                    info.FMap = fMap;
                    LogUtil.e("PlaneTicketActionBiz", "");
                }

                if (map.containsKey("H")){
                    LinkedTreeMap<String, PlaneTicketInternationalInfo.PlaneTicketInternationalHF> hMap = new LinkedTreeMap<>();
                    LinkedTreeMap<String, LinkedTreeMap<String, LinkedTreeMap<String, Object>>> content = (LinkedTreeMap<String, LinkedTreeMap<String, LinkedTreeMap<String, Object>>>) map.get("H");
                    for (String hKey: content.keySet()){ //F1
                        PlaneTicketInternationalInfo.PlaneTicketInternationalHF planeTicketInternationalHF = new PlaneTicketInternationalInfo.PlaneTicketInternationalHF();
                        LinkedTreeMap<String, LinkedTreeMap<String, Object>> hFContent = content.get(hKey); //F1—>content
                        for (String hfCKey: hFContent.keySet()){ //舱位组合(去程/回程, 验价时需要回传)
                            planeTicketInternationalHF.aircraftCabin = hfCKey;
                            PlaneTicketInternationalInfo.PlaneTicketInternationalHFCabin cabin = new PlaneTicketInternationalInfo.PlaneTicketInternationalHFCabin();LinkedTreeMap<String, Object> contentS = hFContent.get(hfCKey);
                            for (String key: contentS.keySet()) {
                                ArrayList list = (ArrayList) contentS.get(key);
                                if (key.equals("ADT")||key.equals("CHD")||key.equals("INF")) {
                                    cabin.passengerTypeKey = key;
                                    PlaneTicketInternationalInfo.PassengerType passengerType = new PlaneTicketInternationalInfo.PassengerType();
                                    passengerType.faceValue = String.valueOf(list.get(0));
                                    passengerType.releasePriceFlightCompanyCheck = String.valueOf(list.get(1));
                                    passengerType.fromAirportCheck = String.valueOf(list.get(2));
                                    passengerType.toAirportCheck = String.valueOf(list.get(3));
                                    passengerType.airportCabinCode = String.valueOf(list.get(4));
                                    passengerType.airportCabinType = String.valueOf(list.get(5));
                                    passengerType.changeBackSign = String.valueOf(list.get(6));
                                    passengerType.priceSource = String.valueOf(list.get(7));
                                    passengerType.freightBaseCheck = String.valueOf(list.get(8));
                                    passengerType.currencyType = String.valueOf(list.get(9));
                                    passengerType.passengerCount = String.valueOf(list.get(10));

                                    Map<String, PlaneTicketInternationalInfo.TaxTypeCode> mapTax = new LinkedTreeMap<>();
                                    LinkedTreeMap<String, ArrayList> taxes = (LinkedTreeMap<String, ArrayList>) list.get(11);
                                    for (String keyTax: taxes.keySet()){
                                        PlaneTicketInternationalInfo.TaxTypeCode taxTypeContent = new PlaneTicketInternationalInfo.TaxTypeCode();
                                        ArrayList taxList = taxes.get(keyTax);
                                        taxTypeContent.price = String.valueOf(taxList.get(0));
                                        taxTypeContent.currencyType = String.valueOf(taxList.get(1));
                                        mapTax.put(keyTax, taxTypeContent);
                                    }
                                    passengerType.taxTypeCodeMap = mapTax;

                                    passengerType.mainCarrierCheck = String.valueOf(list.get(12));
                                    passengerType.cabinCount = String.valueOf(list.get(13));
                                    passengerType.rebate = String.valueOf(list.get(14));
                                    passengerType.award = String.valueOf(list.get(15));
                                    passengerType.backMoney = String.valueOf(list.get(16));
                                    passengerType.serviceCharge = String.valueOf(list.get(17));
                                    passengerType.extraCharge = String.valueOf(list.get(18));

                                    cabin.passengerType = passengerType;
                                }else if (key.equals("BaseFare")){
                                    PlaneTicketInternationalInfo.BaseFare baseFare = new PlaneTicketInternationalInfo.BaseFare();
                                    baseFare.faceValueTotal = String.valueOf(list.get(0));
                                    baseFare.currencyType = String.valueOf(list.get(1));
                                    cabin.baseFare = baseFare;
                                }else if (key.equals("TotalFare")){
                                    PlaneTicketInternationalInfo.TotalFare totalFare = new PlaneTicketInternationalInfo.TotalFare();
                                    totalFare.taxTotal = String.valueOf(list.get(0));
                                    totalFare.currencyType = String.valueOf(list.get(1));
                                    cabin.totalFare = totalFare;
                                }
                            }
                            planeTicketInternationalHF.cabin = cabin;
                        }

                        hMap.put(hKey, planeTicketInternationalHF); //F1—>
                    }
                    info.HMap = hMap;
                    LogUtil.e("PlaneTicketActionBiz", "");
                }

                GenericResponseModel<PlaneTicketInternationalInfo> returnModel = new GenericResponseModel<PlaneTicketInternationalInfo>(response.head, info);
                this.bizCallback.onCompletion(returnModel);

            }

            @Override
            public void onError(FetchError error) {
                this.bizCallback.onError(error);
            }
        };

        HttpUtils.executeXutils(request, new FetchGenericCallback<>(fetchGenericResponse));

    }

    /**
     *  国际飞机票政策
     */

    public void planeTicketInternationalPolicyInfo(){

    }

    /**
     *  出发城市，到达城市
     */
    public void planeTicketDepartureAndReachCity(){

    }
    /**
     * 国际机票下单
     */
    public void planeTicketOrderInternational(){

    }
}
