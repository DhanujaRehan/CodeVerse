package com.example.codeverse.Admin.Helpers;

import com.example.codeverse.Admin.Models.MitigationRequest;
import com.example.codeverse.Admin.Models.SupportingDocument;

import java.util.Arrays;
import java.util.List;

public class MitigationRequestHelper {

    public static List<MitigationRequest> getHardcodedMitigationRequests() {
        return Arrays.asList(
                new MitigationRequest(
                        "REQ001",
                        "ST12345",
                        "John Doe",
                        "Computer Science",
                        "Batch 45",
                        "Data Structures and Algorithms",
                        "Final Exam",
                        "Extension Request",
                        "I was hospitalized due to a severe illness during the exam period and have medical documentation to support this. I request an extension to complete my final exam as I was unable to attend due to my medical condition.",
                        "May 15, 2025",
                        MitigationRequest.Priority.HIGH,
                        MitigationRequest.RequestStatus.PENDING,
                        Arrays.asList(
                                new SupportingDocument("Medical_Certificate.pdf", "https://example.com/medical_cert.pdf", "PDF", "2.3 MB"),
                                new SupportingDocument("Hospital_Report.pdf", "https://example.com/hospital_report.pdf", "PDF", "1.8 MB")
                        )
                ),
                new MitigationRequest(
                        "REQ002",
                        "ST12346",
                        "Jane Smith",
                        "Business Administration",
                        "Batch 42",
                        "Strategic Management",
                        "Coursework Assignment",
                        "Late Submission Request",
                        "My laptop crashed and I lost all my work. I have been trying to recover the files but was unsuccessful. I request permission to submit my assignment late as I need to redo the entire work.",
                        "May 14, 2025",
                        MitigationRequest.Priority.MEDIUM,
                        MitigationRequest.RequestStatus.PENDING,
                        Arrays.asList(
                                new SupportingDocument("Laptop_Repair_Receipt.pdf", "https://example.com/repair_receipt.pdf", "PDF", "1.2 MB"),
                                new SupportingDocument("Data_Recovery_Report.pdf", "https://example.com/recovery_report.pdf", "PDF", "0.9 MB")
                        )
                ),
                new MitigationRequest(
                        "REQ003",
                        "ST12347",
                        "Michael Johnson",
                        "Engineering",
                        "Batch 43",
                        "Thermodynamics",
                        "Mid-term Exam",
                        "Re-examination Request",
                        "I experienced severe anxiety during the exam which significantly affected my performance. I have been receiving treatment for anxiety disorder and would like to request a re-examination under special conditions.",
                        "May 13, 2025",
                        MitigationRequest.Priority.HIGH,
                        MitigationRequest.RequestStatus.PENDING,
                        Arrays.asList(
                                new SupportingDocument("Psychiatrist_Letter.pdf", "https://example.com/psychiatrist_letter.pdf", "PDF", "1.5 MB"),
                                new SupportingDocument("Medical_History.pdf", "https://example.com/medical_history.pdf", "PDF", "3.2 MB")
                        )
                ),
                new MitigationRequest(
                        "REQ004",
                        "ST12348",
                        "Emily Davis",
                        "Psychology",
                        "Batch 44",
                        "Research Methods",
                        "Research Project",
                        "Extension Request",
                        "Due to a family emergency (death in the family), I was unable to complete my research project on time. I had to travel abroad for the funeral and handle family matters. I request an extension of 2 weeks.",
                        "May 12, 2025",
                        MitigationRequest.Priority.URGENT,
                        MitigationRequest.RequestStatus.PENDING,
                        Arrays.asList(
                                new SupportingDocument("Death_Certificate.pdf", "https://example.com/death_certificate.pdf", "PDF", "0.8 MB"),
                                new SupportingDocument("Flight_Tickets.pdf", "https://example.com/flight_tickets.pdf", "PDF", "0.6 MB")
                        )
                ),
                new MitigationRequest(
                        "REQ005",
                        "ST12349",
                        "David Wilson",
                        "Mathematics",
                        "Batch 41",
                        "Calculus II",
                        "Quiz 3",
                        "Absence Excuse",
                        "I missed the quiz due to a car accident on my way to university. I was taken to the hospital for treatment and have medical records to prove this. I request permission to take a makeup quiz.",
                        "May 11, 2025",
                        MitigationRequest.Priority.MEDIUM,
                        MitigationRequest.RequestStatus.PENDING,
                        Arrays.asList(
                                new SupportingDocument("Emergency_Room_Report.pdf", "https://example.com/er_report.pdf", "PDF", "2.1 MB"),
                                new SupportingDocument("Police_Report.pdf", "https://example.com/police_report.pdf", "PDF", "1.7 MB"),
                                new SupportingDocument("Car_Insurance_Claim.pdf", "https://example.com/insurance_claim.pdf", "PDF", "1.3 MB")
                        )
                ),
                new MitigationRequest(
                        "REQ006",
                        "ST12350",
                        "Sarah Brown",
                        "Literature",
                        "Batch 46",
                        "Modern Poetry",
                        "Essay Assignment",
                        "Late Submission Request",
                        "I contracted COVID-19 and was in isolation for 2 weeks. During this period, I was too unwell to complete my assignment. I have a positive test result and medical clearance letter.",
                        "May 10, 2025",
                        MitigationRequest.Priority.HIGH,
                        MitigationRequest.RequestStatus.PENDING,
                        Arrays.asList(
                                new SupportingDocument("COVID_Test_Result.pdf", "https://example.com/covid_test.pdf", "PDF", "0.5 MB"),
                                new SupportingDocument("Medical_Clearance.pdf", "https://example.com/clearance.pdf", "PDF", "0.7 MB")
                        )
                ),
                new MitigationRequest(
                        "REQ007",
                        "ST12351",
                        "Robert Taylor",
                        "Physics",
                        "Batch 47",
                        "Quantum Mechanics",
                        "Lab Report",
                        "Extension Request",
                        "My lab partner dropped out of the course unexpectedly, leaving me to complete the entire lab report alone. This has significantly increased the workload beyond what was originally planned.",
                        "May 09, 2025",
                        MitigationRequest.Priority.LOW,
                        MitigationRequest.RequestStatus.PENDING,
                        Arrays.asList(
                                new SupportingDocument("Course_Withdrawal_Notice.pdf", "https://example.com/withdrawal.pdf", "PDF", "0.4 MB")
                        )
                ),
                new MitigationRequest(
                        "REQ008",
                        "ST12352",
                        "Lisa Anderson",
                        "Chemistry",
                        "Batch 48",
                        "Organic Chemistry",
                        "Practical Exam",
                        "Re-examination Request",
                        "The fire alarm went off during my practical exam, causing interruption and evacuation. When we returned, I was very distracted and couldn't focus properly, which affected my performance.",
                        "May 08, 2025",
                        MitigationRequest.Priority.URGENT,
                        MitigationRequest.RequestStatus.PENDING,
                        Arrays.asList(
                                new SupportingDocument("Fire_Department_Report.pdf", "https://example.com/fire_report.pdf", "PDF", "1.1 MB"),
                                new SupportingDocument("Witness_Statement.pdf", "https://example.com/witness.pdf", "PDF", "0.6 MB")
                        )
                )
        );
    }

