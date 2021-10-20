import React, { useState } from 'react';
import {
  Box, Collapse, IconButton, TableCell, TableRow,
} from '@mui/material';
import KeyboardArrowDownIcon from '@mui/icons-material/KeyboardArrowDown';
import KeyboardArrowUpIcon from '@mui/icons-material/KeyboardArrowUp';
import FileUpload from '@mui/icons-material/FileUpload';
import { ObjectStorageItems } from './ObjectStorageItems';

interface Props {
  row: {
    id: string
    name: string
  }
  onUploadClick: (file: File) => Promise<void>
}

const ObjectStorageRow: React.FunctionComponent<Props> = ({ row, onUploadClick }) => {
  const [open, setOpen] = useState(false);
  const [uploadKey, setUploadKey] = useState<string>('');

  return (
    <>
      <TableRow>
        <TableCell width="10%">
          <IconButton
            size="small"
            onClick={() => setOpen(!open)}
          >
            {open ? <KeyboardArrowUpIcon /> : <KeyboardArrowDownIcon />}
          </IconButton>
        </TableCell>
        <TableCell component="th" scope="row">
          {row.name}
        </TableCell>
        <TableCell width="10%">
          <IconButton
            size="small"
            component="label"
          >
            <FileUpload />
            <input
              type="file"
              hidden
              onChange={(e) => {
                const file = e.target.files?.[0];
                if (file) {
                  onUploadClick(file)
                    .then(() => setUploadKey(file.name));
                }
              }}
            />
          </IconButton>
        </TableCell>
      </TableRow>
      <TableRow>
        <TableCell style={{ paddingBottom: 0, paddingTop: 0 }} colSpan={6}>
          <Collapse in={open} timeout="auto" unmountOnExit>
            <Box sx={{ margin: 1 }}>
              <ObjectStorageItems key={uploadKey} objectStorageName={row.name} />
            </Box>
          </Collapse>
        </TableCell>
      </TableRow>
    </>
  );
};

export { ObjectStorageRow };
