import {ProductType} from "@/types/ProductType.ts";
import {Box, Heading, IconButton, Image, Stack, Text} from "@chakra-ui/react";
import {useColorModeValue} from "@/components/ui/color-mode.tsx";
import {FaRegEdit} from "react-icons/fa";
import {MdOutlineDeleteSweep} from "react-icons/md";
import {useProductStore} from "@/store/productStore.ts";
import {toaster} from "@/components/ui/toaster.tsx";
import {ProductUpdateDialog} from "@/components/ProductUpdateDialog.tsx";

interface ProductProps {
    product: ProductType;
}

export const ProductCard = ({product}: ProductProps) => {


    const deleteProduct = useProductStore(state => state.deleteProduct);

    const handleDelete = (id: number, name: string) => {

        const promise = deleteProduct(id, name);

        toaster.promise(promise, {
            success: {
                title: promise.then(response => response.status),
                description: promise.then(response => response.message),
                duration: 5000,
                action: {
                    label: "View products",
                    onClick: () => {
                        window.location.href = "/";
                    }
                }
            },
            error: {
                title: promise.catch(reason => reason.status),
                description: promise.catch(reason => reason.message),
            },
            loading: {
                title: "Creating product",
                description: "Please wait...",
            }
        });
    }

    const bg = useColorModeValue('gray.200', 'gray.800');

    const fullImageUrl = `${import.meta.env.VITE_PRODUCT_SERVICE_URL}/photos/${product.imageUrl}`;

    return (
        <Box
            shadow="lg"
            rounded={"lg"}
            overflow={"hidden"}
            transition={'all 0.3s'}
            _hover={{transform: 'translateY(-5px)', shadow: 'xl'}}
            bg={bg}
        >
            <Image src={fullImageUrl} alt={product.name} h={48} w={'full'} objectFit={"cover"}/>
            <Box p={6}>
                <Heading as={"h3"} size={'md'} mb={2}>
                    {product.name}
                </Heading>
                <Text fontWeight={600} color={useColorModeValue('gray.900', 'gray.200')} mb={4}>
                    {product.price}
                </Text>
            </Box>
            <Stack direction={"row"} align={"center"} gap={4} p={6}>
                <IconButton aria-label="Edit" bg={"blue.400"} onClick={() => {
                    console.log("Edit");
                }}>
                    {/*<FaRegEdit />*/}
                    <ProductUpdateDialog product={product}
                                         trigger={<FaRegEdit/>}
                                         onCancel={() => console.log('cancel')}
                    />
                </IconButton>
                <IconButton aria-label="Delete" bg={"red.400"} onClick={() => {
                    console.log("Delete id", product.id);
                    handleDelete(product.id!, product.name);
                }}>
                    <MdOutlineDeleteSweep/>
                </IconButton>
            </Stack>
        </Box>
    );

}