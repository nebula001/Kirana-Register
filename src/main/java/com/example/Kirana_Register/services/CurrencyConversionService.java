package com.example.Kirana_Register.services;

import com.example.Kirana_Register.Exceptions.ExternalServiceException;
import com.example.Kirana_Register.Exceptions.ResourceNotFoundException;
import com.example.Kirana_Register.dto.ExchangeApiDTO;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class CurrencyConversionService {
    private static final String FX_RATES_API_URL = "https://api.fxratesapi.com/latest";

    private final RestTemplate restTemplate;

    public CurrencyConversionService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Cacheable(value = "externalApiCache")
    public ExchangeApiDTO getExchangeRates() {
        try {

            ExchangeApiDTO response = restTemplate.getForObject(FX_RATES_API_URL, ExchangeApiDTO.class);

            if (response == null || response.getInrRate() == null) {
                throw new ResourceNotFoundException("Failed to fetch INR exchange rate");
            }
            return response;
        } catch (Exception e) {
            throw new ExternalServiceException("Currency conversion service unavailable", e);
        }
    }
}
