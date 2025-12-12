import {Button, Dialog, DialogActions, DialogContent, DialogContentText, DialogTitle} from "@mui/material";

export const ConfirmLoginCancelDialog = (isDialogOpen: boolean, closeDialog: () => void, abortController: AbortController | null, setSubmitting: (isSubmitting: boolean) => void) => {
    return <Dialog
        open={isDialogOpen}
        onClose={closeDialog}
        aria-labelledby="alert-dialog-title"
        aria-describedby="alert-dialog-description"
        // disableRestoreFocus={true}
    >
        <DialogTitle id="alert-dialog-title">
            {"Cancel Login Request?"}
        </DialogTitle>
        <DialogContent>
            <DialogContentText id="alert-dialog-description">
                Are you sure you want to cancel the ongoing login request?
            </DialogContentText>
        </DialogContent>
        <DialogActions>
            <Button
                variant={"outlined"}
                color={"info"}
                onClick={closeDialog}
                // if you want autofocus, you have to disableRestoreFocus on Dialog,
                // which means you won't focus back on where you opened the dialog from
                // autoFocus={true}
                tabIndex={0}
            >No</Button>
            <Button
                variant={"contained"}
                type={"reset"}
                color={"error"}
                tabIndex={1}
                onClick={() => {
                    if (abortController) {
                        abortController.abort('Login attempt canceled by the user');
                        setSubmitting(false);
                    }
                    closeDialog();
                }}
            >
                Yes, Cancel
            </Button>
        </DialogActions>
    </Dialog>;
}