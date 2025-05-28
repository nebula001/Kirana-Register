package com.example.Kirana_Register.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class ExchangeApiDTO {
    private String base;
    private Map<String, Double> rates;

    public Double getInrRate() {
        return rates != null ? rates.get("INR") : null;
    }
}
