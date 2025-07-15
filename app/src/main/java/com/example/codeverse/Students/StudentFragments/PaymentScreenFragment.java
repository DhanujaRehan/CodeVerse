package com.example.codeverse.Students.StudentFragments;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.codeverse.Students.Models.PaymentDetail;
import com.example.codeverse.Students.Helpers.PaymentHelper;
import com.example.codeverse.R;
import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import lk.payhere.android.sdk.PHConfigs;
import lk.payhere.android.sdk.PHMainActivity;
import lk.payhere.android.sdk.model.Address;
import lk.payhere.android.sdk.model.Customer;
import lk.payhere.android.sdk.model.InitRequest;
import lk.payhere.android.sdk.model.Item;
import lk.payhere.android.sdk.model.Payment;
import lk.payhere.android.sdk.model.PaymentInitResponse;
import lk.payhere.android.sdk.model.StatusResponse;

import android.content.Intent;

public class PaymentScreenFragment extends Fragment {

    private CardView btnUploadPaymentSlip;
    private CardView btnMakePayment;
    private CardView btnDownloadReceipts;
    private PaymentHelper paymentHelper;
    private MaterialButton btncontactsupport;

    private static final int PAYHERE_REQUEST = 11010;

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
        btncontactsupport = view.findViewById(R.id.btn_contact_support);
    }

    private void setupClickListeners() {
        btnUploadPaymentSlip.setOnClickListener(v -> openReceiptUploadFragment());
        btnMakePayment.setOnClickListener(v -> openPayHerePayment());
        btnDownloadReceipts.setOnClickListener(v -> downloadReceipts());
        btncontactsupport.setOnClickListener(v -> openContactSupportFragment());
    }

    private void openReceiptUploadFragment() {
        RecieptUpload fragment = new RecieptUpload();
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.framelayout, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void openContactSupportFragment() {
        DialogSupport dialogHelp = new DialogSupport();
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.framelayout, dialogHelp);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    // ---------- PAYHERE INTEGRATION ----------
    private void openPayHerePayment() {
        InitRequest req = new InitRequest();
        req.setMerchantId("1211149"); // Replace with your PayHere Merchant ID
        req.setMerchantSecret("test"); // Only needed in testing
        req.setCurrency("LKR");
        req.setAmount(1000.00); // Example amount
        req.setOrderId("ORDER12345");
        req.setItemsDescription("University Fee Payment");
        req.setCustom1("Custom Note");

        Customer customer = new Customer();
        customer.setFirstName("Rehan");
        customer.setLastName("Student");
        customer.setEmail("rehan@example.com");
        customer.setPhone("+94770000000");

        Address address = new Address();
        address.setAddress("123 Main St");
        address.setCity("Colombo");
        address.setCountry("Sri Lanka");

        customer.setAddress(address);
        req.setCustomer(customer);

        Intent intent = new Intent(getContext(), PHMainActivity.class);
        intent.putExtra(PHConfigs.INTENT_EXTRA_DATA, req);
        startActivityForResult(intent, PAYHERE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PAYHERE_REQUEST && data != null && data.hasExtra(PHConfigs.INTENT_EXTRA_RESULT)) {
            Object response = data.getSerializableExtra(PHConfigs.INTENT_EXTRA_RESULT);
            if (response instanceof PaymentInitResponse) {
                Toast.makeText(getContext(), "Payment initiated!", Toast.LENGTH_SHORT).show();
            } else if (response instanceof StatusResponse) {
                StatusResponse resp = (StatusResponse) response;
                if (resp.isSuccess()) {
                    Toast.makeText(getContext(), "Payment successful!", Toast.LENGTH_LONG).show();
                    // Save successful payment to SQLite if needed
                } else {
                    Toast.makeText(getContext(), "Payment failed: " + resp.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    // ---------- RECEIPT GENERATION ----------
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
            } catch (NumberFormatException ignored) {}
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
