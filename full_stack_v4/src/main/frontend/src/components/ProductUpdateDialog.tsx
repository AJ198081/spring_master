
import { Button, Dialog, Field, Input, Portal, Stack } from "@chakra-ui/react"
import {ReactNode, useRef} from "react"
import {ProductType} from "@/types/ProductType.ts";

interface UpdateDialogProps {
    product: ProductType
    trigger: ReactNode
    onCancel: () => void
    onUpdate: (product: ProductType) => void
}

export const ProductUpdateDialog = ({product, trigger, onUpdate, onCancel}: UpdateDialogProps) => {
    const ref = useRef<HTMLInputElement>(null)
    return (
        <Dialog.Root initialFocusEl={() => ref.current}>
            <Dialog.Trigger asChild>
                {trigger}
            </Dialog.Trigger>
            <Portal>
                <Dialog.Backdrop />
                <Dialog.Positioner>
                    <Dialog.Content>
                        <Dialog.Header>
                            <Dialog.Title>Update {product.name}</Dialog.Title>
                        </Dialog.Header>
                        <Dialog.Body pb="4">
                            <Stack gap="4">
                                <Field.Root>
                                    <Field.Label>Name</Field.Label>
                                    <Input placeholder={product.name} disabled={false} />
                                </Field.Root>
                                <Field.Root>
                                    <Field.Label>Description</Field.Label>
                                    <Input ref={ref} placeholder={product.description} disabled={false} />
                                </Field.Root>
                                <Field.Root>
                                    <Field.Label>Price</Field.Label>
                                    <Input type={'number'} placeholder={String(product.price)} disabled={false} />
                                </Field.Root>
                                <Field.Root>
                                    <Field.Label>Image URL</Field.Label>
                                    <Input placeholder={product.imageUrl} disabled={false} />
                                </Field.Root>
                            </Stack>
                        </Dialog.Body>
                        <Dialog.Footer>
                            <Dialog.ActionTrigger asChild>
                                <Button variant="outline" onClick={onCancel}>Cancel</Button>
                            </Dialog.ActionTrigger>
                            <Button variant={'solid'} color={'purple'} onClick={() => onUpdate(product)}>Update</Button>
                        </Dialog.Footer>
                    </Dialog.Content>
                </Dialog.Positioner>
            </Portal>
        </Dialog.Root>
    )
}
