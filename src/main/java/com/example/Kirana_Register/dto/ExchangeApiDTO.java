package com.example.Kirana_Register.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExchangeApiDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String base;
    private Map<String, Double> rates = new HashMap<>();

    public Double getInrRate() {
        return rates != null ? rates.get("INR") : null;
    }
}