    public static int getPendingRequestsCount(List<MitigationRequest> requests) {
        int count = 0;
        for (MitigationRequest request : requests) {
            if (request.getStatus() == MitigationRequest.RequestStatus.PENDING) {
                count++;
            }
        }
        return count;
    }

    public static int getUrgentRequestsCount(List<MitigationRequest> requests) {
        int count = 0;
        for (MitigationRequest request : requests) {
            if (request.getStatus() == MitigationRequest.RequestStatus.PENDING &&
                    request.getPriority() == MitigationRequest.Priority.URGENT) {
                count++;
            }
        }
        return count;
    }

    public static String getFormattedDate() {
        return java.text.DateFormat.getDateInstance().format(new java.util.Date());
    }

    public static String getEmailTemplate(boolean isAccepted, String studentName,
                                          String module, String assessment, String responseMessage) {
        String subject = "Mitigation Request " + (isAccepted ? "Approved" : "Rejected") +
                " - " + module + " " + assessment;

        StringBuilder emailBody = new StringBuilder();
        emailBody.append("Dear ").append(studentName).append(",\n\n");

        if (isAccepted) {
            emailBody.append("We are pleased to inform you that your mitigation request for ")
                    .append(module).append(" - ").append(assessment)
                    .append(" has been APPROVED.\n\n");
        } else {
            emailBody.append("We regret to inform you that your mitigation request for ")
                    .append(module).append(" - ").append(assessment)
                    .append(" has been REJECTED.\n\n");
        }

        emailBody.append("Staff Response:\n")
                .append(responseMessage).append("\n\n");

        emailBody.append("If you have any questions or concerns, please don't hesitate to contact us.\n\n");
        emailBody.append("Best regards,\n");
        emailBody.append("Academic Affairs Department\n");
        emailBody.append("ICBT Campus");

        return emailBody.toString();
    }

