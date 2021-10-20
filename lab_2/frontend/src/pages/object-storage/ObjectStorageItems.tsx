import React, { useState, useEffect } from 'react';
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableRow,
  Typography,
  IconButton,
} from '@mui/material';
import DeleteIcon from '@mui/icons-material/Delete';
import DownloadIcon from '@mui/icons-material/Download';
import { useSnackbar } from 'notistack';
import {
  deleteObjectStorageItem,
  downloadObjectStorageItem,
  getObjectStorageItems,
} from '../../api/object-storage-api';
import { ObjectItem } from '../../api/model';
import { ErrorState } from '../../components/ErrorState';
import { LoadingState } from '../../components/LoadingState';
import { ConfirmationDialog } from '../../components/ConfirmationDialog';

const objectStorageItemsHook = (storageName: string): {
  objectStorageItems: Array<ObjectItem>,
  loading: boolean,
  error: boolean,
  fetchObjectStorageItems: () => void
  deleteObjectItem: (objectItemKey: string) => void
  downloadObjectItem: (objectItemKey: string) => void
} => {
  const [objectStorageItems, setObjectsStorageItems] = useState<Array<ObjectItem>>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(false);
  const { enqueueSnackbar } = useSnackbar();

  const fetchObjectStorageItems = (): void => {
    if (!loading) setLoading(true);
    if (error) setError(false);
    getObjectStorageItems(storageName)
      .then((x) => {
        setObjectsStorageItems(x);
        setLoading(false);
      }).catch(() => setError(true));
  };

  const deleteObjectItem = (objectItemKey: string): void => {
    enqueueSnackbar(
      `Deleting the object with ${objectItemKey} key from ${storageName} started!`,
      { variant: 'info' },
    );
    deleteObjectStorageItem(storageName, objectItemKey)
      .then(() => {
        enqueueSnackbar(
          `Deleting the object with ${objectItemKey} key from ${storageName} completed succesfully!`,
          { variant: 'success' },
        );
        fetchObjectStorageItems();
      })
      .catch(() => enqueueSnackbar(
        `Deleting the object with ${objectItemKey} key from ${storageName} completed unsuccesfully!`,
        { variant: 'error' },
      ));
  };

  const downloadObjectItem = (objectItemKey: string): void => {
    const fileName = objectItemKey.split('/').pop() ?? objectItemKey;
    enqueueSnackbar(
      `Downloading the object with ${fileName} key from ${storageName} started!`,
      { variant: 'info' },
    );
    downloadObjectStorageItem(storageName, objectItemKey)
      .then((blob) => saveAs(blob, fileName))
      .catch(() => enqueueSnackbar(
        `Downloading the object with ${objectItemKey} key from ${storageName} completed unsuccesfully!`,
        { variant: 'error' },
      ));
  };

  useEffect((): void => {
    fetchObjectStorageItems();
  }, []);

  return {
    objectStorageItems,
    loading,
    error,
    fetchObjectStorageItems,
    deleteObjectItem,
    downloadObjectItem,
  };
};

interface Props {
  objectStorageName: string
}

const ObjectStorageItems: React.FunctionComponent<Props> = ({ objectStorageName }) => {
  const {
    objectStorageItems,
    loading,
    error,
    fetchObjectStorageItems,
    deleteObjectItem,
    downloadObjectItem,
  } = objectStorageItemsHook(objectStorageName);

  const [openDownloadingDialog, setOpenDownloadingDialog] = useState(false);
  const [openDeletionDialog, setOpenDeletionDialog] = useState(false);
  const [selectedObjectItemKey, setSelectedObjectItemKey] = useState<string>();

  const openDownloading = (objectItemKey: string): void => {
    setSelectedObjectItemKey(objectItemKey);
    setOpenDownloadingDialog(true);
  };

  const openDeletion = (objectItemKey: string): void => {
    setSelectedObjectItemKey(objectItemKey);
    setOpenDeletionDialog(true);
  };

  if (error) {
    return (
      <ErrorState onClick={fetchObjectStorageItems} />
    );
  }

  if (loading) {
    return (
      <LoadingState />
    );
  }

  return (
    <>
      <Typography variant="h6" gutterBottom component="div">
        Objects
      </Typography>
      <Table size="small">
        <TableHead>
          <TableRow>
            <TableCell>Name</TableCell>
            <TableCell />
          </TableRow>
        </TableHead>
        <TableBody>
          {objectStorageItems.map((row) => (
            <TableRow key={row.key}>
              <TableCell>
                {row.key}
              </TableCell>
              <TableCell>
                <IconButton onClick={() => openDeletion(row.key)}>
                  <DeleteIcon />
                </IconButton>
              </TableCell>
              <TableCell>
                <IconButton onClick={() => openDownloading(row.key)}>
                  <DownloadIcon />
                </IconButton>
              </TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>
      <ConfirmationDialog
        title="Object deletion"
        message="Are you sure that you want to delete file?"
        open={openDeletionDialog}
        onClose={() => setOpenDeletionDialog(false)}
        onCancelClick={() => setOpenDeletionDialog(false)}
        onConfirmClick={() => {
          if (selectedObjectItemKey) { deleteObjectItem(selectedObjectItemKey); }
          setOpenDeletionDialog(false);
        }}
      />
      <ConfirmationDialog
        title="Object downloading"
        message="Are you sure that you want to download file?"
        open={openDownloadingDialog}
        onClose={() => setOpenDownloadingDialog(false)}
        onCancelClick={() => setOpenDeletionDialog(false)}
        onConfirmClick={() => {
          if (selectedObjectItemKey) { downloadObjectItem(selectedObjectItemKey); }
          setOpenDeletionDialog(false);
        }}
      />
    </>
  );
};

export { ObjectStorageItems };
