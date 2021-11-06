import { AccessLog } from './model';

export interface Filters {
  method?: string,
  userLogin?: string,
  from?: Date,
  to?: Date
}

function queryParam(name: string, param: unknown | undefined): string {
  return param ? `${name}=${param}` : '';
}

export function getAccessLogs(filters: Filters): Promise<AccessLog[]> {
  const queryParams = [
    queryParam('method', filters.method),
    queryParam('userLogin', filters.userLogin),
    queryParam('from', filters.from?.toISOString()),
    queryParam('to', filters.to?.toISOString()),
  ].filter((x) => x.length !== 0).join('&');

  return fetch(`/api/v1/accessLogs?${queryParams}`)
    .then((response) => response.json());
}
