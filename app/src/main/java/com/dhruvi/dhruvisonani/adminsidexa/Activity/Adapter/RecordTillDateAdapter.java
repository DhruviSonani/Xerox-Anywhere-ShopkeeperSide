package com.dhruvi.dhruvisonani.adminsidexa.Activity.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.dhruvi.dhruvisonani.adminsidexa.Activity.Entity.RecordTillDateEntity;
import com.dhruvi.dhruvisonani.adminsidexa.R;

import java.util.ArrayList;

public class RecordTillDateAdapter extends RecyclerView.Adapter<RecordTillDateAdapter.MyHolder> {

    ArrayList<RecordTillDateEntity> mDataset;
    Context c;

    public RecordTillDateAdapter() {
    }

    public RecordTillDateAdapter(Context c,ArrayList<RecordTillDateEntity> mDataset) {
        this.mDataset = mDataset;
        this.c = c;
    }

    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.data_for_record_till_date,null);

        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyHolder holder, final int i) {
            int k=0;
            holder.tv_Record_onlinepaid.setText(String.valueOf(mDataset.get(i).getInt_onlinePaid()));
            holder.tv_Record_Amount.setText(String.valueOf(mDataset.get(i).getInt_amount()));
            holder.tv_Record_mobileNumber.setText(mDataset.get(i).getStr_mobileNum());
            holder.tv_Record_Name.setText(mDataset.get(i).getStr_name());

            holder.cv_recordTillDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.lv_record_show.setVisibility(View.VISIBLE);

                    holder.cv_recordTillDate.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            holder.lv_record_show.setVisibility(View.GONE);

                        }
                    });
                }
            });

    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {

        TextView tv_Record_onlinepaid,tv_Record_Amount,tv_Record_Name,tv_Record_mobileNumber,tv_Record_pageNumber,tv_Record_Print,tv_Record_NumOfCopy,tv_Record_Bound;
        CardView cv_recordTillDate;
        LinearLayout lv_record_show;
        public MyHolder(@NonNull View itemView) {
            super(itemView);

            cv_recordTillDate = itemView.findViewById(R.id.cv_recordTillDate);
//            tv_Record_onlinepaid = itemView.findViewById(R.id.tv_Record_onlinepaid);
            tv_Record_Amount = itemView.findViewById(R.id.tv_Record_Amount);
            tv_Record_mobileNumber = itemView.findViewById(R.id.tv_Record_mobileNumber);
            tv_Record_Name = itemView.findViewById(R.id.tv_Record_Name);
            lv_record_show = itemView.findViewById(R.id.lv_record_show);
        }
    }
}
