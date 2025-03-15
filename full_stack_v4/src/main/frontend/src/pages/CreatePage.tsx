import {useState} from "react";
import {Box, Button, Container, Heading, Input, VStack} from "@chakra-ui/react";
import {useColorModeValue} from "@/components/ui/color-mode.tsx";

export function CreatePage() {

    const [newProduct, setNewProduct] = useState({
        name: "",
        description: "",
        price: 0,
        image: ""
    })

    const handleClick = () => {
        console.log(newProduct);
    };

    return <Container maxW={"container.sm"}>
        <VStack>
            <Heading as={"h1"} size={"3xl"} textAlign={"center"} mb={"8"}>Create Product</Heading>
            <Box w={"2xl"} bg={useColorModeValue("white", "gray.800")} p={12} rounded={"md"} shadow={"md"}>
                <VStack>
                    <Input placeholder={"Name"} value={newProduct.name}
                           borderColor={"gray.300"}
                           onChange={e => setNewProduct({...newProduct, name: e.target.value})}/>
                    <Input placeholder={"Description"} value={newProduct.description}
                           borderColor={"gray.300"}
                           onChange={e => setNewProduct({...newProduct, description: e.target.value})}/>
                    <Input placeholder={"Price"} type={"number"} value={newProduct.price}
                           borderColor={"gray.300"}
                           onChange={e => setNewProduct({...newProduct, price: Number(e.target.value)})}/>
                    <Input placeholder={"Image"} value={newProduct.image}
                           borderColor={"gray.300"}
                           onChange={e => setNewProduct({...newProduct, image: e.target.value})}/>
                    <Button colorScheme={'blue'} onClick={handleClick}>Add product</Button>
                </VStack>

            </Box>
        </VStack>
    </Container>;
}