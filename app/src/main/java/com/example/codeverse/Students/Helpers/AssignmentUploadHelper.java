package com.example.codeverse.Students.Helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.codeverse.Students.Models.AssignmentModel;

import java.util.ArrayList;
import java.util.List;

public class AssignmentUploadHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "AssignmentsUploads.db";
    private static final int DATABASE_VERSION = 2;

    private static final String TABLE_ASSIGNMENTS = "AssignmentsUpload";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_SUBJECT = "subject";
    private static final String COLUMN_DESCRIPTION = "description";
    private static final String COLUMN_FILE_NAME = "file_name";
    private static final String COLUMN_FILE_PATH = "file_path";
    private static final String COLUMN_UPLOAD_DATE = "upload_date";
    private static final String COLUMN_FILE_SIZE = "file_size";
    private static final String COLUMN_STATUS = "status";

    private static final String COLUMN_STUDENT_ID = "student_id";
    private static final String COLUMN_STUDENT_NAME = "student_name";
    private static final String COLUMN_BATCH = "batch";
    private static final String COLUMN_PROGRAMME = "programme";
    private static final String COLUMN_MODULE = "module";
    private static final String COLUMN_MARKS = "marks";
    private static final String COLUMN_GRADE = "grade";
    private static final String COLUMN_FEEDBACK = "feedback";
    private static final String COLUMN_IS_GRADED = "is_graded";
    private static final String COLUMN_GRADED_DATE = "graded_date";

    private static final String CREATE_TABLE_ASSIGNMENTS =
            "CREATE TABLE " + TABLE_ASSIGNMENTS + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_TITLE + " TEXT NOT NULL, " +
                    COLUMN_SUBJECT + " TEXT NOT NULL, " +
                    COLUMN_DESCRIPTION + " TEXT, " +
                    COLUMN_FILE_NAME + " TEXT NOT NULL, " +
                    COLUMN_FILE_PATH + " TEXT NOT NULL, " +
                    COLUMN_UPLOAD_DATE + " INTEGER NOT NULL, " +
                    COLUMN_FILE_SIZE + " INTEGER DEFAULT 0, " +
                    COLUMN_STATUS + " TEXT DEFAULT 'uploaded', " +
                    COLUMN_STUDENT_ID + " TEXT, " +
                    COLUMN_STUDENT_NAME + " TEXT, " +
                    COLUMN_BATCH + " TEXT, " +
                    COLUMN_PROGRAMME + " TEXT, " +
                    COLUMN_MODULE + " TEXT, " +
                    COLUMN_MARKS + " REAL DEFAULT 0, " +
                    COLUMN_GRADE + " TEXT, " +
                    COLUMN_FEEDBACK + " TEXT, " +
                    COLUMN_IS_GRADED + " INTEGER DEFAULT 0, " +
                    COLUMN_GRADED_DATE + " INTEGER DEFAULT 0" +
                    ")";

    public AssignmentUploadHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_ASSIGNMENTS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            // Add new columns for existing tables
            db.execSQL("ALTER TABLE " + TABLE_ASSIGNMENTS + " ADD COLUMN " + COLUMN_STUDENT_ID + " TEXT");
            db.execSQL("ALTER TABLE " + TABLE_ASSIGNMENTS + " ADD COLUMN " + COLUMN_STUDENT_NAME + " TEXT");
            db.execSQL("ALTER TABLE " + TABLE_ASSIGNMENTS + " ADD COLUMN " + COLUMN_BATCH + " TEXT");
            db.execSQL("ALTER TABLE " + TABLE_ASSIGNMENTS + " ADD COLUMN " + COLUMN_PROGRAMME + " TEXT");
            db.execSQL("ALTER TABLE " + TABLE_ASSIGNMENTS + " ADD COLUMN " + COLUMN_MODULE + " TEXT");
            db.execSQL("ALTER TABLE " + TABLE_ASSIGNMENTS + " ADD COLUMN " + COLUMN_MARKS + " REAL DEFAULT 0");
            db.execSQL("ALTER TABLE " + TABLE_ASSIGNMENTS + " ADD COLUMN " + COLUMN_GRADE + " TEXT");
            db.execSQL("ALTER TABLE " + TABLE_ASSIGNMENTS + " ADD COLUMN " + COLUMN_FEEDBACK + " TEXT");
            db.execSQL("ALTER TABLE " + TABLE_ASSIGNMENTS + " ADD COLUMN " + COLUMN_IS_GRADED + " INTEGER DEFAULT 0");
            db.execSQL("ALTER TABLE " + TABLE_ASSIGNMENTS + " ADD COLUMN " + COLUMN_GRADED_DATE + " INTEGER DEFAULT 0");
        }
    }

    public long insertAssignment(AssignmentModel assignment) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_TITLE, assignment.getTitle());
        values.put(COLUMN_SUBJECT, assignment.getSubject());
        values.put(COLUMN_DESCRIPTION, assignment.getDescription());
        values.put(COLUMN_FILE_NAME, assignment.getFileName());
        values.put(COLUMN_FILE_PATH, assignment.getFilePath());
        values.put(COLUMN_UPLOAD_DATE, assignment.getUploadDate());
        values.put(COLUMN_FILE_SIZE, assignment.getFileSize());
        values.put(COLUMN_STATUS, assignment.getStatus());
        values.put(COLUMN_STUDENT_ID, assignment.getStudentId());
        values.put(COLUMN_STUDENT_NAME, assignment.getStudentName());
        values.put(COLUMN_BATCH, assignment.getBatch());
        values.put(COLUMN_PROGRAMME, assignment.getProgramme());
        values.put(COLUMN_MODULE, assignment.getModule());
        values.put(COLUMN_IS_GRADED, assignment.isGraded() ? 1 : 0);

        long id = db.insert(TABLE_ASSIGNMENTS, null, values);
        db.close();

        return id;
    }

    public List<AssignmentModel> getAllAssignments() {
        List<AssignmentModel> assignments = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_ASSIGNMENTS + " ORDER BY " + COLUMN_UPLOAD_DATE + " DESC";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                AssignmentModel assignment = getAssignmentFromCursor(cursor);
                assignments.add(assignment);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return assignments;
    }

    public AssignmentModel getAssignmentById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_ASSIGNMENTS + " WHERE " + COLUMN_ID + " = ?";

        Cursor cursor = db.rawQuery(selectQuery, new String[]{String.valueOf(id)});
        AssignmentModel assignment = null;

        if (cursor.moveToFirst()) {
            assignment = getAssignmentFromCursor(cursor);
        }

        cursor.close();
        db.close();

        return assignment;
    }

    public List<AssignmentModel> getAssignmentsBySubject(String subject) {
        List<AssignmentModel> assignments = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_ASSIGNMENTS + " WHERE " + COLUMN_SUBJECT + " = ? ORDER BY " + COLUMN_UPLOAD_DATE + " DESC";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[]{subject});

        if (cursor.moveToFirst()) {
            do {
                AssignmentModel assignment = getAssignmentFromCursor(cursor);
                assignments.add(assignment);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return assignments;
    }

    // New method to get assignments by criteria for grading
    public List<AssignmentModel> getAssignmentsByCriteria(String programme, String batch, String module, String assessment) {
        List<AssignmentModel> assignments = new ArrayList<>();
        StringBuilder queryBuilder = new StringBuilder("SELECT * FROM " + TABLE_ASSIGNMENTS + " WHERE 1=1");
        List<String> selectionArgs = new ArrayList<>();

        if (programme != null && !programme.isEmpty()) {
            queryBuilder.append(" AND ").append(COLUMN_PROGRAMME).append(" = ?");
            selectionArgs.add(programme);
        }

        if (batch != null && !batch.isEmpty()) {
            queryBuilder.append(" AND ").append(COLUMN_BATCH).append(" = ?");
            selectionArgs.add(batch);
        }

        if (module != null && !module.isEmpty()) {
            queryBuilder.append(" AND ").append(COLUMN_MODULE).append(" = ?");
            selectionArgs.add(module);
        }

        if (assessment != null && !assessment.isEmpty()) {
            queryBuilder.append(" AND ").append(COLUMN_TITLE).append(" LIKE ?");
            selectionArgs.add("%" + assessment + "%");
        }

        queryBuilder.append(" ORDER BY ").append(COLUMN_UPLOAD_DATE).append(" DESC");

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(queryBuilder.toString(), selectionArgs.toArray(new String[0]));

        if (cursor.moveToFirst()) {
            do {
                AssignmentModel assignment = getAssignmentFromCursor(cursor);
                assignments.add(assignment);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return assignments;
    }

    // Method to update grade information
    public int gradeAssignment(int assignmentId, double marks, String grade, String feedback) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_MARKS, marks);
        values.put(COLUMN_GRADE, grade);
        values.put(COLUMN_FEEDBACK, feedback);
        values.put(COLUMN_IS_GRADED, 1);
        values.put(COLUMN_GRADED_DATE, System.currentTimeMillis());

        int result = db.update(TABLE_ASSIGNMENTS, values, COLUMN_ID + " = ?", new String[]{String.valueOf(assignmentId)});
        db.close();

        return result;
    }

    // Get distinct programmes
    public List<String> getAllProgrammes() {
        List<String> programmes = new ArrayList<>();
        String selectQuery = "SELECT DISTINCT " + COLUMN_PROGRAMME + " FROM " + TABLE_ASSIGNMENTS +
                " WHERE " + COLUMN_PROGRAMME + " IS NOT NULL ORDER BY " + COLUMN_PROGRAMME;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                programmes.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return programmes;
    }

    // Get distinct batches
    public List<String> getAllBatches() {
        List<String> batches = new ArrayList<>();
        String selectQuery = "SELECT DISTINCT " + COLUMN_BATCH + " FROM " + TABLE_ASSIGNMENTS +
                " WHERE " + COLUMN_BATCH + " IS NOT NULL ORDER BY " + COLUMN_BATCH;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                batches.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return batches;
    }

    // Get distinct modules
    public List<String> getAllModules() {
        List<String> modules = new ArrayList<>();
        String selectQuery = "SELECT DISTINCT " + COLUMN_MODULE + " FROM " + TABLE_ASSIGNMENTS +
                " WHERE " + COLUMN_MODULE + " IS NOT NULL ORDER BY " + COLUMN_MODULE;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                modules.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return modules;
    }

    // Get distinct assessment types
    public List<String> getAllAssessmentTypes() {
        List<String> assessments = new ArrayList<>();
        String selectQuery = "SELECT DISTINCT " + COLUMN_TITLE + " FROM " + TABLE_ASSIGNMENTS +
                " WHERE " + COLUMN_TITLE + " IS NOT NULL ORDER BY " + COLUMN_TITLE;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                assessments.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return assessments;
    }

    public int updateAssignment(AssignmentModel assignment) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_TITLE, assignment.getTitle());
        values.put(COLUMN_SUBJECT, assignment.getSubject());
        values.put(COLUMN_DESCRIPTION, assignment.getDescription());
        values.put(COLUMN_FILE_NAME, assignment.getFileName());
        values.put(COLUMN_FILE_PATH, assignment.getFilePath());
        values.put(COLUMN_FILE_SIZE, assignment.getFileSize());
        values.put(COLUMN_STATUS, assignment.getStatus());

        int result = db.update(TABLE_ASSIGNMENTS, values, COLUMN_ID + " = ?", new String[]{String.valueOf(assignment.getId())});
        db.close();

        return result;
    }

    public int deleteAssignment(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_ASSIGNMENTS, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();

        return result;
    }

    public int getAssignmentCount() {
        String countQuery = "SELECT COUNT(*) FROM " + TABLE_ASSIGNMENTS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }

        cursor.close();
        db.close();

        return count;
    }

    public int getAssignmentCountBySubject(String subject) {
        String countQuery = "SELECT COUNT(*) FROM " + TABLE_ASSIGNMENTS + " WHERE " + COLUMN_SUBJECT + " = ?";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, new String[]{subject});

        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }

        cursor.close();
        db.close();

        return count;
    }

    public long getTotalFileSize() {
        String sumQuery = "SELECT SUM(" + COLUMN_FILE_SIZE + ") FROM " + TABLE_ASSIGNMENTS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(sumQuery, null);

        long totalSize = 0;
        if (cursor.moveToFirst()) {
            totalSize = cursor.getLong(0);
        }

        cursor.close();
        db.close();

        return totalSize;
    }

    public List<String> getAllSubjects() {
        List<String> subjects = new ArrayList<>();
        String selectQuery = "SELECT DISTINCT " + COLUMN_SUBJECT + " FROM " + TABLE_ASSIGNMENTS + " ORDER BY " + COLUMN_SUBJECT;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                subjects.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return subjects;
    }

    public boolean isAssignmentExists(String title, String subject) {
        String selectQuery = "SELECT COUNT(*) FROM " + TABLE_ASSIGNMENTS + " WHERE " + COLUMN_TITLE + " = ? AND " + COLUMN_SUBJECT + " = ?";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[]{title, subject});

        boolean exists = false;
        if (cursor.moveToFirst()) {
            exists = cursor.getInt(0) > 0;
        }

        cursor.close();
        db.close();

        return exists;
    }

    public void deleteAllAssignments() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ASSIGNMENTS, null, null);
        db.close();
    }

    // Helper method to create AssignmentModel from cursor
    private AssignmentModel getAssignmentFromCursor(Cursor cursor) {
        AssignmentModel assignment = new AssignmentModel();
        assignment.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
        assignment.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE)));
        assignment.setSubject(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SUBJECT)));
        assignment.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION)));
        assignment.setFileName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FILE_NAME)));
        assignment.setFilePath(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FILE_PATH)));
        assignment.setUploadDate(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_UPLOAD_DATE)));
        assignment.setFileSize(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_FILE_SIZE)));
        assignment.setStatus(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STATUS)));

        // Handle new columns with null checks
        int studentIdIndex = cursor.getColumnIndex(COLUMN_STUDENT_ID);
        if (studentIdIndex != -1) assignment.setStudentId(cursor.getString(studentIdIndex));

        int studentNameIndex = cursor.getColumnIndex(COLUMN_STUDENT_NAME);
        if (studentNameIndex != -1) assignment.setStudentName(cursor.getString(studentNameIndex));

        int batchIndex = cursor.getColumnIndex(COLUMN_BATCH);
        if (batchIndex != -1) assignment.setBatch(cursor.getString(batchIndex));

        int programmeIndex = cursor.getColumnIndex(COLUMN_PROGRAMME);
        if (programmeIndex != -1) assignment.setProgramme(cursor.getString(programmeIndex));

        int moduleIndex = cursor.getColumnIndex(COLUMN_MODULE);
        if (moduleIndex != -1) assignment.setModule(cursor.getString(moduleIndex));

        int marksIndex = cursor.getColumnIndex(COLUMN_MARKS);
        if (marksIndex != -1) assignment.setMarks(cursor.getDouble(marksIndex));

        int gradeIndex = cursor.getColumnIndex(COLUMN_GRADE);
        if (gradeIndex != -1) assignment.setGrade(cursor.getString(gradeIndex));

        int feedbackIndex = cursor.getColumnIndex(COLUMN_FEEDBACK);
        if (feedbackIndex != -1) assignment.setFeedback(cursor.getString(feedbackIndex));

        int isGradedIndex = cursor.getColumnIndex(COLUMN_IS_GRADED);
        if (isGradedIndex != -1) assignment.setGraded(cursor.getInt(isGradedIndex) == 1);

        int gradedDateIndex = cursor.getColumnIndex(COLUMN_GRADED_DATE);
        if (gradedDateIndex != -1) assignment.setGradedDate(cursor.getLong(gradedDateIndex));

        return assignment;
    }
}