package com.example.codeverse.Students.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.example.codeverse.R;
import com.example.codeverse.Students.Models.AssignmentModel;

import java.util.List;

public class UploadedFilesAdapter extends RecyclerView.Adapter<UploadedFilesAdapter.FileViewHolder> {

    private List<AssignmentModel> assignments;
    private OnFileActionListener actionListener;

    public interface OnFileActionListener {
        void onFileAction(AssignmentModel assignment, String action);
    }

    public UploadedFilesAdapter(List<AssignmentModel> assignments, OnFileActionListener actionListener) {
        this.assignments = assignments;
        this.actionListener = actionListener;
    }

    @NonNull
    @Override
    public FileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_uploaded_file, parent, false);
        return new FileViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FileViewHolder holder, int position) {
        AssignmentModel assignment = assignments.get(position);
        holder.bind(assignment);
    }

    @Override
    public int getItemCount() {
        return assignments.size();
    }

    public class FileViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout cardFile;
        private ImageView ivFileIcon;
        private TextView tvFileName, tvSubject, tvUploadDate, tvFileSize;
        private MaterialButton btnDownload, btnEdit, btnDelete;

        public FileViewHolder(@NonNull View itemView) {
            super(itemView);

            cardFile = itemView.findViewById(R.id.card_file);
            ivFileIcon = itemView.findViewById(R.id.iv_file_icon);
            tvFileName = itemView.findViewById(R.id.tv_file_name);
            tvSubject = itemView.findViewById(R.id.tv_subject);
            tvUploadDate = itemView.findViewById(R.id.tv_upload_date);
            tvFileSize = itemView.findViewById(R.id.tv_file_size);
            btnDownload = itemView.findViewById(R.id.btn_download);
            btnEdit = itemView.findViewById(R.id.btn_edit);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }

        public void bind(AssignmentModel assignment) {
            tvFileName.setText(assignment.getTitle());
            tvSubject.setText(assignment.getSubject());
            tvUploadDate.setText(assignment.getFormattedUploadDate());
            tvFileSize.setText(assignment.getFormattedFileSize());

            if (assignment.isPdfFile()) {
                ivFileIcon.setImageResource(R.drawable.ic_pdf);
            } else if (assignment.isWordFile()) {
                ivFileIcon.setImageResource(R.drawable.ic_word);
            } else {
                ivFileIcon.setImageResource(R.drawable.greyfile);
            }

            btnDownload.setOnClickListener(v -> {
                if (actionListener != null) {
                    actionListener.onFileAction(assignment, "download");
                }
            });

            btnEdit.setOnClickListener(v -> {
                if (actionListener != null) {
                    actionListener.onFileAction(assignment, "edit");
                }
            });

            btnDelete.setOnClickListener(v -> {
                if (actionListener != null) {
                    actionListener.onFileAction(assignment, "delete");
                }
            });
        }
    }
}