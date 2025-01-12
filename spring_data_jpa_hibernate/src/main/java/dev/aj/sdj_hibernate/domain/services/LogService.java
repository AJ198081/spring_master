package dev.aj.sdj_hibernate.domain.services;

import jakarta.transaction.Transactional;

public interface LogService {
    // Transaction is 'required', if already then this method runs in that transaction
    // otherwise 'creates' a new transaction
    @Transactional(value = Transactional.TxType.REQUIRED)
    void logMessage(String message);
}
