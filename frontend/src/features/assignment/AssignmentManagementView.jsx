import React, { useMemo, useState } from "react";
import {
  Box,
  Typography,
  Paper,
  TextField,
  MenuItem,
  Button,
  Grid,
  FormControl,
  InputLabel,
  Select,
  Autocomplete,
  Radio,
  RadioGroup,
  FormControlLabel,
} from "@mui/material";
import { toast } from 'react-toastify';
import { useNavigate } from 'react-router-dom';
import { useTranslation } from "react-i18next";
import { useSelector } from "react-redux";
import { selectToken, selectUser } from "../user/login/LoginSlice";
import { useGetEmergenciesQuery } from "../../api/emergencyApi";
import { useCreateAssignmentMutation } from "../../api/assignmentApi";
import EmergencySelector from "./EmergencySelector";
import EmergencyInline from "./EmergencyInline";
import TeamInline from "./TeamInline";
import VehicleInline from "./VehicleInline";
import './assignmentView.css';
import formatDate from '../../utils/formatDate';

export default function AssignmentManagementView() {
  const { t, i18n } = useTranslation();
  const token = useSelector(selectToken);
  const user = useSelector(selectUser);
  const navigate = useNavigate();

  const locale = i18n?.language || "es";

  const { data: emergencies = [], isLoading: loadingEmergencies } = useGetEmergenciesQuery({ token, locale });

  const [createAssignment, { isLoading: creating }] = useCreateAssignmentMutation();

  const [selectedEmergency, setSelectedEmergency] = useState("");
  const [selectedQuadrant, setSelectedQuadrant] = useState("");
  const [selectedResourceType, setSelectedResourceType] = useState("TEAM");
  const [selectedTeamObj, setSelectedTeamObj] = useState(null);
  const [selectedVehicleObj, setSelectedVehicleObj] = useState(null);
  const [notes, setNotes] = useState("");
  const [selectorOpen, setSelectorOpen] = useState(false);

  const emergencyObj = useMemo(() => emergencies.find((e) => String(e.id) === String(selectedEmergency)), [emergencies, selectedEmergency]);

  const emergencyQuadrants = useMemo(() => {
    if (!emergencyObj) return [];
    return emergencyObj.quadrantInfo || [];
  }, [emergencyObj]);


  const canCreate = useMemo(() => {
    if (!selectedEmergency) return false;
    const em = emergencies.find((e) => String(e.id) === String(selectedEmergency));
    if (!em) return false;
    const hasQuadrants = em.quadrantInfo && em.quadrantInfo.length > 0;
    const hasPoint = !!em.location;
    if (!hasQuadrants && !hasPoint) return false;
    if (hasQuadrants && !selectedQuadrant) return false;
    if (selectedResourceType === 'TEAM' && !selectedTeamObj) return false;
    if (selectedResourceType === 'VEHICLE' && !selectedVehicleObj) return false;
    return true;
  }, [selectedEmergency, selectedQuadrant, selectedResourceType, selectedTeamObj, selectedVehicleObj, emergencies]);

  const handleCreate = async () => {
    const resourceId = selectedResourceType === 'TEAM' ? (selectedTeamObj && selectedTeamObj.id) : (selectedVehicleObj && selectedVehicleObj.id);

    let quadrantIdToSend = null;
    try {
      const em = emergencyObj;
      const hasQuadrants = em && em.quadrantInfo && em.quadrantInfo.length > 0;
      const hasPoint = em && em.location;
      if (hasQuadrants) {
        quadrantIdToSend = selectedQuadrant ? Number(selectedQuadrant) : null;
      } else if (hasPoint) {
        quadrantIdToSend = 0;
      } else {
        quadrantIdToSend = null;
      }
    } catch (err) {
      quadrantIdToSend = selectedQuadrant ? Number(selectedQuadrant) : null;
    }

    const payload = {
      emergencyId: Number(selectedEmergency),
      quadrantId: quadrantIdToSend,
      resourceId: Number(resourceId),
      notes: notes,
      token,
      locale,
    };

    console.log('handleCreate: calling createAssignment with payload', payload);
    createAssignment(payload)
      .unwrap()
      .then((response) => {
        console.log('createAssignment response', response);
        toast.success(t('assignment-created', 'Assignment created successfully'));
        // navigate to newly created assignment details
        if (response && response.id) {
          navigate(`/assignments/${response.id}`);
        } else {
          // fallback: reset form
          setSelectedEmergency('');
          setSelectedQuadrant('');
          setSelectedTeamObj(null);
          setSelectedVehicleObj(null);
          setNotes('');
        }
      })
      .catch((e) => {
        console.error('createAssignment error', e);
        let msg = 'Unknown error';
        if (e?.data) {
          try { msg = typeof e.data === 'string' ? e.data : JSON.stringify(e.data); } catch (_) { msg = String(e.data); }
        } else if (e?.error) {
          msg = e.error;
        } else if (e?.status) {
          msg = `Status ${e.status}`;
        } else if (e?.message) {
          msg = e.message;
        }
        toast.error(msg);
      });
  };


  return (
    <Paper
      sx={{
        display: "flex",
        flexDirection: "column",
        padding: "10px",
        minWidth: "1000px",
        height: "calc(100vh - 120px)",
        boxSizing: "border-box",
        overflow: "hidden",
      }}
    >
      <Typography variant="h5" sx={{ mb: 2 }}> {t('create-assignments', 'Creación de asignaciones de emergencias')} </Typography>
      <Box className="parent" sx={{ flex: 1, minHeight: 0, overflow: 'auto' }}>
        <div className="div1">
          <FormControl fullWidth required error={!selectedEmergency}>
            <InputLabel shrink sx={{ mb: 1 }}>{t('emergency-selection', 'Emergency')}</InputLabel>
            <EmergencyInline emergencies={emergencies} onSelect={(e) => { if (e) setSelectedEmergency(e.id); else { setSelectedEmergency(''); setSelectedQuadrant(''); } }} externalSelectedId={selectedEmergency} />
          </FormControl>
          {emergencyQuadrants && emergencyQuadrants.length > 0 && (
            <div style={{ marginTop: 8 }}>
              <FormControl fullWidth required error={emergencyQuadrants && emergencyQuadrants.length > 0 && !selectedQuadrant}>
                <InputLabel id="quadrant-select-label">{t("quadrant-selection", "Select quadrant")}</InputLabel>
                <Select labelId="quadrant-select-label" value={selectedQuadrant} label={t("quadrant-selection", "Select quadrant")} onChange={(e) => setSelectedQuadrant(e.target.value)}>
                  {emergencyQuadrants.map((q) => (
                    <MenuItem key={q.id} value={q.id}>
                      {q.nombre || q.name} (#{q.id})
                    </MenuItem>
                  ))}
                </Select>
              </FormControl>
            </div>
          )}
        </div>

        <div className="div2">
          <FormControl component="fieldset" required error={selectedResourceType === 'TEAM' ? !selectedTeamObj : !selectedVehicleObj}>
            <RadioGroup
              row
              aria-label="resource-type"
              name="resource-type"
              value={selectedResourceType}
              onChange={(e) => { setSelectedResourceType(e.target.value); setSelectedTeamObj(null); setSelectedVehicleObj(null); }}
            >
              <FormControlLabel value="TEAM" control={<Radio />} label={t('team', 'Team')} />
              <FormControlLabel value="VEHICLE" control={<Radio />} label={t('vehicle', 'Vehicle')} />
            </RadioGroup>
          </FormControl>

          <div style={{ marginTop: 8 }}>
            <FormControl fullWidth required error={selectedResourceType === 'TEAM' ? !selectedTeamObj : !selectedVehicleObj}>
              <InputLabel shrink sx={{ mb: 1 }}>{t('resource-selection', 'Select resource')}</InputLabel>
              {selectedResourceType === 'TEAM' ? (
                <TeamInline onSelect={(r) => { if (r) setSelectedTeamObj(r); else setSelectedTeamObj(null); }} organizations={[]} externalSelectedId={selectedTeamObj && selectedTeamObj.id} />
              ) : (
                <VehicleInline onSelect={(r) => { if (r) setSelectedVehicleObj(r); else setSelectedVehicleObj(null); }} organizations={[]} externalSelectedId={selectedVehicleObj && selectedVehicleObj.id} />
              )}
            </FormControl>
          </div>
        </div>

        <div className="div3">
          {selectedEmergency ? (
            <Paper variant="outlined" sx={{ p: 2 }}>
              <Typography variant="subtitle1" sx={{ mb: 1 }}>{t('emergency-details', 'Emergency details')}</Typography>
              {(() => {
                const em = emergencies.find((e) => String(e.id) === String(selectedEmergency));
                if (!em) return null;
                return (
                  <Grid container spacing={1}>
                    <Grid item xs={12}><Typography variant="body2"><strong>{t('emergency-name', 'Name')}:</strong> {em.description}</Typography></Grid>
                    <Grid item xs={6}><Typography variant="body2"><strong>{t('emergency-date', 'Created at')}:</strong> {em.createdAt ? formatDate(em.createdAt, locale) : '-'}</Typography></Grid>
                    <Grid item xs={6}><Typography variant="body2"><strong>{t('emergency-type', 'Type')}:</strong> {em.emergencyTypeName || '-'}</Typography></Grid>
                    <Grid item xs={6}><Typography variant="body2"><strong>{t('emergency-index', 'Index')}:</strong> {em.emergencyIndex || '-'}</Typography></Grid>
                    <Grid item xs={6}><Typography variant="body2"><strong>{t('emergency-location', 'Location')}:</strong> {em.location ? `${em.location.coordinates || JSON.stringify(em.location)}` : '-'}</Typography></Grid>
                  </Grid>
                );
              })()}
            </Paper>
          ) : null}
        </div>

        <div className="div4">
          {((selectedResourceType === 'TEAM' && selectedTeamObj) || (selectedResourceType === 'VEHICLE' && selectedVehicleObj)) ? (
            <Paper variant="outlined" sx={{ p: 2 }}>
              <Typography variant="subtitle1" sx={{ mb: 1 }}>{t('resource-details', 'Resource details')}</Typography>
              {selectedResourceType === 'TEAM' && selectedTeamObj && (
                <Grid container spacing={1}>
                  <Grid item xs={12}><Typography variant="body2"><strong>{t('name', 'Name')}:</strong> {selectedTeamObj.name || selectedTeamObj.code}</Typography></Grid>
                  <Grid item xs={12}><Typography variant="body2"><strong>{t('status', 'Status')}:</strong> {selectedTeamObj.status || '-'}</Typography></Grid>
                  <Grid item xs={12}><Typography variant="body2"><strong>{t('organization', 'Organization')}:</strong> {(selectedTeamObj.organization && selectedTeamObj.organization.name) || selectedTeamObj.organizationName || '-'}</Typography></Grid>
                  <Grid item xs={12}><Typography variant="body2"><strong>{t('deployAt', 'Deployed at')}:</strong> {selectedTeamObj.deployAt ? formatDate(selectedTeamObj.deployAt, locale) : '-'}</Typography></Grid>
                  <Grid item xs={12}><Typography variant="body2"><strong>{t('description', 'Description')}:</strong> {selectedTeamObj.description || '-'}</Typography></Grid>
                </Grid>
              )}

              {selectedResourceType === 'VEHICLE' && selectedVehicleObj && (
                <Grid container spacing={1}>
                  <Grid item xs={12}><Typography variant="body2"><strong>{t('plate', 'Plate')}:</strong> {selectedVehicleObj.vehiclePlate || selectedVehicleObj.plate || '-'}</Typography></Grid>
                  <Grid item xs={12}><Typography variant="body2"><strong>{t('status', 'Status')}:</strong> {selectedVehicleObj.status || '-'}</Typography></Grid>
                  <Grid item xs={12}><Typography variant="body2"><strong>{t('organization', 'Organization')}:</strong> {(selectedVehicleObj.organization && selectedVehicleObj.organization.name) || selectedVehicleObj.organizationName || '-'}</Typography></Grid>
                  <Grid item xs={12}><Typography variant="body2"><strong>{t('deployAt', 'Deployed at')}:</strong> {selectedVehicleObj.deployAt ? formatDate(selectedVehicleObj.deployAt, locale) : '-'}</Typography></Grid>
                  <Grid item xs={12}><Typography variant="body2"><strong>{t('description', 'Description')}:</strong> {selectedVehicleObj.description || '-'}</Typography></Grid>
                </Grid>
              )}
            </Paper>
          ) : null}
        </div>

        <div className="div5">
          <Paper variant="outlined" sx={{ p: 2 }}>
            <TextField fullWidth multiline rows={3} label={t("notes", "Notes")} value={notes} onChange={(e) => setNotes(e.target.value)} />
            <Box sx={{ display: 'flex', gap: 1, mt: 2 }}>
              <Button variant="contained" color="secondary" disabled={!canCreate || creating} onClick={handleCreate}>
                {t("create-assignment", "Create assignment")}
              </Button>
              <Button variant="outlined" color="secondary" onClick={() => { setSelectedEmergency(''); setSelectedQuadrant(''); setSelectedTeamObj(null); setSelectedVehicleObj(null); setNotes(''); }}>{t('clear-all', 'Clear')}</Button>
            </Box>
          </Paper>
        </div>
      </Box>
    </Paper>
  );
}
