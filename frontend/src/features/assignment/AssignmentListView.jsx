import React, { useState } from 'react';
import { Box, Typography, Paper, Button } from '@mui/material';
import { DataGrid, GridToolbarContainer, GridToolbarColumnsButton, GridToolbarDensitySelector, GridToolbarFilterButton, esES } from '@mui/x-data-grid';
import formatDate from '../../utils/formatDate';
import { useGetEmergenciesQuery } from '../../api/emergencyApi';
import { useGetAssignmentsQuery } from '../../api/assignmentApi';
import { useTranslation } from 'react-i18next';
import { useNavigate } from 'react-router-dom';
import { useSelector } from 'react-redux';
import { selectToken } from '../user/login/LoginSlice';
import { useTheme } from '@mui/material/styles';

export default function AssignmentListView() {
  const { t } = useTranslation();
  const navigate = useNavigate();
  const token = useSelector(selectToken);
  const { i18n } = useTranslation();
  const locale = i18n?.language || 'es';
  const theme = useTheme();

  const { data: emergencies = [] } = useGetEmergenciesQuery({ token, locale });
  const [pendingOnly, setPendingOnly] = useState(false);
  const { data: assignments = [], isLoading, refetch } = useGetAssignmentsQuery({ token, locale }, { refetchOnMountOrArgChange: true });

  const [pageSize, setPageSize] = useState(10);

  function CustomToolbar() {
    return (
      <GridToolbarContainer>
        <Button
          size="small"
          variant={pendingOnly ? 'contained' : 'outlined'}
          color={pendingOnly ? 'success' : 'primary'}
          sx={{ mr: 1 }}
          onClick={() => setPendingOnly((v) => !v)}
        >
          {pendingOnly ? t('filter-pending-on', 'Status: PENDING') : t('filter-pending-off', 'Show only PENDING')}
        </Button>
        <GridToolbarColumnsButton />
        <GridToolbarFilterButton />
        <GridToolbarDensitySelector />
      </GridToolbarContainer>
    );
  }

  const columns = [
    { field: 'id', headerName: t('id', 'ID'), width: 90 },
    {
      field: 'emergencyName',
      headerName: t('emergency-name', 'Emergency name'),
      flex: 1,
      valueGetter: (params) => {
        const a = params.row || {};
        const emInfo = a.emergencyInfo || a.emergency;
        return emInfo ? (emInfo.description || '') : '-';
      },
    },
    {
      field: 'emergencyId',
      headerName: t('emergency-id', 'Emergency ID'),
      width: 120,
      valueGetter: (params) => {
        const a = params.row || {};
        const emInfo = a.emergencyInfo || a.emergency;
        return emInfo ? `#${emInfo.id}` : (a.emergencyId ? `#${a.emergencyId}` : '-');
      },
    },
    {
      field: 'quadrantName',
      headerName: t('quadrant-name', 'Quadrant name'),
      width: 220,
      valueGetter: (params) => {
        const a = params.row || {};
        const qInfo = a.quadrantInfo || (a.emergencyInfo && Array.isArray(a.emergencyInfo.quadrantInfo) && a.emergencyInfo.quadrantInfo.length ? a.emergencyInfo.quadrantInfo[0] : null);
        return qInfo ? (qInfo.nombre || qInfo.name || '') : (a.emergencyQuadrantId ? `#${a.emergencyQuadrantId}` : '-');
      },
    },
    {
      field: 'quadrantId',
      headerName: t('quadrant-id', 'Quadrant ID'),
      width: 120,
      valueGetter: (params) => {
        const a = params.row || {};
        const qInfo = a.quadrantInfo || (a.emergencyInfo && Array.isArray(a.emergencyInfo.quadrantInfo) && a.emergencyInfo.quadrantInfo.length ? a.emergencyInfo.quadrantInfo[0] : null);
        return qInfo ? `#${qInfo.id}` : (a.emergencyQuadrantId ? `#${a.emergencyQuadrantId}` : '-');
      },
    },
    {
      field: 'resourceName',
      headerName: t('resource-name', 'Resource name'),
      width: 220,
      valueGetter: (params) => {
        const a = params.row || {};
        const team = a.teamInfo;
        const vehicle = a.vehicleInfo;
        const resource = team || vehicle || null;
        return resource ? (resource.name || resource.code || resource.plate || '') : '-';
      },
    },
    {
      field: 'resourceId',
      headerName: t('resource-id', 'Resource ID'),
      width: 120,
      valueGetter: (params) => {
        const a = params.row || {};
        const team = a.teamInfo;
        const vehicle = a.vehicleInfo;
        const resource = team || vehicle || null;
        return resource ? `#${resource.id}` : (a.resourceId ? `#${a.resourceId}` : '-');
      },
    },
    {
      field: 'resourceType',
      headerName: t('type', 'Type'),
      width: 140,
      valueGetter: (params) => {
        const a = params.row || {};
        const team = a.teamInfo;
        const vehicle = a.vehicleInfo;
        const raw = a.resourceType || (team ? 'TEAM' : vehicle ? 'VEHICLE' : null);
        return raw ? t(String(raw).toLowerCase(), raw) : '-';
      },
    },
    {
      field: 'assignedAt',
      headerName: t('assigned-at', 'Assigned at'),
      width: 200,
      valueGetter: (params) => {
        const v = params.row ? params.row.assignedAt : null;
        if (!v) return '-';
      try { return formatDate(v, locale); } catch (e) { return v; }
      },
    },
    {
      field: 'status',
      headerName: t('status', 'Status'),
      width: 160,
      renderCell: (params) => {
        const s = params.value;
        if (!s) return '-';
        return <span>{t(String(s).toLowerCase(), s)}</span>;
      },
    },
  ];

  const localeText = locale === 'es' ? esES.components.MuiDataGrid.defaultProps.localeText : undefined;

  return (
    <Paper sx={{ display: 'flex', flexDirection: 'column', padding: '10px', minWidth: '1000px', height: 'calc(100vh - 120px)', boxSizing: 'border-box', overflow: 'hidden' }}>
      <Typography variant="h4" sx={{ mb: 1, fontWeight: 'bold', color: 'primary.light' }}>{t('assignment-list', 'Assignments')}</Typography>

      <Box sx={{ flex: 1, minHeight: 0, display: 'flex', flexDirection: 'column', width: '100%', '& .accepted': { backgroundColor: '#e8f5e9', '&:hover': { backgroundColor: '#c8e6c9' } }, '& .pending': { backgroundColor: '#fff8e1', '&:hover': { backgroundColor: '#ffecb3' } }, '& .released': { backgroundColor: '#ffe0b2', '&:hover': { backgroundColor: '#ffcc80' } }, '& .completed': { backgroundColor: '#eceff1', '&:hover': { backgroundColor: '#cfd8dc' } } }}>
        <Box sx={{ flex: 1, minHeight: 0, width: '100%' }}>
          <DataGrid
            rows={(pendingOnly ? (assignments || []).filter(a => a.status === 'PENDING') : (assignments || []))}
            columns={columns}
            loading={isLoading}
            pageSize={pageSize}
            onPageSizeChange={(newSize) => setPageSize(newSize)}
            rowsPerPageOptions={[10, 25, 50]}
            pagination
            components={{ Toolbar: CustomToolbar }}
            componentsProps={{ pagination: { labelRowsPerPage: t('rows-per-page') } }}
            localeText={localeText}
            getRowId={(row) => row.id}
            onRowClick={(params) => navigate(`/assignments/${params.id}`)}
            getRowClassName={(params) => {
              const status = params.row ? params.row.status : null;
              if (!status) return '';
              if (status === 'ACCEPTED') return 'accepted';
              if (status === 'PENDING' || status === 'CREATED') return 'pending';
              if (status === 'RELEASED') return 'released';
              if (status === 'COMPLETED') return 'completed';
              return '';
            }}
            sx={{ height: '100%' }}
          />
        </Box>

        <Box sx={{ mt: 1, flexShrink: 0, display: 'flex', justifyContent: 'flex-end' }}>
          <Button variant="contained" color="secondary" onClick={() => navigate('/assignment-management')}>{t('create-assignment', 'Create assignment')}</Button>
        </Box>
      </Box>

    </Paper>
  );
}
