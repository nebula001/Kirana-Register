package com.example.Kirana_Register.controllers;

import com.example.Kirana_Register.services.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("api/admin/reports")
public class ReportController {
    @Autowired
    private ReportService reportService;

    @GetMapping("/weekly")
    public Map<String, Object> getWeeklyReport() {
        return reportService.generateWeeklyReport();
    }

    @GetMapping("/monthly")
    public Map<String, Object> getMonthlyReport() {
        return reportService.generateMonthlyReport();
    }

    @GetMapping("/yearly")
    public Map<String, Object> getYearlyReport() {
        return reportService.generateYearlyReport();
    }
}

