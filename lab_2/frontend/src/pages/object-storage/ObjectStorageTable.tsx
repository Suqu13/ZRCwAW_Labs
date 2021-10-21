import React, { useEffect, useState } from 'react';
import {
  Typography,
  Paper,
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableRow,
  TableContainer,
} from '@mui/material';
import { useSnackbar } from 'notistack';
import { getObjectStorages, uploadFile } from '../../api/object-storage-api';
import { ObjectStorage } from '../../api/model';
import { ObjectStorageRow } from './ObjectStorageRow';
import { LoadingState } from '../../components/LoadingState';
import { ErrorState } from '../../components/ErrorState';
import { EmptyState } from '../../components/EmptyState';

const objectStoragesHook = (): {
  objectsStorages: Array<ObjectStorage>,
  loading: boolean,
  error: boolean,
  fetchObjectStorages: () => void
  uploadObjectItem: (file: File, objectStorageName: string) => Promise<void>,
} => {
  const [objectsStorages, setObjectsStorages] = useState<Array<ObjectStorage>>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(false);
  const { enqueueSnackbar } = useSnackbar();

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

  const uploadObjectItem = (
    file: File,
    objectStorageName: string,
  ): Promise<void> => {
    const formData = new FormData();
    formData.append('File', file);
    return uploadFile(formData, objectStorageName, file.name)
      .then(() => {
        enqueueSnackbar(
          `Uploading ${file.name} file to ${objectStorageName} completed succesfully!`,
          { variant: 'success' },
        );
      }).catch(() => {
        enqueueSnackbar(
          `Uploading ${file.name} file to ${objectStorageName} completed unsuccesfully!`,
          { variant: 'error' },
        );
      });
  };

  return {
    objectsStorages,
    loading,
    error,
    fetchObjectStorages,
    uploadObjectItem,
  };
};

const ObjectStorageTable: React.FunctionComponent = () => {
  const {
    objectsStorages,
    loading,
    error,
    fetchObjectStorages,
    uploadObjectItem,
  } = objectStoragesHook();

  const content = (): JSX.Element => {
    if (error) {
      return (
        <ErrorState onClick={fetchObjectStorages} />
      );
    }
    if (loading) {
      return (
        <LoadingState />
      );
    }
    if (objectsStorages.length === 0) {
      return (
        <EmptyState onClick={fetchObjectStorages} />
      );
    }
    return (
      <TableContainer>
        <Typography component="h2" variant="h6" color="primary" gutterBottom>
          S3 - Simple Strorage Service
        </Typography>
        <Table size="small">
          <TableHead>
            <TableRow>
              <TableCell />
              <TableCell>
                <b>
                  Name
                </b>
              </TableCell>
              <TableCell />
            </TableRow>
          </TableHead>
          <TableBody>
            {objectsStorages.map((row) => (
              <ObjectStorageRow
                key={row.name}
                row={{ id: row.name, name: row.name }}
                onUploadClick={
                  (file: File) => uploadObjectItem(file, row.name)
                }
              />
            ))}
          </TableBody>
        </Table>
      </TableContainer>
    );
  };

  return (
    <>
      <Paper variant="outlined" sx={{ my: { xs: 3, md: 6 }, p: { xs: 2, md: 3 } }}>
        {content()}
      </Paper>
    </>
  );
};

export { ObjectStorageTable };
