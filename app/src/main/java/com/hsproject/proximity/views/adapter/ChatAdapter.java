package com.hsproject.proximity.views.adapter;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hsproject.proximity.R;
import com.hsproject.proximity.helper.ChatMsgVO;
import com.hsproject.proximity.repositories.UserRepository;

import java.util.List;



public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    private final List<ChatMsgVO> mValues;

    public ChatAdapter(List<ChatMsgVO> items) {
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.custom_chat_msg, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        ChatMsgVO vo = mValues.get(position);
        if (mValues.get(position).getUid() == UserRepository.getInstance().getLoginUser().getUid()) {
            holder.other_cl.setVisibility(View.GONE);
            holder.my_cl.setVisibility(View.VISIBLE);

            holder.date_tv2.setText(vo.getCrt_dt());
            holder.content_tv2.setText(vo.getContent());
        }else
        {
            holder.other_cl.setVisibility(View.VISIBLE);
            holder.my_cl.setVisibility(View.GONE);

            holder.userid_tv.setText(vo.getName());
            holder.date_tv.setText(vo.getCrt_dt());
            holder.content_tv.setText(vo.getContent());
        }
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ConstraintLayout my_cl, other_cl;
        public TextView userid_tv, date_tv, content_tv, date_tv2, content_tv2;

        public ViewHolder(View view) {
            super(view);
            my_cl = view.findViewById(R.id.my_cl);
            other_cl = view.findViewById(R.id.other_cl);
            userid_tv = view.findViewById(R.id.userid_tv);
            date_tv = view.findViewById(R.id.date_tv);
            content_tv = view.findViewById(R.id.content_tv);
            date_tv2 = view.findViewById(R.id.date_tv2);
            content_tv2 = view.findViewById(R.id.content_tv2);

        }

    }
}
