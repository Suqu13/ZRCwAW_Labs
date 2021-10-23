function downloadTextAudio(text: string, languageCode: string): Promise<Blob> {
  return fetch('/api/v1/read', {
    method: 'POST',
    body: JSON.stringify({
      text,
      language: languageCode,
    }),
  }).then((response) => {
    if (response.ok) return response.blob();
    throw new Error(response.statusText);
  });
}

export { downloadTextAudio };
