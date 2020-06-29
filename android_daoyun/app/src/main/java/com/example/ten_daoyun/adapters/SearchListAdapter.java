package com.example.ten_daoyun.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.ten_daoyun.HttpBean.SearchListBean;
import com.example.ten_daoyun.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchListAdapter extends RecyclerView.Adapter<SearchListAdapter.ViewHolder> {
    private LayoutInflater mInflater;
    private List<SearchListBean> data;
    private Context context;
    private OnListListener mOnListListener;
    private String userType;

    public SearchListAdapter(List<SearchListBean> data, Context context, String userType, OnListListener mOnListListener) {
        this.data = data;
        this.context = context;
        this.mOnListListener = mOnListListener;
        this.userType = userType;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        mInflater = LayoutInflater.from(context);
        View v = mInflater.inflate(R.layout.search_list_item, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        SearchListBean bean = data.get(i);
        viewHolder.courseName.setText("课程名称："+bean.getCourse_name());
        viewHolder.courseTeacher.setText("任课教师："+bean.getTeacher());
        viewHolder.courseTime.setText("时间："+bean.getTime());
        viewHolder.addCourse.setOnClickListener(v -> mOnListListener.onButtonClick(v, data.get(i).getCourse_id()));
        viewHolder.searchListItem.setOnClickListener(v -> mOnListListener.onItemClick(v, data.get(i).getCourse_id()));
        if (this.userType.equals("2"))
            viewHolder.addCourse.setVisibility(View.GONE);
        else if (bean.getAdded() == 1) {
            viewHolder.addCourse.setClickable(false);
            viewHolder.addCourse.setText("已添加");
            //viewHolder.addCourse.setBackgroundColor(context.getColor(R.color.colorSecondary));
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public interface OnListListener {
        void onButtonClick(View view, int id);

        void onItemClick(View view, int id);
    }

    static
    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.course_name)
        TextView courseName;
        @BindView(R.id.course_teacher)
        TextView courseTeacher;
        @BindView(R.id.course_time)
        TextView courseTime;
        @BindView(R.id.add_course)
        Button addCourse;
        @BindView(R.id.search_list_item)
        RelativeLayout searchListItem;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
