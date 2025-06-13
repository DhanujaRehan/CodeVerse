package com.example.codeverse.Students.StudentFragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.codeverse.R;
import com.example.codeverse.StudentGradesFragment;
import com.google.android.material.card.MaterialCardView;

public class StudentAssignmentHome extends Fragment {

    LinearLayout cardAddAssignment, cardManageAssignment, grades;
    MaterialCardView cvBack;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("WrongViewCast")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_student_assignment_home, container, false);
        cardAddAssignment = view.findViewById(R.id.cardAddAssignment);
        cardManageAssignment = view.findViewById(R.id.cardManageAssignment);
        grades = view.findViewById(R.id.grades);
        cvBack = view.findViewById(R.id.cv_back);

        cardAddAssignment.setOnClickListener(view1 -> {
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.framelayout, new AssignmentUpload());
            transaction.addToBackStack(null);
            transaction.commit();

        });

        cardManageAssignment.setOnClickListener(view1 -> {
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.framelayout, new StudentAssignmentDownloadFragment());
            transaction.addToBackStack(null);
            transaction.commit();
        });

        grades.setOnClickListener(view1 -> {
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.framelayout, new StudentGradesFragment());
            transaction.addToBackStack(null);
            transaction.commit();
        });

        cvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() != null) {
                    getActivity().onBackPressed();
                }
            }
        });

        return view;
    }
}