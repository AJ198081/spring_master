package dev.aj.sdj_hibernate.domain.repositories;

import dev.aj.sdj_hibernate.domain.entities.HotelEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HotelEntityRepository extends JpaRepository<HotelEntity, Long> {

}
