package com.example.daoyun.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.daoyun.HttpBean.CoursesListBean;
import com.example.daoyun.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CoursesListAdapter extends RecyclerView.Adapter<CoursesListAdapter.ViewHolder> {
    private LayoutInflater mInflater;
    private List<CoursesListBean> data;
    private Context context;
    private OnItemClickListener mOnItemClickListener;

    public CoursesListAdapter(List<CoursesListBean> data, Context context) {
        this.data = data;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        mInflater = LayoutInflater.from(context);
        View v = mInflater.inflate(R.layout.course_list_item, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CoursesListBean bean = data.get(position);
        if (mOnItemClickListener != null) {
            holder.courseListItem.setOnClickListener(view -> mOnItemClickListener.onItemClick(view, position));
        }
        holder.courseNameTV.setText("课程名称："+bean.getCourse_name());
        holder.courseTeacherTV.setText("任课教师："+bean.getTeacher());
        holder.courseTimeTV.setText("时间："+bean.getTime());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    static
    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.course_name_TV)
        TextView courseNameTV;
        @BindView(R.id.course_teacher_TV)
        TextView courseTeacherTV;
        @BindView(R.id.course_time_TV)
        TextView courseTimeTV;
        @BindView(R.id.course_list_item)
        RelativeLayout courseListItem;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
