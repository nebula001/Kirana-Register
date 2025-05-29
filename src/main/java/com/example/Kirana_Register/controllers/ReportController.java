package com.example.Kirana_Register.controllers;

import com.example.Kirana_Register.dto.ReportResponseDTO;
import com.example.Kirana_Register.services.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/admin/reports")
public class ReportController {
    @Autowired
    private ReportService reportService;

    @GetMapping("/weekly")
    public ResponseEntity<ReportResponseDTO> getWeeklyReport() {
        ReportResponseDTO report = reportService.generateWeeklyReport();
        return ResponseEntity.ok(report);
    }

    @GetMapping("/monthly")
    public ResponseEntity<ReportResponseDTO> getMonthlyReport() {
        ReportResponseDTO report = reportService.generateMonthlyReport();
        return ResponseEntity.ok(report);
    }

    @GetMapping("/yearly")
    public ResponseEntity<ReportResponseDTO> getYearlyReport() {
        ReportResponseDTO report = reportService.generateYearlyReport();
        return ResponseEntity.ok(report);
    }
}


