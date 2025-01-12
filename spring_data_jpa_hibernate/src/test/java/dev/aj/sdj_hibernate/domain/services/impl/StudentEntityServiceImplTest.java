package dev.aj.sdj_hibernate.domain.services.impl;

import dev.aj.sdj_hibernate.PostgresConfiguration;
import dev.aj.sdj_hibernate.domain.entities.StudentEntity;
import dev.aj.sdj_hibernate.domain.entities.auditing.AuditingConfig;
import dev.aj.sdj_hibernate.domain.repositories.GuideEntityRepository;
import dev.aj.sdj_hibernate.domain.repositories.StudentEntityRepository;
import dev.aj.sdj_hibernate.domain.services.impl.init.StudentEntities;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import java.util.List;


@DataJpaTest
@Import(value = {StudentEntityServiceImpl.class, PostgresConfiguration.class, AuditingConfig.class, StudentEntities.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestPropertySource(locations = {"/application-test.properties", "/junit-platform.properties"}, properties = {
        "logging.level.root=off",
        "spring.jpa.properties.hibernate.format_sql=true",
        "spring.jpa.properties.hibernate.show_sql=true",
        "logging.level.org.hibernate.orm.jdbc.bind=trace",
})
class StudentEntityServiceImplTest {

    @Autowired
    private StudentEntityServiceImpl studentEntityServiceImpl;

    @Autowired
    private StudentEntityRepository studentEntityRepository;

    @Autowired
    private GuideEntityRepository guideEntityRepository;

    @Autowired
    private StudentEntities studentEntities;

    @BeforeAll
    void beforeAll() {





    }

    @Test
    void persistStudent() {
        List<StudentEntity> dummyStudents = studentEntities.getDummyStudents().limit(10).toList();

        List<StudentEntity> persistedStudents = studentEntityServiceImpl.persistStudents(dummyStudents);

        studentEntities.writeTestDataToCsv(studentEntities.getDummyStudents().limit(20), "student_data.csv");

        org.assertj.core.api.Assertions.assertThat(studentEntityRepository.findAll()).hasSize(10);
        org.assertj.core.api.Assertions.assertThat(guideEntityRepository.count()).isEqualTo(0L);
        org.assertj.core.api.Assertions.assertThat(studentEntityServiceImpl.findStudentById(persistedStudents.getFirst().getId()))
                .extracting(StudentEntity::getGuide)
        .isNull();
    }
}