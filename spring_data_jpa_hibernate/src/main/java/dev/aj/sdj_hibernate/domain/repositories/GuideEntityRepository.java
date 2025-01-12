package dev.aj.sdj_hibernate.domain.repositories;

import dev.aj.sdj_hibernate.domain.entities.GuideEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface GuideEntityRepository extends JpaRepository<GuideEntity, Long> {

    Optional<GuideEntity> findById(Long id);


//    @EntityGraph(value = "GuideEntity.withStudents", type = EntityGraph.EntityGraphType.LOAD)
    @EntityGraph(attributePaths = {"studentEntities"}, type = EntityGraph.EntityGraphType.LOAD)
    @Query("select ge from GuideEntity ge where ge.id = :id")
    Optional<GuideEntity> findByIdEager(Long id);

    List<GuideEntity> id(Long id);

    @EntityGraph(attributePaths = {"studentEntities.hotel"}, type = EntityGraph.EntityGraphType.LOAD)
    @Query("select ge from GuideEntity ge where ge.id = :id")
    GuideEntity findByIdWithStudentsAndHotelEagerlyFetched(Long id);

}
