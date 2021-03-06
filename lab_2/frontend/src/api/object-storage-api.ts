import { ObjectStorage, ObjectItem } from './model';

function getObjectStorages(): Promise<Array<ObjectStorage>> {
  return fetch('/api/v1/s3').then((response) => response.json());
}

function getObjectStorageItems(storageName: string): Promise<Array<ObjectItem>> {
  return fetch(`/api/v1/s3/${storageName}`).then((response) => response.json());
}

function deleteObjectStorageItem(storageName: string, objectKey: string): Promise<boolean> {
  return fetch(`/api/v1/s3/${storageName}/${objectKey}`, { method: 'DELETE' })
    .then((response) => response.status >= 200 && response.status < 300);
}

function downloadObjectStorageItem(storageName: string, objectKey: string): Promise<Blob> {
  return fetch(`/api/v1/s3/${storageName}/${objectKey}`).then((response) => response.blob());
}

function uploadFile(
  formData: FormData,
  storageName: string,
  objectKey: string,
): Promise<ObjectItem> {
  return fetch(`/api/v1/s3/${storageName}/${objectKey}`,
    {
      method: 'POST',
      body: formData,
    }).then((response) => response.json());
}

export {
  getObjectStorages,
  getObjectStorageItems,
  downloadObjectStorageItem,
  uploadFile,
  deleteObjectStorageItem,
};
