/* eslint-disable react/jsx-props-no-spreading */
import React, { useEffect, useState } from 'react';
import MUIDataTable, {
  MUIDataTableColumn,
  MUIDataTableColumnDef,
  MUIDataTableOptions,
  MUIDataTableState,
} from 'mui-datatables';
import {
  FormLabel,
  FormGroup,
  TextField,
  Paper,
} from '@mui/material';
import {
  DatePicker,
} from '@mui/lab';
import { AccessLog } from '../../api/model';
import { Filters, getAccessLogs } from '../../api/access-logs-api';
import { LoadingState } from '../../components/LoadingState';

const AccessLogsPage: React.FC = () => {
  const [accessLogs, setAccessLogs] = useState<Array<AccessLog>>([]);
  const [loading, setLoading] = useState(true);

  const columns: MUIDataTableColumnDef[] = [
    {
      name: 'server',
      label: 'Server',
      options: {
        filter: false,
        sort: false,
      },
    },
    {
      name: 'httpVersion',
      label: 'HTTP Version',
      options: {
        filter: false,
        sort: false,
      },
    },
    {
      name: 'authority',
      label: 'Authority',
      options: {
        filter: false,
        sort: false,
      },
    },
    {
      name: 'uri',
      label: 'URI',
      options: {
        filter: false,
        sort: false,
      },
    },
    {
      name: 'method',
      label: 'Method',
      options: {
        filter: true,
        customFilterListOptions: { render: (v) => `Method: ${v}` },
        filterOptions: {
          names: ['GET', 'POST', 'DELETE', 'PUT', 'PATCH'],
        },
        sort: false,
      },
    },
    {
      name: 'requestContentType',
      label: 'Request Content-Type',
      options: {
        filter: false,
        sort: false,
      },
    },
    {
      name: 'requestBodySize',
      label: 'Request Body Size',
      options: {
        filter: false,
        sort: false,
      },
    },
    {
      name: 'responseStatus',
      label: 'Response Status',
      options: {
        filter: false,
        sort: false,
      },
    },
    {
      name: 'uri',
      label: 'URI',
      options: {
        filter: false,
        sort: false,
      },
    },
    {
      name: 'responseContentType',
      label: 'Response Content-Type',
      options: {
        filter: false,
        sort: false,
      },
    },
    {
      name: 'responseBodySize',
      label: 'Response Body Size',
      options: {
        filter: false,
        sort: false,
      },
    },
    {
      name: 'responseBodySize',
      label: 'Response Body Size',
      options: {
        filter: false,
        sort: false,
      },
    },
    {
      name: 'userLogin',
      label: 'User Login',
      options: {
        filter: true,
        customFilterListOptions: { render: (v) => `User Login: ${v}` },
        filterType: 'textField',
        sort: false,
      },
    },
    {
      name: 'id',
      label: 'ID',
      options: {
        filter: false,
        customFilterListOptions: { render: (v) => `User Login: ${v}` },
        filterType: 'textField',
        sort: false,
      },
    },
    {
      name: 'timestamp',
      label: 'Timestamp',
      options: {
        filter: true,
        filterType: 'custom',
        customFilterListOptions: {
          render: (v) => {
            if (v[0] && v[1]) {
              return [`Date from: ${new Date(v[0]).toISOString().split('T')[0]}, Date to: ${new Date(v[1]).toISOString().split('T')[0]}`];
            } if (v[0]) {
              return `Date from: ${new Date(v[0]).toISOString().split('T')[0]}`;
            } if (v[1]) {
              return `Date to: ${new Date(v[1]).toISOString().split('T')[0]}`;
            }
            return [];
          },
          update: (filterList, filterPos, index) => {
            if (filterPos === 0) {
              filterList[index].splice(filterPos, 1, '');
            } else if (filterPos === 1) {
              filterList[index].splice(filterPos, 1);
            } else if (filterPos === -1) {
              // eslint-disable-next-line no-param-reassign
              filterList[index] = [];
            }
            return filterList;
          },
        },
        filterOptions: {
          fullWidth: true,
          names: [],
          logic: (age, filters) => {
            if (filters[0] && filters[1]) {
              return age < filters[0] || age > filters[1];
            } if (filters[0]) {
              return age < filters[0];
            } if (filters[1]) {
              return age > filters[1];
            }
            return false;
          },
          display: (filterList, onChange, index, column) => (
            <div>
              <FormLabel>Date</FormLabel>
              <FormGroup sx={{ mt: '0.5rem', gap: '1rem' }}>
                <DatePicker
                  label="From"
                  value={filterList[index][0]}
                  maxDate={filterList[index][1]}
                  onChange={(e) => {
                    // eslint-disable-next-line no-param-reassign
                    filterList[index][0] = e || '';
                    onChange(filterList[index], index, column);
                  }}
                  renderInput={(params) => <TextField {...params} />}
                  inputFormat="yyyy-MM-dd"
                />
                <DatePicker
                  label="To"
                  value={filterList[index][1]}
                  minDate={filterList[index][0]}
                  onChange={(e) => {
                    // eslint-disable-next-line no-param-reassign
                    filterList[index][1] = e || '';
                    onChange(filterList[index], index, column);
                  }}
                  renderInput={(params) => <TextField {...params} />}
                  inputFormat="yyyy-MM-dd"
                />
              </FormGroup>
            </div>
          ),
        },
        print: false,
        sort: false,
      },
    },
  ];

  const options: MUIDataTableOptions = {
    filter: true,
    onFilterChange: (_: string | MUIDataTableColumn | null, filterList: MUIDataTableState['filterList']) => {
      const tempFilters: Filters = ({
        method: filterList[4][0],
        userLogin: filterList[12][0],
        from: filterList[14][0] ? new Date(filterList[14][0]) : undefined,
        to: filterList[14][1] ? new Date(filterList[14][1]) : undefined,
      });
      getAccessLogs(tempFilters).then((r) => setAccessLogs(r));
    },
    filterType: 'dropdown',
    search: false,
    print: false,
    download: false,
    selectableRowsHeader: false,
    selectableRows: 'none',
  };

  useEffect(() => {
    getAccessLogs({}).then((r) => {
      setAccessLogs(r);
      setLoading(false);
    });
  }, []);

  const datatable = (
    <span className="datatable_span">
      <MUIDataTable title="Access Logs" data={accessLogs} columns={columns} options={options} />
    </span>
  );

  const content = loading ? (
    <LoadingState />
  ) : datatable;

  return (
    <Paper variant="outlined" sx={{ my: { xs: 3, md: 6 }, p: { xs: 2, md: 3 } }}>
      {content}
    </Paper>
  );
};

export { AccessLogsPage };
