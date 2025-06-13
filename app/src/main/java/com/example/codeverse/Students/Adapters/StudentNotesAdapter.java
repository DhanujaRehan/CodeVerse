package com.example.codeverse.Students.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.codeverse.Lecturer.Models.LecturerNotes;
import com.example.codeverse.R;
import com.google.android.material.button.MaterialButton;
import java.util.List;

public class StudentNotesAdapter extends RecyclerView.Adapter<StudentNotesAdapter.ViewHolder> {

    private List<LecturerNotes> notesList;
    private Context context;
    private OnDownloadClickListener listener;

    public interface OnDownloadClickListener {
        void onDownloadClick(LecturerNotes notes);
    }

    public StudentNotesAdapter(Context context, List<LecturerNotes> notesList) {
        this.context = context;
        this.notesList = notesList;
    }

    public void setOnDownloadClickListener(OnDownloadClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_student_notes, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LecturerNotes notes = notesList.get(position);

        holder.tvLectureTitle.setText(notes.getLectureTitle());
        holder.tvSubject.setText(notes.getSubject());
        holder.tvLecturerName.setText(notes.getLecturerName());
        holder.tvLectureDate.setText(notes.getLectureDate());
        holder.tvChapter.setText(notes.getChapter());
        holder.tvDescription.setText(notes.getDescription());

        holder.btnDownload.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDownloadClick(notes);
            }
        });
    }

    @Override
    public int getItemCount() {
        return notesList.size();
    }

    public void updateNotesList(List<LecturerNotes> newNotesList) {
        this.notesList = newNotesList;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvLectureTitle, tvSubject, tvLecturerName, tvLectureDate, tvChapter, tvDescription;
        MaterialButton btnDownload;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvLectureTitle = itemView.findViewById(R.id.tv_lecture_title);
            tvSubject = itemView.findViewById(R.id.tv_subject);
            tvLecturerName = itemView.findViewById(R.id.tv_lecturer_name);
            tvLectureDate = itemView.findViewById(R.id.tv_lecture_date);
            tvChapter = itemView.findViewById(R.id.tv_chapter);
            tvDescription = itemView.findViewById(R.id.tv_description);
            btnDownload = itemView.findViewById(R.id.btn_download);
        }
    }
}