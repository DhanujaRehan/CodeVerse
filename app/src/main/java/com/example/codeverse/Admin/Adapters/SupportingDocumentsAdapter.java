package com.example.codeverse.Admin.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.codeverse.R;
import com.example.codeverse.Admin.Models.SupportingDocument;

import java.util.List;

public class SupportingDocumentsAdapter extends RecyclerView.Adapter<SupportingDocumentsAdapter.ViewHolder> {

    private List<SupportingDocument> documents;
    private Context context;

    public SupportingDocumentsAdapter(Context context, List<SupportingDocument> documents) {
        this.context = context;
        this.documents = documents;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_supporting_document, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SupportingDocument document = documents.get(position);

        holder.tvDocumentName.setText(document.getName());
        holder.tvDocumentDetails.setText(document.getType().toUpperCase() + " • " + document.getSize());

        // Set appropriate icon based on file type
        int iconResource = getDocumentIcon(document.getType());
        holder.ivDocumentIcon.setImageResource(iconResource);

        // Set download click listener
        holder.ivDownload.setOnClickListener(v -> {
            // Simulate download action
            Toast.makeText(context, "Downloading " + document.getName(), Toast.LENGTH_SHORT).show();
            // In real implementation, you would handle the download here
            // downloadDocument(document.getUrl());
        });

        // Set item click listener to view document
        holder.itemView.setOnClickListener(v -> {
            // Simulate opening document
            Toast.makeText(context, "Opening " + document.getName(), Toast.LENGTH_SHORT).show();
            // In real implementation, you would open the document here
            // openDocument(document.getUrl());
        });
    }

    @Override
    public int getItemCount() {
        return documents.size();
    }

    private int getDocumentIcon(String fileType) {
        switch (fileType.toLowerCase()) {
            case "pdf":
                return R.drawable.menu;
            case "doc":
            case "docx":
                return R.drawable.menu;
            case "jpg":
            case "jpeg":
            case "png":
                return R.drawable.menu;
            case "xlsx":
            case "xls":
                return R.drawable.menu;
            default:
                return R.drawable.menu;
        }
    }

    // Method to simulate downloading a document
    private void downloadDocument(String url) {
        // In a real implementation, you would handle the download here
        // For example, using DownloadManager or an HTTP client
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            context.startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(context, "Error opening document", Toast.LENGTH_SHORT).show();
        }
    }

    // Method to simulate opening a document
    private void openDocument(String url) {
        // In a real implementation, you would open the document in an appropriate viewer
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            context.startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(context, "Error opening document", Toast.LENGTH_SHORT).show();
        }
    }

    public void updateDocuments(List<SupportingDocument> newDocuments) {
        this.documents = newDocuments;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDocumentName, tvDocumentDetails;
        ImageView ivDocumentIcon, ivDownload;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDocumentName = itemView.findViewById(R.id.tvDocumentName);
            tvDocumentDetails = itemView.findViewById(R.id.tvDocumentDetails);
            ivDocumentIcon = itemView.findViewById(R.id.ivDocumentIcon);
            ivDownload = itemView.findViewById(R.id.ivDownload);
        }
    }
}