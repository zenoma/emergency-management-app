import React from 'react';
import { Box, Typography, Paper, Grid, Button, Chip, Divider, CircularProgress, Dialog, DialogTitle, DialogContent, DialogActions } from '@mui/material';
import { toast } from 'react-toastify';
import { useParams, useNavigate } from 'react-router-dom';
import { useGetAssignmentQuery, useDeleteAssignmentMutation, useUpdateAssignmentStatusMutation } from '../../api/assignmentApi';
import LandingMap from '../../features/map/LandingMap';
import { useSelector } from 'react-redux';
import { selectToken } from '../user/login/LoginSlice';
import { useTranslation } from 'react-i18next';
import ReplayIcon from '@mui/icons-material/Replay';

export default function AssignmentDetailsView() {
  const { id } = useParams();
  const navigate = useNavigate();
  const { t } = useTranslation();
  const token = useSelector(selectToken);
  const locale = (useTranslation().i18n || {}).language || 'es';

  const { data: assignment, isLoading, isFetching } = useGetAssignmentQuery({ id, token, locale });
  const [deleteAssignment, { isLoading: deleting }] = useDeleteAssignmentMutation();
  const [confirmOpen, setConfirmOpen] = React.useState(false);
  const [confirmAcceptOpen, setConfirmAcceptOpen] = React.useState(false);
  const [updateStatus, { isLoading: updating }] = useUpdateAssignmentStatusMutation();

  if (isLoading || isFetching) {
    return (
      <Box p={4} display="flex" justifyContent="center"><CircularProgress /></Box>
    );
  }

  if (!assignment) {
    return (
      <Box p={2}>
        <Typography variant="h5">{t('assignment-details', 'Assignment details')}</Typography>
        <Typography variant="body1">{t('assignment-not-found', 'Assignment not found')}</Typography>
        <Button variant="outlined" sx={{ mt: 2 }} onClick={() => navigate(-1)}>{t('back', 'Back')}</Button>
      </Box>
    );
  }

  const resource = assignment.teamInfo || assignment.vehicleInfo || null;
  const quadrant = assignment.quadrantInfo || null;
  const emergency = assignment.emergencyInfo || assignment.emergency || null;

  // build quadrants array for LandingMap
  const quadrants = (() => {
    if (assignment.quadrantInfo) {
      // assignment.quadrantInfo might be an object or array
      return Array.isArray(assignment.quadrantInfo) ? assignment.quadrantInfo : [assignment.quadrantInfo];
    }
    if (assignment.emergencyInfo && Array.isArray(assignment.emergencyInfo.quadrantInfo) && assignment.emergencyInfo.quadrantInfo.length) {
      return assignment.emergencyInfo.quadrantInfo;
    }
    return [];
  })();

  return (
    <Box p={2}>
      <Box display="flex" alignItems="center" justifyContent="space-between" mb={2}>
        <Typography variant="h4">{t('assignment-details', 'Assignment details')} #{assignment.id}</Typography>
        <Box>
          <Chip label={assignment.status} color={assignment.status === 'ACCEPTED' ? 'success' : assignment.status === 'PENDING' ? 'warning' : 'default'} sx={{ mr: 1, fontWeight: 600 }} />
        </Box>
      </Box>

      {/* Map centered on quadrant (if available) */}
      {quadrants && quadrants.length > 0 && (
        <Box sx={{ width: '100%', height: { xs: 280, md: 360 }, mb: 2 }}>
          <Paper variant="outlined" sx={{ width: '100%', height: '100%' }}>
            <LandingMap quadrants={quadrants} />
          </Paper>
        </Box>
      )}

      <Grid container spacing={2}>
        <Grid item xs={12} md={4}>
          <Paper variant="outlined" sx={{ p: 2 }}>
            <Typography variant="subtitle1" gutterBottom>{t('emergency', 'Emergency')}</Typography>
            <Typography variant="body2"><strong>{t('name', 'Name')}:</strong> {emergency ? emergency.description : '-'}</Typography>
            <Typography variant="body2"><strong>{t('id', 'ID')}:</strong> {emergency ? `#${emergency.id}` : '-'}</Typography>
            <Typography variant="body2"><strong>{t('created-at', 'Created at')}:</strong> {emergency && emergency.createdAt ? new Date(emergency.createdAt).toLocaleString() : '-'}</Typography>
          </Paper>
        </Grid>

        <Grid item xs={12} md={4}>
          <Paper variant="outlined" sx={{ p: 2 }}>
            <Typography variant="subtitle1" gutterBottom>{t('resource', 'Resource')}</Typography>
            <Typography variant="body2"><strong>{t('resource-name', 'Resource name')}:</strong> {resource ? (resource.name || resource.code || resource.plate) : '-'}</Typography>
            <Typography variant="body2"><strong>{t('resource-id', 'Resource ID')}:</strong> {resource ? `#${resource.id}` : '-'}</Typography>
            <Typography variant="body2"><strong>{t('deployed-at', 'Deployed at')}:</strong> {resource && resource.deployAt ? new Date(resource.deployAt).toLocaleString() : '-'}</Typography>
          </Paper>
        </Grid>

        <Grid item xs={12} md={4}>
          <Paper variant="outlined" sx={{ p: 2 }}>
            <Typography variant="subtitle1" gutterBottom>{t('quadrant', 'Quadrant')}</Typography>
            <Typography variant="body2"><strong>{t('quadrant-name', 'Name')}:</strong> {quadrant ? (quadrant.nombre || quadrant.name) : '-'}</Typography>
            <Typography variant="body2"><strong>{t('quadrant-id', 'ID')}:</strong> {quadrant ? `#${quadrant.id}` : '-'}</Typography>
          </Paper>
        </Grid>

        <Grid item xs={12}>
          <Paper variant="outlined" sx={{ p: 2 }}>
            <Typography variant="subtitle1">{t('assignment-info', 'Assignment info')}</Typography>
            <Divider sx={{ my: 1 }} />
            <Grid container spacing={1}>
              <Grid item xs={12} md={4}><Typography variant="body2"><strong>{t('assigned-at', 'Assigned at')}:</strong> {assignment.assignedAt ? new Date(assignment.assignedAt).toLocaleString() : '-'}</Typography></Grid>
              <Grid item xs={12} md={4}><Typography variant="body2"><strong>{t('accepted-at', 'Accepted at')}:</strong> {assignment.acceptedAt ? new Date(assignment.acceptedAt).toLocaleString() : '-'}</Typography></Grid>
              <Grid item xs={12} md={4}><Typography variant="body2"><strong>{t('completed-at', 'Completed at')}:</strong> {assignment.completedAt ? new Date(assignment.completedAt).toLocaleString() : '-'}</Typography></Grid>
              <Grid item xs={12}><Typography variant="body2"><strong>{t('notes', 'Notes')}:</strong> {assignment.notes || '-'}</Typography></Grid>
            </Grid>
            <Box sx={{ mt: 2, display: 'flex', gap: 1 }}>
              <Button variant="outlined" onClick={() => navigate(-1)}>{t('back', 'Back')}</Button>
          {assignment.status === 'PENDING' && (
            <>
              <Button color="success" variant="contained" sx={{ ml: 1 }} disabled={updating} onClick={() => setConfirmAcceptOpen(true)}>{t('accept-assignment', 'Accept')}</Button>
              <Button color="error" variant="contained" sx={{ ml: 1 }} onClick={() => setConfirmOpen(true)}>{t('delete-assignment', 'Delete')}</Button>
            </>
          )}
            </Box>
          </Paper>
        </Grid>
      </Grid>

      <Dialog open={confirmOpen} onClose={() => setConfirmOpen(false)}>
        <DialogTitle>{t('confirm-delete', 'Confirm deletion')}</DialogTitle>
        <DialogContent>
          <Typography>{t('confirm-delete-msg', 'Are you sure you want to delete this assignment? This action cannot be undone.')}</Typography>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setConfirmOpen(false)}>{t('cancel', 'Cancel')}</Button>
          <Button color="error" variant="contained" disabled={deleting} onClick={async () => {
            try {
              await deleteAssignment({ id: assignment.id, token, locale }).unwrap();
              setConfirmOpen(false);
              toast.success(t('assignment-deleted', 'Assignment deleted'));
              navigate('/assignments');
            } catch (err) {
              console.error('delete failed', err);
              // Try to surface a useful error message from the server payload
              const serverMessage = err?.data?.errorMessage || err?.data?.message || err?.error || err?.message;
              toast.error(serverMessage || t('assignment-delete-failed', 'Failed to delete assignment'));
              setConfirmOpen(false);
            }
          }}>{t('delete', 'Delete')}</Button>
        </DialogActions>
      </Dialog>

      <Dialog open={confirmAcceptOpen} onClose={() => setConfirmAcceptOpen(false)}>
        <DialogTitle>{t('confirm-accept', 'Confirm accept')}</DialogTitle>
        <DialogContent>
          <Typography>{t('confirm-accept-msg', '¿Está seguro de aceptar esta asignación? Esto pondrá el recurso asignado como ocupado.')}</Typography>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setConfirmAcceptOpen(false)}>{t('cancel', 'Cancel')}</Button>
          <Button color="success" variant="contained" onClick={async () => {
            try {
              await updateStatus({ id: assignment.id, status: 'ACCEPTED', token, locale }).unwrap();
              setConfirmAcceptOpen(false);
              toast.success(t('assignment-accepted', 'Assignment accepted'));
            } catch (err) {
              console.error('accept failed', err);
              toast.error(t('assignment-accept-failed', 'Failed to accept assignment'));
              setConfirmAcceptOpen(false);
            }
          }}>{t('accept', 'Accept')}</Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
}
