import React, { useState } from 'react';
import { SnackbarProvider } from 'notistack';
import { AppWrapper } from './AppWrapper';
import { UserContext } from './UserContext';

const App = (): JSX.Element => {
  const [userName, setUserName] = useState('');
  const value = { userName, setUserName };

  return (
    <SnackbarProvider maxSnack={3}>
      <UserContext.Provider value={value}>
        <AppWrapper />
      </UserContext.Provider>
    </SnackbarProvider>
  );
};

export default App;
