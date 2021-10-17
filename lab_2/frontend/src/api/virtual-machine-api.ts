import { VirtualMachine } from './model';

function getVirtualMachines(): Promise<Array<VirtualMachine>> {
  return fetch('/api/v1/ec2').then((response) => response.json());
}

export {
  getVirtualMachines,
};
