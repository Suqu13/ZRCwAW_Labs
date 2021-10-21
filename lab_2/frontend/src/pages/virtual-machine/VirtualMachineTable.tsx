import React, { useEffect, useState } from 'react';
import {
  Typography,
  Paper,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
} from '@mui/material';
import { getVirtualMachines } from '../../api/virtual-machine-api';
import { VirtualMachine } from '../../api/model';
import { EmptyState } from '../../components/EmptyState';
import { ErrorState } from '../../components/ErrorState';
import { LoadingState } from '../../components/LoadingState';
import { VirtualMachineItem } from './VirtualMachineItem';

const virtualMachinesHook = (): {
  virtualMachines: Array<VirtualMachine>,
  loading: boolean,
  error: boolean,
  fetchVirtualMachines: () => void
} => {
  const [virtualMachines, setVirtualMachines] = useState<Array<VirtualMachine>>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(false);

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

const VirtualMachineTable = (): JSX.Element => {
  const {
    virtualMachines,
    loading,
    error,
    fetchVirtualMachines,
  } = virtualMachinesHook();

  const content = (): JSX.Element => {
    if (error) {
      return (
        <ErrorState onClick={fetchVirtualMachines} />
      );
    }
    if (loading) {
      return (
        <LoadingState />
      );
    }
    if (virtualMachines.length === 0) {
      return (
        <EmptyState onClick={fetchVirtualMachines} />
      );
    }
    return (
      <TableContainer>
        <Typography component="h2" variant="h6" color="primary" gutterBottom>
          EC2 - Elastic Compute Cloud
        </Typography>
        <Table size="small">
          <TableHead>
            <TableRow>
              <TableCell>
                <b>
                  ID
                </b>
              </TableCell>
              <TableCell>
                <b>
                  State
                </b>
              </TableCell>
              <TableCell width="10%" />
              <TableCell width="10%" />
              <TableCell width="10%" />
            </TableRow>
          </TableHead>
          <TableBody>
            {virtualMachines.map((row) => <VirtualMachineItem key={row.instanceId} row={row} />)}
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

export { VirtualMachineTable };
