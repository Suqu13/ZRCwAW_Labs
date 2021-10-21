import React from 'react';
import {
  Typography, IconButton, Modal, Box,
} from '@mui/material';
import ImageSearchIcon from '@mui/icons-material/ImageSearch';
import { getLabels, getTexts } from '../api/image-analysis-api';

interface Props {
  storageName: string
  imageName: string
}

const AnalyseImage: React.FunctionComponent<Props> = ({ storageName, imageName }) => {
  const [open, setOpen] = React.useState(false);
  const [labelList, setlabelList] = React.useState(Array<JSX.Element>());
  const labelListTemp: Array<JSX.Element> = [];
  const [textList, setTextList] = React.useState(Array<JSX.Element>());
  const textListTemp: Array<JSX.Element> = [];

  const analyse: () => void = () => {
    getLabels(storageName, imageName)
      .then((labels) => {
        labels.forEach((label) => {
          labelListTemp.push(
            <li key={label.name}>
              {label.name}
              (conf.
              {label.confidence}
              )
            </li>,
          );
        });
        setlabelList(labelListTemp);
      });

    getTexts(storageName, imageName)
      .then((texts) => {
        texts.forEach((text) => {
          textListTemp.push(
            <li key={text.content}>
              {text.content}
              (conf.
              {text.confidence}
              )
            </li>,
          );
        });
        setTextList(textListTemp);
      });
  };

  const style = {
    // position: 'absolute', //fixme to wywala error, nie wiem czemu...?
    // transform: 'translate(-50%, -50%)',
    top: '50%',
    left: '50%',
    width: 400,
    bgcolor: 'background.paper',
    border: '2px solid #000',
    boxShadow: 24,
    p: 4,
  };

  const handleOpen: () => void = () => {
    analyse();
    setOpen(true);
  };
  const handleClose: () => void = () => setOpen(false);

  const shouldBeRendered: () => boolean = () => ['jpg', 'png', 'jpeg', 'gif'].map((ext) => imageName.endsWith(ext)).includes(true);

  return shouldBeRendered() ? (
    <div>
      <IconButton size="small" onClick={handleOpen}>
        <ImageSearchIcon />
      </IconButton>
      <Modal
        open={open}
        onClose={handleClose}
        aria-labelledby="modal-modal-title"
        aria-describedby="modal-modal-description"
      >
        <Box sx={style}>
          <Typography id="modal-modal-title" variant="h5" component="h2">
            {imageName}
          </Typography>
          <Typography id="modal-modal-description" sx={{ mt: 2 }} variant="h6" component="h4">
            Labels:
          </Typography>
          <ul>{labelList}</ul>
          <Typography id="modal-modal-description" sx={{ mt: 2 }} variant="h6" component="h4">
            Detected text lines:
          </Typography>
          <ul>{textList}</ul>
        </Box>
      </Modal>
    </div>
  ) : null;
};

export { AnalyseImage };
