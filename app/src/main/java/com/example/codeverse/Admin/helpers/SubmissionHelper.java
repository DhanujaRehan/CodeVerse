package com.example.codeverse.Admin.helpers;

import com.example.codeverse.Admin.models.StudentSubmission;
import com.example.codeverse.Admin.models.SubmissionFile;

import java.util.ArrayList;
import java.util.List;

/**
 * Service class for handling API calls related to student submissions and grading.
 * This is a mock implementation that would be replaced with actual API calls in a real app.
 */
public class SubmissionHelper {

    /**
     * Fetches student submissions based on the provided filters
     * @param programme The selected programme
     * @param batch The selected batch
     * @param module The selected module
     * @param assessment The selected assessment
     * @return List of student submissions
     */
    public static List<StudentSubmission> getStudentSubmissions(
            String programme, String batch, String module, String assessment) {

        // In a real app, this would make an API call to fetch submissions from a server
        // For now, return mock data

        // Mock implementation
        List<StudentSubmission> submissions = new ArrayList<>();

        // Add mock data (in a real app, this would come from the API)
        submissions.add(new StudentSubmission("ST12345", "John Doe", "May 15, 2025", "Submitted"));
        submissions.add(new StudentSubmission("ST12346", "Jane Smith", "May 14, 2025", "Submitted"));
        submissions.add(new StudentSubmission("ST12347", "Michael Johnson", "May 13, 2025", "Submitted"));
        submissions.add(new StudentSubmission("ST12348", "Emily Brown", "May 12, 2025", "Graded"));
        submissions.add(new StudentSubmission("ST12349", "David Wilson", "May 11, 2025", "Submitted"));

        return submissions;
    }

    /**
     * Fetches submission files for a specific student submission
     * @param studentId The student ID
     * @param assessmentId The assessment ID
     * @return List of submission files
     */
    public static List<SubmissionFile> getSubmissionFiles(String studentId, String assessmentId) {
        // In a real app, this would make an API call to fetch submission files from a server

        // Mock implementation
        List<SubmissionFile> files = new ArrayList<>();

        // Add mock data (in a real app, this would come from the API)
        files.add(new SubmissionFile("Assignment1.pdf", "2.5 MB"));
        files.add(new SubmissionFile("Supporting_Document.docx", "1.2 MB"));
        files.add(new SubmissionFile("Data_Analysis.xlsx", "3.7 MB"));

        return files;
    }

    /**
     * Submits a grade for a student submission
     * @param studentId The student ID
     * @param assessmentId The assessment ID
     * @param marks The marks awarded
     * @param grade The grade awarded
     * @param feedback The feedback provided
     * @return true if successful, false otherwise
     */
    public static boolean submitGrade(
            String studentId, String assessmentId, float marks, String grade, String feedback) {

        // In a real app, this would make an API call to submit the grade to a server

        // Mock implementation - always returns success
        return true;
    }
}