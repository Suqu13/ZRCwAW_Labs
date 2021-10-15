import { ObjectStorage, ObjectStorageItem } from './model';

function getObjectStorages(): Promise<Array<ObjectStorage>> {
  return fetch('/api/v1/s3').then((response) => response.json());
}

function getObjectStorageItems(storageName: string): Promise<Array<ObjectStorageItem>> {
  return fetch(`/api/v1/s3/${storageName}`).then((response) => response.json());
}

function downloadFile(storageName: string, objectKey: string): Promise<Blob> {
  return fetch(`/api/v1/s3/${storageName}/${objectKey}`).then((response) => response.blob());
}

export { getObjectStorages, getObjectStorageItems, downloadFile };
