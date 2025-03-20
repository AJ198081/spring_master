import {Container, Link, SimpleGrid, Stack, Text} from "@chakra-ui/react";
import {useProductStore} from "@/store/productStore.ts";
import {useEffect} from "react";
import {ProductCard} from "@/components/ProductCard.tsx";

export function HomePage() {

    const fetchProducts = useProductStore(state => state.fetchProducts);
    const products = useProductStore(state => state.products);

    useEffect(() => {
        fetchProducts();
    }, []);

    console.log(products.length);

    return (
        <Container maxW={"container.xl"}>
            <Stack gap={4} direction={{base: "column"}} align={"center"}>
                <Text
                    fontSize={"2xl"}
                    fontWeight={"bold"}
                    bgGradient={"to-r"}
                    gradientFrom={"cyan.10"}
                    gradientTo={"blue.200"}
                    textAlign={"center"}
                >
                    Current Products 🚀
                </Text>

                {products.length === 0
                    ? <>No product found 😒
                        <Text
                            fontSize={"xl"}
                            fontWeight={"bold"}
                            textAlign={"center"}
                            color={"gray.500"}
                        >
                            <Link href={"/create"}>
                                <Text as={"span"}
                                      ps={4}
                                      _hover={{textDecoration: "underline"}}
                                      color={"blue.500"}>
                                    Create product
                                </Text>
                            </Link>
                        </Text>
                    </>
                    : <SimpleGrid
                        columns={{base: 1, md: 2, lg: 3}}
                        gap={10}
                        w={'full'}
                    >
                        {products.map((product, index) => (
                            <ProductCard
                                key={index}
                                product={product}/>
                        ))}
                    </SimpleGrid>
                }
            </Stack>

        </Container>
    )
        ;
}