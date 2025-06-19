package com.example.codeverse.Staff.StaffFragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.codeverse.R;
import com.example.codeverse.Students.StudentFragments.StudentListFragment;
import com.example.codeverse.Students.StudentFragments.StudentNotificationFragment;
import com.google.android.material.card.MaterialCardView;

public class StaffHome extends Fragment {

    private static final int PDF_PICKER_REQUEST = 1001;

    private LinearLayout layoutEditPassword;
    private LinearLayout layoutEditExams;
    private LinearLayout layoutCreateEvent;
    private LinearLayout layoutUploadPdf;
    private TextView tvViewMore;
    private RecyclerView recyclerViewActivities;
    private TextView tvNoActivities;
    private ImageView ivNotification;
    private MaterialCardView cvCoordinatorProfile;

    private View rootView;

    public StaffHome() {

    }

    public static StaffHome newInstance() {
        return new StaffHome();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_staff_home, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews();
        setupListeners();
        setupRecyclerView();
    }

    private void initViews() {
        layoutEditPassword = rootView.findViewById(R.id.layout_edit_password);
        layoutEditExams = rootView.findViewById(R.id.layout_edit_exams);
        layoutCreateEvent = rootView.findViewById(R.id.layout_create_event);
        layoutUploadPdf = rootView.findViewById(R.id.layout_upload_pdf);
        tvViewMore = rootView.findViewById(R.id.tv_view_more);
        recyclerViewActivities = rootView.findViewById(R.id.recycler_view_activities);
        tvNoActivities = rootView.findViewById(R.id.tv_no_activities);
        ivNotification = rootView.findViewById(R.id.iv_notification);
        cvCoordinatorProfile = rootView.findViewById(R.id.cv_coordinator_profile);
    }

    private void setupListeners() {
        layoutEditPassword.setOnClickListener(v -> {
            navigateToEditPassword();
        });

        layoutEditExams.setOnClickListener(v -> {
            navigateToEditExams();
        });

        layoutCreateEvent.setOnClickListener(v -> {
            navigateToCreateEvent();
        });

        layoutUploadPdf.setOnClickListener(v -> {
            navigateToTimeTablePDF();
        });

        tvViewMore.setOnClickListener(v -> {
            navigateToOverview();
        });

        ivNotification.setOnClickListener(v -> {
            navigateToNotifications();
        });

        cvCoordinatorProfile.setOnClickListener(v -> {
            navigateToProfile();
        });
    }

    private void setupRecyclerView() {
        recyclerViewActivities.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewActivities.setNestedScrollingEnabled(false);
        loadActivities();
    }

    private void navigateToEditPassword() {

        StudentListFragment editProfile = new StudentListFragment();
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.framelayout, editProfile);
        transaction.addToBackStack(null);
        transaction.commit();

    }

    private void navigateToEditExams() {
        StaffExam staffExam = new StaffExam();
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.framelayout, staffExam);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void navigateToTimeTablePDF(){
        StaffTimeTabePDF staffTimeTabePDF = new StaffTimeTabePDF();
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.framelayout, staffTimeTabePDF);
        transaction.addToBackStack(null);
        transaction.commit();
    }
    private void navigateToCreateEvent() {
        CreateEvent createEvent = new CreateEvent();
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.framelayout, createEvent);
        transaction.addToBackStack(null);
        transaction.commit();    }

    private void openPdfPicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, PDF_PICKER_REQUEST);
    }

    private void navigateToOverview() {
        Toast.makeText(getContext(), "Overview feature coming soon", Toast.LENGTH_SHORT).show();
    }

    private void navigateToNotifications() {
        StudentNotificationFragment notificationFragment = new StudentNotificationFragment();
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.framelayout, notificationFragment);
        transaction.addToBackStack(null);
        transaction.commit();

    }

    private void navigateToProfile() {
        StaffProfile staffProfile = new StaffProfile();
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.framelayout, staffProfile);
        transaction.addToBackStack(null);
        transaction.commit();    }

    private void loadActivities() {
        tvNoActivities.setVisibility(View.VISIBLE);
        recyclerViewActivities.setVisibility(View.GONE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PDF_PICKER_REQUEST && resultCode == Activity.RESULT_OK) {
            if (data != null && data.getData() != null) {
                Uri pdfUri = data.getData();
                uploadPdfTimetable(pdfUri);
            }
        }
    }

    private void uploadPdfTimetable(Uri pdfUri) {
        Toast.makeText(getContext(), "PDF selected for upload", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        rootView = null;
    }
}