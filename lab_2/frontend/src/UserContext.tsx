import { createContext } from 'react';

interface UserContextProps {
  userName: string,
  setUserName: (a: string) => void
}

const UserContext = createContext<UserContextProps>({
  userName: '',
  setUserName: () => {},
});

export { UserContext };
