import {Button, CloseButton, Dialog, Portal} from "@chakra-ui/react";
import {ReactNode} from "react";

export function Modal({children}: { children: ReactNode }): ReactNode {

    return (
        <Dialog.Root>
            <Dialog.Trigger asChild>
                <Button variant="outline" size="sm">
                    Open Dialog
                </Button>
            </Dialog.Trigger>
            <Portal>
                <Dialog.Backdrop/>
                <Dialog.Positioner>
                    <Dialog.Content>
                        <Dialog.Context>
                            {(store) => (
                                <Dialog.Body pt="6" spaceY="3">
                                    {children}
                                    <button onClick={() => store.setOpen(false)}>Close</button>
                                </Dialog.Body>
                            )}
                        </Dialog.Context>
                        <Dialog.CloseTrigger asChild>
                            <CloseButton size="sm"/>
                        </Dialog.CloseTrigger>
                    </Dialog.Content>
                </Dialog.Positioner>
            </Portal>
        </Dialog.Root>
    );
}