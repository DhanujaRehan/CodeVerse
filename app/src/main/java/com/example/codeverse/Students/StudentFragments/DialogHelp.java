package com.example.codeverse.Students.StudentFragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.codeverse.R;
import com.example.codeverse.StudentExam;

public class DialogHelp extends Fragment {

    private ImageView close;


    public DialogHelp() {
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dialog_help, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        close = view.findViewById(R.id.btn_close);

        close.setOnClickListener(v->{

            StudentExam studentExamFragment = new StudentExam();

            AdmissionDownload admission = new AdmissionDownload();

            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.framelayout, admission)
                    .addToBackStack(null)
                    .commit();
        });

    }

}