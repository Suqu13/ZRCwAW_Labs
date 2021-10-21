import React from 'react';
import Button from '@mui/material/Button';
import Dialog from '@mui/material/Dialog';
import DialogActions from '@mui/material/DialogActions';
import DialogContent from '@mui/material/DialogContent';
import DialogContentText from '@mui/material/DialogContentText';
import DialogTitle from '@mui/material/DialogTitle';

interface Props {
  title: string,
  message: string,
  open: boolean
  onConfirmClick: () => void
  onCancelClick: () => void
  onClose: () => void
}

const ConfirmationDialog: React.FunctionComponent<Props> = ({
  title,
  message,
  open,
  onConfirmClick,
  onCancelClick,
  onClose,
}) => (
  <Dialog
    open={open}
    onClose={onClose}
    aria-labelledby="alert-dialog-title"
    aria-describedby="alert-dialog-description"
  >
    <DialogTitle id="alert-dialog-title">
      {title}
    </DialogTitle>
    <DialogContent>
      <DialogContentText id="alert-dialog-description">
        {message}
      </DialogContentText>
    </DialogContent>
    <DialogActions>
      <Button onClick={onConfirmClick} autoFocus>
        Confirm
      </Button>
      <Button onClick={onCancelClick}>Cancel</Button>
    </DialogActions>
  </Dialog>
);

export { ConfirmationDialog };
