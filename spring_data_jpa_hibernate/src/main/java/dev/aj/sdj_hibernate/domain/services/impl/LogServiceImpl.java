package dev.aj.sdj_hibernate.domain.services.impl;

import dev.aj.sdj_hibernate.domain.entities.Log;
import dev.aj.sdj_hibernate.domain.repositories.LogRepository;
import dev.aj.sdj_hibernate.domain.services.LogService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogServiceImpl implements LogService {

    private final LogRepository logRepository;

    // Transaction is 'required', if already then this method runs in that transaction
    // otherwise 'creates' a new transaction
    @Transactional(value = Transactional.TxType.REQUIRED)
    @Override public void logMessage(String message) {
        logRepository.save(Log.builder().message(message).build());
    }
}
