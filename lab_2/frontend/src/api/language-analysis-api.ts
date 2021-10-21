function languageAnalysis(
  text: string,
): Promise<string> {
  return fetch('/api/v1/comprehend/language',
    {
      method: 'POST',
      body: JSON.stringify({ text }),
    }).then((response) => response.json());
}

function sentimentAnalysis(
  text: string,
): Promise<string> {
  return fetch('/api/v1/comprehend/sentiment',
    {
      method: 'POST',
      body: JSON.stringify({ text }),
    }).then((response) => response.json());
}

export { languageAnalysis, sentimentAnalysis };
