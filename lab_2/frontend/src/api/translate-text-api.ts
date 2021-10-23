function translateText(
  text: string,
  sourceLanguage: string,
  targetLanguage: string,
): Promise<{ text: string }> {
  return fetch('/api/v1/translate',
    {
      method: 'POST',
      body: JSON.stringify({
        text, sourceLanguage, targetLanguage,
      }),
    }).then((response) => {
    if (response.ok) {
      return response.json();
    }
    throw new Error(response.statusText);
  });
}

function translateFile(
  file: File,
  sourceLanguage: string,
  targetLanuage: string,
): Promise<Blob> {
  const formData = new FormData();
  formData.append('File', file);
  return fetch(`/api/v1/translate/sourceLanguage/${sourceLanguage}/targetLanguage/${targetLanuage}`,
    {
      method: 'POST',
      body: formData,
    }).then((response) => {
    if (response.ok) {
      return response.blob();
    }
    throw new Error(response.statusText);
  });
}

export {
  translateText,
  translateFile,
};
