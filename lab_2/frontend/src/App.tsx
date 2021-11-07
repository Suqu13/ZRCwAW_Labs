import React, { useState } from 'react';
import { SnackbarProvider } from 'notistack';
import DateAdapter from '@mui/lab/AdapterDateFns';
import { LocalizationProvider } from '@mui/lab';
import CssBaseline from '@mui/material/CssBaseline';
import {
  createTheme,
  GlobalStyles,
  ThemeProvider,
} from '@mui/material';
import { AppWrapper } from './AppWrapper';
import { UserContext } from './UserContext';

const App = (): JSX.Element => {
  const theme = createTheme();

  const [userName, setUserName] = useState('');
  const value = { userName, setUserName };

  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <GlobalStyles
        styles={{
          ul: { margin: 0, padding: 0, listStyle: 'none' },
          a: { textDecoration: 'none !important' },
        }}
      />
      <CssBaseline />
      <LocalizationProvider dateAdapter={DateAdapter}>
        <SnackbarProvider maxSnack={3}>

          <UserContext.Provider value={value}>
            <AppWrapper />
          </UserContext.Provider>
        </SnackbarProvider>
      </LocalizationProvider>
    </ThemeProvider>

  );
};

export default App;