    public static boolean validateDecisionInput(int selectedDecision, String responseMessage,
                                                String extensionDate, boolean isAccepted) {
        // Check if a decision is selected
        if (selectedDecision == -1) {
            return false;
        }

        // Check if response message is provided
        if (responseMessage == null || responseMessage.trim().isEmpty()) {
            return false;
        }

        // If accepted and it's an extension request, check if extension date is provided
        if (isAccepted && extensionDate != null && extensionDate.trim().isEmpty()) {
            return false;
        }

        return true;
    }

    public static void processDecision(MitigationRequest request, boolean isAccepted,
                                       String responseMessage, String extensionDate) {
        // Update request status
        if (isAccepted) {
            request.setStatus(MitigationRequest.RequestStatus.ACCEPTED);
        } else {
            request.setStatus(MitigationRequest.RequestStatus.REJECTED);
        }

        // In a real implementation, you would:
        // 1. Update the database
        // 2. Send email to student
        // 3. Log the decision
        // 4. Update any related records

        // For now, we'll just simulate these actions
        System.out.println("Decision processed for request: " + request.getId());
        System.out.println("Status: " + request.getStatus().getDisplayName());
        System.out.println("Response: " + responseMessage);
        if (isAccepted && extensionDate != null && !extensionDate.trim().isEmpty()) {
            System.out.println("Extension date: " + extensionDate);
        }
    }

    // Additional helper methods for filtering and sorting
    public static List<MitigationRequest> filterByPriority(List<MitigationRequest> requests,
                                                           MitigationRequest.Priority priority) {
        List<MitigationRequest> filtered = new java.util.ArrayList<>();
        for (MitigationRequest request : requests) {
            if (request.getPriority() == priority) {
                filtered.add(request);
            }
        }
        return filtered;
    }

    public static List<MitigationRequest> filterByStatus(List<MitigationRequest> requests,
                                                         MitigationRequest.RequestStatus status) {
        List<MitigationRequest> filtered = new java.util.ArrayList<>();
        for (MitigationRequest request : requests) {
            if (request.getStatus() == status) {
                filtered.add(request);
            }
        }
        return filtered;
    }

    public static List<MitigationRequest> searchByStudentName(List<MitigationRequest> requests,
                                                              String searchQuery) {
        List<MitigationRequest> filtered = new java.util.ArrayList<>();
        String query = searchQuery.toLowerCase().trim();

        for (MitigationRequest request : requests) {
            if (request.getStudentName().toLowerCase().contains(query) ||
                    request.getStudentId().toLowerCase().contains(query)) {
                filtered.add(request);
            }
        }
        return filtered;
    }

    // Sort requests by date (newest first)
    public static List<MitigationRequest> sortByDate(List<MitigationRequest> requests) {
        List<MitigationRequest> sorted = new java.util.ArrayList<>(requests);
        // In a real implementation, you would parse the date strings and sort properly
        // For now, we'll just reverse the list since they're already in date order
        java.util.Collections.reverse(sorted);
        return sorted;
    }

    // Sort requests by priority (urgent first)
    public static List<MitigationRequest> sortByPriority(List<MitigationRequest> requests) {
        List<MitigationRequest> sorted = new java.util.ArrayList<>(requests);
        sorted.sort((r1, r2) -> {
            int priority1 = getPriorityWeight(r1.getPriority());
            int priority2 = getPriorityWeight(r2.getPriority());
            return Integer.compare(priority2, priority1); // Descending order
        });
        return sorted;
    }

    private static int getPriorityWeight(MitigationRequest.Priority priority) {
        switch (priority) {
            case URGENT:
                return 4;
            case HIGH:
                return 3;
            case MEDIUM:
                return 2;
            case LOW:
            default:
                return 1;
        }
    }
}