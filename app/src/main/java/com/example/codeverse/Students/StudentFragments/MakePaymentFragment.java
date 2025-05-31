package com.example.codeverse.Students.StudentFragments;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.codeverse.Students.Models.CreditCard;
import com.example.codeverse.Students.Adapters.CreditCardAdapter;
import com.example.codeverse.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class MakePaymentFragment extends Fragment {

    private TabLayout tabPaymentMethods;
    private LinearLayout layoutCardPayment;
    private LinearLayout layoutBankTransfer;
    private LinearLayout layoutDigitalWallet;
    private View rootView;
    private List<CreditCard> creditCards;
    private CreditCardAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_make_payment, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews();
        setupCardCarousel();
        setupTabSelectionListener();
        setupBackButton();
        setupPaymentButton();
        setupCopyButtons();
    }

    private void initViews() {
        tabPaymentMethods = rootView.findViewById(R.id.tab_payment_methods);
        layoutCardPayment = rootView.findViewById(R.id.layout_card_payment);
        layoutBankTransfer = rootView.findViewById(R.id.layout_bank_transfer);
        layoutDigitalWallet = rootView.findViewById(R.id.layout_digital_wallet);
    }

    private void setupCardCarousel() {
        RecyclerView recyclerCards = rootView.findViewById(R.id.recycler_cards);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerCards.setLayoutManager(layoutManager);

        creditCards = new ArrayList<>();
        creditCards.add(new CreditCard("5432109876543210", "JOHN SMITH", "05/27", "PRIME BANK",
                CreditCard.CardType.VISA, R.drawable.card_bg_gradient_1));
        creditCards.add(new CreditCard("4111111111111111", "JOHN SMITH", "08/25", "CITY BANK",
                CreditCard.CardType.MASTERCARD, R.drawable.card_bg_gradient_2));
        creditCards.add(new CreditCard("378282246310005", "JOHN SMITH", "12/26", "METRO BANK",
                CreditCard.CardType.AMEX, R.drawable.card_bg_gradient_3));

        adapter = new CreditCardAdapter(getContext(), creditCards, (card, position) -> {
            updateCardPreview(card);
        });

        recyclerCards.setAdapter(adapter);

        if (!creditCards.isEmpty()) {
            updateCardPreview(creditCards.get(0));
        }

        FloatingActionButton fabAddCard = rootView.findViewById(R.id.fab_add_card);
        fabAddCard.setOnClickListener(v -> showAddCardDialog());
    }

    private void showAddCardDialog() {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_card, null);

        TextInputEditText etCardNumber = dialogView.findViewById(R.id.et_card_number);
        TextInputEditText etCardHolder = dialogView.findViewById(R.id.et_card_holder);
        TextInputEditText etExpiryDate = dialogView.findViewById(R.id.et_expiry_date);
        TextInputEditText etBankName = dialogView.findViewById(R.id.et_bank_name);

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setTitle("Add New Card")
                .setView(dialogView)
                .setPositiveButton("Add", null)
                .setNegativeButton("Cancel", (d, w) -> d.dismiss())
                .create();

        dialog.setOnShowListener(dialogInterface -> {
            Button button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            button.setOnClickListener(view -> {
                String cardNumber = etCardNumber.getText().toString().trim();
                String cardHolder = etCardHolder.getText().toString().trim();
                String expiryDate = etExpiryDate.getText().toString().trim();
                String bankName = etBankName.getText().toString().trim();

                if (cardNumber.isEmpty() || cardHolder.isEmpty() || expiryDate.isEmpty() || bankName.isEmpty()) {
                    Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                CreditCard.CardType cardType = getCardType(cardNumber);
                int backgroundRes = getRandomBackground();

                CreditCard newCard = new CreditCard(cardNumber, cardHolder, expiryDate, bankName, cardType, backgroundRes);
                creditCards.add(newCard);
                adapter.notifyItemInserted(creditCards.size() - 1);

                Toast.makeText(getContext(), "Card added successfully", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            });
        });

        dialog.show();
    }

    private CreditCard.CardType getCardType(String cardNumber) {
        if (cardNumber.startsWith("4")) {
            return CreditCard.CardType.VISA;
        } else if (cardNumber.startsWith("5") || cardNumber.startsWith("2")) {
            return CreditCard.CardType.MASTERCARD;
        } else if (cardNumber.startsWith("3")) {
            return CreditCard.CardType.AMEX;
        }
        return CreditCard.CardType.VISA;
    }

    private int getRandomBackground() {
        int[] backgrounds = {
                R.drawable.card_bg_gradient_1,
                R.drawable.card_bg_gradient_2,
                R.drawable.card_bg_gradient_3
        };
        return backgrounds[(int) (Math.random() * backgrounds.length)];
    }

    private void updateCardPreview(CreditCard card) {
        CardView cardPreview = rootView.findViewById(R.id.card_preview);
    }

    private void setupTabSelectionListener() {
        tabPaymentMethods.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                layoutCardPayment.setVisibility(View.GONE);
                layoutBankTransfer.setVisibility(View.GONE);
                layoutDigitalWallet.setVisibility(View.GONE);

                switch (tab.getPosition()) {
                    case 0:
                        layoutCardPayment.setVisibility(View.VISIBLE);
                        break;
                    case 1:
                        layoutBankTransfer.setVisibility(View.VISIBLE);
                        break;
                    case 2:
                        layoutDigitalWallet.setVisibility(View.VISIBLE);
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    private void setupBackButton() {
        ImageView ivBack = rootView.findViewById(R.id.iv_back);
        ivBack.setOnClickListener(v -> {
            if (getParentFragmentManager().getBackStackEntryCount() > 0) {
                getParentFragmentManager().popBackStack();
            } else if (getActivity() != null) {
                getActivity().finish();
            }
        });
    }

    private void setupPaymentButton() {
        MaterialButton btnProceed = rootView.findViewById(R.id.btn_proceed);
        btnProceed.setOnClickListener(v -> {
            int selectedTabPosition = tabPaymentMethods.getSelectedTabPosition();

            switch (selectedTabPosition) {
                case 0:
                    processCardPayment();
                    break;
                case 1:
                    processBankTransfer();
                    break;
                case 2:
                    processDigitalWalletPayment();
                    break;
            }
        });
    }

    private void processCardPayment() {
        TextInputEditText etCvv = rootView.findViewById(R.id.et_cvv);
        String cvv = etCvv.getText().toString().trim();

        if (cvv.isEmpty()) {
            etCvv.setError("Please enter CVV");
            return;
        }

        showPaymentProcessingDialog();
        new Handler().postDelayed(this::showPaymentSuccessDialog, 2000);
    }

    private void processBankTransfer() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Bank Transfer Initiated")
                .setMessage("Please make the transfer using the details provided, and upload the payment proof when complete.")
                .setPositiveButton("Upload Proof", (dialog, which) -> {
                    Toast.makeText(getContext(), "Upload proof feature will launch here", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Do it Later", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void processDigitalWalletPayment() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Redirecting to Payment Gateway")
                .setMessage("You will be redirected to complete your payment.")
                .setPositiveButton("Continue", (dialog, which) -> {
                    showPaymentProcessingDialog();
                    new Handler().postDelayed(this::showPaymentSuccessDialog, 2000);
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void showPaymentProcessingDialog() {
        if (getContext() == null) return;

        ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Processing your payment...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        new Handler().postDelayed(() -> {
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }, 2000);
    }

    private void showPaymentSuccessDialog() {
        if (getContext() == null) return;

        new AlertDialog.Builder(requireContext())
                .setTitle("Payment Successful!")
                .setMessage("Your payment of Rs 126,000.00 has been processed successfully.")
                .setPositiveButton("View Receipt", (dialog, which) -> {
                    Toast.makeText(getContext(), "Receipt view will be shown here", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Done", (dialog, which) -> {
                    if (getParentFragmentManager().getBackStackEntryCount() > 0) {
                        getParentFragmentManager().popBackStack();
                    } else if (getActivity() != null) {
                        getActivity().finish();
                    }
                })
                .setCancelable(false)
                .show();
    }

    private void setupCopyButtons() {
        ImageView btnCopyAccount = rootView.findViewById(R.id.btn_copy_account);
        ImageView btnCopyReference = rootView.findViewById(R.id.btn_copy_reference);

        final TextView accountNumberTextView = rootView.findViewById(R.id.tv_invoice_number);
        final TextView referenceTextView = rootView.findViewById(R.id.tv_description);

        btnCopyAccount.setOnClickListener(v -> {
            String accountNumber = "0025 7896 4521 3698";
            copyToClipboard("Account Number", accountNumber);
        });

        btnCopyReference.setOnClickListener(v -> {
            String reference = "STU12345-INV02";
            copyToClipboard("Reference", reference);
        });
    }

    private void copyToClipboard(String label, String text) {
        if (getContext() == null) return;

        ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(label, text);
        clipboard.setPrimaryClip(clip);

        Toast.makeText(getContext(), label + " copied to clipboard", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        rootView = null;
    }
}