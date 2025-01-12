package dev.aj.sdj_hibernate.domain.services;

import dev.aj.sdj_hibernate.domain.entities.Item;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public interface ItemService {
    @Transactional(propagation = Propagation.REQUIRED)
    void persistItem(Item item);
}
