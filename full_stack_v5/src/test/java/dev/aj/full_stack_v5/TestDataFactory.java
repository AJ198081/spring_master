package dev.aj.full_stack_v5;

import dev.aj.full_stack_v5.auth.domain.dtos.UpdateUserDto;
import dev.aj.full_stack_v5.auth.domain.dtos.UserRegistrationDto;
import dev.aj.full_stack_v5.auth.domain.dtos.UserResponseDto;
import dev.aj.full_stack_v5.order.domain.dtos.CartItemDto;
import dev.aj.full_stack_v5.order.domain.dtos.CustomerDto;
import dev.aj.full_stack_v5.order.domain.entities.Cart;
import dev.aj.full_stack_v5.order.domain.entities.CartItem;
import dev.aj.full_stack_v5.order.domain.entities.Customer;
import dev.aj.full_stack_v5.order.domain.entities.Order;
import dev.aj.full_stack_v5.order.domain.entities.OrderItem;
import dev.aj.full_stack_v5.order.domain.entities.enums.OrderStatus;
import dev.aj.full_stack_v5.product.domain.dtos.CategoryDto;
import dev.aj.full_stack_v5.product.domain.dtos.ImageRequestDto;
import dev.aj.full_stack_v5.product.domain.dtos.ProductRequestDto;
import dev.aj.full_stack_v5.product.domain.entities.Product;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import net.datafaker.Faker;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
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

    public Stream<FileSystemResource> generateStreamOfPhotoFileResources() {
        return Stream.generate(this::getRandomPhotoFile);
    }

    public Stream<ImageRequestDto> generateStreamOfImages() {
        return Stream.generate(() -> ImageRequestDto.builder().file(getRandomImageFile()).build());
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

    public Stream<CartItem> generateStreamOfCartItems(Cart cart, Product product) {
        return Stream.generate(() -> CartItem.builder()
                .quantity(faker.random().nextInt(1, 10))
                .unitPrice(product.getPrice())
                .product(product)
                .cart(cart)
                .build());
    }

    public Stream<CartItemDto> generateStreamOfCartItemDtos() {
        return Stream.generate(getCartItemDtoSupplier());

    }

    private @NotNull Supplier<CartItemDto> getCartItemDtoSupplier() {

        Integer quantity = faker.random().nextInt(1, 10);

        return () -> CartItemDto.builder()
                .quantity(quantity)
                .build();
    }


    private Set<String> getRandomRoles() {

        List<String> availableRoles = Arrays.asList("ROLE_USER", "ROLE_ADMIN", "ROLE_MANAGER", "ROLE_SALES_REP", "ROLE_GUEST");

        Collections.shuffle(availableRoles);

        return availableRoles.stream()
                .limit(faker.random().nextInt(availableRoles.size()))
                .collect(Collectors.toSet());
    }

    @SneakyThrows
    private MultipartFile getRandomImageFile() {
        String photoPath = photosFactory.getRandomPhoto();
        File photoFile = new File(PhotosFactory.ABSOLUTE_PHOTOS_DIRECTORY_PATH + "/" + photoPath);
        return new MockMultipartFile(
                photoPath.toUpperCase(),
                photoFile.getName(),
                Files.probeContentType(photoFile.toPath()),
                Files.readAllBytes(photoFile.toPath())
        );
    }

    private FileSystemResource getRandomPhotoFile() {
        String photoPath = photosFactory.getRandomPhoto();
        File photoFile = new File(PhotosFactory.ABSOLUTE_PHOTOS_DIRECTORY_PATH + "/" + photoPath);

        return new FileSystemResource(photoFile);
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

    public Stream<Order> generateStreamOfOrders(Customer customer, Set<Product> products) {
        return Stream.generate(() -> {
            Order order = Order.builder()
                    .orderDate(ZonedDateTime.now())
                    .status(OrderStatus.PENDING)
                    .comments(faker.lorem().sentence())
                    .customer(customer)
                    .build();

            Set<OrderItem> orderItems = products.stream()
                    .map(product ->
                            OrderItem.builder()
                                    .order(order)
                                    .product(product)
                                    .quantity(faker.random().nextInt(1, 5))
                                    .price(product.getPrice())
                                    .build())
                    .collect(Collectors.toSet());

            order.setOrderItems(orderItems);
            order.updateTotal();

            return order;
        });
    }
}
