package com.jhhy.cuiweitourism.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jhhy.cuiweitourism.R;
import com.jhhy.cuiweitourism.model.UserContacts;

import java.util.List;

/**
 * Created by birney1 on 16/9/24.
 */
public class UserContactsListAdapter extends MyBaseAdapter {

    public UserContactsListAdapter(Context ct, List list) {
        super(ct, list);
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder = null;
        if (view == null){
            view = LayoutInflater.from(context).inflate(R.layout.item_my_contacts, null);
            holder = new ViewHolder();
            holder.tvName = (TextView) view.findViewById(R.id.tv_contact_name);
            holder.tvMobile = (TextView) view.findViewById(R.id.tv_contact_mobile);
            view.setTag(holder);
        }else {
            holder = (ViewHolder) view.getTag();
        }
        UserContacts contacts = (UserContacts) getItem(i);
        holder.tvName.setText(contacts.getContactsName());
        holder.tvMobile.setText(contacts.getContactsMobile());
        return view;
    }

    @Override
    public void setData(List list) {
        super.setData(list);
        notifyDataSetChanged();
    }

//    public void setData(List<UserContacts> lists) {
//        this.list = lists;
//        notifyDataSetChanged();
//    }

    class ViewHolder{
        private TextView tvName;
        private TextView tvMobile;
    }
}
