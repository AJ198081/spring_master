package dev.aj.sdj_hibernate.domain.services;

import dev.aj.sdj_hibernate.domain.entities.GuideEntity;

import java.util.List;

public interface GuideEntityService {
    GuideEntity getGuideEntityById(Long id);
    Iterable<GuideEntity> getAllGuideEntities();
    GuideEntity persistGuideEntity(GuideEntity guideEntity);
    void deleteGuideEntity(GuideEntity guideEntity);
    void deleteGuideEntityById(Long id);
    void updateGuideEntity(GuideEntity guideEntity);
    void deleteAllGuideEntities();

    List<GuideEntity> persistAllGuideEntities(List<GuideEntity> guides);

    GuideEntity findGuideByIdWithoutStudents(Long id);

    GuideEntity findGuideByIdWithStudentsEagerlyFetched(Long id);

    GuideEntity findGuideByIdWithStudentsAndHotelEagerlyFetched(Long firstGuideEntityId);
}
