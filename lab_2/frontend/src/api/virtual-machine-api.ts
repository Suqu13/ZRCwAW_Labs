import { VirtualMachine } from './model';

function getVirtualMachines(): Promise<Array<VirtualMachine>> {
  return fetch('/api/v1/ec2').then((response) => response.json());
}

function getVirtualMachine(instanceId: string): Promise<VirtualMachine> {
  return fetch(`/api/v1/ec2/${instanceId}`).then((response) => response.json());
}

function startVirtualMachine(instanceId: string): Promise<string> {
  return fetch(`/api/v1/ec2/${instanceId}/start`).then((response) => response.json());
}

function stopVirtualMachine(instanceId: string): Promise<string> {
  return fetch(`/api/v1/ec2/${instanceId}/stop`).then((response) => response.json());
}

export {
  getVirtualMachines, getVirtualMachine, startVirtualMachine, stopVirtualMachine,
};
