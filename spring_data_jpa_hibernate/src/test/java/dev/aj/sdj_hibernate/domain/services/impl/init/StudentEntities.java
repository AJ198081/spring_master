package dev.aj.sdj_hibernate.domain.services.impl.init;

import com.github.javafaker.Faker;
import dev.aj.sdj_hibernate.CSVWriter;
import dev.aj.sdj_hibernate.domain.entities.StudentEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.test.context.TestComponent;

import java.util.stream.Stream;

@TestComponent
@RequiredArgsConstructor
public class StudentEntities {

    private final Faker faker;

    private final CSVWriter<StudentEntity> csvWriter = new CSVWriter<>();

    public Stream<StudentEntity> getDummyStudents() {
        return Stream.generate(() -> StudentEntity.builder()
                .name(faker.name().fullName())
                .enrollmentId(faker.bothify("z??#####", true))
                .build());
    }


    public void writeTestDataToCsv(Stream<StudentEntity> dummyStudents, String dataFilePath) {
        csvWriter.writeCsvData(dummyStudents.limit(10).toList(), dataFilePath);
    }

}
