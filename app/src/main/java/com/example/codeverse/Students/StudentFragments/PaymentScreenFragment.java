package com.example.codeverse.Students.StudentFragments;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.codeverse.PaymentDetail;
import com.example.codeverse.PaymentHelper;
import com.example.codeverse.R;
import com.example.codeverse.RecieptUpload;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PaymentScreenFragment extends Fragment {

    private CardView btnUploadPaymentSlip;
    private CardView btnMakePayment;
    private CardView btnDownloadReceipts;
    private PaymentHelper paymentHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_payment_screen, container, false);

        initViews(view);
        setupClickListeners();
        paymentHelper = new PaymentHelper(getContext());

        return view;
    }

    private void initViews(View view) {
        btnUploadPaymentSlip = view.findViewById(R.id.btn_upload_payment_slip);
        btnMakePayment = view.findViewById(R.id.btn_make_payment);
        btnDownloadReceipts = view.findViewById(R.id.btn_download_receipts);
    }

    private void setupClickListeners() {
        btnUploadPaymentSlip.setOnClickListener(v -> openReceiptUploadFragment());
        btnMakePayment.setOnClickListener(v -> openMakePaymentFragment());
        btnDownloadReceipts.setOnClickListener(v -> downloadReceipts());
    }

    private void openReceiptUploadFragment() {
        RecieptUpload fragment = new RecieptUpload();
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.framelayout, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void openMakePaymentFragment() {
        MakePaymentFragment fragment = new MakePaymentFragment();
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.framelayout, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void downloadReceipts() {
        List<PaymentDetail> payments = paymentHelper.getAllPayments();

        if (payments.isEmpty()) {
            Toast.makeText(getContext(), "No payment records found", Toast.LENGTH_SHORT).show();
            return;
        }

        String receiptContent = generateReceiptContent(payments);
        showReceiptDialog(receiptContent);
    }

    private String generateReceiptContent(List<PaymentDetail> payments) {
        StringBuilder content = new StringBuilder();
        content.append("PAYMENT RECEIPT\n");
        content.append("=====================================\n");
        content.append("Generated on: ").append(getCurrentDateTime()).append("\n\n");

        double totalAmount = 0;

        for (PaymentDetail payment : payments) {
            content.append("Payment ID: ").append(payment.getId()).append("\n");
            content.append("Student ID: ").append(payment.getStudentId()).append("\n");
            content.append("University Number: ").append(payment.getUniversityNumber()).append("\n");
            content.append("Payment Date: ").append(payment.getPaymentDate()).append("\n");
            content.append("Amount: ").append(payment.getPaymentAmount()).append("\n");
            content.append("Status: ").append(payment.getStatus()).append("\n");
            content.append("Remarks: ").append(payment.getRemarks()).append("\n");
            content.append("-------------------------------------\n");

            try {
                String amountStr = payment.getPaymentAmount().replaceAll("[^\\d.]", "");
                totalAmount += Double.parseDouble(amountStr);
            } catch (NumberFormatException e) {

            }
        }

        content.append("TOTAL AMOUNT: Rs ").append(String.format("%.2f", totalAmount)).append("\n");
        content.append("=====================================\n");

        return content.toString();
    }

    private void showReceiptDialog(String receiptContent) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Payment Receipt");
        builder.setMessage(receiptContent);
        builder.setPositiveButton("Copy to Clipboard", (dialog, which) -> {
            ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Payment Receipt", receiptContent);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(getContext(), "Receipt copied to clipboard", Toast.LENGTH_SHORT).show();
        });
        builder.setNegativeButton("Close", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private String getCurrentDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.getDefault());
        return sdf.format(new Date());
    }
}