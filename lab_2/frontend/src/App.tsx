import React from 'react';
import CssBaseline from '@mui/material/CssBaseline';
import AppBar from '@mui/material/AppBar';
import Toolbar from '@mui/material/Toolbar';
import Typography from '@mui/material/Typography';
import {
  Container, createTheme, GlobalStyles, Link, ThemeProvider,
} from '@mui/material';
import {
  BrowserRouter as Router, Link as RouterLink, Route, Switch,
} from 'react-router-dom';
import { SnackbarProvider } from 'notistack';
import { ObjectStorageTable } from './pages/object-storage/ObjectStorageTable';
import { LanguageAnalysisPage } from './pages/language-analysis/LanguageAnalysisPage';

const App = (): JSX.Element => {
  const theme = createTheme();

  return (
    <SnackbarProvider maxSnack={3}>
      <ThemeProvider theme={theme}>
        <CssBaseline />
        <GlobalStyles
          styles={{
            ul: { margin: 0, padding: 0, listStyle: 'none' },
            a: { textDecoration: 'none !important' },
          }}
        />
        <CssBaseline />
        <Router>
          <AppBar
            position="static"
            color="default"
            elevation={0}
            sx={{ borderBottom: (t) => `1px solid ${t.palette.divider}` }}
          >
            <Toolbar sx={{ flexWrap: 'wrap' }}>
              <Typography variant="h6" color="inherit" noWrap sx={{ flexGrow: 1 }}>
                ZRCWaw Lab
              </Typography>
              <nav>
                <Link
                  component={RouterLink}
                  variant="button"
                  color="text.primary"
                  to="s3"
                  sx={{ my: 1, mx: 1.5 }}
                >
                  S3
                </Link>
                <Link
                  component={RouterLink}
                  variant="button"
                  color="text.primary"
                  to="ec2"
                  sx={{ my: 1, mx: 1.5 }}
                >
                  EC2
                </Link>
                <Link
                  component={RouterLink}
                  variant="button"
                  color="text.primary"
                  to="comprehend"
                  sx={{ my: 1, mx: 1.5 }}
                >
                  Comprehend
                </Link>
              </nav>
            </Toolbar>
          </AppBar>

          <Container component="main" maxWidth="sm" sx={{ mb: 4 }}>
            <Switch>
              <Route path="/s3">
                <ObjectStorageTable />
              </Route>
              <Route exact path="/ec2">
                <p>ec2</p>
              </Route>
              <Route exact path="/comprehend">
                <LanguageAnalysisPage />
              </Route>
            </Switch>
          </Container>
        </Router>
      </ThemeProvider>
    </SnackbarProvider>
  );
};

export default App;
