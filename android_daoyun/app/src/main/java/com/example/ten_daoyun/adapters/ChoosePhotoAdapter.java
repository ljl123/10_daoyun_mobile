package com.example.ten_daoyun.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ten_daoyun.R;


public class ChoosePhotoAdapter extends BaseAdapter {

    private LayoutInflater layoutInflater;
    //图片资源
    private int[] resource;
    //图片数
    private int count;
    //文字
    private String[] titles;

    public ChoosePhotoAdapter(Context context, int[] resource, int count, String[] titles) {
        layoutInflater = LayoutInflater.from(context);
        this.resource = resource;
        this.count = count;
        this.titles = titles;
    }

    public ChoosePhotoAdapter(Context context, int[] resource, int count) {
        layoutInflater = LayoutInflater.from(context);
        this.resource = resource;
        this.count = count;
        this.titles = new String[]{"拍照", "从相册选择"};
    }

    public ChoosePhotoAdapter(Context context, int[] resource) {
        layoutInflater = LayoutInflater.from(context);
        this.resource = resource;
        this.count = 2;
        this.titles = new String[]{"拍照", "从相册选择"};
    }

    public ChoosePhotoAdapter(Context context) {
        layoutInflater = LayoutInflater.from(context);
//        R.drawable.take_photo,
        this.resource = new int[]{ R.drawable.photo_album};
        this.count = 1;
        this.titles = new String[]{ "从相册选择"};
    }

    @Override
    public int getCount() {
        return count;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        View view = convertView;

        if (view == null) {
            view = layoutInflater.inflate(R.layout.dialog_grid_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.textView = view.findViewById(R.id.TextView_itemOfDialog);
            viewHolder.imageView = view.findViewById(R.id.ImageView_itemOfDialog);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        /**
         * 设置文字和图片
         */
        if (!titles[position].isEmpty()) {
            viewHolder.textView.setText(titles[position]);
        }
        viewHolder.imageView.setImageResource(resource[position]);

        return view;
    }

    private static class ViewHolder {
        TextView textView;
        ImageView imageView;
    }
}

