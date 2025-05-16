package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class PasswordAdapter extends ArrayAdapter<PasswordEntry> {

    private List<PasswordEntry> passwordList;
    private Context context;
    private int resource;

    public PasswordAdapter(Context context, List<PasswordEntry> passwordList) {
        super(context, R.layout.item_password, passwordList);
        this.context = context;
        this.passwordList = passwordList;
        this.resource = R.layout.item_password;
    }

    public void updateList(List<PasswordEntry> newList) {
        this.passwordList.clear();
        this.passwordList.addAll(newList);
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(resource, parent, false);
            holder = new ViewHolder();
            holder.serviceTextView = convertView.findViewById(R.id.item_service);
            holder.loginTextView = convertView.findViewById(R.id.item_login);
            holder.passwordHiddenTextView = convertView.findViewById(R.id.item_password_hidden);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        PasswordEntry entry = getItem(position);
        if (entry != null) {
            holder.serviceTextView.setText(entry.getServiceName());
            holder.loginTextView.setText(entry.getLogin());
            holder.passwordHiddenTextView.setText("******"); // или оставить пустым
        }

        return convertView;
    }

    static class ViewHolder {
        TextView serviceTextView;
        TextView loginTextView;
        TextView passwordHiddenTextView;
    }
}