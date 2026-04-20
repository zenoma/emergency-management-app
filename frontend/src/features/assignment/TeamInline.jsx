import React, { useMemo, useState } from "react";
import {
  Paper,
  TextField,
  Grid,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Button,
  Typography,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
} from "@mui/material";
import { useTranslation } from "react-i18next";
import { useSelector } from 'react-redux';
import { selectToken } from "../user/login/LoginSlice";
import { useGetActiveTeamsQuery } from '../../api/teamApi';

export default function TeamInline({ onSelect, organizations = [], externalSelectedId = null }) {
  const { t, i18n } = useTranslation();
  const token = useSelector(selectToken);
  const locale = i18n?.language || 'es';

  const { data: teams = [] } = useGetActiveTeamsQuery({ token, locale });

  const [q, setQ] = useState("");
  const [orgFilter, setOrgFilter] = useState("");
  const [selected, setSelected] = useState(null);

  React.useEffect(() => {
    if (!externalSelectedId) setSelected(null);
  }, [externalSelectedId]);

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
        const name = (r.name || r.code || "").toLowerCase();
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
    <Paper variant="outlined" sx={{ p: 2, mt: 1 }}>
      <Typography variant="subtitle1" sx={{ mb: 1 }}>{t('team-selection', 'Teams')}</Typography>
      <Grid container spacing={2} sx={{ mb: 1 }}>
        <Grid item xs={12} md={6}>
          <TextField fullWidth label={t('filter', 'Filter')} value={q} onChange={(e) => setQ(e.target.value)} placeholder={t('filter-placeholder', 'Search by name, code or id')} />
        </Grid>
        <Grid item xs={12} md={4}>
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

      <TableContainer component={Paper} sx={{ maxHeight: '35vh' }}>
        <Table stickyHeader size="small">
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
            {selected ? (
              <TableRow>
                <TableCell colSpan={5}>
                  <Paper variant="outlined" sx={{ p: 2, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                    <div>
                      <Typography sx={{ fontWeight: 700 }}>{selected.name || selected.code} (#{selected.id})</Typography>
                      <Typography variant="caption" color="text.secondary">{selected.status || '-'}</Typography>
                    </div>
                    <div>
                      <Button variant="contained" color="primary" sx={{ mr: 1 }} onClick={() => { onSelect(selected); }}>{t('apply-selection', 'Apply')}</Button>
                      <Button variant="outlined" onClick={() => { setSelected(null); onSelect(null); }}>{t('clear-selection', 'Clear selection')}</Button>
                    </div>
                  </Paper>
                </TableCell>
              </TableRow>
            ) : (
              filtered.map((r) => (
                <TableRow key={r.id} hover>
                  <TableCell>{r.id}</TableCell>
                  <TableCell>
                    <Typography sx={{ fontWeight: 600 }}>{r.name || r.code}</Typography>
                    <Typography variant="caption" color="text.secondary">{r.description || ''}</Typography>
                  </TableCell>
                  <TableCell>{r.status || '-'}</TableCell>
                  <TableCell>{(r.organization && r.organization.name) || r.organizationName || r.organizationId || '-'}</TableCell>
                  <TableCell>
                    <Button size="small" variant="outlined" onClick={() => setSelected(r)}>{t('select', 'Select')}</Button>
                  </TableCell>
                </TableRow>
              ))
            )}
          </TableBody>
        </Table>
      </TableContainer>
    </Paper>
  );
}
