package dev.aj.full_stack_v5;

import dev.aj.full_stack_v5.product.domain.dtos.CategoryDto;
import dev.aj.full_stack_v5.product.domain.dtos.ImageRequestDto;
import dev.aj.full_stack_v5.product.domain.dtos.ProductDto;
import lombok.RequiredArgsConstructor;
import net.datafaker.Faker;
import org.springframework.boot.test.context.TestComponent;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Stream;

@TestComponent
@RequiredArgsConstructor
public class TestDataFactory {

    private final Faker faker;
    private final PhotosFactory photosFactory;
    private final TestDataFactory testDataFactory;

    public Stream<ProductDto> generateStreamOfProducts() {

        List<String> availableBrands = List.of("Nike", "Adidas", "Puma", "Reebok", "Bata", "Bata", "Bata");

        return Stream.generate(() -> ProductDto.builder()
                .name(faker.commerce().productName())
                .description(faker.lorem().paragraph())
                .price(new BigDecimal(faker.commerce().price(50, 500)))
                .inventory(faker.random().nextInt(10, 100))
                .brand(availableBrands.get(faker.number().numberBetween(0, availableBrands.size())))
                .categoryName(faker.commerce().department())
                .build()
        );
    }

    public Stream<CategoryDto> generateStreamOfCategories() {
        return Stream.generate(() -> CategoryDto.builder()
                .name(faker.commerce().department())
                .build()
        );
    }

    public Stream<ImageRequestDto> generateStreamOfImages() {
        return Stream.generate(() -> ImageRequestDto.builder()
                .file(null)
                .build());
    }
}
