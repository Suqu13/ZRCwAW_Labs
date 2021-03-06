import { ImageLabel, ImageText } from './model';

function getLabels(storageName: string, imageName: string): Promise<ImageLabel[]> {
  return fetch(`/api/v1/rekognition/${storageName}/${imageName}/labels`)
    .then((response) => response.json());
}

function getTexts(storageName: string, imageName: string): Promise<ImageText[]> {
  return fetch(`/api/v1/rekognition/${storageName}/${imageName}/texts`)
    .then((response) => response.json());
}

export {
  getLabels, getTexts,
};
