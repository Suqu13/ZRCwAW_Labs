import * as React from 'react';
import {
  Box, Collapse, IconButton, TableCell, TableRow,
} from '@mui/material';
import KeyboardArrowDownIcon from '@mui/icons-material/KeyboardArrowDown';
import KeyboardArrowUpIcon from '@mui/icons-material/KeyboardArrowUp';
import { ObjectStorageItems } from './ObjectStorageItems';

interface Props {
  row: {
    id: string
    name: string
  }
}

const ObjectStorageRow: React.FunctionComponent<Props> = ({ row }) => {
  const [open, setOpen] = React.useState(false);

  return (
    <>
      <TableRow sx={{ '& > *': { borderBottom: 'unset' } }}>
        <TableCell>
          <IconButton
            aria-label="expand row"
            size="small"
            onClick={() => setOpen(!open)}
          >
            {open ? <KeyboardArrowUpIcon /> : <KeyboardArrowDownIcon />}
          </IconButton>
        </TableCell>
        <TableCell component="th" scope="row">
          {row.name}
        </TableCell>
      </TableRow>
      <TableRow>
        <TableCell style={{ paddingBottom: 0, paddingTop: 0 }} colSpan={6}>
          <Collapse in={open} timeout="auto" unmountOnExit>
            <Box sx={{ margin: 1 }}>
              <ObjectStorageItems objectStorageName={row.name} />
            </Box>
          </Collapse>
        </TableCell>
      </TableRow>
    </>
  );
};

export { ObjectStorageRow };
