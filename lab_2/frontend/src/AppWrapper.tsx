import React, { useContext, useEffect, useState } from 'react';
import AppBar from '@mui/material/AppBar';
import Toolbar from '@mui/material/Toolbar';
import Typography from '@mui/material/Typography';
import {
  Container,
  Link,
  Grid,
  Paper,
  TextField,
  Button,
} from '@mui/material';
import {
  BrowserRouter as Router,
  Link as RouterLink,
  Route,
  Redirect,
  Switch,
} from 'react-router-dom';
import LogoutIcon from '@mui/icons-material/Logout';
import { useSnackbar } from 'notistack';
import { signIn as signIn_ } from './api/authentication-api';
import { ObjectStorageTable } from './pages/object-storage/ObjectStorageTable';
import { LanguageAnalysisPage } from './pages/language-analysis/LanguageAnalysisPage';
import { VirtualMachineTable } from './pages/virtual-machine/VirtualMachineTable';
import { ReadTextPage } from './pages/read-text/ReadTextPage';
import { TranslateTextPage } from './pages/translate-text/TranslateText';
import { UserContext } from './UserContext';
import { AccessLogsPage } from './pages/access-logs/AccessLogsPage';

const getCookie = (cookieName: string): string => {
  const name = `${cookieName}=`;
  const decodedCookie = decodeURIComponent(document.cookie);
  const ca = decodedCookie.split(';');
  for (let i = 0; i < ca.length; i += 1) {
    let c = ca[i];
    while (c.charAt(0) === ' ') {
      c = c.substring(1);
    }
    if (c.indexOf(name) === 0) {
      return c.substring(name.length, c.length);
    }
  }
  return '';
};

const setCookie = (cookieName: string, cookieValue: string, expiresInDays: number): void => {
  const d = new Date();
  d.setTime(d.getTime() + (expiresInDays * 24 * 60 * 60 * 1000));
  const expires = `expires=${d.toUTCString()}`;
  document.cookie = `${cookieName}=${cookieValue};${expires};path=/`;
};

const AppWrapper: React.FunctionComponent<{}> = () => {
  const { userName, setUserName } = useContext(UserContext);
  const [login, setLogin] = useState('');
  const [password, setPassword] = useState('');

  useEffect(() => {
    const userNameCookie: string = getCookie('userNameCookie');
    if (userNameCookie !== '' && userNameCookie.length > 0) {
      setUserName(userNameCookie);
    }
  }, [setUserName]);

  const handleChangeLogin = (event: React.ChangeEvent<HTMLInputElement>): void => {
    setLogin(event.target.value);
  };

  const handleChangePassword = (event: React.ChangeEvent<HTMLInputElement>): void => {
    setPassword(event.target.value);
  };

  const checkIfFormIsCorrectlyFilled = (): boolean => {
    return !!login && !!password;
  };

  const { enqueueSnackbar } = useSnackbar();
  const signIn = (): void => {
    if (checkIfFormIsCorrectlyFilled()) {
      signIn_(login, password).then((response) => {
        if (response === true) {
          enqueueSnackbar(
            'Signed in successfuly',
            { variant: 'success' },
          );
          setUserName(login);
          setCookie('userNameCookie', login, 1);
        }
      })
        .catch(() => {
          enqueueSnackbar(
            'Incorrect login or password',
            { variant: 'error' },
          );
        });
    }
  };

  const signOut = (): void => {
    setUserName('');
    setCookie('userNameCookie', '', 1);
    enqueueSnackbar(
      'Signed out successfuly',
      { variant: 'success' },
    );
  };

  const loginView = (
    <Container component="main">
      <Grid
        container
        spacing={0}
        direction="column"
        alignItems="center"
        justifyContent="center"
        sx={{ minHeight: '100vh' }}
      >
        <Grid item>
          <Paper variant="outlined" sx={{ my: { xs: 3, md: 6 }, p: { xs: 2, md: 3 } }}>
            <form onSubmit={(e) => {
              e.preventDefault();
              signIn();
            }}
            >
              <Grid
                container
                rowSpacing={2}
                direction="column"
                alignItems="center"
              >
                <Grid item xs={4}>
                  <Typography component="h2" variant="h6" color="primary" gutterBottom>
                    Sign in to app
                  </Typography>
                </Grid>
                <Grid item xs={4}>
                  <TextField
                    id="outlined-basic"
                    label="Login"
                    value={login}
                    placeholder="Provide a login"
                    onChange={handleChangeLogin}
                    fullWidth
                  />
                </Grid>
                <Grid item xs={4}>
                  <TextField
                    id="outlined-basic"
                    label="Password"
                    value={password}
                    type="password"
                    placeholder="Provide a password"
                    onChange={handleChangePassword}
                    fullWidth
                  />
                </Grid>
                <Grid item xs={4}>
                  <Button
                    type="submit"
                    variant="contained"
                    disabled={!checkIfFormIsCorrectlyFilled()}
                    fullWidth
                  >
                    Sign in
                  </Button>
                </Grid>
              </Grid>
            </form>
          </Paper>
        </Grid>
      </Grid>
    </Container>
  );

  const appView = (
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
          <Typography variant="h6" color="inherit" noWrap sx={{ flexGrow: 1 }}>
            User: {userName}
          </Typography>
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
          <Link
            component={RouterLink}
            variant="button"
            color="text.primary"
            to="polly"
            sx={{ my: 1, mx: 1.5 }}
          >
            Polly
          </Link>
          <Link
            component={RouterLink}
            variant="button"
            color="text.primary"
            to="translate"
            sx={{ my: 1, mx: 1.5 }}
          >
            Translate
          </Link>
          <Link
            component={RouterLink}
            variant="button"
            color="text.primary"
            to="accessLogs"
            sx={{ my: 1, mx: 1.5 }}
          >
            ACCESS LOGS
          </Link>
          <Button
            sx={{ ml: '1rem' }}
            type="button"
            variant="contained"
            onClick={signOut}
          >
            <LogoutIcon sx={{ ml: 1 }} />
          </Button>
        </Toolbar>
      </AppBar>

      <Container component="main" maxWidth="sm" sx={{ mb: 4 }}>
        <Switch>
          <Route exact path="/">
            <Redirect to="/s3" />
          </Route>
          <Route path="/s3">
            <ObjectStorageTable />
          </Route>
          <Route path="/ec2">
            <VirtualMachineTable />
          </Route>
          <Route path="/comprehend">
            <LanguageAnalysisPage />
          </Route>
          <Route path="/polly">
            <ReadTextPage />
          </Route>
          <Route path="/translate">
            <TranslateTextPage />
          </Route>
          <Route path="/accessLogs">
            <AccessLogsPage />
          </Route>
        </Switch>
      </Container>
    </Router>
  );

  const appContent = userName === '' ? loginView : appView;

  return (
    <>
      {appContent}
    </>
  );
};

export { AppWrapper };
