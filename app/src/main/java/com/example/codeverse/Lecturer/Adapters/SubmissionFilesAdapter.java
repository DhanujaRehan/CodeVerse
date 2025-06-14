package com.example.codeverse.Lecturer.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.codeverse.R;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class SubmissionFilesAdapter extends RecyclerView.Adapter<SubmissionFilesAdapter.FileViewHolder> {

    private List<String> files;
    private OnFileActionListener listener;

    public interface OnFileActionListener {
        void onFileAction(String fileName, String action);
    }

    public SubmissionFilesAdapter(List<String> files, OnFileActionListener listener) {
        this.files = files;
        this.listener = listener;
    }

    @NonNull
    @Override
    public FileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_submission_file, parent, false);
        return new FileViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FileViewHolder holder, int position) {
        String fileName = files.get(position);
        holder.bind(fileName);
    }

    @Override
    public int getItemCount() {
        return files.size();
    }

    class FileViewHolder extends RecyclerView.ViewHolder {
        private MaterialCardView cardFile;
        private ImageView ivFileIcon, ivDownload;
        private TextView tvFileName;

        public FileViewHolder(@NonNull View itemView) {
            super(itemView);
            cardFile = itemView.findViewById(R.id.card_file);
            ivFileIcon = itemView.findViewById(R.id.iv_file_icon);
            tvFileName = itemView.findViewById(R.id.tv_file_name);
            ivDownload = itemView.findViewById(R.id.iv_download);
        }

        public void bind(String fileName) {
            tvFileName.setText(fileName);


            setFileIcon(fileName);


            cardFile.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onFileAction(fileName, "view");
                }
            });

            ivDownload.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onFileAction(fileName, "download");
                }
            });
        }

        private void setFileIcon(String fileName) {
            if (fileName.endsWith(".pdf")) {
                ivFileIcon.setImageResource(R.drawable.ic_pdf);
            } else if (fileName.endsWith(".doc") || fileName.endsWith(".docx")) {
                ivFileIcon.setImageResource(R.drawable.ic_word);
            } else if (fileName.endsWith(".txt")) {
                ivFileIcon.setImageResource(R.drawable.pdf);
            } else if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") || fileName.endsWith(".png")) {
                ivFileIcon.setImageResource(R.drawable.pdf);
            } else {
                ivFileIcon.setImageResource(R.drawable.pdf);
            }
        }
    }
}