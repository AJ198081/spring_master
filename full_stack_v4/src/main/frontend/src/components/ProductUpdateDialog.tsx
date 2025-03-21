import {Button, Dialog, Field, Input, Portal, Stack} from "@chakra-ui/react"
import {ReactNode, useRef, useState} from "react"
import {ProductType} from "@/types/ProductType.ts";
import {useColorModeValue} from "@/components/ui/color-mode.tsx";
import {useProductStore} from "@/store/productStore.ts";
import {toaster} from "@/components/ui/toaster.tsx";

interface UpdateDialogProps {
    product: ProductType
    trigger: ReactNode
    onCancel: () => void
}

export const ProductUpdateDialog = ({product, trigger, onCancel}: UpdateDialogProps) => {
    const ref = useRef<HTMLInputElement>(null)
    const [updateProduct, setUpdateProduct] = useState<ProductType>(product)
    const updateProductHandler = useProductStore(state => state.updateProduct);


    // const bg = useColorModeValue('purple.200', 'purple.800')
    const bg = useColorModeValue('green.600', 'green.600')
    const text = useColorModeValue('gray.100', 'gray.100')

    const productUpdateHandler = () => {
        const updateResponse = updateProductHandler(updateProduct);
        toaster.promise(updateResponse, {
            success: {
                title: updateResponse.then(response => response.status),
                description: updateResponse.then(response => response.message),
                duration: 3000,
                meta: {
                    closable: true,
                },
                action: {
                    label: "View products",
                    onClick: () => {
                        window.location.href = "/";
                    }
                }
            },
            error: {
                title: updateResponse.catch(reason => reason.status),
                description: updateResponse.catch(reason => reason.message),
            },
            loading: {
                title: `Updating ${updateProduct.name}`,
                description: "Please wait...",
            }
        })
    }

    return (
        <Dialog.Root modal={true}
                     closeOnInteractOutside={false}
                     onFocusOutside={onCancel}
                     initialFocusEl={() => ref.current}>
            <Dialog.Trigger asChild>
                {trigger}
            </Dialog.Trigger>
            <Portal>
                <Dialog.Backdrop/>
                <Dialog.Positioner>
                    <Dialog.Content>
                        <Dialog.Header>
                            <Dialog.Title>Update {updateProduct.name}</Dialog.Title>
                        </Dialog.Header>
                        <Dialog.Body pb="4">
                            <Stack gap="4">
                                <Field.Root>
                                    <Field.Label>Name</Field.Label>
                                    <Input
                                    onChange={(event) => setUpdateProduct({...updateProduct, name: event.target.value})}
                                        value={updateProduct.name} disabled={false}/>
                                </Field.Root>
                                <Field.Root>
                                    <Field.Label>Description</Field.Label>
                                    <Input ref={ref}
                                    onChange={(event) => setUpdateProduct({...updateProduct, description: event.target.value})}
                                           value={updateProduct.description} disabled={false}/>
                                </Field.Root>
                                <Field.Root>
                                    <Field.Label>Price</Field.Label>
                                    <Input type={'number'}
                                    onChange={(event) => setUpdateProduct({...updateProduct, price: +event.target.value})}
                                           value={updateProduct.price} disabled={false}/>
                                </Field.Root>
                                <Field.Root>
                                    <Field.Label>Image URL</Field.Label>
                                    <Input
                                    onChange={(event) => setUpdateProduct({...updateProduct, imageUrl: event.target.value})}
                                        value={updateProduct.imageUrl} disabled={false}/>
                                </Field.Root>
                            </Stack>
                        </Dialog.Body>
                        <Dialog.Footer>
                            <Dialog.ActionTrigger asChild>
                                <Button variant="outline" onClick={onCancel}>
                                    Cancel
                                </Button>
                            </Dialog.ActionTrigger>
                            <Button variant={'solid'} bg={bg} color={text}
                                    onClick={productUpdateHandler}>
                                Update
                            </Button>
                        </Dialog.Footer>
                    </Dialog.Content>
                </Dialog.Positioner>
            </Portal>
        </Dialog.Root>
    )
}
