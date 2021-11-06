import { createContext } from 'react';

const UserContext = createContext({
  userName: '',
  // eslint's rule no-unused-vars forces me to do console.log(a)
  setUserName: (a: string) => { console.log(a); },
});

export { UserContext };
