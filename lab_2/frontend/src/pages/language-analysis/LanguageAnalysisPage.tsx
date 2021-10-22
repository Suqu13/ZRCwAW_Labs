import React, { useState } from 'react';
import {
  Box,
  Divider,
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
  const [text, setText] = useState('Provide text in any language.');
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
      <TextField
        id="outlined-multiline-static"
        label="Text"
        multiline
        rows={4}
        value={text}
        onChange={handleChange}
        fullWidth
      />
      <Box sx={{ margin: '8px 0 8px 0' }}>
        <Button variant="outlined" sx={{ marginRight: '4px' }} onClick={() => analyseLanguage(text)}>Language Analyis</Button>
        <Button variant="outlined" onClick={() => analyseSentiment(text)}>Sentiment Analysis</Button>
      </Box>
      <Divider />
      <Typography component="h4" variant="h6" color="primary" gutterBottom>
        Result
      </Typography>
      <Box sx={{
        padding: '8px', marginTop: '8px', border: '1px dashed grey', width: '100%', borderRadius: '4px',
      }}
      >
        <JSONPretty data={result} />
      </Box>
    </Paper>
  );
};

export { LanguageAnalysisPage };
