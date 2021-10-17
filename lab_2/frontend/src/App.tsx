import React from 'react';
import CssBaseline from '@mui/material/CssBaseline';
import AppBar from '@mui/material/AppBar';
import Toolbar from '@mui/material/Toolbar';
import Typography from '@mui/material/Typography';
import {
  Container, createTheme, ThemeProvider, Paper,
} from '@mui/material';
import { ObjectStorageList } from './pages/ObjectStorageList';
import { VirtualMachineList } from './pages/VirtualMachineList';

const App = (): JSX.Element => {
  const theme = createTheme();

  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <AppBar
        position="absolute"
        color="default"
        elevation={0}
        sx={{
          position: 'relative',
          borderBottom: (t) => `1px solid ${t.palette.divider}`,
        }}
      >
        <Toolbar>
          <Typography variant="h6" color="inherit" noWrap>
            ZRCWaW Lab no 2
          </Typography>
        </Toolbar>
      </AppBar>
      <Container component="main" maxWidth="sm" sx={{ mb: 4 }}>
        <Paper variant="outlined" sx={{ my: { xs: 3, md: 6 }, p: { xs: 2, md: 3 } }}>
          <Typography component="h1" variant="h4" align="center">
            Object Storages
          </Typography>
          <Container sx={{ mt: 3 }}>
            <ObjectStorageList />
          </Container>
          <Typography component="h1" variant="h4" align="center" sx={{ mt: 3 }}>
            Virtual machines
          </Typography>
          <Container sx={{ mt: 3 }}>
            <VirtualMachineList />
          </Container>
        </Paper>
      </Container>
    </ThemeProvider>
  );
};

export default App;
