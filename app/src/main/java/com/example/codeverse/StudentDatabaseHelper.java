import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class StudentDatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "StudentDatabaseHelper";

    // Database Info
    private static final String DATABASE_NAME = "StudentDetails.db";
    private static final int DATABASE_VERSION = 1;

    // Table Names
    private static final String TABLE_STUDENTS = "StudentDetails";

    // Student Table Columns
    private static final String KEY_ID = "id";
    private static final String KEY_FULL_NAME = "full_name";
    private static final String KEY_UNIVERSITY_ID = "university_id";
    private static final String KEY_NIC_NUMBER = "nic_number";
    private static final String KEY_GENDER = "gender";
    private static final String KEY_DATE_OF_BIRTH = "date_of_birth";
    private static final String KEY_PHOTO_PATH = "photo_path";

    // Academic Details
    private static final String KEY_FACULTY = "faculty";
    private static final String KEY_DEPARTMENT = "department";
    private static final String KEY_BATCH = "batch";
    private static final String KEY_SEMESTER = "semester";
    private static final String KEY_ENROLLMENT_DATE = "enrollment_date";

    // Account Details
    private static final String KEY_EMAIL = "email";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_PASSWORD = "password";

    // Contact Details
    private static final String KEY_MOBILE_NUMBER = "mobile_number";
    private static final String KEY_ALTERNATE_NUMBER = "alternate_number";
    private static final String KEY_PERMANENT_ADDRESS = "permanent_address";
    private static final String KEY_CITY = "city";
    private static final String KEY_PROVINCE = "province";
    private static final String KEY_POSTAL_CODE = "postal_code";

    // Emergency Contact
    private static final String KEY_EMERGENCY_NAME = "emergency_contact_name";
    private static final String KEY_EMERGENCY_RELATIONSHIP = "emergency_relationship";
    private static final String KEY_EMERGENCY_NUMBER = "emergency_contact_number";

    // Timestamps
    private static final String KEY_CREATED_AT = "created_at";
    private static final String KEY_UPDATED_AT = "updated_at";

    public StudentDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_STUDENTS_TABLE = "CREATE TABLE " + TABLE_STUDENTS +
                "(" +
                KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                KEY_FULL_NAME + " TEXT NOT NULL," +
                KEY_UNIVERSITY_ID + " TEXT UNIQUE NOT NULL," +
                KEY_NIC_NUMBER + " TEXT UNIQUE NOT NULL," +
                KEY_GENDER + " TEXT," +
                KEY_DATE_OF_BIRTH + " TEXT," +
                KEY_PHOTO_PATH + " TEXT," +
                KEY_FACULTY + " TEXT," +
                KEY_DEPARTMENT + " TEXT," +
                KEY_BATCH + " TEXT," +
                KEY_SEMESTER + " TEXT," +
                KEY_ENROLLMENT_DATE + " TEXT," +
                KEY_EMAIL + " TEXT UNIQUE NOT NULL," +
                KEY_USERNAME + " TEXT UNIQUE NOT NULL," +
                KEY_PASSWORD + " TEXT NOT NULL," +
                KEY_MOBILE_NUMBER + " TEXT," +
                KEY_ALTERNATE_NUMBER + " TEXT," +
                KEY_PERMANENT_ADDRESS + " TEXT," +
                KEY_CITY + " TEXT," +
                KEY_PROVINCE + " TEXT," +
                KEY_POSTAL_CODE + " TEXT," +
                KEY_EMERGENCY_NAME + " TEXT," +
                KEY_EMERGENCY_RELATIONSHIP + " TEXT," +
                KEY_EMERGENCY_NUMBER + " TEXT," +
                KEY_CREATED_AT + " TEXT DEFAULT CURRENT_TIMESTAMP," +
                KEY_UPDATED_AT + " TEXT DEFAULT CURRENT_TIMESTAMP" +
                ")";

        db.execSQL(CREATE_STUDENTS_TABLE);
        Log.d(TAG, "Database tables created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_STUDENTS);
            onCreate(db);
        }
    }

    // Add a new student
    public long addStudent(Student student) {
        SQLiteDatabase db = this.getWritableDatabase();
        long studentId = -1;

        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_FULL_NAME, student.getFullName());
            values.put(KEY_UNIVERSITY_ID, student.getUniversityId());
            values.put(KEY_NIC_NUMBER, student.getNicNumber());
            values.put(KEY_GENDER, student.getGender());
            values.put(KEY_DATE_OF_BIRTH, student.getDateOfBirth());
            values.put(KEY_PHOTO_PATH, student.getPhotoPath());
            values.put(KEY_FACULTY, student.getFaculty());
            values.put(KEY_DEPARTMENT, student.getDepartment());
            values.put(KEY_BATCH, student.getBatch());
            values.put(KEY_SEMESTER, student.getSemester());
            values.put(KEY_ENROLLMENT_DATE, student.getEnrollmentDate());
            values.put(KEY_EMAIL, student.getEmail());
            values.put(KEY_USERNAME, student.getUsername());
            values.put(KEY_PASSWORD, student.getPassword());
            values.put(KEY_MOBILE_NUMBER, student.getMobileNumber());
            values.put(KEY_ALTERNATE_NUMBER, student.getAlternateNumber());
            values.put(KEY_PERMANENT_ADDRESS, student.getPermanentAddress());
            values.put(KEY_CITY, student.getCity());
            values.put(KEY_PROVINCE, student.getProvince());
            values.put(KEY_POSTAL_CODE, student.getPostalCode());
            values.put(KEY_EMERGENCY_NAME, student.getEmergencyContactName());
            values.put(KEY_EMERGENCY_RELATIONSHIP, student.getEmergencyRelationship());
            values.put(KEY_EMERGENCY_NUMBER, student.getEmergencyContactNumber());

            String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
            values.put(KEY_CREATED_AT, timeStamp);
            values.put(KEY_UPDATED_AT, timeStamp);

            studentId = db.insertOrThrow(TABLE_STUDENTS, null, values);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e(TAG, "Error while trying to add student to database", e);
        } finally {
            db.endTransaction();
        }

        return studentId;
    }

    // Get all students
    public List<Student> getAllStudents() {
        List<Student> students = new ArrayList<>();
        String STUDENTS_SELECT_QUERY = "SELECT * FROM " + TABLE_STUDENTS;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(STUDENTS_SELECT_QUERY, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    Student student = new Student();
                    student.setId(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ID)));
                    student.setFullName(cursor.getString(cursor.getColumnIndexOrThrow(KEY_FULL_NAME)));
                    student.setUniversityId(cursor.getString(cursor.getColumnIndexOrThrow(KEY_UNIVERSITY_ID)));
                    student.setNicNumber(cursor.getString(cursor.getColumnIndexOrThrow(KEY_NIC_NUMBER)));
                    student.setGender(cursor.getString(cursor.getColumnIndexOrThrow(KEY_GENDER)));
                    student.setDateOfBirth(cursor.getString(cursor.getColumnIndexOrThrow(KEY_DATE_OF_BIRTH)));
                    student.setPhotoPath(cursor.getString(cursor.getColumnIndexOrThrow(KEY_PHOTO_PATH)));
                    student.setFaculty(cursor.getString(cursor.getColumnIndexOrThrow(KEY_FACULTY)));
                    student.setDepartment(cursor.getString(cursor.getColumnIndexOrThrow(KEY_DEPARTMENT)));
                    student.setBatch(cursor.getString(cursor.getColumnIndexOrThrow(KEY_BATCH)));
                    student.setSemester(cursor.getString(cursor.getColumnIndexOrThrow(KEY_SEMESTER)));
                    student.setEnrollmentDate(cursor.getString(cursor.getColumnIndexOrThrow(KEY_ENROLLMENT_DATE)));
                    student.setEmail(cursor.getString(cursor.getColumnIndexOrThrow(KEY_EMAIL)));
                    student.setUsername(cursor.getString(cursor.getColumnIndexOrThrow(KEY_USERNAME)));
                    student.setPassword(cursor.getString(cursor.getColumnIndexOrThrow(KEY_PASSWORD)));
                    student.setMobileNumber(cursor.getString(cursor.getColumnIndexOrThrow(KEY_MOBILE_NUMBER)));
                    student.setAlternateNumber(cursor.getString(cursor.getColumnIndexOrThrow(KEY_ALTERNATE_NUMBER)));
                    student.setPermanentAddress(cursor.getString(cursor.getColumnIndexOrThrow(KEY_PERMANENT_ADDRESS)));
                    student.setCity(cursor.getString(cursor.getColumnIndexOrThrow(KEY_CITY)));
                    student.setProvince(cursor.getString(cursor.getColumnIndexOrThrow(KEY_PROVINCE)));
                    student.setPostalCode(cursor.getString(cursor.getColumnIndexOrThrow(KEY_POSTAL_CODE)));
                    student.setEmergencyContactName(cursor.getString(cursor.getColumnIndexOrThrow(KEY_EMERGENCY_NAME)));
                    student.setEmergencyRelationship(cursor.getString(cursor.getColumnIndexOrThrow(KEY_EMERGENCY_RELATIONSHIP)));
                    student.setEmergencyContactNumber(cursor.getString(cursor.getColumnIndexOrThrow(KEY_EMERGENCY_NUMBER)));
                    student.setCreatedAt(cursor.getString(cursor.getColumnIndexOrThrow(KEY_CREATED_AT)));
                    student.setUpdatedAt(cursor.getString(cursor.getColumnIndexOrThrow(KEY_UPDATED_AT)));

                    students.add(student);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error while trying to get students from database", e);
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return students;
    }

    // Get student by ID
    public Student getStudentById(int studentId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_STUDENTS,
                null,
                KEY_ID + " = ?",
                new String[]{String.valueOf(studentId)},
                null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            Student student = new Student();
            student.setId(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ID)));
            student.setFullName(cursor.getString(cursor.getColumnIndexOrThrow(KEY_FULL_NAME)));
            student.setUniversityId(cursor.getString(cursor.getColumnIndexOrThrow(KEY_UNIVERSITY_ID)));
            // ... (set all other fields similar to getAllStudents method)

            cursor.close();
            return student;
        }
        return null;
    }

    // Update student
    public int updateStudent(Student student) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_FULL_NAME, student.getFullName());
        values.put(KEY_UNIVERSITY_ID, student.getUniversityId());
        values.put(KEY_NIC_NUMBER, student.getNicNumber());
        values.put(KEY_GENDER, student.getGender());
        values.put(KEY_DATE_OF_BIRTH, student.getDateOfBirth());
        values.put(KEY_PHOTO_PATH, student.getPhotoPath());
        values.put(KEY_FACULTY, student.getFaculty());
        values.put(KEY_DEPARTMENT, student.getDepartment());
        values.put(KEY_BATCH, student.getBatch());
        values.put(KEY_SEMESTER, student.getSemester());
        values.put(KEY_ENROLLMENT_DATE, student.getEnrollmentDate());
        values.put(KEY_EMAIL, student.getEmail());
        values.put(KEY_USERNAME, student.getUsername());
        values.put(KEY_PASSWORD, student.getPassword());
        values.put(KEY_MOBILE_NUMBER, student.getMobileNumber());
        values.put(KEY_ALTERNATE_NUMBER, student.getAlternateNumber());
        values.put(KEY_PERMANENT_ADDRESS, student.getPermanentAddress());
        values.put(KEY_CITY, student.getCity());
        values.put(KEY_PROVINCE, student.getProvince());
        values.put(KEY_POSTAL_CODE, student.getPostalCode());
        values.put(KEY_EMERGENCY_NAME, student.getEmergencyContactName());
        values.put(KEY_EMERGENCY_RELATIONSHIP, student.getEmergencyRelationship());
        values.put(KEY_EMERGENCY_NUMBER, student.getEmergencyContactNumber());

        String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
        values.put(KEY_UPDATED_AT, timeStamp);

        return db.update(TABLE_STUDENTS, values, KEY_ID + " = ?", new String[]{String.valueOf(student.getId())});
    }

    // Delete student
    public void deleteStudent(int studentId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_STUDENTS, KEY_ID + " = ?", new String[]{String.valueOf(studentId)});
    }

    // Check if university ID exists
    public boolean isUniversityIdExists(String universityId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_STUDENTS,
                new String[]{KEY_ID},
                KEY_UNIVERSITY_ID + " = ?",
                new String[]{universityId},
                null, null, null);

        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    // Check if email exists
    public boolean isEmailExists(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_STUDENTS,
                new String[]{KEY_ID},
                KEY_EMAIL + " = ?",
                new String[]{email},
                null, null, null);

        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    // Check if username exists
    public boolean isUsernameExists(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_STUDENTS,
                new String[]{KEY_ID},
                KEY_USERNAME + " = ?",
                new String[]{username},
                null, null, null);

        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    // Check if NIC exists
    public boolean isNicExists(String nic) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_STUDENTS,
                new String[]{KEY_ID},
                KEY_NIC_NUMBER + " = ?",
                new String[]{nic},
                null, null, null);

        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }
}