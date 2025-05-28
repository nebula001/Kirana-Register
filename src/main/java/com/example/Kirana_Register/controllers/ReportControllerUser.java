package com.example.Kirana_Register.controllers;

import com.example.Kirana_Register.services.ReportService;
import com.example.Kirana_Register.services.ReportServiceUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/reports")
public class ReportControllerUser {
    @Autowired
    private ReportServiceUser reportServiceUser;

    @GetMapping("/weekly")
    public Map<String, Object> getWeeklyReport() {
        return reportServiceUser.generateWeeklyReport();
    }

    @GetMapping("/monthly")
    public Map<String, Object> getMonthlyReport() {
        return reportServiceUser.generateMonthlyReport();
    }

    @GetMapping("/yearly")
    public Map<String, Object> getYearlyReport() {
        return reportServiceUser.generateYearlyReport();
    }

}
