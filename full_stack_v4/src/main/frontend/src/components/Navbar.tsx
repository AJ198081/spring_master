import {Button, Container, Flex, HStack, Link, StackSeparator, Text} from "@chakra-ui/react";
import {CiSquarePlus} from "react-icons/ci";
import {useColorMode} from "@/components/ui/color-mode.tsx";

// import {useProductStore} from "@/store/productStore.ts";

export function Navbar() {

    const {colorMode, toggleColorMode} = useColorMode();
    // const {products, setProducts} = useProductStore();


    return <Container maxW={"1140px"} px={4}>
        <Flex
            as={"nav"}
            align={"center"}
            justify={"space-between"}
            flexDir={
                {
                    base: "column",
                    sm: "row",
                }
            }
            wrap={"wrap"}
            padding={4}
        >
            <Text
                fontSize={{base: "22", sm: "28"}}
                fontWeight={"bold"}
                textAlign={"center"}
                textTransform={"uppercase"}
                bgGradient={"linear(to-r, cyan.400, blue.500)"}
                bgClip={"text"}
            >
                <Link href={"/"}>
                    Product Store 🛒
                </Link>
            </Text>
            <HStack separator={<StackSeparator/>} alignItems="center">
                <Link href={"/create"}>
                    <Button colorScheme="green" variant="subtle">
                        <CiSquarePlus/>
                    </Button>
                    <Button onClick={toggleColorMode} variant={"subtle"}>
                        {colorMode === "light" ? "🌙" : "🌤️"}
                    </Button>
                </Link>
            </HStack>

        </Flex>
    </Container>;
}