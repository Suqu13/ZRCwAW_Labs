import React from 'react';
import { Grid, Typography, Button } from '@mui/material';

interface Props {
  onClick: () => void
}

const EmptyState: React.FunctionComponent<Props> = ({ onClick }) => (
  <Grid
    container
    direction="column"
    justifyContent="center"
    alignItems="center"
    sx={{ mb: 2.5, mt: 2 }}
  >
    <Typography variant="h6" gutterBottom>
      There is no data to display!
    </Typography>
    <Button variant="outlined" color="info" onClick={onClick}>
      Reload
    </Button>
  </Grid>
);

export { EmptyState };
