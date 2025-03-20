import {useState} from "react";
import {Box, Button, Container, Heading, Input, VStack} from "@chakra-ui/react";
import {useColorModeValue} from "@/components/ui/color-mode.tsx";
import {useProductStore} from "@/store/productStore.ts";
import {toaster} from "@/components/ui/toaster.tsx";

export function CreatePage() {


    const [newProduct, setNewProduct] = useState<{
        name: string;
        description: string;
        price: number | undefined;
        imageUrl: string;
    }>({
        name: "",
        description: "",
        price: undefined,
        imageUrl: ""
    })

    const createProduct = useProductStore(state => state.createProduct);
    // const {createProduct} = useProductStore();

    const handleProductCreation = () => {
        const createProductPromise = createProduct(newProduct);
        toaster.promise(createProductPromise, {
            success: {
                title: createProductPromise.then(response => response.status),
                description: createProductPromise.then(response => response.message),
                duration: 5000,
                action: {
                    label: "View products",
                    onClick: () => {
                        window.location.href = "/";
                    }
                }
            },
            error: {
                title: createProductPromise.catch(reason => reason.status),
                description: createProductPromise.catch(reason => reason.message),
            },
            loading: {
                title: "Creating product",
                description: "Please wait...",
            }
        });
    };

    console.log("State changes");

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
                    <Input placeholder={"Price"} type={"number"} value={newProduct.price !== undefined || 0 ? Number(newProduct.price) : ""}
                           borderColor={"gray.300"}
                           onChange={e => setNewProduct({...newProduct, price: e.target.value ? +e.target.value : undefined})}/>
                    <Input placeholder={"Image"} value={newProduct.imageUrl}
                           borderColor={"gray.300"}
                           onChange={e => setNewProduct({...newProduct, imageUrl: e.target.value})}/>
                    <Button bg={"purple.600"} color={'white'} onClick={handleProductCreation}>Add product</Button>
                </VStack>

            </Box>
        </VStack>
    </Container>;
}