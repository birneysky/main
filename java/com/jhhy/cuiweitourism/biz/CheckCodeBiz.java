package com.jhhy.cuiweitourism.biz;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.jhhy.cuiweitourism.http.NetworkUtil;
import com.jhhy.cuiweitourism.net.netcallback.HttpUtils;
import com.jhhy.cuiweitourism.http.ResponseResult;
import com.jhhy.cuiweitourism.net.utils.Consts;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by jiahe008 on 2016/9/3.
 */
public class CheckCodeBiz {

    private Context context;
    private Handler handler;
    private String CODE_GET_CHECK_CODE = "User_code";

    private String UPDATE_CODE = "Publics_versioncompare";

    public void checkUpdate() {
        if (NetworkUtil.checkNetwork(context)) {
            new Thread() {
                @Override
                public void run() {
                    Map<String, Object> headMap = new HashMap<>();
                    headMap.put(Consts.KEY_CODE, UPDATE_CODE);
                    HttpUtils.executeXutils(headMap, null, updateCallback);
                }
            }.start();
        } else {
            handler.sendEmptyMessage(Consts.NET_ERROR);
        }
    }

    private ResponseResult updateCallback = new ResponseResult() {
        @Override
        public void responseSuccess(String result) {
//            {"head": {
//                    "res_code": "0000",
//                    "res_msg": "success",
//                    "res_arg": "获取成功"},
//             "body": {
//                  "version_number": "1.0.1",
//                  "url": "www.cwly1118.com/uploads/apk/cuiweitravel.apk"
//             }}
            Message msg = new Message();
            msg.what = Consts.MESSAGE_UPDATE_CODE;
            try {
                JSONObject resultObj = new JSONObject(result);
                String head = resultObj.getString(Consts.KEY_HEAD);
                JSONObject headObj = new JSONObject(head);
                String resCode = headObj.getString(Consts.KEY_HTTP_RES_CODE);
//                String resMsg = headObj.getString(Consts.KEY_HTTP_RES_MSG);
                String resArg = headObj.getString(Consts.KEY_HTTP_RES_ARG);
                if ("0001".equals(resCode)) {
                    msg.arg1 = 0;
                    msg.obj = resArg;
                } else if ("0000".equals(resCode)) {
                    msg.arg1 = 1;
                    ArrayList list = new ArrayList();
                    JSONObject bodyObj = resultObj.getJSONObject("body");
                    list.add(bodyObj.getString("version_number"));
                    list.add(bodyObj.getString("url"));
                    msg.obj = list;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                handler.sendMessage(msg);
            }
        }

        @Override
        public void onError(Throwable ex, boolean isOnCallback) {
            super.onError(ex, isOnCallback);
            if (ex instanceof SocketTimeoutException) {
                handler.sendEmptyMessage(Consts.NET_ERROR_SOCKET_TIMEOUT);
            }
        }
    };

    //    {"head":{"code":"User_code"},"field":{"tel":"15210656911"}}
    public CheckCodeBiz(Context context, Handler handler) {
        this.context = context;
        this.handler = handler;
    }

    public void getCheckCode(final String mobile) {
        if (NetworkUtil.checkNetwork(context)) {
            new Thread() {
                @Override
                public void run() {
                    Map<String, Object> headMap = new HashMap<>();
                    headMap.put(Consts.KEY_CODE, CODE_GET_CHECK_CODE);
                    Map<String, Object> fieldMap = new HashMap<>();
                    fieldMap.put(Consts.KEY_TEL, mobile);
                    HttpUtils.executeXutils(headMap, fieldMap, checkCodeCallback);
                }
            }.start();
        } else {
            handler.sendEmptyMessage(Consts.NET_ERROR);
        }
    }

    private ResponseResult checkCodeCallback = new ResponseResult() {
        @Override
        public void responseSuccess(String result) {
            Message msg = new Message();
            msg.what = Consts.MESSAGE_CHECK_CODE;
            try {
                JSONObject resultObj = new JSONObject(result);
                String head = resultObj.getString(Consts.KEY_HEAD);
                JSONObject headObj = new JSONObject(head);
                String resCode = headObj.getString(Consts.KEY_HTTP_RES_CODE);
                String resMsg = headObj.getString(Consts.KEY_HTTP_RES_MSG);
                String resArg = headObj.getString(Consts.KEY_HTTP_RES_ARG);
//    {"head":{"res_code":"0001","res_msg":"error","res_arg":"信息发送失败"},"body":[]}
                if ("0001".equals(resCode)) {
                    msg.arg1 = 0;
                } else if ("0000".equals(resCode)) {
                    msg.arg1 = 1;
//{"head":{"res_code":"0001","res_msg":"success","res_arg":"信息发送成功"},"body":{"field":"[]"}}
                }
                msg.obj = resArg;
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                handler.sendMessage(msg);
            }
        }

        @Override
        public void onError(Throwable ex, boolean isOnCallback) {
            super.onError(ex, isOnCallback);
            if (ex instanceof SocketTimeoutException) {
                handler.sendEmptyMessage(Consts.NET_ERROR_SOCKET_TIMEOUT);
            }
        }
    };

}
