import { ObjectStorage } from './model';

function getObjectStorages(): Promise<Array<ObjectStorage>> {
  return fetch('/api/v1/s3').then((response) => response.json());
}

export { getObjectStorages };
