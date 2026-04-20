import React, { useMemo, useState } from "react";
import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Button,
  TextField,
  Grid,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  IconButton,
  Typography,
} from "@mui/material";
import CloseIcon from '@mui/icons-material/Close';
import { useTranslation } from "react-i18next";
import { useSelector } from 'react-redux';
import { selectToken } from "../user/login/LoginSlice";
import { useGetActiveTeamsQuery } from '../../api/teamApi';

export default function TeamSelector({ open, onClose, organizations = [], onSelect }) {
  const { t, i18n } = useTranslation();
  const token = useSelector(selectToken);
  const locale = i18n?.language || 'es';

  const { data: teams = [], isLoading } = useGetActiveTeamsQuery({ token, locale });

  const [q, setQ] = useState("");
  const [orgFilter, setOrgFilter] = useState("");

  const resources = teams;

  const filtered = useMemo(() => {
    const ql = (q || "").toLowerCase();
    return (resources || []).filter((r) => {
      const statusIsBusy = (r.status && String(r.status).toUpperCase() === "BUSY");
      const deployedFlag = r.deployed === true || r.deployAt != null;
      if (statusIsBusy || deployedFlag || r.removed === true) return false;
      if (orgFilter) {
        const orgId = r.organizationId || (r.organization && r.organization.id) || null;
        if (!orgId || String(orgId) !== String(orgFilter)) return false;
      }
      if (ql) {
        const name = (r.name || r.code || r.vehiclePlate || r.plate || "").toLowerCase();
        return name.includes(ql) || String(r.id).includes(ql);
      }
      return true;
    });
  }, [resources, q, orgFilter]);

  const orgOptions = useMemo(() => {
    if (organizations && organizations.length) return organizations;
    const map = new Map();
    (resources || []).forEach((r) => {
      const org = r.organization || r.organizationName ? { id: (r.organization && r.organization.id) || r.organizationId || r.organizationName, name: (r.organization && r.organization.name) || r.organizationName } : null;
      if (org && org.id) map.set(org.id, org);
    });
    return Array.from(map.values());
  }, [organizations, resources]);

  return (
    <Dialog open={open} onClose={onClose} fullWidth maxWidth="lg">
      <DialogTitle sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
        {t('team-selection', 'Select team')}
        <IconButton onClick={onClose}><CloseIcon /></IconButton>
      </DialogTitle>
      <DialogContent>
        <Grid container spacing={2} sx={{ mb: 1 }}>
          <Grid item xs={12} md={6}>
            <TextField fullWidth label={t('filter', 'Filter')} value={q} onChange={(e) => setQ(e.target.value)} placeholder={t('filter-placeholder', 'Search by name, code or id')} />
          </Grid>
          <Grid item xs={6} md={4}>
            <FormControl fullWidth>
              <InputLabel id="org-filter-label">{t('organization', 'Organization')}</InputLabel>
              <Select labelId="org-filter-label" value={orgFilter} label={t('organization', 'Organization')} onChange={(e) => setOrgFilter(e.target.value)}>
                <MenuItem value="">{t('all', 'All')}</MenuItem>
                {orgOptions.map(org => <MenuItem key={org.id} value={org.id}>{org.name || org.id}</MenuItem>)}
              </Select>
            </FormControl>
          </Grid>
          <Grid item xs={12} md={2} sx={{ display: 'flex', alignItems: 'center' }}>
            <Button fullWidth variant="outlined" onClick={() => { setQ(''); setOrgFilter(''); }}>{t('clear-filters', 'Clear filters')}</Button>
          </Grid>
        </Grid>

        <TableContainer component={Paper} sx={{ maxHeight: '50vh' }}>
          <Table stickyHeader>
            <TableHead>
              <TableRow>
                <TableCell>{t('id', 'ID')}</TableCell>
                <TableCell>{t('name', 'Name')}</TableCell>
                <TableCell>{t('status', 'Status')}</TableCell>
                <TableCell>{t('organization', 'Organization')}</TableCell>
                <TableCell>{t('actions', 'Actions')}</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {filtered.map((r) => (
                <TableRow key={r.id} hover>
                  <TableCell>{r.id}</TableCell>
                  <TableCell>
                    <Typography sx={{ fontWeight: 600 }}>{r.name || r.code}</Typography>
                    <Typography variant="caption" color="text.secondary">{r.description || ''}</Typography>
                  </TableCell>
                  <TableCell>{r.status || '-'}</TableCell>
                  <TableCell>{(r.organization && r.organization.name) || r.organizationName || r.organizationId || '-'}</TableCell>
                  <TableCell>
                    <Button size="small" variant="outlined" onClick={() => { onSelect(r); onClose(); }}>{t('select', 'Select')}</Button>
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </TableContainer>
      </DialogContent>
      <DialogActions>
        <Button onClick={onClose}>{t('close', 'Close')}</Button>
      </DialogActions>
    </Dialog>
  );
}
