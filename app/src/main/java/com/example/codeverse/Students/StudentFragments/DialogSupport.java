package com.example.codeverse.Students.StudentFragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.codeverse.R;
import com.google.android.material.card.MaterialCardView;


public class DialogSupport extends Fragment {

    private MaterialCardView btnCall;
    private MaterialCardView btnEmail;
    private MaterialCardView btnChat;
    private ImageView btnClose;

    public DialogSupport() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_dialog_support, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnCall = view.findViewById(R.id.btn_call);
        btnEmail = view.findViewById(R.id.btn_email);
        btnChat = view.findViewById(R.id.btn_chat);
        btnClose = view.findViewById(R.id.btn_close);

        setupClickListeners();
    }

    private void setupClickListeners() {
        btnClose.setOnClickListener(v -> {
            if (getParentFragmentManager().getBackStackEntryCount() > 0) {
                getParentFragmentManager().popBackStack();
            } else if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });

        btnCall.setOnClickListener(v -> {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("content://contacts/people/"));
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(getContext(), "Cannot open contacts", Toast.LENGTH_SHORT).show();
            }
        });

        btnEmail.setOnClickListener(v -> {
            try {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:exam@university.edu"));
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(getContext(), "Cannot open email", Toast.LENGTH_SHORT).show();
            }
        });

        btnChat.setOnClickListener(v -> {
            try {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                intent.setType("vnd.android-dir/mms-sms");
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(getContext(), "Cannot open messages", Toast.LENGTH_SHORT).show();
            }
        });
    }
}