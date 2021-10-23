import React, { useState } from 'react';
import {
  Box,
  FormControl,
  Grid,
  InputLabel,
  MenuItem,
  Paper,
  Select,
  SelectChangeEvent,
  TextField,
  ToggleButton,
  ToggleButtonGroup,
  Typography,
} from '@mui/material';
import Button from '@mui/material/Button';
import 'react-json-pretty/themes/monikai.css';
import { useSnackbar } from 'notistack';
import { FileUpload } from '@mui/icons-material';
import { supportedLanguages } from './SupportedLanguages';
import {
  translateText as translateText_,
  translateFile as translateFile_,
} from '../../api/translate-text-api';

const translateTextHook = (): {
  translateText: (text: string, sourceLanguage: string, targetLanguage: string) => Promise<void>
  translateFile: (file: File, sourceLanguage: string, targetLanguage: string) => Promise<void>
  result?: string
} => {
  const [result, setResult] = useState<string>();
  const { enqueueSnackbar } = useSnackbar();

  const translateText = (
    text: string,
    sourceLanguage: string,
    targetLanguage: string,
  ): Promise<void> => {
    return translateText_(text, sourceLanguage, targetLanguage)
      .then((response) => {
        setResult(response.text);
      })
      .catch(() => {
        enqueueSnackbar(
          'An error occurred while translating the text!',
          { variant: 'error' },
        );
      });
  };

  const translateFile = (
    file: File,
    sourceLanguage: string,
    targetLanguage: string,
  ): Promise<void> => {
    return translateFile_(file, sourceLanguage, targetLanguage)
      .then((blob) => saveAs(blob))
      .catch(() => {
        enqueueSnackbar(
          'An error occurred while translating the file!',
          { variant: 'error' },
        );
      });
  };

  return {
    translateText,
    translateFile,
    result,
  };
};

const TranslateTextPage: React.FunctionComponent = () => {
  const [text, setText] = useState<string>();
  const [file, setFile] = useState<File>();
  const [isText, setIsText] = useState(true);
  const [sourceLanguage, setSourceLanguage] = useState<string>();
  const [targetLanguage, setTargetLanguage] = useState<string>();
  const {
    result,
    translateText,
    translateFile,
  } = translateTextHook();

  const handleTranslationSourceChange = (): void => {
    setIsText(!isText);
    setText(undefined);
    setFile(undefined);
  };

  const checkIfFormIsCorrectlyFilled = (): boolean => {
    const isSourceProvided = !!((isText && text) || file);
    const isSourceLanguageProvided = (!!sourceLanguage);
    const isTargetLanguageProvided = (!!targetLanguage);
    return isSourceProvided && isSourceLanguageProvided && isTargetLanguageProvided;
  };

  const handleSourceLanguageChange = (event: SelectChangeEvent<string>): void => {
    setSourceLanguage(event.target.value);
  };

  const handleTargetLanguageChange = (event: SelectChangeEvent<string>): void => {
    setTargetLanguage(event.target.value);
  };

  const handleChange = (event: React.ChangeEvent<HTMLInputElement>): void => {
    setText(event.target.value);
  };

  const translate = (): void => {
    if (sourceLanguage && targetLanguage) {
      if (isText && text) {
        translateText(text, sourceLanguage, targetLanguage);
      }
      if (file) {
        translateFile(file, sourceLanguage, targetLanguage);
      }
    }
  };

  return (
    <Paper variant="outlined" sx={{ my: { xs: 3, md: 6 }, p: { xs: 2, md: 3 } }}>
      <form onSubmit={(e) => {
        e.preventDefault();
        translate();
      }}
      >
        <Typography component="h2" variant="h6" color="primary" gutterBottom>
          Translate - Fluent and accurate machine translation
        </Typography>
        <Grid container rowSpacing={2}>
          <Grid item xs={12}>
            <ToggleButtonGroup
              color="primary"
              exclusive
              value={isText}
              onChange={handleTranslationSourceChange}
              fullWidth
            >
              <ToggleButton value>Text</ToggleButton>
              <ToggleButton value={false}>File</ToggleButton>
            </ToggleButtonGroup>
          </Grid>
          <Grid item xs={12}>
            {
              isText ? (
                <TextField
                  id="outlined-multiline-static"
                  label="Text"
                  multiline
                  rows={4}
                  value={text}
                  placeholder="Provide a text to translate"
                  onChange={handleChange}
                  fullWidth
                />
              ) : (
                <Button
                  component="label"
                  endIcon={<FileUpload />}
                  fullWidth
                >
                  {file ? file.name : 'Upload file'}
                  <input
                    type="file"
                    hidden
                    onChange={(e) => setFile(e.target.files?.[0])}
                  />
                </Button>
              )
            }
          </Grid>
          <Grid
            item
            container
            columnSpacing={2}
          >
            <Grid item xs={6}>
              <FormControl fullWidth>
                <InputLabel id="source">Source language</InputLabel>
                <Select
                  labelId="source"
                  label="Source language"
                  onChange={handleSourceLanguageChange}
                >
                  {
                    supportedLanguages
                      .filter((x) => targetLanguage !== x.code)
                      .map((supportedLanguage) => (
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
            <Grid item xs={6}>
              <FormControl fullWidth>
                <InputLabel id="target">Target language</InputLabel>
                <Select
                  labelId="target"
                  label="Target language"
                  onChange={handleTargetLanguageChange}
                >
                  {
                    supportedLanguages
                      .filter((x) => sourceLanguage !== x.code)
                      .map((supportedLanguage) => (
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
          </Grid>
          <Grid item xs={12}>
            <Button
              type="submit"
              variant="contained"
              disabled={!checkIfFormIsCorrectlyFilled()}
              fullWidth
            >
              Translate
            </Button>
          </Grid>
          {
            isText ? (
              <Grid item xs={12}>
                <Box sx={{
                  padding: '8px',
                  border: '1px dashed grey',
                  width: '100%',
                  borderRadius: '4px',
                  minHeight: '6rem',
                }}
                >
                  {result}
                </Box>
              </Grid>
            ) : (<></>)
          }
        </Grid>
      </form>
    </Paper>
  );
};

export { TranslateTextPage };
