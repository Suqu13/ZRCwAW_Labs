import React, { FunctionComponent, useEffect, useState } from 'react';
import {
  Button, CircularProgress,
  Grid,
  IconButton, List, ListItem, ListItemText, Typography,
} from '@mui/material';
import DownloadIcon from '@mui/icons-material/Download';
import DeleteIcon from '@mui/icons-material/Delete';
import { saveAs } from 'file-saver';
import { deleteObjectStorageItem, downloadFile, getObjectStorageItems } from '../api/object-storage-api';
import { ObjectStorageItem } from '../api/model';

const objectStorageItemsHook = (storageName: string): {
  objectStorageItems: Array<ObjectStorageItem>,
  loading: boolean,
  error: boolean,
  fetchObjectStorageItems: () => void
} => {
  const [objectStorageItems, setObjectsStorageItems] = useState<Array<ObjectStorageItem>>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<boolean>(false);

  const fetchObjectStorageItems = (): void => {
    if (!loading) setLoading(true);
    if (error) setError(false);
    getObjectStorageItems(storageName)
      .then((x) => {
        setObjectsStorageItems(x);
        setLoading(false);
      }).catch(() => setError(true));
  };

  useEffect((): void => {
    fetchObjectStorageItems();
  }, []);

  return {
    objectStorageItems,
    loading,
    error,
    fetchObjectStorageItems,
  };
};

const downloadAndSaveFile = (storageName: string, objectKey: string): Promise<void> => {
  const fileName = objectKey.split('/').pop();
  return downloadFile(storageName, objectKey).then((blob) => saveAs(blob, fileName ?? objectKey));
};

const deleteFile = (
  storageName: string,
  objectKey: string,
  refetch: () => void,
): Promise<void> => deleteObjectStorageItem(storageName, objectKey).then(() => refetch());

interface Props {
  objectStorageName: string
}

const ObjectStorageContent: FunctionComponent<Props> = ({ objectStorageName }) => {
  const {
    objectStorageItems,
    loading,
    error,
    fetchObjectStorageItems,
  } = objectStorageItemsHook(objectStorageName);

  if (error) {
    return (
      <Grid
        container
        direction="column"
        justifyContent="center"
        alignItems="center"
      >
        <Typography variant="h6" gutterBottom>
          Unlucky, some error occured!
        </Typography>
        <Button variant="outlined" color="error" onClick={fetchObjectStorageItems}>
          Reload
        </Button>
      </Grid>
    );
  }

  if (loading) {
    return (
      <Grid
        container
        direction="row"
        justifyContent="center"
        alignItems="center"
      >
        <CircularProgress />
      </Grid>
    );
  }

  return (
    <>
      <List>
        {objectStorageItems.map((item) => (
          <ListItem
            key={item.key}
            secondaryAction={(
              <IconButton onClick={() => downloadAndSaveFile(objectStorageName, item.key)}>
                <DownloadIcon />
              </IconButton>
            )}
          >
            <IconButton
              onClick={() => deleteFile(objectStorageName, item.key, fetchObjectStorageItems)}
            >
              <DeleteIcon />
            </IconButton>
            <ListItemText primary={item.key} />
          </ListItem>
        ))}
      </List>
    </>
  );
};

export { ObjectStorageContent };
