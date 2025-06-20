package com.example.codeverse.Admin.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.codeverse.Admin.Helpers.StaffDatabaseHelper;
import com.example.codeverse.Admin.Helpers.StudentDatabaseHelper;
import com.example.codeverse.Admin.Models.User;
import com.example.codeverse.R;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private Context context;
    private List<User> userList;
    private OnUserClickListener listener;

    public interface OnUserClickListener {
        void onUserClick(User user);
    }

    public UserAdapter(Context context, List<User> userList) {
        this.context = context;
        this.userList = userList;
    }

    public void setOnUserClickListener(OnUserClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);

        holder.tvUserName.setText(user.getName());
        holder.tvUserId.setText(user.getIdentifier());

        if (user.getUserType() == User.UserType.STUDENT) {
            holder.tvUserTypeText.setText("Student");
            holder.ivUserType.setImageResource(R.drawable.iv_student);

            holder.tvPrimaryInfo.setText(user.getFaculty() != null ? user.getFaculty() : "Faculty");
            holder.tvSecondaryInfo.setText(user.getBatch() != null ? user.getBatch() : "Batch");
        } else {
            holder.tvUserTypeText.setText("Staff");
            holder.ivUserType.setImageResource(R.drawable.iv_staff);

            holder.tvPrimaryInfo.setText(user.getPosition() != null ? user.getPosition() : "Position");
            holder.tvSecondaryInfo.setText(user.getDepartment() != null ? user.getDepartment() : "Department");
        }

        if (user.getPhotoUri() != null && !user.getPhotoUri().isEmpty()) {
            Bitmap photo = null;
            if (user.getUserType() == User.UserType.STUDENT) {
                photo = StudentDatabaseHelper.getStudentPhoto(user.getPhotoUri());
            } else {
                photo = StaffDatabaseHelper.getStaffPhoto(user.getPhotoUri());
            }

            if (photo != null) {
                holder.ivUserPhoto.setImageBitmap(photo);
            } else {
                holder.ivUserPhoto.setImageResource(R.drawable.addpropic);
            }
        } else {
            holder.ivUserPhoto.setImageResource(R.drawable.addpropic);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onUserClick(user);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public void updateList(List<User> newUserList) {
        this.userList = newUserList;
        notifyDataSetChanged();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        ImageView ivUserPhoto, ivUserType, ivArrow;
        TextView tvUserName, tvUserId, tvPrimaryInfo, tvSecondaryInfo, tvUserTypeText;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            ivUserPhoto = itemView.findViewById(R.id.iv_user_photo);
            ivUserType = itemView.findViewById(R.id.iv_user_type);
            ivArrow = itemView.findViewById(R.id.iv_arrow);
            tvUserName = itemView.findViewById(R.id.tv_user_name);
            tvUserId = itemView.findViewById(R.id.tv_user_id);
            tvPrimaryInfo = itemView.findViewById(R.id.tv_primary_info);
            tvSecondaryInfo = itemView.findViewById(R.id.tv_secondary_info);
            tvUserTypeText = itemView.findViewById(R.id.tv_user_type_text);
        }
    }
}