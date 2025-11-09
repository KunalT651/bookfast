package com.bookfast.backend.admin.controller;

import com.bookfast.backend.admin.service.AdminReportService;
import com.bookfast.backend.common.dto.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/reports")
public class AdminReportController {
    private final AdminReportService adminReportService;

    public AdminReportController(AdminReportService adminReportService) {
        this.adminReportService = adminReportService;
    }

    @GetMapping
    public ResponseEntity<?> getSystemReports(@RequestParam(defaultValue = "30") String period) {
        try {
            Map<String, Object> reports = adminReportService.getSystemReports(period);
            return ResponseEntity.ok(reports);
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Failed to fetch system reports: " + ex.getMessage()));
        }
    }

    @GetMapping("/dashboard")
    public ResponseEntity<?> getDashboardStats() {
        try {
            Map<String, Object> stats = adminReportService.getDashboardStats();
            return ResponseEntity.ok(stats);
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Failed to fetch dashboard stats: " + ex.getMessage()));
        }
    }

    @GetMapping("/users")
    public ResponseEntity<?> getUserReports(@RequestParam(defaultValue = "30") String period) {
        try {
            Map<String, Object> reports = adminReportService.getUserReports(period);
            return ResponseEntity.ok(reports);
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Failed to fetch user reports: " + ex.getMessage()));
        }
    }

    @GetMapping("/bookings")
    public ResponseEntity<?> getBookingReports(@RequestParam(defaultValue = "30") String period) {
        try {
            Map<String, Object> reports = adminReportService.getBookingReports(period);
            return ResponseEntity.ok(reports);
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Failed to fetch booking reports: " + ex.getMessage()));
        }
    }

    @GetMapping("/revenue")
    public ResponseEntity<?> getRevenueReports(@RequestParam(defaultValue = "30") String period) {
        try {
            Map<String, Object> reports = adminReportService.getRevenueReports(period);
            return ResponseEntity.ok(reports);
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Failed to fetch revenue reports: " + ex.getMessage()));
        }
    }

    @GetMapping("/providers")
    public ResponseEntity<?> getProviderReports(@RequestParam(defaultValue = "30") String period) {
        try {
            Map<String, Object> reports = adminReportService.getProviderReports(period);
            return ResponseEntity.ok(reports);
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Failed to fetch provider reports: " + ex.getMessage()));
        }
    }

    @GetMapping("/export/{reportType}")
    public ResponseEntity<byte[]> exportReport(@PathVariable String reportType, @RequestParam(defaultValue = "30") String period) {
        try {
            byte[] csvData = adminReportService.exportReportToCSV(reportType, period);
            
            org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
            headers.setContentType(org.springframework.http.MediaType.parseMediaType("text/csv"));
            headers.setContentDispositionFormData("attachment", reportType + "_report_" + java.time.LocalDate.now() + ".csv");
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
            
            return new ResponseEntity<>(csvData, headers, org.springframework.http.HttpStatus.OK);
        } catch (Exception ex) {
            return ResponseEntity.badRequest().build();
        }
    }
}
