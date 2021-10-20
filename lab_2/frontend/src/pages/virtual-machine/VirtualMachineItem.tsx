import React, { FunctionComponent, useState } from 'react';
import {
  IconButton, ListItem, ListItemText,
} from '@mui/material';
import PlayCircleFilledIcon from '@mui/icons-material/PlayCircleFilled';
import StopCircleIcon from '@mui/icons-material/StopCircle';
import RefreshIcon from '@mui/icons-material/Refresh';
import { VirtualMachine } from '../../api/model';
import { getVirtualMachine, startVirtualMachine, stopVirtualMachine } from '../../api/virtual-machine-api';

const virtualMachineItemHook = (injectedVirtualMachine: VirtualMachine): {
  virtualMachine: VirtualMachine,
  refetch: () => void
} => {
  const [virtualMachine, setVirtualMachine] = useState<VirtualMachine>(injectedVirtualMachine);

  const refetch = (): void => {
    getVirtualMachine(virtualMachine.instanceId)
      .then((fetchedVirtualMachine) => {
        setVirtualMachine(fetchedVirtualMachine);
      });
  };

  return {
    virtualMachine,
    refetch,
  };
};

interface Props {
  injectedVirtualMachine: VirtualMachine
}

const startMachine = (
  virtualMachine: VirtualMachine,
  refetch: () => void,
): Promise<void> => startVirtualMachine(virtualMachine.instanceId)
  .then(() => refetch());

const stopMachine = (
  virtualMachine: VirtualMachine,
  refetch: () => void,
): Promise<void> => stopVirtualMachine(virtualMachine.instanceId)
  .then(() => refetch());

const VirtualMachineItem: FunctionComponent<Props> = ({ injectedVirtualMachine }) => {
  const {
    virtualMachine,
    refetch,
  } = virtualMachineItemHook(injectedVirtualMachine);

  return (
    <>
      <ListItem
        key={virtualMachine.instanceId}
      >
        <ListItemText primary={virtualMachine.instanceId} secondary={virtualMachine.stateName} />
        <IconButton onClick={() => refetch()}>
          <RefreshIcon />
        </IconButton>
        <IconButton onClick={() => startMachine(virtualMachine, refetch)}>
          <PlayCircleFilledIcon />
        </IconButton>
        <IconButton onClick={() => stopMachine(virtualMachine, refetch)}>
          <StopCircleIcon />
        </IconButton>
      </ListItem>
    </>
  );
};

export { VirtualMachineItem };
