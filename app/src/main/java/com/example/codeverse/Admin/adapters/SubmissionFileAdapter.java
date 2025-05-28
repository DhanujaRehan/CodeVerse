package com.example.codeverse.Admin.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.codeverse.R;
import com.example.codeverse.Admin.models.SubmissionFile;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class SubmissionFileAdapter extends RecyclerView.Adapter<SubmissionFileAdapter.FileViewHolder> {

    private final List<SubmissionFile> files;

    public SubmissionFileAdapter(List<SubmissionFile> files) {
        this.files = files;
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
        SubmissionFile file = files.get(position);
        holder.bind(file);
    }

    @Override
    public int getItemCount() {
        return files.size();
    }

    static class FileViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvFileName;
        private final TextView tvFileSize;
        private final MaterialButton btnViewFile;

        public FileViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFileName = itemView.findViewById(R.id.tvFileName);
            tvFileSize = itemView.findViewById(R.id.tvFileSize);
            btnViewFile = itemView.findViewById(R.id.btnViewFile);
        }

        public void bind(final SubmissionFile file) {
            tvFileName.setText(file.getFileName());
            tvFileSize.setText(file.getFileSize());

            btnViewFile.setOnClickListener(v -> {
                // In a real app, this would open the file viewer
                Toast.makeText(itemView.getContext(),
                        "Opening file: " + file.getFileName(),
                        Toast.LENGTH_SHORT).show();
            });
        }
    }
}