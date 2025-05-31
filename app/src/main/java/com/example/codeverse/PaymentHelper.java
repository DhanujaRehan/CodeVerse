package com.example.codeverse;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PaymentHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "PaymentUploads.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_PAYMENTS = "PaymentsDetails";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_STUDENT_ID = "student_id";
    private static final String COLUMN_UNIVERSITY_NUMBER = "university_number";
    private static final String COLUMN_PAYMENT_DATE = "payment_date";
    private static final String COLUMN_PAYMENT_AMOUNT = "payment_amount";
    private static final String COLUMN_RECEIPT_PATH = "receipt_path";
    private static final String COLUMN_REMARKS = "remarks";
    private static final String COLUMN_STATUS = "status";
    private static final String COLUMN_CREATED_AT = "created_at";

    public PaymentHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_PAYMENTS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_STUDENT_ID + " TEXT NOT NULL, " +
                COLUMN_UNIVERSITY_NUMBER + " TEXT NOT NULL, " +
                COLUMN_PAYMENT_DATE + " TEXT NOT NULL, " +
                COLUMN_PAYMENT_AMOUNT + " TEXT NOT NULL, " +
                COLUMN_RECEIPT_PATH + " TEXT NOT NULL, " +
                COLUMN_REMARKS + " TEXT, " +
                COLUMN_STATUS + " TEXT DEFAULT 'PENDING', " +
                COLUMN_CREATED_AT + " TEXT NOT NULL" +
                ")";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PAYMENTS);
        onCreate(db);
    }

    public long insertPaymentDetails(String studentId, String universityNumber,
                                     String paymentDate, String paymentAmount,
                                     String receiptPath, String remarks) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_STUDENT_ID, studentId);
        values.put(COLUMN_UNIVERSITY_NUMBER, universityNumber);
        values.put(COLUMN_PAYMENT_DATE, paymentDate);
        values.put(COLUMN_PAYMENT_AMOUNT, paymentAmount);
        values.put(COLUMN_RECEIPT_PATH, receiptPath);
        values.put(COLUMN_REMARKS, remarks);
        values.put(COLUMN_STATUS, "PENDING");
        values.put(COLUMN_CREATED_AT, getCurrentDateTime());

        long result = db.insert(TABLE_PAYMENTS, null, values);
        db.close();
        return result;
    }

    public List<PaymentDetail> getAllPayments() {
        List<PaymentDetail> paymentList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_PAYMENTS + " ORDER BY " + COLUMN_CREATED_AT + " DESC";
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                PaymentDetail payment = new PaymentDetail();
                payment.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
                payment.setStudentId(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STUDENT_ID)));
                payment.setUniversityNumber(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_UNIVERSITY_NUMBER)));
                payment.setPaymentDate(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PAYMENT_DATE)));
                payment.setPaymentAmount(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PAYMENT_AMOUNT)));
                payment.setReceiptPath(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_RECEIPT_PATH)));
                payment.setRemarks(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_REMARKS)));
                payment.setStatus(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STATUS)));
                payment.setCreatedAt(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CREATED_AT)));

                paymentList.add(payment);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return paymentList;
    }

    public boolean updatePaymentStatus(int id, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_STATUS, status);

        int rowsAffected = db.update(TABLE_PAYMENTS, values,
                COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
        return rowsAffected > 0;
    }

    public boolean deletePayment(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsDeleted = db.delete(TABLE_PAYMENTS,
                COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
        return rowsDeleted > 0;
    }

    public PaymentDetail getPaymentById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        PaymentDetail payment = null;

        String query = "SELECT * FROM " + TABLE_PAYMENTS + " WHERE " + COLUMN_ID + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(id)});

        if (cursor.moveToFirst()) {
            payment = new PaymentDetail();
            payment.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
            payment.setStudentId(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STUDENT_ID)));
            payment.setUniversityNumber(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_UNIVERSITY_NUMBER)));
            payment.setPaymentDate(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PAYMENT_DATE)));
            payment.setPaymentAmount(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PAYMENT_AMOUNT)));
            payment.setReceiptPath(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_RECEIPT_PATH)));
            payment.setRemarks(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_REMARKS)));
            payment.setStatus(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STATUS)));
            payment.setCreatedAt(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CREATED_AT)));
        }

        cursor.close();
        db.close();
        return payment;
    }

    private String getCurrentDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }
}