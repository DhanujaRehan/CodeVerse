package com.example.codeverse.Students.StudentFragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.codeverse.R;
import com.example.codeverse.StudentNotificationFragment;

public class StudentHomeFragment extends Fragment {

    private ImageView iv_notification;

    public StudentHomeFragment() {}

    public static StudentHomeFragment newInstance(String param1, String param2) {
        StudentHomeFragment fragment = new StudentHomeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout and return the view
        return inflater.inflate(R.layout.fragment_student_home, container, false);
    }

    // This is the right place to bind views and set listeners
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        iv_notification = view.findViewById(R.id.iv_notification);

        iv_notification.setOnClickListener(v -> {
            StudentNotificationFragment fragment = new StudentNotificationFragment();
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.framelayout, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });
    }
}
