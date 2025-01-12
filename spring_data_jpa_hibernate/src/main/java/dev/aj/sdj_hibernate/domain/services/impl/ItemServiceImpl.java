package dev.aj.sdj_hibernate.domain.services.impl;

import dev.aj.sdj_hibernate.domain.entities.Item;
import dev.aj.sdj_hibernate.domain.repositories.ItemRepository;
import dev.aj.sdj_hibernate.domain.services.ItemService;
import dev.aj.sdj_hibernate.domain.services.LogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final LogService logService;

    @Transactional(propagation = Propagation.REQUIRED)
    @Override public void persistItem(Item item) {
        itemRepository.save(item);
        logService.logMessage(String.format("Adding item %s", item.getName()));
    }


}
