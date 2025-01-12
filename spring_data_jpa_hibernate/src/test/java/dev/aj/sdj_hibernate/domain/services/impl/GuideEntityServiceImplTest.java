package dev.aj.sdj_hibernate.domain.services.impl;

import dev.aj.sdj_hibernate.PostgresConfiguration;
import dev.aj.sdj_hibernate.domain.entities.GuideEntity;
import dev.aj.sdj_hibernate.domain.entities.HotelEntity;
import dev.aj.sdj_hibernate.domain.entities.StudentEntity;
import dev.aj.sdj_hibernate.domain.entities.auditing.AuditingConfig;
import dev.aj.sdj_hibernate.domain.repositories.GuideEntityRepository;
import dev.aj.sdj_hibernate.domain.repositories.HotelEntityRepository;
import dev.aj.sdj_hibernate.domain.repositories.StudentEntityRepository;
import dev.aj.sdj_hibernate.domain.services.impl.init.GuideEntities;
import dev.aj.sdj_hibernate.domain.services.impl.init.HotelEntities;
import dev.aj.sdj_hibernate.domain.services.impl.init.StudentEntities;
import org.hibernate.LazyInitializationException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

//TODO (left on 10/01/2025): Junit Tests run in a transaction, default behaviour needed for 'dirty context' and cleanups.
// if need to replicate a non-transactional scenario make Propagation NOT_SUPPORTED,
// not preferred though you might compromise the transactional integrity of rest of your buisiness logic


@DataJpaTest
@Import(value = {GuideEntityServiceImpl.class, AuditingConfig.class, PostgresConfiguration.class, GuideEntities.class, StudentEntities.class, HotelEntities.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestPropertySource(locations = {"/application-test.properties"}, properties = {
        "logging.level.root=off",
        "spring.jpa.hibernate.ddl-auto=none",
        "spring.jpa.properties.hibernate.format_sql=true",
        "spring.jpa.properties.hibernate.show_sql=true",
        "logging.level.org.hibernate.orm.jdbc.bind=trace",
        "spring.mvc.problemdetails.enabled=true"
})
class GuideEntityServiceImplTest {

    @Autowired
    private GuideEntityServiceImpl guideEntityServiceImpl;

    @Autowired
    private StudentEntityRepository studentEntityRepository;

    @Autowired
    private GuideEntityRepository guideEntityRepository;

    @Autowired
    private HotelEntityRepository hotelEntityRepository;

    @Autowired
    private HotelEntities hotelEntities;

    @BeforeAll
    void beforeAll() {
        HotelEntity hotelEntity = hotelEntities.getDummyHotels().limit(1).findFirst().orElseThrow();
        HotelEntity persistedHotelEntity = hotelEntityRepository.save(hotelEntity);

        guideEntityServiceImpl.findGuideByIdWithStudentsEagerlyFetched(176L)
                .getStudentEntities().stream()
                .peek(studentEntity -> studentEntity.addHotel(persistedHotelEntity))
                .forEach(studentEntityRepository::saveAndFlush);
    }

    @AfterAll
    void afterAll() {
        studentEntityRepository.deleteAll();
        guideEntityRepository.deleteAll();
    }

    @Test
    void persistGuideEntity() {
        assertThat(guideEntityRepository.count())
                .isGreaterThanOrEqualTo(2L);

        assertThat(studentEntityRepository.count())
                .isGreaterThanOrEqualTo(20L);
    }

    @Test
    void testFindGuideEntityByIdWithLazyFetchStudents() {

        GuideEntity guideWithoutStudents = guideEntityServiceImpl.findGuideByIdWithoutStudents(176L);

        //TODO: I think the persistence context still has the associated Students loaded hence the query just returns with students

        LazyInitializationException lazyInitException = Assertions.assertThrows(
                LazyInitializationException.class,
                () -> guideWithoutStudents.getStudentEntities()
                        .stream()
                        .findFirst()
                        .map(StudentEntity::getName)
                        .orElseThrow());

        assertThat(lazyInitException.getMessage()).containsIgnoringCase("failed to lazily initialize a collection of role: dev.aj.sdj_hibernate.domain.entities.GuideEntity.studentEntities: could not initialize proxy - no Session");
    }


    @Test
    void testFindGuideEntityByIdWithEagerlyFetchStudents() {
        Long firstGuideEntityId = guideEntityRepository.findAll().getFirst().getId();
        GuideEntity guideWithStudents = guideEntityServiceImpl.findGuideByIdWithStudentsEagerlyFetched(firstGuideEntityId);

        Assertions.assertDoesNotThrow(() -> guideWithStudents.getStudentEntities()
                .stream()
                .findFirst()
                .orElseThrow());
    }

    @Test
    void testFetchingEntityGraphAndSubgraph() {
        Long firstGuideEntityId = guideEntityRepository.findAll().getFirst().getId();
        GuideEntity guideWithStudentsAndHotel = guideEntityServiceImpl.findGuideByIdWithStudentsAndHotelEagerlyFetched(firstGuideEntityId);

        Assertions.assertDoesNotThrow(() -> guideWithStudentsAndHotel.getStudentEntities().stream().findFirst().orElseThrow());
        Assertions.assertDoesNotThrow(() -> guideWithStudentsAndHotel.getStudentEntities().stream().findFirst()
                .map(studentEntity -> studentEntity.getHotel().getName())
                .orElseThrow());

        org.assertj.core.api.Assertions.assertThat(guideWithStudentsAndHotel.getStudentEntities())
                .isNotNull()
                .extracting(StudentEntity::getHotel)
                .contains(hotelEntityRepository.findAll().getFirst());
    }
}