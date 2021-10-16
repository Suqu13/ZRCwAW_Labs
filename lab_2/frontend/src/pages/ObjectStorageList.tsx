import React, { useEffect, useState } from 'react';
import {
  Accordion,
  AccordionDetails,
  AccordionSummary,
  Typography,
  CircularProgress,
  Grid,
  Button,
  Alert,
  Snackbar,
} from '@mui/material';
import ExpandMoreIcon from '@mui/icons-material/ExpandMore';
import { FileUpload } from '@mui/icons-material';
import { getObjectStorages, uploadFile } from '../api/object-storage-api';
import { ObjectStorage } from '../api/model';
import { ObjectStorageContent } from './ObjectStorageContent';

type UploadObjectStatus = { status: 'info' | 'success' | 'error', message: string };

const objectStoragesHook = (): {
  objectsStorages: Array<ObjectStorage>,
  loading: boolean,
  error: boolean,
  fetchObjectStorages: () => void
  uploadObject: (e: React.ChangeEvent<HTMLInputElement>, objectStorageName: string) => void,
  uploadObjectStatus: UploadObjectStatus,
  showUploadObjectStatus: boolean,
  closeUploadObjectStatus: () => void
} => {
  const [objectsStorages, setObjectsStorages] = useState<Array<ObjectStorage>>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<boolean>(false);
  const [uploadObjectStatus, setUploadObjectStatus] = useState<UploadObjectStatus>({ status: 'info', message: '' });
  const [showUploadObjectStatus, setShowUploadObjectStatus] = useState(false);

  const fetchObjectStorages = (): void => {
    if (!loading) setLoading(true);
    if (error) setError(false);
    getObjectStorages()
      .then((x) => {
        setObjectsStorages(x);
        setLoading(false);
      }).catch(() => setError(true));
  };

  useEffect((): void => {
    fetchObjectStorages();
  }, []);

  const uploadObject = (
    e: React.ChangeEvent<HTMLInputElement>,
    objectStorageName: string,
  ): void => {
    const file = e.target.files?.[0];
    if (file) {
      const formData = new FormData();
      formData.append('File', file);
      setUploadObjectStatus({ status: 'info', message: `Uploading ${file.name} file to ${objectStorageName} started!` });
      setShowUploadObjectStatus(true);
      uploadFile(formData, objectStorageName, file.name)
        .then(() => {
          setUploadObjectStatus({ status: 'success', message: `Uploading ${file.name} file to ${objectStorageName} completed succesfully!` });
          setShowUploadObjectStatus(true);
        }).catch(() => {
          setUploadObjectStatus({ status: 'error', message: `Uploading ${file.name} file to ${objectStorageName} completed unsuccesfully!` });
          setShowUploadObjectStatus(true);
        });
    }
  };

  const closeUploadObjectStatus = (): void => setShowUploadObjectStatus(false);

  return {
    objectsStorages,
    loading,
    error,
    fetchObjectStorages,
    uploadObject,
    uploadObjectStatus,
    showUploadObjectStatus,
    closeUploadObjectStatus,
  };
};

const ObjectStorageList = (): JSX.Element => {
  const {
    objectsStorages,
    loading,
    error,
    fetchObjectStorages,
    uploadObject,
    uploadObjectStatus,
    showUploadObjectStatus,
    closeUploadObjectStatus,
  } = objectStoragesHook();

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
        <Button variant="outlined" color="error" onClick={fetchObjectStorages}>
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
      {objectsStorages.map((objectsStorage) => (
        <Accordion key={objectsStorage.name}>
          <AccordionSummary
            expandIcon={<ExpandMoreIcon />}
          >
            <Grid
              container
              direction="column"
            >
              <Typography>{objectsStorage.name}</Typography>
              <Button
                variant="contained"
                sx={{ mb: 2, mt: 2, alignSelf: 'center' }}
                component="label"
              >
                <Typography>Upload file</Typography>
                <FileUpload />
                <input
                  type="file"
                  hidden
                  onChange={(e) => uploadObject(e, objectsStorage.name)}
                />
              </Button>
            </Grid>
          </AccordionSummary>
          <AccordionDetails>
            <ObjectStorageContent objectStorageName={objectsStorage.name} />
          </AccordionDetails>
        </Accordion>
      ))}
      <Snackbar
        open={showUploadObjectStatus}
        autoHideDuration={6000}
        onClose={closeUploadObjectStatus}
      >
        <Alert
          onClose={closeUploadObjectStatus}
          severity={uploadObjectStatus.status}
          sx={{ width: '100%' }}
        >
          {uploadObjectStatus.message}
        </Alert>
      </Snackbar>
    </>
  );
};

export { ObjectStorageList };
