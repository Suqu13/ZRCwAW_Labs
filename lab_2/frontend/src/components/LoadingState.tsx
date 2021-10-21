import React from 'react';
import { Grid, Typography, CircularProgress } from '@mui/material';

const LoadingState: React.FunctionComponent = () => (
  <Grid
    container
    direction="column"
    justifyContent="center"
    alignItems="center"
    sx={{ mb: 2, mt: 2.5 }}
  >
    <CircularProgress />
    <Typography variant="h6">
      Loading!
    </Typography>
  </Grid>
);

export { LoadingState };
