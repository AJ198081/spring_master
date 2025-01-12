package dev.aj.sdj_hibernate.domain.services.impl;

import dev.aj.sdj_hibernate.domain.entities.GuideEntity;
import dev.aj.sdj_hibernate.domain.repositories.GuideEntityRepository;
import dev.aj.sdj_hibernate.domain.services.GuideEntityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GuideEntityServiceImpl implements GuideEntityService {

    private final GuideEntityRepository guideEntityRepository;

    @Override
    public GuideEntity getGuideEntityById(Long id) {
        return guideEntityRepository.findById(id).orElse(null);
    }

    @Override
    public Iterable<GuideEntity> getAllGuideEntities() {
        return guideEntityRepository.findAll();
    }

    @Override
    public GuideEntity persistGuideEntity(GuideEntity guideEntity) {
        return guideEntityRepository.saveAndFlush(guideEntity);
    }

    @Override
    public void deleteGuideEntity(GuideEntity guideEntity) {
        guideEntityRepository.delete(guideEntity);
    }

    @Override
    public void deleteGuideEntityById(Long id) {
        guideEntityRepository.deleteById(id);
    }

    @Override
    public void updateGuideEntity(GuideEntity guideEntity) {
        guideEntityRepository.save(guideEntity);
    }

    @Override
    public void deleteAllGuideEntities() {
        guideEntityRepository.deleteAll();
    }

    @Override
    public List<GuideEntity> persistAllGuideEntities(List<GuideEntity> guides) {
        return guideEntityRepository.saveAll(guides);
    }

    @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
    @Override
    public GuideEntity findGuideByIdWithoutStudents(Long id) {
        return guideEntityRepository.findById(id)
                .orElse(null);
    }

    @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
    @Override
    public GuideEntity findGuideByIdWithStudentsEagerlyFetched(Long id) {
        return guideEntityRepository.findByIdEager(id)
                .orElseThrow();
    }

    @Override
    public GuideEntity findGuideByIdWithStudentsAndHotelEagerlyFetched(Long id) {
        return guideEntityRepository.findByIdWithStudentsAndHotelEagerlyFetched(id);
    }
}
