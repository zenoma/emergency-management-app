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
  MenuItem,
  FormControl,
  InputLabel,
  Select,
  IconButton,
  Typography,
} from "@mui/material";
import CloseIcon from '@mui/icons-material/Close';
import { LocalizationProvider } from "@mui/x-date-pickers/LocalizationProvider";
import { AdapterDayjs } from "@mui/x-date-pickers/AdapterDayjs";
import { DatePicker } from "@mui/x-date-pickers/DatePicker";
import dayjs from "dayjs";
import { useTranslation } from "react-i18next";

export default function EmergencySelector({ open, onClose, emergencies = [], types = [], onSelect }) {
  const { t } = useTranslation();
  const [q, setQ] = useState("");
  const [typeFilter, setTypeFilter] = useState("");
  const [dateFrom, setDateFrom] = useState(null);
  const [dateTo, setDateTo] = useState(null);

  const filtered = useMemo(() => {
    const ql = (q || "").toLowerCase();
    return (emergencies || []).filter((e) => {
      if (e.resolvedAt) return false;
      if (typeFilter && String(e.emergencyTypeId || e.emergencyType) !== String(typeFilter)) return false;
      if (ql) {
        const created = e.createdAt ? formatDate(e.createdAt, locale) : "";
        const inText = (e.description || "").toLowerCase().includes(ql) || (e.emergencyTypeName || "").toLowerCase().includes(ql) || String(e.emergencyIndex || "").toLowerCase().includes(ql) || created.toLowerCase().includes(ql) || String(e.id).includes(ql);
        if (!inText) return false;
      }
      if (dateFrom) {
        const d = e.createdAt ? new Date(e.createdAt) : null;
        if (!d || d < dayjs(dateFrom).toDate()) return false;
      }
      if (dateTo) {
        const d = e.createdAt ? new Date(e.createdAt) : null;
        if (!d || d > dayjs(dateTo).endOf('day').toDate()) return false;
      }
      return true;
    });
  }, [emergencies, q, typeFilter, dateFrom, dateTo]);

  return (
    <Dialog open={open} onClose={onClose} fullWidth maxWidth="lg">
      <DialogTitle sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
        {t('emergency-selection', 'Select emergency')}
        <IconButton onClick={onClose}><CloseIcon /></IconButton>
      </DialogTitle>
      <DialogContent>
        <Grid container spacing={2} sx={{ mb: 1 }}>
          <Grid item xs={12} md={4}>
            <TextField fullWidth label={t('filter', 'Filter')} value={q} onChange={(e) => setQ(e.target.value)} placeholder={t('filter-placeholder', 'Search by name, type, index, id or date')} />
          </Grid>
          <Grid item xs={6} md={2}>
            <LocalizationProvider dateAdapter={AdapterDayjs}>
              <DatePicker label={t('date-from', 'Date from')} value={dateFrom} onChange={(d) => setDateFrom(d)} renderInput={(params) => <TextField {...params} fullWidth />} />
            </LocalizationProvider>
          </Grid>
          <Grid item xs={6} md={2}>
            <LocalizationProvider dateAdapter={AdapterDayjs}>
              <DatePicker label={t('date-to', 'Date to')} value={dateTo} onChange={(d) => setDateTo(d)} renderInput={(params) => <TextField {...params} fullWidth />} />
            </LocalizationProvider>
          </Grid>
          <Grid item xs={12} md={2} sx={{ display: 'flex', alignItems: 'center' }}>
            <Button fullWidth variant="outlined" onClick={() => { setQ(''); setTypeFilter(''); setDateFrom(null); setDateTo(null); }}>{t('clear-filters', 'Clear filters')}</Button>
          </Grid>
        </Grid>

        <TableContainer component={Paper} sx={{ maxHeight: '50vh' }}>
          <Table stickyHeader>
            <TableHead>
              <TableRow>
                <TableCell>{t('id', 'ID')}</TableCell>
                <TableCell>{t('emergency-name', 'Name')}</TableCell>
                <TableCell>{t('date', 'Date')}</TableCell>
                <TableCell>{t('emergency-type', 'Type')}</TableCell>
                <TableCell>{t('emergency-index', 'Index')}</TableCell>
                <TableCell>{t('actions', 'Actions')}</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {filtered.map((e) => (
                <TableRow key={e.id} hover>
                  <TableCell>{e.id}</TableCell>
                  <TableCell>
                    <Typography sx={{ fontWeight: 600 }}>{e.description}</Typography>
                    <Typography variant="caption" color="text.secondary">{e.location ? `${t('linked-point', 'Linked point')}` : e.quadrantInfo && e.quadrantInfo.length ? `${t('linked-quadrants', 'Linked quadrants')}: ${e.quadrantInfo.length}` : ''}</Typography>
                  </TableCell>
                  <TableCell>{e.createdAt ? formatDate(e.createdAt, locale) : '-'}</TableCell>
                  <TableCell>{e.emergencyTypeName || '-'}</TableCell>
                  <TableCell>{e.emergencyIndex || '-'}</TableCell>
                  <TableCell>
                    <Button size="small" variant="outlined" onClick={() => { onSelect(e); onClose(); }}>{t('select', 'Select')}</Button>
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
