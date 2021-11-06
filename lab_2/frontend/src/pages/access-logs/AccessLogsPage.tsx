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
} from '@mui/material';
import {
  DatePicker,
} from '@material-ui/pickers';
import { AccessLog } from '../../api/model';
import { getAccessLogs } from '../../api/access-logs-api';

const AccessLogsPage: React.FC = () => {
  const [accessLogs, setAccessLogs] = useState<Array<AccessLog>>([]);

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
              return [`Date from: ${v[0]}, Date to: ${v[1]}`];
            } if (v[0]) {
              return `Date from: ${v[0]}`;
            } if (v[1]) {
              return `Date to: ${v[1]}`;
            }
            return [];
          },
          update: (filterList, filterPos, index) => {
            console.log('customFilterListOnDelete: ', filterList, filterPos, index);

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
              <FormGroup row>
                <DatePicker
                  label="Date from"
                  variant="inline"
                  value={filterList[index][0]}
                  maxDate={filterList[index][1]}
                  onChange={(e) => {
                  // eslint-disable-next-line no-param-reassign
                    filterList[index][0] = e?.toDateString() || '';
                    onChange(filterList[index], index, column);
                  }}
                />
                <DatePicker
                  label="Date from"
                  variant="inline"
                  value={filterList[index][1]}
                  minDate={filterList[index][0]}
                  onChange={(e) => {
                    // eslint-disable-next-line no-param-reassign
                    filterList[index][1] = e?.toDateString() || '';
                    onChange(filterList[index], index, column);
                  }}
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
    onFilterChange: (changedColumn: string | MUIDataTableColumn | null, filterList: MUIDataTableState['filterList']) => {
      console.log(changedColumn, filterList);
    },
    filterType: 'dropdown',
    search: false,
    print: false,
    download: false,
    selectableRowsHeader: false,
    selectableRows: 'none',
  };

  useEffect(() => {
    getAccessLogs().then((r) => setAccessLogs(r));
  }, []);

  return (
    <>
      <MUIDataTable title="Access Logs" data={accessLogs} columns={columns} options={options} />
    </>
  );
};

export { AccessLogsPage };
