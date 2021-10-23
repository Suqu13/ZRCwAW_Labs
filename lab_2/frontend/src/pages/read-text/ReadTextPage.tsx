import React, { useState } from 'react';
import {
  Grid,
  InputLabel,
  MenuItem,
  Paper,
  Select,
  TextField,
  Typography,
  FormControl,
  SelectChangeEvent,
} from '@mui/material';
import Button from '@mui/material/Button';
import 'react-json-pretty/themes/monikai.css';
import { useSnackbar } from 'notistack';
import { supportedLanguages } from './SupportedLanguages';
import { downloadTextAudio } from '../../api/read-text-api';

const readTextHook = (): {
  startReading: (text: string, languageCode: string) => void
} => {
  const { enqueueSnackbar } = useSnackbar();

  const startReading = (text: string, languageCode: string): void => {
    downloadTextAudio(text, languageCode).then((blob) => {
      enqueueSnackbar(
        'Reading the text!',
        { variant: 'success' },
      );
      new Audio(URL.createObjectURL(blob)).play();
    }).catch(() => {
      enqueueSnackbar(
        'An error occurred while reading the text!',
        { variant: 'error' },
      );
    });
  };

  return {
    startReading,
  };
};

const ReadTextPage: React.FunctionComponent = () => {
  const [text, setText] = useState<string>();
  // eslint-disable-next-line @typescript-eslint/no-unused-vars
  const [languageCode, setLanguageCode] = useState<string>();

  const {
    startReading,
  } = readTextHook();

  const handleTextChange = (event: React.ChangeEvent<HTMLInputElement>): void => {
    setText(event.target.value);
  };

  const handleLanguageCodeChange = (event: SelectChangeEvent<string>): void => {
    setLanguageCode(event.target.value);
  };

  return (
    <Paper variant="outlined" sx={{ my: { xs: 3, md: 6 }, p: { xs: 2, md: 3 } }}>
      <Typography component="h2" variant="h6" color="primary" gutterBottom>
        Polly - Text-to-Speech Service
      </Typography>
      <form onSubmit={(e) => {
        e.preventDefault();
        if (text && languageCode) { startReading(text, languageCode); }
      }}
      >
        <Grid container rowSpacing={2}>
          <Grid item container>
            <FormControl fullWidth>
              <TextField
                label="Text"
                multiline
                rows={8}
                value={text}
                placeholder="Provide text..."
                onChange={handleTextChange}
                fullWidth
              />
            </FormControl>
          </Grid>
          <Grid
            item
            container
            columnSpacing={2}
          >
            <Grid item container xs={8}>
              <FormControl fullWidth>
                <InputLabel id="language">Language</InputLabel>
                <Select
                  labelId="language"
                  label="Language"
                  onChange={handleLanguageCodeChange}
                >
                  {
                supportedLanguages.map((supportedLanguage) => (
                  <MenuItem
                    key={supportedLanguage.code}
                    value={supportedLanguage.code}
                  >
                    {supportedLanguage.natural}
                  </MenuItem>
                ))
              }
                </Select>
              </FormControl>
            </Grid>
            <Grid
              item
              container
              xs={4}
              justifyContent="center"
            >
              <Button type="submit" disabled={!(text && languageCode)} variant="contained" fullWidth>To speach</Button>
            </Grid>
          </Grid>
        </Grid>
      </form>
    </Paper>
  );
};

export { ReadTextPage };
