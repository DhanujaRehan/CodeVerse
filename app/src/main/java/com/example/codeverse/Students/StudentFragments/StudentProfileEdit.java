package com.example.codeverse.Students.StudentFragments;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.airbnb.lottie.LottieAnimationView;
import com.example.codeverse.R;
import com.example.codeverse.Admin.Helpers.StudentDatabaseHelper;
import com.example.codeverse.Students.Models.Student;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class StudentProfileEdit extends Fragment {

    private static final int CAMERA_REQUEST = 1001;
    private static final int GALLERY_REQUEST = 1002;
    private static final int CAMERA_PERMISSION_CODE = 100;

    private LottieAnimationView ivBack, ivSave;
    private ImageView ivProfilePic;
    private FloatingActionButton fabEditPicture;
    private MaterialCardView cvBack, cvSave;
    private FrameLayout savingOverlay, successOverlay;

    private TextInputEditText etName, etStudentId, etBatch, etSemester;
    private TextInputEditText etEmail, etPhone, etAlternatePhone, etDateOfBirth, etNic;
    private TextInputEditText etAddress, etCity, etProvince, etPostalCode;
    private TextInputEditText etEmergencyName, etEmergencyRelationship, etEmergencyNumber;

    private AutoCompleteTextView dropdownFaculty, dropdownGender;
    private MaterialButton btnCancel, btnSaveChanges;

    private StudentDatabaseHelper databaseHelper;
    private SharedPreferences sharedPreferences;
    private Student currentStudent;
    private String selectedImagePath;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_student_profile_edit, container, false);

        initViews(view);
        initDatabase();
        setupDropdowns();
        loadStudentData();
        setupClickListeners();

        return view;
    }

    private void initViews(View view) {
        ivBack = view.findViewById(R.id.iv_back);
        ivSave = view.findViewById(R.id.iv_save);
        ivProfilePic = view.findViewById(R.id.iv_profile_pic);
        fabEditPicture = view.findViewById(R.id.fab_edit_picture);
        cvBack = view.findViewById(R.id.cv_back);
        cvSave = view.findViewById(R.id.cv_save);
        savingOverlay = view.findViewById(R.id.saving_overlay);
        successOverlay = view.findViewById(R.id.success_overlay);

        etName = view.findViewById(R.id.et_name);
        etStudentId = view.findViewById(R.id.et_student_id);
        etBatch = view.findViewById(R.id.et_batch);
        etSemester = view.findViewById(R.id.et_semester);
        etEmail = view.findViewById(R.id.et_email);
        etPhone = view.findViewById(R.id.et_phone);
        etAlternatePhone = view.findViewById(R.id.et_alternate_phone);
        etDateOfBirth = view.findViewById(R.id.et_date_of_birth);
        etNic = view.findViewById(R.id.et_nic);
        etAddress = view.findViewById(R.id.et_address);
        etCity = view.findViewById(R.id.et_city);
        etProvince = view.findViewById(R.id.et_province);
        etPostalCode = view.findViewById(R.id.et_postal_code);
        etEmergencyName = view.findViewById(R.id.et_emergency_name);
        etEmergencyRelationship = view.findViewById(R.id.et_emergency_relationship);
        etEmergencyNumber = view.findViewById(R.id.et_emergency_number);

        dropdownFaculty = view.findViewById(R.id.dropdown_faculty);
        dropdownGender = view.findViewById(R.id.dropdown_gender);
        btnCancel = view.findViewById(R.id.btn_cancel);
        btnSaveChanges = view.findViewById(R.id.btn_save_changes);
    }

    private void initDatabase() {
        databaseHelper = new StudentDatabaseHelper(getContext());
        sharedPreferences = getActivity().getSharedPreferences("StudentPrefs", Context.MODE_PRIVATE);
    }

    private void setupDropdowns() {
        String[] faculties = {
                "Faculty of Computing",
                "Faculty of Engineering",
                "Faculty of Business",
                "Faculty of Science",
                "Faculty of Arts",
                "Faculty of Medicine"
        };

        String[] genders = {"Male", "Female", "Other"};

        ArrayAdapter<String> facultyAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_dropdown_item_1line, faculties);
        dropdownFaculty.setAdapter(facultyAdapter);

        ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_dropdown_item_1line, genders);
        dropdownGender.setAdapter(genderAdapter);
    }

    private void loadStudentData() {
        long studentId = sharedPreferences.getLong("current_student_id", -1);

        if (studentId != -1) {
            currentStudent = databaseHelper.getStudentById(studentId);
            if (currentStudent != null) {
                populateFields();
            }
        }
    }

    private void populateFields() {
        if (currentStudent.getFullName() != null) {
            etName.setText(currentStudent.getFullName());
        }

        if (currentStudent.getUniversityId() != null) {
            etStudentId.setText(currentStudent.getUniversityId());
        }

        if (currentStudent.getFaculty() != null) {
            dropdownFaculty.setText(currentStudent.getFaculty(), false);
        }

        if (currentStudent.getBatch() != null) {
            etBatch.setText(currentStudent.getBatch());
        }

        if (currentStudent.getSemester() != null) {
            etSemester.setText(currentStudent.getSemester());
        }

        if (currentStudent.getEmail() != null) {
            etEmail.setText(currentStudent.getEmail());
        }

        if (currentStudent.getMobileNumber() != null) {
            etPhone.setText(currentStudent.getMobileNumber());
        }

        if (currentStudent.getAlternateNumber() != null) {
            etAlternatePhone.setText(currentStudent.getAlternateNumber());
        }

        if (currentStudent.getDateOfBirth() != null) {
            etDateOfBirth.setText(currentStudent.getDateOfBirth());
        }

        if (currentStudent.getGender() != null) {
            dropdownGender.setText(currentStudent.getGender(), false);
        }

        if (currentStudent.getNicNumber() != null) {
            etNic.setText(currentStudent.getNicNumber());
        }

        if (currentStudent.getPermanentAddress() != null) {
            etAddress.setText(currentStudent.getPermanentAddress());
        }

        if (currentStudent.getCity() != null) {
            etCity.setText(currentStudent.getCity());
        }

        if (currentStudent.getProvince() != null) {
            etProvince.setText(currentStudent.getProvince());
        }

        if (currentStudent.getPostalCode() != null) {
            etPostalCode.setText(currentStudent.getPostalCode());
        }

        if (currentStudent.getEmergencyName() != null) {
            etEmergencyName.setText(currentStudent.getEmergencyName());
        }

        if (currentStudent.getEmergencyRelationship() != null) {
            etEmergencyRelationship.setText(currentStudent.getEmergencyRelationship());
        }

        if (currentStudent.getEmergencyNumber() != null) {
            etEmergencyNumber.setText(currentStudent.getEmergencyNumber());
        }

        loadProfilePhoto();
    }

    private void loadProfilePhoto() {
        if (currentStudent.getPhotoUri() != null && !currentStudent.getPhotoUri().isEmpty()) {
            Bitmap photo = StudentDatabaseHelper.getStudentPhoto(currentStudent.getPhotoUri());
            if (photo != null) {
                ivProfilePic.setImageBitmap(photo);
            }
        }
    }

    private void setupClickListeners() {
        cvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() != null) {
                    getActivity().onBackPressed();
                }
            }
        });

        cvSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveChanges();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() != null) {
                    getActivity().onBackPressed();
                }
            }
        });

        btnSaveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveChanges();
            }
        });

        fabEditPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImagePickerDialog();
            }
        });

        etDateOfBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });
    }

    private void showImagePickerDialog() {
        String[] options = {"Camera", "Gallery"};

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext());
        builder.setTitle("Select Image");
        builder.setItems(options, (dialog, which) -> {
            if (which == 0) {
                openCamera();
            } else {
                openGallery();
            }
        });
        builder.show();
    }

    private void openCamera() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        } else {
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, CAMERA_REQUEST);
        }
    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, GALLERY_REQUEST);
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getContext(),
                (view, year, month, dayOfMonth) -> {
                    calendar.set(year, month, dayOfMonth);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    etDateOfBirth.setText(sdf.format(calendar.getTime()));
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        datePickerDialog.show();
    }

    private void saveChanges() {
        if (!validateInputs()) {
            return;
        }

        showSavingOverlay();

        updateStudentObject();

        new Handler().postDelayed(() -> {
            int result = databaseHelper.updateStudent(currentStudent);

            hideSavingOverlay();

            if (result > 0) {
                showSuccessOverlay();

                new Handler().postDelayed(() -> {
                    hideSuccessOverlay();
                    if (getActivity() != null) {
                        getActivity().onBackPressed();
                    }
                }, 2000);
            } else {
                Toast.makeText(getContext(), "Failed to update profile", Toast.LENGTH_SHORT).show();
            }
        }, 1500);
    }

    private boolean validateInputs() {
        if (etName.getText().toString().trim().isEmpty()) {
            etName.setError("Name is required");
            return false;
        }

        if (etEmail.getText().toString().trim().isEmpty()) {
            etEmail.setError("Email is required");
            return false;
        }

        if (etPhone.getText().toString().trim().isEmpty()) {
            etPhone.setError("Phone number is required");
            return false;
        }

        return true;
    }

    private void updateStudentObject() {
        currentStudent.setFullName(etName.getText().toString().trim());
        currentStudent.setFaculty(dropdownFaculty.getText().toString().trim());
        currentStudent.setBatch(etBatch.getText().toString().trim());
        currentStudent.setSemester(etSemester.getText().toString().trim());
        currentStudent.setEmail(etEmail.getText().toString().trim());
        currentStudent.setMobileNumber(etPhone.getText().toString().trim());
        currentStudent.setAlternateNumber(etAlternatePhone.getText().toString().trim());
        currentStudent.setDateOfBirth(etDateOfBirth.getText().toString().trim());
        currentStudent.setGender(dropdownGender.getText().toString().trim());
        currentStudent.setNicNumber(etNic.getText().toString().trim());
        currentStudent.setPermanentAddress(etAddress.getText().toString().trim());
        currentStudent.setCity(etCity.getText().toString().trim());
        currentStudent.setProvince(etProvince.getText().toString().trim());
        currentStudent.setPostalCode(etPostalCode.getText().toString().trim());
        currentStudent.setEmergencyName(etEmergencyName.getText().toString().trim());
        currentStudent.setEmergencyRelationship(etEmergencyRelationship.getText().toString().trim());
        currentStudent.setEmergencyNumber(etEmergencyNumber.getText().toString().trim());

        if (selectedImagePath != null) {
            currentStudent.setPhotoUri(selectedImagePath);
        }
    }

    private void showSavingOverlay() {
        savingOverlay.setVisibility(View.VISIBLE);
    }

    private void hideSavingOverlay() {
        savingOverlay.setVisibility(View.GONE);
    }

    private void showSuccessOverlay() {
        successOverlay.setVisibility(View.VISIBLE);
    }

    private void hideSuccessOverlay() {
        successOverlay.setVisibility(View.GONE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == CAMERA_REQUEST) {
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                ivProfilePic.setImageBitmap(imageBitmap);
                selectedImagePath = saveImageToInternalStorage(imageBitmap);
            } else if (requestCode == GALLERY_REQUEST) {
                Uri selectedImage = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImage);
                    ivProfilePic.setImageBitmap(bitmap);
                    selectedImagePath = saveImageToInternalStorage(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private String saveImageToInternalStorage(Bitmap bitmap) {
        try {
            File directory = new File(getContext().getFilesDir(), "student_photos");
            if (!directory.exists()) {
                directory.mkdirs();
            }

            String fileName = "student_" + currentStudent.getId() + "_" + System.currentTimeMillis() + ".jpg";
            File file = new File(directory, fileName);

            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
            fos.close();

            return file.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}