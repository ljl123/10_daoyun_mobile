package com.example.daoyun.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.daoyun.HttpBean.StudentsListBean;
import com.example.daoyun.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StudentListAdapter extends RecyclerView.Adapter<StudentListAdapter.ViewHolder> {
    private List<StudentsListBean> data;
    private Context context;

    public StudentListAdapter(List<StudentsListBean> data, Context context) {
        this.data = data;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater mInflater = LayoutInflater.from(context);
        View v = mInflater.inflate(R.layout.student_list_item, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.studentName.setText(data.get(i).getName());
        viewHolder.studentCheck.setText("经验值: "+Integer.parseInt(data.get(i).getCheck_count())*2);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static
    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.student_name)
        TextView studentName;
        @BindView(R.id.student_check)
        TextView studentCheck;
        @BindView(R.id.course_list_item)
        LinearLayout courseListItem;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
