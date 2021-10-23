import React, { useState } from 'react';
import {
  IconButton,
  Dialog,
  DialogTitle,
  Table,
  TableBody,
  TableRow,
  DialogContent,
  TableCell,
  TableHead,
  Typography,
} from '@mui/material';
import ImageSearchIcon from '@mui/icons-material/ImageSearch';
import { useSnackbar } from 'notistack';
import { getLabels as getLabels_, getTexts as getTexts_ } from '../api/image-analysis-api';
import { ImageLabel, ImageText } from '../api/model';
import { LoadingState } from './LoadingState';
import { EmptyState } from './EmptyState';
import { ErrorState } from './ErrorState';

interface Props {
  storageName: string
  imageName: string
}

const AnalyseImage: React.FunctionComponent<Props> = ({ storageName, imageName }) => {
  const [open, setOpen] = useState(false);
  const [labels, setLabels] = useState<ImageLabel[]>();
  const [labelsLoading, setLabelsLoading] = useState(true);
  const [labelsError, setLabelsError] = useState(false);
  const [texts, setTexts] = useState<ImageText[]>();
  const [textsLoading, setTextsLoading] = useState(true);
  const [textsError, setTextsError] = useState(false);
  const { enqueueSnackbar } = useSnackbar();

  const getLabels = (): void => {
    setLabelsLoading(true);
    setLabelsError(false);

    getLabels_(storageName, imageName)
      .then((l) => {
        setLabels(l);
        setLabelsLoading(false);
      })
      .catch(() => {
        setLabelsError(true);
        enqueueSnackbar('An error occured while recogniting labels!', { variant: 'error' });
      });
  };

  const getTexts = (): void => {
    setTextsLoading(true);
    setTextsError(false);
    getTexts_(storageName, imageName)
      .then((t) => {
        setTexts(t);
        setTextsLoading(false);
      })
      .catch(() => {
        setTextsError(true);
        enqueueSnackbar('An error occured while recogniting text!', { variant: 'error' });
      });
  };

  const analyse: () => void = () => {
    getLabels();
    getTexts();
  };

  const handleOpen: () => void = () => {
    analyse();
    setOpen(true);
  };
  const handleClose: () => void = () => setOpen(false);

  const shouldBeRendered: () => boolean = () => ['jpg', 'png', 'jpeg', 'gif'].map((ext) => imageName.endsWith(ext)).includes(true);

  const labelsContent = (): JSX.Element => {
    if (labelsError) {
      return (<ErrorState onClick={getLabels} />);
    }

    if (labelsLoading) {
      return (<LoadingState />);
    }

    if (labels?.length === 0) {
      return (<EmptyState onClick={getLabels} />);
    }

    return (
      <Table>
        <TableHead>
          <TableRow>
            <TableCell>
              <b>Label</b>
            </TableCell>
            <TableCell align="right">
              <b>Confidence</b>
            </TableCell>
          </TableRow>

        </TableHead>
        <TableBody>
          {labels && labels.map((label) => (
            <TableRow key={label.name}>
              <TableCell>{label.name}</TableCell>
              <TableCell align="right">
                {label.confidence.toFixed(2)}
                %
              </TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>
    );
  };

  const textsContent = (): JSX.Element => {
    if (textsError) {
      return (<ErrorState onClick={getTexts} />);
    }

    if (textsLoading) {
      return (<LoadingState />);
    }

    if (texts?.length === 0) {
      return (<EmptyState onClick={getTexts} />);
    }

    return (
      <Table>
        <TableHead>
          <TableRow>
            <TableCell>
              <b>Text</b>
            </TableCell>
            <TableCell align="right">
              <b>Confidence</b>
            </TableCell>
          </TableRow>
        </TableHead>
        <TableBody>
          {texts && texts.map((text) => (
            <TableRow key={text.content}>
              <TableCell>{text.content}</TableCell>
              <TableCell align="right">
                {text.confidence.toFixed(2)}
                %
              </TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>
    );
  };

  return shouldBeRendered() ? (
    <>
      <IconButton size="small" onClick={handleOpen}>
        <ImageSearchIcon />
      </IconButton>
      <Dialog onClose={handleClose} open={open}>
        <DialogTitle>
          <Typography component="h2" variant="h6" color="primary" gutterBottom>
            Rekognition
          </Typography>
        </DialogTitle>
        <DialogContent dividers>
          {labelsContent()}
        </DialogContent>
        <DialogContent dividers>
          {textsContent()}
        </DialogContent>
      </Dialog>
    </>
  ) : null;
};

export { AnalyseImage };
