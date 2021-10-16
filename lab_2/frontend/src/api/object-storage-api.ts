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

function uploadFile(
  formData: FormData,
  storageName: string,
  objectKey: string,
): Promise<ObjectStorageItem> {
  return fetch(`/api/v1/s3/${storageName}/${objectKey}`,
    {
      method: 'POST',
      body: formData,
    }).then((response) => response.json());
}

export {
  getObjectStorages, getObjectStorageItems, downloadFile, uploadFile,
};
