import React, { FunctionComponent, useState } from 'react';
import {
  IconButton,
  TableCell,
  TableRow,
} from '@mui/material';
import PlayCircleFilledIcon from '@mui/icons-material/PlayCircleFilled';
import StopCircleIcon from '@mui/icons-material/StopCircle';
import RefreshIcon from '@mui/icons-material/Refresh';
import { useSnackbar } from 'notistack';
import { VirtualMachine } from '../../api/model';
import {
  getVirtualMachine,
  startVirtualMachine,
  stopVirtualMachine,
} from '../../api/virtual-machine-api';

const virtualMachineItemHook = (initialVirtualMachine : VirtualMachine): {
  virtualMachine: VirtualMachine,
  refresh: () => void
  start: () => void
  stop: () => void
} => {
  const [virtualMachine, setVirtualMachine] = useState(initialVirtualMachine);
  const { enqueueSnackbar } = useSnackbar();

  const refresh = (): void => {
    getVirtualMachine(virtualMachine.instanceId)
      .then((fetchedVirtualMachine) => {
        setVirtualMachine(fetchedVirtualMachine);
        enqueueSnackbar(
          `Refreshing ${virtualMachine.instanceId} EC2 completed succesfully!`,
          { variant: 'success' },
        );
      })
      .catch(() => enqueueSnackbar(
        `Refreshing ${virtualMachine.instanceId} EC2 completed unsuccesfully!`,
        { variant: 'error' },
      ));
  };

  const start = (): void => {
    startVirtualMachine(virtualMachine.instanceId)
      .then(() => {
        enqueueSnackbar(
          `Starting ${virtualMachine.instanceId} EC2 completed succesfully!`,
          { variant: 'success' },
        );
        refresh();
      })
      .catch(() => enqueueSnackbar(
        `Starting ${virtualMachine.instanceId} EC2 completed unsuccesfully!`,
        { variant: 'error' },
      ));
  };

  const stop = (): void => {
    stopVirtualMachine(virtualMachine.instanceId)
      .then(() => {
        enqueueSnackbar(
          `Stopping ${virtualMachine.instanceId} EC2 completed succesfully!`,
          { variant: 'success' },
        );
        refresh();
      })
      .catch(() => enqueueSnackbar(
        `Stopping ${virtualMachine.instanceId} EC2 completed unsuccesfully!`,
        { variant: 'error' },
      ));
  };

  return {
    virtualMachine,
    refresh,
    start,
    stop,
  };
};

interface Props {
  row: VirtualMachine
}

const VirtualMachineItem: FunctionComponent<Props> = ({ row }) => {
  const {
    virtualMachine,
    refresh,
    start,
    stop,
  } = virtualMachineItemHook(row);

  return (
    <TableRow>
      <TableCell>
        {virtualMachine.instanceId}
      </TableCell>
      <TableCell>
        {virtualMachine.stateName}
      </TableCell>
      <TableCell width="10%">
        <IconButton onClick={() => refresh()}>
          <RefreshIcon />
        </IconButton>
      </TableCell>
      <TableCell width="10%">
        <IconButton
          size="small"
        >
          <IconButton onClick={() => start()}>
            <PlayCircleFilledIcon />
          </IconButton>
        </IconButton>
      </TableCell>
      <TableCell width="10%">
        <IconButton
          size="small"
        >
          <IconButton onClick={() => stop()}>
            <StopCircleIcon />
          </IconButton>
        </IconButton>
      </TableCell>
    </TableRow>
  );
};

export { VirtualMachineItem };
