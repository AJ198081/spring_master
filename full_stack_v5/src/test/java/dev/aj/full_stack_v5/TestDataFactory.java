package dev.aj.full_stack_v5;

import dev.aj.full_stack_v5.auth.domain.dtos.UpdateUserDto;
import dev.aj.full_stack_v5.auth.domain.dtos.UserRegistrationDto;
import dev.aj.full_stack_v5.auth.domain.dtos.UserResponseDto;
import dev.aj.full_stack_v5.order.domain.dtos.CustomerDto;
import dev.aj.full_stack_v5.product.domain.dtos.CategoryDto;
import dev.aj.full_stack_v5.product.domain.dtos.ProductRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import net.datafaker.Faker;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.mock.web.MockMultipartFile;

import java.io.File;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@TestConfiguration(proxyBeanMethods = false)
@RequiredArgsConstructor
public class TestDataFactory {

    private final Faker faker;
    private final PhotosFactory photosFactory;

    public Stream<ProductRequestDto> generateStreamOfProductRequests() {
        List<String> availableBrands = List.of("Nike", "Adidas", "Puma", "Reebok", "Bata", "Bata", "Bata");

        return Stream.generate(() -> ProductRequestDto.builder()
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

    public Stream<UserRegistrationDto> generateStreamOfUserRegistrationDtos() {

        return Stream.generate(() -> UserRegistrationDto.builder()
                .username(faker.internet().username()
                        .concat(faker.random().nextInt(1, 999) + "")
                        .concat(faker.internet().emailAddress())
                )
                .password(faker.internet().password())
                .roles(getRandomRoles())
                .build());
    }

    private Set<String> getRandomRoles() {

        List<String> availableRoles = Arrays.asList("ROLE_USER", "ROLE_ADMIN", "ROLE_MANAGER", "ROLE_SALES_REP", "ROLE_GUEST");

        Collections.shuffle(availableRoles);

        return availableRoles.stream()
                .limit(faker.random().nextInt(availableRoles.size()))
                .collect(Collectors.toSet());
    }

    public Stream<MockMultipartFile> generateStreamOfImages() {
        return Stream.generate(this::getRandomImageFile);
    }

    @SneakyThrows
    public MockMultipartFile getRandomImageFile() {
        String photoPath = photosFactory.getRandomPhoto();
        File photoFile = new File(PhotosFactory.ABSOLUTE_PHOTOS_DIRECTORY_PATH + "/" + photoPath);
        return new MockMultipartFile(
                "file",
                photoFile.getName(),
                Files.probeContentType(photoFile.toPath()),
                Files.readAllBytes(photoFile.toPath())
        );
    }

    public UpdateUserDto getUpdatedUser(UserResponseDto registeredUser) {

        return UpdateUserDto.builder()
                .username(registeredUser.getUsername())
                .password(faker.internet().password())
                .rolesToBeUpdated(getRandomRoles())
                .build();
    }

    public Stream<CustomerDto> generateStreamOfCustomerRequests() {
        return Stream.generate(() -> CustomerDto.builder()
                .firstName(faker.name().firstName())
                .lastName(faker.name().lastName())
                .email(faker.internet().emailAddress())
                .address(faker.address().fullAddress())
                .phone(faker.phoneNumber().phoneNumber())
                .build());
    }
}
