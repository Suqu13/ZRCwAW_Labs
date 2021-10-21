import React from 'react';
import { Grid, Typography, Button } from '@mui/material';

interface Props {
  onClick: () => void
}

const ErrorState: React.FunctionComponent<Props> = ({ onClick }) => (
  <Grid
    container
    direction="column"
    justifyContent="center"
    alignItems="center"
    sx={{ mb: 2.5, mt: 2 }}

  >
    <Typography variant="h6" gutterBottom>
      Unlucky, some error occured!
    </Typography>
    <Button variant="outlined" color="error" onClick={onClick}>
      Reload
    </Button>
  </Grid>
);

export { ErrorState };
