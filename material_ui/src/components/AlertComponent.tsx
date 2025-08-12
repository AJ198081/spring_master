import React from "react";
import { Alert, AlertColor, AlertTitle, Button, Link, Snackbar, Stack } from "@mui/material";

/**
 * MUI Alert component — quick explanation and examples
 *
 * What it is:
 * - Alert is a feedback component to display important messages.
 * - Common severities: "success", "info", "warning", "error".
 * - Variants: "standard" (default), "outlined", and "filled".
 *
 * Key props you will often use:
 * - severity: AlertColor — controls the intent color (success | info | warning | error)
 * - variant: "standard" | "outlined" | "filled" — controls the fill style
 * - onClose: () => void — shows a close icon button when provided and lets you dismiss the alert
 * - action: ReactNode — add custom action buttons/links on the right side
 * - icon: ReactNode | false — override or hide the leading icon (false hides it)
 * - children: ReactNode — your alert message body; you can also use <AlertTitle> for a bold title line
 *
 * Patterns demonstrated below:
 * 1) Basic severities
 * 2) Variants (standard, outlined, filled)
 * 3) With title and custom action
 * 4) Closable alerts (onClose)
 * 5) Alert used inside a Snackbar for transient messages
 */

export const AlertComponent: React.FC = () => {
  const [openSnack, setOpenSnack] = React.useState(false);
  const handleOpenSnack = () => setOpenSnack(true);
  const handleCloseSnack = () => setOpenSnack(false);

  return (
    <Stack spacing={2} sx={{ m: 2 }}>
      {/* 1) Basic severities */}
      <Stack spacing={1}>
        <Alert severity="success">This is a success alert — it indicates a positive action or result.</Alert>
        <Alert severity="info">This is an info alert — it provides neutral information.</Alert>
        <Alert severity="warning">This is a warning alert — it draws attention to a potential issue.</Alert>
        <Alert severity="error">This is an error alert — something went wrong.</Alert>
      </Stack>

      {/* 2) Variants */}
      <Stack spacing={1}>
        <Alert severity="info" variant="standard">Standard variant (default)</Alert>
        <Alert severity="info" variant="outlined">Outlined variant</Alert>
        <Alert severity="info" variant="filled">Filled variant</Alert>
      </Stack>

      {/* 3) With title and custom action */}
      <Alert
        severity="warning"
        variant="outlined"
        action={
          <Stack direction="row" spacing={1}>
            <Button size="small" color="inherit" onClick={() => alert("Undo clicked")}>Undo</Button>
            <Link href="#" underline="hover" color="inherit" onClick={(e) => { e.preventDefault(); alert("Learn more clicked"); }}>Learn more</Link>
          </Stack>
        }
      >
        <AlertTitle>Low disk space</AlertTitle>
        You are running out of space on drive C:. Free up some space to continue.
      </Alert>

      {/* 4) Closable (dismiss) alert. Providing onClose renders the close icon automatically. */}
      <ClosableAlert severity="success">Profile updated successfully.</ClosableAlert>

      {/* 5) Alert inside a Snackbar for transient messages. */}
      <Button variant="contained" onClick={handleOpenSnack}>Show Snackbar Alert</Button>
      <Snackbar
        open={openSnack}
        autoHideDuration={3000}
        onClose={handleCloseSnack}
        anchorOrigin={{ vertical: "bottom", horizontal: "center" }}
      >
        <Alert onClose={handleCloseSnack} severity="success" variant="filled" sx={{ width: "100%" }}>
          Saved successfully!
        </Alert>
      </Snackbar>
    </Stack>
  );
};

/**
 * Small helper component to demonstrate a reusable closable Alert with typed severity.
 */
const ClosableAlert: React.FC<{
  severity: AlertColor;
  children: React.ReactNode;
}> = ({ severity, children }) => {
  const [open, setOpen] = React.useState(true);
  if (!open) return null;
  return (
    <Alert severity={severity} onClose={() => setOpen(false)}>
      {children}
    </Alert>
  );
};

export default AlertComponent;
