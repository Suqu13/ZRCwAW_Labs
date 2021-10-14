import React, { FunctionComponent, useEffect, useState } from 'react';
import {
  Accordion, AccordionDetails, AccordionSummary, Typography,
} from '@mui/material';
import ExpandMoreIcon from '@mui/icons-material/ExpandMore';
import { ObjectStorage } from '../api/model';
import { getObjectStorages } from '../api/object-storage-api';

const objectStoragesHook = (): { objectsStorages: Array<ObjectStorage> } => {
  const [objectsStorages, setObjectsStorages] = useState<Array<ObjectStorage>>([]);
  useEffect((): void => {
    getObjectStorages()
      .then((x) => setObjectsStorages(x));
  }, []);

  return {
    objectsStorages,
  };
};

const ObjectStorages: FunctionComponent = () => {
  const { objectsStorages } = objectStoragesHook();

  return (
    <div>
      {objectsStorages.map((objectsStorage) => (
        <Accordion key={objectsStorage.name}>
          <AccordionSummary
            expandIcon={<ExpandMoreIcon />}
            aria-controls="panel1a-content"
          >
            <Typography>{objectsStorage.name}</Typography>
          </AccordionSummary>
          <AccordionDetails>
            <Typography>
              Lorem ipsum dolor sit amet, consectetur adipiscing elit. Suspendisse
              malesuada lacus ex, sit amet blandit leo lobortis eget.
            </Typography>
          </AccordionDetails>
        </Accordion>
      ))}
    </div>
  );
};

export { ObjectStorages };
