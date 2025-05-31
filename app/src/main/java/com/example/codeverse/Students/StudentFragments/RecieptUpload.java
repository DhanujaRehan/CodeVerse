package com.example.codeverse.Students.StudentFragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.codeverse.R;
import com.example.codeverse.Students.Helpers.PaymentHelper;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import android.widget.ImageView;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class RecieptUpload extends Fragment {

    private TextInputEditText etStudentId, etUniversityNumber, etPaymentDate, etPaymentAmount, etRemarks;
    private ImageView ivReceiptPreview, ivBack,iv_help;
    private ConstraintLayout clUploadContainer;
    private FloatingActionButton fabRemoveReceipt;
    private MaterialButton btnSubmit;
    private View animationUpload, tvUploadInstruction, tvUploadHint;

    private PaymentHelper paymentHelper;
    private Uri selectedImageUri;
    private Calendar calendar = Calendar.getInstance();

    private ActivityResultLauncher<Intent> documentPickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    showImagePreview();
                }
            }
    );

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_receipt_upload, container, false);
        initViews(view);
        setupClickListeners();
        paymentHelper = new PaymentHelper(getContext());
        return view;
    }

    private void initViews(View view) {
        etStudentId = view.findViewById(R.id.et_student_id);
        etUniversityNumber = view.findViewById(R.id.et_university_number);
        etPaymentDate = view.findViewById(R.id.et_payment_date);
        etPaymentAmount = view.findViewById(R.id.et_payment_amount);
        etRemarks = view.findViewById(R.id.et_remarks);

        ivReceiptPreview = view.findViewById(R.id.iv_receipt_preview);
        ivBack = view.findViewById(R.id.iv_back);
        clUploadContainer = view.findViewById(R.id.cl_upload_container);
        fabRemoveReceipt = view.findViewById(R.id.fab_remove_receipt);
        btnSubmit = view.findViewById(R.id.btn_submit);
        iv_help= view.findViewById(R.id.iv_help);

        animationUpload = view.findViewById(R.id.animation_upload);
        tvUploadInstruction = view.findViewById(R.id.tv_upload_instruction);
        tvUploadHint = view.findViewById(R.id.tv_upload_hint);
    }

    private void setupClickListeners() {
        clUploadContainer.setOnClickListener(v -> openDocumentPicker());

        fabRemoveReceipt.setOnClickListener(v -> removeImage());

        etPaymentDate.setOnClickListener(v -> showDatePicker());

        btnSubmit.setOnClickListener(v -> submitReceipt());

        ivBack.setOnClickListener(v -> requireActivity().onBackPressed());

        ivBack.setOnClickListener(v -> goBack());
        iv_help.setOnClickListener(v -> goHelp());

    }

    private void goHelp() {
        DialogHelp dialogHelp = new DialogHelp();
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.framelayout, dialogHelp);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void goBack() {
        PaymentScreenFragment paymentscreen = new PaymentScreenFragment();
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.framelayout, paymentscreen);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void openDocumentPicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        String[] mimeTypes = {"image/jpeg", "image/png", "application/pdf"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        documentPickerLauncher.launch(Intent.createChooser(intent, "Select Receipt"));
    }


    private void showImagePreview() {
        if (selectedImageUri != null) {
            ivReceiptPreview.setImageURI(selectedImageUri);
            ivReceiptPreview.setVisibility(View.VISIBLE);
            fabRemoveReceipt.setVisibility(View.VISIBLE);

            animationUpload.setVisibility(View.GONE);
            tvUploadInstruction.setVisibility(View.GONE);
            tvUploadHint.setVisibility(View.GONE);
        }
    }

    private void removeImage() {
        selectedImageUri = null;
        ivReceiptPreview.setVisibility(View.GONE);
        fabRemoveReceipt.setVisibility(View.GONE);

        animationUpload.setVisibility(View.VISIBLE);
        tvUploadInstruction.setVisibility(View.VISIBLE);
        tvUploadHint.setVisibility(View.VISIBLE);
    }

    private void showDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getContext(),
                (view, year, month, dayOfMonth) -> {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                    etPaymentDate.setText(sdf.format(calendar.getTime()));
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void submitReceipt() {
        if (!validateFields()) {
            return;
        }

        String studentId = etStudentId.getText().toString().trim();
        String universityNumber = etUniversityNumber.getText().toString().trim();
        String paymentDate = etPaymentDate.getText().toString().trim();
        String paymentAmount = etPaymentAmount.getText().toString().trim();
        String remarks = etRemarks.getText().toString().trim();
        String receiptPath = selectedImageUri != null ? selectedImageUri.toString() : "";

        long result = paymentHelper.insertPaymentDetails(
                studentId,
                universityNumber,
                paymentDate,
                paymentAmount,
                receiptPath,
                remarks
        );

        if (result > 0) {
            Toast.makeText(getContext(), "Receipt submitted successfully", Toast.LENGTH_SHORT).show();
            clearForm();
        } else {
            Toast.makeText(getContext(), "Failed to submit receipt", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validateFields() {
        if (etStudentId.getText().toString().trim().isEmpty()) {
            etStudentId.setError("Student ID is required");
            etStudentId.requestFocus();
            return false;
        }

        if (etUniversityNumber.getText().toString().trim().isEmpty()) {
            etUniversityNumber.setError("University Number is required");
            etUniversityNumber.requestFocus();
            return false;
        }

        if (etPaymentDate.getText().toString().trim().isEmpty()) {
            etPaymentDate.setError("Payment Date is required");
            etPaymentDate.requestFocus();
            return false;
        }

        if (etPaymentAmount.getText().toString().trim().isEmpty()) {
            etPaymentAmount.setError("Payment Amount is required");
            etPaymentAmount.requestFocus();
            return false;
        }

        if (selectedImageUri == null) {
            Toast.makeText(getContext(), "Please upload a receipt", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void clearForm() {
        etStudentId.setText("");
        etUniversityNumber.setText("");
        etPaymentDate.setText("");
        etPaymentAmount.setText("");
        etRemarks.setText("");
        removeImage();
    }
}