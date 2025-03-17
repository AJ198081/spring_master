import {Container, Link, Stack, Text} from "@chakra-ui/react";
import {useProductStore} from "@/store/productStore.ts";
import {useEffect} from "react";

export function HomePage() {

    const fetchProducts = useProductStore(state => state.fetchProducts);
    // const _ = useProductStore(state => state.products);

    useEffect(() => {
        fetchProducts();
    }, []);



    return (
        <Container maxW={"container.xl"}>
            <Stack gap={4} direction={{base: "column"}} align={"center"}>
                <Text
                    fontSize={"2xl"}
                    fontWeight={"bold"}
                    bgGradient={"to-r"}
                    gradientFrom={"cyan.500"}
                    gradientTo={"blue.500"}
                    textAlign={"center"}
                >
                    Current Product 🚀 {" "}
                </Text>
                <Text
                    fontSize={"xl"}
                    fontWeight={"bold"}
                    textAlign={"center"}
                    color={"gray.500"}
                >
                    No product found 😒
                    <Link href={"/create"}>
                        <Text as={"span"}
                              ps={4}
                              _hover={{textDecoration: "underline"}}
                              color={"blue.500"}>
                            Create product
                        </Text>
                    </Link>
                </Text>

            </Stack>

        </Container>
    );
}