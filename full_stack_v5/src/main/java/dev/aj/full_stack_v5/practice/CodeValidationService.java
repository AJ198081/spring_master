package dev.aj.full_stack_v5.practice;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.Random;

@Component
@RequiredArgsConstructor
@Slf4j
public class CodeValidationService {

    private final PromotionRepository promotionRepository;

    public boolean testCode(RestClient restClient) {

        var code = generateRandomCode();

        while (promotionRepository.existsByCode(code)) {
            code = generateRandomCode();
        }

        var response = fetchPromotionDetails(restClient, code);

        if (response.getStatusCode().is2xxSuccessful()) {
            promotionRepository.save(PromotionEntity.builder()
                    .promotionCode(code)
                    .active(true)
                    .build());
            return true;
        } else {
            promotionRepository.save(PromotionEntity.builder()
                    .promotionCode(code)
                    .active(false)
                    .build());
            return false;
        }
    }

    public void validateAndUpdatePromotions(RestClient restClient) {
        promotionRepository.findAll().forEach(promotion -> {
            if (promotion.getActive()) {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                if (!fetchPromotionDetails(restClient, promotion.getPromotionCode()).getStatusCode().is2xxSuccessful()) {
                    promotion.setActive(false);
                    promotionRepository.save(promotion);
                }
            }
        });
    }

    private ResponseEntity<Void> fetchPromotionDetails(RestClient builder, String code) {
        RestClient.ResponseSpec responseSpec = builder.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("code", code)
                        .queryParam("fulfilmentMethod", "PICKUP")
                        .queryParam("merchantId", "3927")
                        .build())
                .retrieve();
        try {
            return responseSpec
                    .toBodilessEntity();
        } catch (Exception e) {
            if (e instanceof HttpClientErrorException.BadRequest exception) {
                return ResponseEntity.badRequest().build();
            } else {
                log.error("Error fetching promotion details: {}", e.getMessage());
                return ResponseEntity.internalServerError().build();
            }
        }
    }

    private static final String ALPHABETS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String NUMBERS = "0123456789";
    private static final String ALLOWED_CHARS = ALPHABETS + NUMBERS;

    private String generateRandomCode() {

        Random random = new Random();
        StringBuilder code = new StringBuilder();

        code.append("F");

        for (int i = 0; i < 5; i++) {
            if (i == 0 || i == 4) {
                code.append(ALPHABETS.charAt(random.nextInt(ALPHABETS.length())));
            } else if (i == 2) {
                code.append(NUMBERS.charAt(random.nextInt(NUMBERS.length())));
            } else {
                code.append(ALLOWED_CHARS.charAt(random.nextInt(ALLOWED_CHARS.length())));
            }
        }

        return code.toString();
    }
}

//https://www.skipapp.com.au/api/promotions?code=FKL9EG&fulfilmentMethod=PICKUP&merchantId=3927