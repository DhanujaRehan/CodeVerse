package com.example.codeverse;

import static com.example.codeverse.R.id.cardAddAssignment;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.codeverse.R;
import com.example.codeverse.Students.StudentFragments.AssignmentUpload;
import com.example.codeverse.Students.StudentFragments.StudentAssignmentDownloadFragment;

public class StudentAssignmentHome extends Fragment {

    LinearLayout cardAddAssignment, cardManageAssignment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("WrongViewCast")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_staff_assignment, container, false);
        cardAddAssignment = view.findViewById(R.id.cardAddAssignment);
        cardManageAssignment = view.findViewById(R.id.cardManageAssignment);


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

        return view;
    }
}