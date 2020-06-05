package com.cgitsoft.convertgeneration.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.cgitsoft.convertgeneration.AttendanceModel.Details;
import com.cgitsoft.convertgeneration.R;
import com.cgitsoft.convertgeneration.models.attendance.AttendanceDetail;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.cgitsoft.convertgeneration.activities.ProfileActivity.IMAGE_URL;

public class AttendanceAdapter extends RecyclerView.Adapter<AttendanceAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Details> list;
    private LayoutInflater inflater;
    public static String IMAGE_URL="http://cgitsoft.com/emp/img/uploads/";

    public AttendanceAdapter(Context context, ArrayList<Details> list) {
        this.context = context;
        this.list = list;
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public AttendanceAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.layout_attendance,parent,false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AttendanceAdapter.ViewHolder holder, int position) {
        Details detail = list.get(position);
        if(detail != null){
            holder.att_sts.setText(detail.getAtt_sts());
            holder.inTime.setText(detail.getIn_time());
            holder.outTime.setText(detail.getOut_time());
            holder.att_date.setText(detail.getAtt_date());
            holder.description.setText(detail.getDescription());
            holder.emp_id.setText(detail.getEmp_id());
            holder.emp_name.setText(detail.getUser_fullname());
            holder.emp_desi.setText(detail.getDesignation());
            Glide.with(context).load(IMAGE_URL+detail.getUser_pic()).placeholder(R.drawable.no_image).into(holder.profileImage);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView emp_id,emp_name,emp_desi,att_sts,inTime,outTime,att_date,description;
        CircleImageView profileImage;
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            emp_id = itemView.findViewById(R.id.txt_emp_id);
            att_sts = itemView.findViewById(R.id.txt_att_sts);
            inTime = itemView.findViewById(R.id.txt_in_time);
            outTime = itemView.findViewById(R.id.txt_out_time);
            att_date = itemView.findViewById(R.id.txt_att_date);
            description = itemView.findViewById(R.id.txt_desc);
            emp_name = itemView.findViewById(R.id.txt_emp_name);
            emp_desi = itemView.findViewById(R.id.txt_emp_designation);
            profileImage = itemView.findViewById(R.id.profileImage);
        }
    }
}
