package dev.aj.elasticsearch;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import dev.aj.elasticsearch.domain.Employee;
import dev.aj.elasticsearch.domain.Product;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.boot.test.context.TestComponent;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Stream;

@TestComponent
@RequiredArgsConstructor
public class TestData {

    public static final String TEST_DATA_DIRECTORY = "src/test/resources/test-data";
    private final Faker faker;
    private final ObjectMapper objectMapper;


    public Stream<Employee> getStreamOfEmployees() {
        return Stream.generate(() -> Employee.builder()
                .id(faker.number().randomNumber(10, true))
                .name(faker.name().fullName())
                .email(faker.internet().emailAddress())
                .department(faker.commerce().department())
                .build());
    }

    public Stream<Product> getStreamOfProducts() {

        List<String> brands = List.of("Nike", "Adidas", "Puma", "Reebok", "Bata", "Bata", "Bata");

        return Stream.generate(() -> Product.builder()
                .id(UUID.randomUUID().toString())
                .name(faker.commerce().productName())
                .brand(brands.get(faker.random().nextInt(0, brands.size() - 1)))
                .category(faker.commerce().department())
                .price(new BigDecimal(faker.commerce().price(95.47, 999.99)))
                .quantity(faker.number().numberBetween(1, 100))
                .build());
    }

    public <T> T readResource(String fileName, TypeReference<T> typeReference) {

        Objects.requireNonNull(fileName, "Path cannot be null");

        try {

            Path testDataDirectory = Paths.get(TEST_DATA_DIRECTORY);
            Path filePath = testDataDirectory.resolve(fileName);

            if (!Files.exists(filePath)) {
                throw new RuntimeException("File not found: " + filePath);
            }

            File file = new File(filePath.toUri());

            return objectMapper.readValue(file, typeReference);

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @SneakyThrows
    public <T> void writeResource(String fileName, T objectToWrite) {
        Objects.requireNonNull(fileName, "Path cannot be null");

        Path testDataDirectory = Paths.get(TEST_DATA_DIRECTORY);
        if (!Files.exists(testDataDirectory)) {
            Files.createDirectory(testDataDirectory);
        }

        Path targetFilePath = testDataDirectory.resolve(fileName);
        if (!Files.exists(targetFilePath)) {
            Files.createFile(targetFilePath);
        }

        try (FileWriter writer = new FileWriter(targetFilePath.toFile())) {
            String serializedData = objectMapper.writeValueAsString(objectToWrite);
            writer.write(serializedData);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e.getMessage());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public void deleteTestDataFile(String fileName) {
        Objects.requireNonNull(fileName, "Path cannot be null");
        Path testDataDirectory = Paths.get(TEST_DATA_DIRECTORY);
        Path filePath = testDataDirectory.resolve(fileName);
        if (Files.exists(filePath)) {
            try {
                Files.delete(filePath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
