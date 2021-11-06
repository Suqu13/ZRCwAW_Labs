import { AccessLog } from './model';

export function getAccessLogs(): Promise<AccessLog[]> {
  return fetch('/api/v1/accessLogs')
    .then((response) => response.json());
}
