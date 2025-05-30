package com.example.codeverse.Students.StudentFragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.codeverse.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class AdmissionDownload extends Fragment {

    // UI Components
    private RecyclerView rvAdmissionCards;
    private MaterialCardView cvBack;
    private ImageView ivHelp;
    private MaterialCardView cardSemesterSelector;
    private MaterialCardView cardSupport;
    private TextView tvSelectedSemester;
    private TextView tvSemesterLabel;
    private MaterialButton semesterSwitch;
    private LottieAnimationView animationCalendar;

    // For semester selection
    private BottomSheetDialog currentDialog;

    // Data
    private List<AdmissionCardModel> admissionCardList;

    // Root view reference
    private View rootView;

    // Interface for communicating with parent Activity
    public interface OnAdmissionDownloadListener {
        void onBackPressed();
        void onHelpRequested();
        void onSupportRequested();
    }

    private OnAdmissionDownloadListener listener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_admission_download, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize views and setup components
        initializeViews();
        setupClickListeners();
        setupAdmissionCards();
    }

    /**
     * Initialize all the UI components
     */
    private void initializeViews() {
        // Main UI elements
        rvAdmissionCards = rootView.findViewById(R.id.rv_admission_cards);
        cvBack = rootView.findViewById(R.id.cv_back);
        ivHelp = rootView.findViewById(R.id.iv_help);
        cardSemesterSelector = rootView.findViewById(R.id.card_semester_selector);
        cardSupport = rootView.findViewById(R.id.card_support);

        // Semester selector components - working with the new XML layout
        tvSelectedSemester = rootView.findViewById(R.id.tv_selected_semester);
        tvSemesterLabel = rootView.findViewById(R.id.tv_semester_label);
        semesterSwitch = rootView.findViewById(R.id.semester_switch);
        animationCalendar = rootView.findViewById(R.id.animation_calendar);

        // Ensure animation is playing
        if (animationCalendar != null) {
            animationCalendar.playAnimation();
        }
    }

    /**
     * Set up click listeners for UI components
     */
    private void setupClickListeners() {
        // Back button click listener
        cvBack.setOnClickListener(view -> {
            if (listener != null) {
                listener.onBackPressed();
            } else {
                // Fallback navigation
                navigateBack();
            }
        });

        // Help button click listener - UPDATED TO NAVIGATE TO FRAGMENT
        ivHelp.setOnClickListener(view -> {
            navigateToHelpFragment();
        });

        // Semester selector click listener
        cardSemesterSelector.setOnClickListener(view -> {
            showSemesterSelectionDialog();
        });

        // Semester switch button click listener
        if (semesterSwitch != null) {
            semesterSwitch.setOnClickListener(view -> {
                showSemesterSelectionDialog();
            });
        }

        // Support card click listener
        cardSupport.setOnClickListener(view -> {
            if (listener != null) {
                listener.onSupportRequested();
            } else {
                showSupportDialog();
            }
        });
    }

    /**
     * Navigate to DialogHelp Fragment - VERY SIMPLE
     */
    private void navigateToHelpFragment() {
        try {
            // Create DialogHelp fragment
            DialogHelp helpFragment = new DialogHelp();

            // Navigate to help fragment
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.framelayout, helpFragment) // Use your container ID
                    .addToBackStack(null)
                    .commit();

        } catch (Exception e) {
            // If something goes wrong, show simple message
            Toast.makeText(getContext(), "Help not available", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Show the help dialog - UPDATED
     */
    private void showHelpDialog() {
        navigateToHelpFragment();
    }

    /**
     * Show the semester selection dialog
     */
    private void showSemesterSelectionDialog() {
        if (getContext() == null) return;

        // Create a bottom sheet dialog and store reference
        currentDialog = new BottomSheetDialog(requireContext());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_semester_selection, null);
        currentDialog.setContentView(dialogView);

        // Set up RecyclerView for semesters
        RecyclerView rvSemesters = dialogView.findViewById(R.id.rv_semesters);

        // Create semester list
        List<SemesterModel> semesters = new ArrayList<>();
        semesters.add(new SemesterModel("Spring 2025", true));
        semesters.add(new SemesterModel("Fall 2024", false));
        semesters.add(new SemesterModel("Summer 2024", false));
        semesters.add(new SemesterModel("Spring 2024", false));

        // Create and set adapter with callback
        SemesterAdapter adapter = new SemesterAdapter(semesters, new SemesterSelectionListener() {
            @Override
            public void onSemesterSelected(SemesterModel semester) {
                // Update UI
                tvSelectedSemester.setText(semester.getName());
                tvSemesterLabel.setText("Selected Semester");

                // Dismiss dialog
                if (currentDialog != null && currentDialog.isShowing()) {
                    currentDialog.dismiss();
                }

                // Refresh cards
                refreshAdmissionCards(semester.getName());
            }
        });

        rvSemesters.setLayoutManager(new LinearLayoutManager(getContext()));
        rvSemesters.setAdapter(adapter);

        // Set up buttons if they exist
        View btnCurrentSemester = dialogView.findViewById(R.id.btn_current_semester);
        View btnAllSemesters = dialogView.findViewById(R.id.btn_all_semesters);

        if (btnCurrentSemester != null) {
            btnCurrentSemester.setOnClickListener(v -> {
                tvSelectedSemester.setText("Spring 2025");
                tvSemesterLabel.setText("Current Semester");
                currentDialog.dismiss();
                refreshAdmissionCards("Spring 2025");
            });
        }

        if (btnAllSemesters != null) {
            btnAllSemesters.setOnClickListener(v -> {
                tvSelectedSemester.setText("All Semesters");
                tvSemesterLabel.setText("All Academic Periods");
                currentDialog.dismiss();
                refreshAdmissionCards("All");
            });
        }

        // Show dialog
        currentDialog.show();
    }

    /**
     * Show the support dialog
     */
    private void showSupportDialog() {
        if (getContext() != null) {
            Intent intent = new Intent(getContext(), DialogSupport.class);
            startActivity(intent);
        }
    }

    /**
     * Refresh admission cards based on selected semester
     */
    private void refreshAdmissionCards(String semester) {
        // This would typically fetch new data from your database
        // For this example, we'll just update the existing data

        // Show loading animation
        showToast("Loading admission cards for " + semester);

        // Simulate loading delay
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            // Create updated admission cards based on semester
            List<AdmissionCardModel> updatedCards;

            if ("All".equals(semester)) {
                // Show more cards for all semesters
                updatedCards = createHardcodedAdmissionCards();
                updatedCards.addAll(createPastAdmissionCards());
            } else {
                // Show only cards for current semester
                updatedCards = createHardcodedAdmissionCards();
            }

            // Update adapter with new cards
            AdmissionCardAdapter adapter = new AdmissionCardAdapter(updatedCards);
            rvAdmissionCards.setAdapter(adapter);

        }, 500); // Short delay to simulate loading
    }

    /**
     * Set up and populate the admission cards RecyclerView
     */
    private void setupAdmissionCards() {
        // Create hardcoded admission cards
        admissionCardList = createHardcodedAdmissionCards();

        // Create and set the adapter
        AdmissionCardAdapter adapter = new AdmissionCardAdapter(admissionCardList);
        rvAdmissionCards.setLayoutManager(new LinearLayoutManager(getContext()));
        rvAdmissionCards.setAdapter(adapter);
    }

    /**
     * Create hardcoded admission card data
     * @return List of admission card models
     */
    private List<AdmissionCardModel> createHardcodedAdmissionCards() {
        List<AdmissionCardModel> cards = new ArrayList<>();

        // Card 1: Advanced Algorithms (Available)
        cards.add(new AdmissionCardModel(
                "Advanced Algorithms",
                "Final Exam",
                "May 20, 2025",
                "10:00 AM",
                "Hall A-103",
                "Available",
                "#4CAF50",
                R.drawable.chip_available_background,
                true
        ));

        // Card 2: Database Systems (Available)
        cards.add(new AdmissionCardModel(
                "Database Systems",
                "Midterm Exam",
                "May 15, 2025",
                "2:00 PM",
                "Hall B-201",
                "Available",
                "#4CAF50",
                R.drawable.chip_available_background,
                true
        ));

        // Card 3: Software Engineering (Downloaded)
        cards.add(new AdmissionCardModel(
                "Software Engineering",
                "Project Defense",
                "May 22, 2025",
                "11:30 AM",
                "Conference Room C",
                "Downloaded",
                "#2196F3",
                R.drawable.chip_downloaded_background,
                true
        ));

        // Card 4: Computer Networks (Not Available)
        cards.add(new AdmissionCardModel(
                "Computer Networks",
                "Final Exam",
                "May 28, 2025",
                "9:00 AM",
                "Hall A-105",
                "Not Available",
                "#F44336",
                R.drawable.chip_not_available_background,
                false
        ));

        // Card 5: Artificial Intelligence (Available)
        cards.add(new AdmissionCardModel(
                "Artificial Intelligence",
                "Lab Test",
                "May 24, 2025",
                "1:00 PM",
                "Computer Lab 3",
                "Available",
                "#4CAF50",
                R.drawable.chip_available_background,
                true
        ));

        return cards;
    }

    /**
     * Create past semester admission cards
     */
    private List<AdmissionCardModel> createPastAdmissionCards() {
        List<AdmissionCardModel> cards = new ArrayList<>();

        // Past semester cards
        cards.add(new AdmissionCardModel(
                "Machine Learning",
                "Final Exam (Fall 2024)",
                "December 18, 2024",
                "2:00 PM",
                "Hall D-202",
                "Downloaded",
                "#2196F3",
                R.drawable.chip_downloaded_background,
                true
        ));

        cards.add(new AdmissionCardModel(
                "Operating Systems",
                "Midterm (Fall 2024)",
                "October 25, 2024",
                "10:00 AM",
                "Hall C-105",
                "Downloaded",
                "#2196F3",
                R.drawable.chip_downloaded_background,
                true
        ));

        return cards;
    }

    /**
     * Display a toast message
     * @param message Message to display
     */
    private void showToast(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Display a snackbar message
     * @param message Message to display
     */
    private void showSnackbar(String message) {
        if (getView() != null) {
            Snackbar.make(rvAdmissionCards, message, Snackbar.LENGTH_LONG).show();
        }
    }

    /**
     * Navigate back using fragment manager
     */
    private void navigateBack() {
        if (getParentFragmentManager().getBackStackEntryCount() > 0) {
            getParentFragmentManager().popBackStack();
        } else if (getActivity() != null) {
            getActivity().finish();
        }
    }

    /**
     * Interface for semester selection callback
     */
    interface SemesterSelectionListener {
        void onSemesterSelected(SemesterModel semester);
    }

    /**
     * Adapter for semester options RecyclerView
     */
    private class SemesterAdapter extends RecyclerView.Adapter<SemesterAdapter.SemesterViewHolder> {

        private List<SemesterModel> semesters;
        private SemesterSelectionListener listener;

        public SemesterAdapter(List<SemesterModel> semesters, SemesterSelectionListener listener) {
            this.semesters = semesters;
            this.listener = listener;
        }

        @NonNull
        @Override
        public SemesterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_semester, parent, false);
            return new SemesterViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull SemesterViewHolder holder, int position) {
            SemesterModel semester = semesters.get(position);
            holder.tvSemesterName.setText(semester.getName());

            // Set selected state
            if (semester.isSelected()) {
                holder.itemView.setBackgroundColor(Color.parseColor("#F3E5F5"));
                holder.ivSelectedIndicator.setVisibility(View.VISIBLE);
            } else {
                holder.itemView.setBackgroundColor(Color.TRANSPARENT);
                holder.ivSelectedIndicator.setVisibility(View.INVISIBLE);
            }

            // Set click listener
            holder.itemView.setOnClickListener(v -> {
                // Update selection
                for (SemesterModel s : semesters) {
                    s.setSelected(false);
                }
                semester.setSelected(true);

                // Update adapter
                notifyDataSetChanged();

                // Callback to fragment
                if (listener != null) {
                    listener.onSemesterSelected(semester);
                }
            });
        }

        @Override
        public int getItemCount() {
            return semesters.size();
        }

        class SemesterViewHolder extends RecyclerView.ViewHolder {
            TextView tvSemesterName;
            ImageView ivSelectedIndicator;

            public SemesterViewHolder(@NonNull View itemView) {
                super(itemView);
                tvSemesterName = itemView.findViewById(R.id.tv_semester_name);
                ivSelectedIndicator = itemView.findViewById(R.id.iv_selected_indicator);
            }
        }
    }

    /**
     * Adapter for the admission cards RecyclerView
     */
    private class AdmissionCardAdapter extends RecyclerView.Adapter<AdmissionCardAdapter.ViewHolder> {

        private final List<AdmissionCardModel> cards;

        public AdmissionCardAdapter(List<AdmissionCardModel> cards) {
            this.cards = cards;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.admission_card_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            AdmissionCardModel card = cards.get(position);

            // Set basic information
            holder.tvSubjectName.setText(card.getSubjectName());
            holder.tvExamType.setText(card.getExamType());
            holder.tvExamDate.setText(card.getExamDate());
            holder.tvExamTime.setText(card.getExamTime());
            holder.tvExamVenue.setText(card.getVenue());

            // Set status
            holder.tvStatus.setText(card.getStatus());
            holder.tvStatus.setTextColor(Color.parseColor(card.getStatusColor()));
            holder.layoutStatus.setBackgroundResource(card.getStatusBackground());

            // Configure download button based on status
            configureDownloadButton(holder, card, position);

            // Add entrance animation
            animateCardEntrance(holder.itemView, position);
        }

        /**
         * Animate card entrance
         */
        private void animateCardEntrance(View itemView, int position) {
            // Set initial state - slightly off-screen and transparent
            itemView.setTranslationY(50f);
            itemView.setAlpha(0f);

            // Animate to final position with delay based on position
            itemView.animate()
                    .translationY(0f)
                    .alpha(1f)
                    .setDuration(300)
                    .setStartDelay(position * 100)
                    .start();
        }

        /**
         * Configure the download button based on card status
         */
        private void configureDownloadButton(ViewHolder holder, AdmissionCardModel card, int position) {
            holder.btnDownload.setEnabled(card.isDownloadable());

            // Set button text based on status
            if ("Downloaded".equals(card.getStatus())) {
                holder.btnDownload.setText("View Admission Card");
            } else if ("Available".equals(card.getStatus())) {
                holder.btnDownload.setText("Download Admission Card");
            } else {
                holder.btnDownload.setText("Not Available Yet");
            }

            // Set click listener
            holder.btnDownload.setOnClickListener(view -> {
                if ("Downloaded".equals(card.getStatus())) {
                    // View the admission card
                    showAdmissionCardDialog(card);
                } else if ("Available".equals(card.getStatus())) {
                    // Download the admission card
                    simulateDownload(card, position);
                }
            });
        }

        /**
         * Simulate downloading an admission card
         */
        private void simulateDownload(AdmissionCardModel card, int position) {
            showSnackbar("Downloading admission card for " + card.getSubjectName() + "...");

            // Simulate download completion after delay
            rvAdmissionCards.postDelayed(() -> {
                showSnackbar("Download completed for " + card.getSubjectName());

                // Update card status to downloaded
                card.setStatus("Downloaded");
                card.setStatusColor("#2196F3");
                card.setStatusBackground(R.drawable.chip_downloaded_background);

                // Notify adapter of change
                notifyItemChanged(position);
            }, 2000); // 2 second delay
        }

        @Override
        public int getItemCount() {
            return cards.size();
        }

        /**
         * ViewHolder for admission card items
         */
        class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvSubjectName, tvExamType, tvStatus;
            TextView tvExamDate, tvExamTime, tvExamVenue;
            LinearLayout layoutStatus;
            MaterialButton btnDownload;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                // Find views
                tvSubjectName = itemView.findViewById(R.id.tv_subject_name);
                tvExamType = itemView.findViewById(R.id.tv_exam_type);
                tvStatus = itemView.findViewById(R.id.tv_status);
                tvExamDate = itemView.findViewById(R.id.tv_exam_date);
                tvExamTime = itemView.findViewById(R.id.tv_exam_time);
                tvExamVenue = itemView.findViewById(R.id.tv_exam_venue);
                layoutStatus = itemView.findViewById(R.id.layout_status);
                btnDownload = itemView.findViewById(R.id.btn_download);
            }
        }
    }

    /**
     * Model class for admission card data
     */
    private static class AdmissionCardModel {
        private final String subjectName;
        private final String examType;
        private final String examDate;
        private final String examTime;
        private final String venue;
        private String status;
        private String statusColor;
        private int statusBackground;
        private final boolean downloadable;

        public AdmissionCardModel(String subjectName, String examType, String examDate,
                                  String examTime, String venue, String status,
                                  String statusColor, int statusBackground, boolean downloadable) {
            this.subjectName = subjectName;
            this.examType = examType;
            this.examDate = examDate;
            this.examTime = examTime;
            this.venue = venue;
            this.status = status;
            this.statusColor = statusColor;
            this.statusBackground = statusBackground;
            this.downloadable = downloadable;
        }

        public String getSubjectName() {
            return subjectName;
        }

        public String getExamType() {
            return examType;
        }

        public String getExamDate() {
            return examDate;
        }

        public String getExamTime() {
            return examTime;
        }

        public String getVenue() {
            return venue;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getStatusColor() {
            return statusColor;
        }

        public void setStatusColor(String statusColor) {
            this.statusColor = statusColor;
        }

        public int getStatusBackground() {
            return statusBackground;
        }

        public void setStatusBackground(int statusBackground) {
            this.statusBackground = statusBackground;
        }

        public boolean isDownloadable() {
            return downloadable;
        }
    }

    private void showAdmissionCardDialog(AdmissionCardModel card) {
        if (getContext() == null) return;

        try {
            // Create a bottom sheet dialog
            BottomSheetDialog dialog = new BottomSheetDialog(requireContext());

            // Use try-catch for inflating the view
            View dialogView;
            try {
                dialogView = getLayoutInflater().inflate(R.layout.dialog_admission_card, null);
            } catch (Exception e) {
                // If the layout doesn't exist, show an error and return
                showToast("Error: Layout not found. Please check your resources.");
                e.printStackTrace();
                return;
            }

            dialog.setContentView(dialogView);

            // Set admission card details - wrapped in try-catch to handle missing views
            try {
                TextView tvSubject = dialogView.findViewById(R.id.tv_subject);
                TextView tvExamType = dialogView.findViewById(R.id.tv_exam_type);
                TextView tvDate = dialogView.findViewById(R.id.tv_date);
                TextView tvTime = dialogView.findViewById(R.id.tv_time);
                TextView tvVenue = dialogView.findViewById(R.id.tv_venue);
                TextView tvStudentName = dialogView.findViewById(R.id.tv_student_name);
                TextView tvStudentId = dialogView.findViewById(R.id.tv_student_id);

                // Only set text if views are found
                if (tvSubject != null) tvSubject.setText(card.getSubjectName());
                if (tvExamType != null) tvExamType.setText(card.getExamType());
                if (tvDate != null) tvDate.setText(card.getExamDate());
                if (tvTime != null) tvTime.setText(card.getExamTime());
                if (tvVenue != null) tvVenue.setText(card.getVenue());
                if (tvStudentName != null) tvStudentName.setText("John Doe");
                if (tvStudentId != null) tvStudentId.setText("S12345678");

                // Set up action buttons
                MaterialButton btnShare = dialogView.findViewById(R.id.btn_share);
                MaterialButton btnSave = dialogView.findViewById(R.id.btn_save);
                View btnClose = dialogView.findViewById(R.id.btn_close);

                if (btnShare != null) {
                    btnShare.setOnClickListener(v -> {
                        try {
                            // Share the admission card
                            Intent shareIntent = new Intent(Intent.ACTION_SEND);
                            shareIntent.setType("text/plain");
                            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Admission Card for " + card.getSubjectName());
                            shareIntent.putExtra(Intent.EXTRA_TEXT, "My admission card for " +
                                    card.getSubjectName() + " " + card.getExamType() +
                                    " on " + card.getExamDate() + " at " + card.getExamTime() +
                                    " in " + card.getVenue());
                            startActivity(Intent.createChooser(shareIntent, "Share Admission Card"));
                        } catch (Exception e) {
                            showToast("Error sharing: " + e.getMessage());
                        }
                    });
                }

                if (btnSave != null) {
                    btnSave.setOnClickListener(v -> {
                        // Save the admission card (in a real app, you might save as PDF)
                        showToast("Admission card saved to device");
                        dialog.dismiss();
                    });
                }

                if (btnClose != null) {
                    btnClose.setOnClickListener(v -> dialog.dismiss());
                }
            } catch (Exception e) {
                showToast("Error setting up dialog views: " + e.getMessage());
                e.printStackTrace();
            }

            // Show the dialog - wrap in try-catch to prevent crashes
            try {
                dialog.show();
            } catch (Exception e) {
                showToast("Error showing dialog: " + e.getMessage());
                e.printStackTrace();
            }
        } catch (Exception e) {
            // Catch any other exceptions that might occur
            showToast("An error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Model class for semester data
     */
    private static class SemesterModel {
        private String name;
        private boolean selected;

        public SemesterModel(String name, boolean selected) {
            this.name = name;
            this.selected = selected;
        }

        public String getName() {
            return name;
        }

        public boolean isSelected() {
            return selected;
        }

        public void setSelected(boolean selected) {
            this.selected = selected;
        }
    }

    // Method to set the listener from parent Activity/Fragment
    public void setOnAdmissionDownloadListener(OnAdmissionDownloadListener listener) {
        this.listener = listener;
    }

    // Static method to create fragment instance
    public static AdmissionDownload newInstance() {
        return new AdmissionDownload();
    }

    // Method to create fragment with arguments if needed
    public static AdmissionDownload newInstance(String semester) {
        AdmissionDownload fragment = new AdmissionDownload();
        Bundle args = new Bundle();
        args.putString("selected_semester", semester);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        // Dismiss any open dialogs to prevent memory leaks
        if (currentDialog != null && currentDialog.isShowing()) {
            currentDialog.dismiss();
        }

        // Clear references
        rootView = null;
        listener = null;
        admissionCardList = null;
    }
}