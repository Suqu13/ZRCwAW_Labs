import React, { useState } from 'react';
import {
  Box, Divider,
  Paper, TextField, Typography,
} from '@mui/material';
import Button from '@mui/material/Button';
import JSONPretty from 'react-json-pretty';
import 'react-json-pretty/themes/monikai.css';
import { languageAnalysis, sentimentAnalysis } from '../../api/language-analysis-api';

const LanguageAnalysisPage: React.FunctionComponent = () => {
  const [text, setText] = useState<string>('Provide text in any language.');
  const [result, setResult] = useState<string | undefined>(undefined);

  const handleChange = (event: React.ChangeEvent<HTMLInputElement>): void => {
    setText(event.target.value);
  };

  const analyseLanguage = (): void => {
    languageAnalysis(text).then((res) => setResult(res));
  };

  const analyseSentiment = (): void => {
    sentimentAnalysis(text).then((res) => setResult(res));
  };

  const content = (): JSX.Element => (
    <>
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
        <Button variant="outlined" sx={{ marginRight: '4px' }} onClick={analyseLanguage}>Language Analyis</Button>
        <Button variant="outlined" onClick={analyseSentiment}>Sentiment Analysis</Button>
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
    </>
  );

  return (
    <>
      <Paper variant="outlined" sx={{ my: { xs: 3, md: 6 }, p: { xs: 2, md: 3 } }}>
        {content()}
      </Paper>
    </>
  );
};

export { LanguageAnalysisPage };
