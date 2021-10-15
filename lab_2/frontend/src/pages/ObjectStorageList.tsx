import React, { useEffect, useState } from 'react';
import {
  Accordion,
  AccordionDetails,
  AccordionSummary,
  Typography,
  CircularProgress,
  Grid,
  Button,
} from '@mui/material';
import ExpandMoreIcon from '@mui/icons-material/ExpandMore';
import { getObjectStorages } from '../api/object-storage-api';
import { ObjectStorage } from '../api/model';
import { ObjectStorageContent } from './ObjectStorageContent';

const objectStoragesHook = (): {
  objectsStorages: Array<ObjectStorage>,
  loading: boolean,
  error: boolean,
  fetchObjectStorages: () => void
} => {
  const [objectsStorages, setObjectsStorages] = useState<Array<ObjectStorage>>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<boolean>(false);

  // TODO: rename
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

  return {
    objectsStorages,
    loading,
    error,
    fetchObjectStorages,
  };
};

const ObjectStorageList = (): JSX.Element => {
  const {
    objectsStorages,
    loading,
    error,
    fetchObjectStorages,
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
            aria-controls="panel1a-content"
          >
            <Typography>{objectsStorage.name}</Typography>
          </AccordionSummary>
          <AccordionDetails>
            <ObjectStorageContent objectStorageName={objectsStorage.name} />
          </AccordionDetails>
        </Accordion>
      ))}
    </>
  );
};

export { ObjectStorageList };
