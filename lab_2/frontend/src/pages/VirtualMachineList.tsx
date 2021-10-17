import React, { useEffect, useState } from 'react';
import {
  Typography,
  CircularProgress,
  Grid,
  Button,
  List,
} from '@mui/material';
import { getVirtualMachines } from '../api/virtual-machine-api';
import { VirtualMachine } from '../api/model';
import { VirtualMachineItem } from './VirtualMachineItem';

const virtualMachinesHook = (): {
  virtualMachines: Array<VirtualMachine>,
  loading: boolean,
  error: boolean,
  fetchVirtualMachines: () => void
} => {
  const [virtualMachines, setVirtualMachines] = useState<Array<VirtualMachine>>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<boolean>(false);

  const fetchVirtualMachines = (): void => {
    if (!loading) setLoading(true);
    if (error) setError(false);
    getVirtualMachines()
      .then((x) => {
        setVirtualMachines(x);
        setLoading(false);
      }).catch(() => setError(true));
  };

  useEffect((): void => {
    fetchVirtualMachines();
  }, []);

  return {
    virtualMachines,
    loading,
    error,
    fetchVirtualMachines,
  };
};

const VirtualMachineList = (): JSX.Element => {
  const {
    virtualMachines,
    loading,
    error,
    fetchVirtualMachines,
  } = virtualMachinesHook();

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
        <Button variant="outlined" color="error" onClick={fetchVirtualMachines}>
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
      {virtualMachines.map((virtualMachine) => (
        <List>
          <VirtualMachineItem injectedVirtualMachine={virtualMachine} />
        </List>
      ))}
    </>
  );
};

export { VirtualMachineList };
