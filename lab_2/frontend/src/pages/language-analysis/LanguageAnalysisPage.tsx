import React, { useState } from 'react';
import {
  Box,
  Divider,
  Grid,
  Paper,
  TextField,
  Typography,
} from '@mui/material';
import Button from '@mui/material/Button';
import JSONPretty from 'react-json-pretty';
import 'react-json-pretty/themes/monikai.css';
import { useSnackbar } from 'notistack';
import { languageAnalysis, sentimentAnalysis } from '../../api/language-analysis-api';

const languageAnalyseHook = (): {
  analyseLanguage: (text: string) => Promise<void>
  analyseSentiment: (text: string) => Promise<void>
  result?: string
} => {
  const [result, setResult] = useState<string>();
  const { enqueueSnackbar } = useSnackbar();

  const analyseLanguage = (text: string): Promise<void> => languageAnalysis(text)
    .then((res) => {
      enqueueSnackbar(
        'Language analyse completed succesfully!',
        { variant: 'success' },
      );
      setResult(res);
    })
    .catch(() => {
      enqueueSnackbar(
        'Language analyse completed unsuccesfully!',
        { variant: 'error' },
      );
    });

  const analyseSentiment = (text: string): Promise<void> => sentimentAnalysis(text)
    .then((res) => {
      enqueueSnackbar(
        'Sentiment analyse completed succesfully!',
        { variant: 'success' },
      );
      setResult(res);
    })
    .catch(() => {
      enqueueSnackbar(
        'Sentiment analyse completed unsuccesfully!',
        { variant: 'error' },
      );
    });

  return {
    analyseLanguage,
    analyseSentiment,
    result,
  };
};

const LanguageAnalysisPage: React.FunctionComponent = () => {
  const [text, setText] = useState<string>();
  const {
    analyseLanguage,
    analyseSentiment,
    result,
  } = languageAnalyseHook();

  const handleChange = (event: React.ChangeEvent<HTMLInputElement>): void => {
    setText(event.target.value);
  };

  return (
    <Paper variant="outlined" sx={{ my: { xs: 3, md: 6 }, p: { xs: 2, md: 3 } }}>
      <Typography component="h2" variant="h6" color="primary" gutterBottom>
        Comprehend - Language Analysis
      </Typography>
      <Grid container rowSpacing={2}>
        <Grid item container>
          <TextField
            id="outlined-multiline-static"
            label="Text"
            multiline
            rows={4}
            value={text}
            placeholder="Provide text in any language..."
            onChange={handleChange}
            fullWidth
          />
        </Grid>
        <Grid item container direction="row" columnSpacing={2}>
          <Grid item xs={6}>
            <Button variant="outlined" onClick={() => text && analyseLanguage(text)} fullWidth>Language Analyis</Button>
          </Grid>
          <Grid item xs={6}>
            <Button variant="outlined" onClick={() => text && analyseSentiment(text)} fullWidth>Sentiment Analysis</Button>
          </Grid>
        </Grid>
        <Grid item xs={12}>
          <Divider />
        </Grid>
        <Grid item xs={12}>
          <Typography component="h4" variant="h6" color="primary" gutterBottom>
            Result
          </Typography>
          <Box sx={{
            padding: '8px', border: '1px dashed grey', width: '100%', borderRadius: '4px',
          }}
          >
            <JSONPretty mainStyle="background:none" data={result} />
          </Box>
        </Grid>
      </Grid>
    </Paper>
  );
};

export { LanguageAnalysisPage };
