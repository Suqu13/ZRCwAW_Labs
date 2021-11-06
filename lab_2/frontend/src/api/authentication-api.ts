function signIn(login: string, password: string): Promise<boolean> {
  return fetch('/api/v1/login', {
    method: 'POST',
    body: JSON.stringify({
      login, password,
    }),
  }).then((response) => {
    if (response.ok) {
      return true;
    }
    throw new Error(response.statusText);
  });
}

export {
  signIn,
};
