package com.bookfast.backend.admin.controller;

import com.bookfast.backend.admin.service.SystemReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/reports")
public class SystemReportController {
    @Autowired
    private SystemReportService systemReportService;

    @GetMapping("")
    public Map<String, Object> getSystemReports() {
        return systemReportService.getSystemReports();
    }
}
