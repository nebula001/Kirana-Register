package com.example.Kirana_Register.controllers;

import com.example.Kirana_Register.dto.ReportResponseDTO;
import com.example.Kirana_Register.services.ReportServiceUser;
import com.example.Kirana_Register.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;

@RestController
@RequestMapping("/api/reports")
public class ReportControllerUser {

    @Autowired
    private ReportServiceUser reportService;


    @GetMapping("/weekly")
    public ReportResponseDTO generateWeeklyReport() {
        Long userId = getCurrentUserId();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = now.minusWeeks(1);

        return reportService.generateWeeklyReportForUser(userId, start, now);
    }


    @GetMapping("/monthly")
    public ReportResponseDTO generateMonthlyReport() {
        Long userId = getCurrentUserId();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = now.with(TemporalAdjusters.firstDayOfMonth());

        return reportService.generateMonthlyReportForUser(userId, start, now);
    }


    @GetMapping("/yearly")
    public ReportResponseDTO generateYearlyReport() {
        Long userId = getCurrentUserId();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = now.with(TemporalAdjusters.firstDayOfYear());

        return reportService.generateYearlyReportForUser(userId, start, now);
    }

    private Long getCurrentUserId() {
        CustomUserDetails userDetails = (CustomUserDetails)
                SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userDetails.getUser().getId();
    }
}