import {
  Alert,
  Box,
  CircularProgress,
  Grid,
  Paper,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Typography,
  List,
  ListItem,
  ListItemIcon,
  ListItemText,
  Divider,
} from "@mui/material";
import Button from "@mui/material/Button";

import { AdapterDayjs } from "@mui/x-date-pickers/AdapterDayjs";
import { LocalizationProvider } from "@mui/x-date-pickers/LocalizationProvider";
import { MobileDateTimePicker } from "@mui/x-date-pickers/MobileDateTimePicker";

import dayjs from "dayjs";
import LocationOnIcon from '@mui/icons-material/LocationOn';
import PublicIcon from '@mui/icons-material/Public';
import MapIcon from '@mui/icons-material/Map';
import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { useSelector } from "react-redux";
import { useLocation, useNavigate } from "react-router-dom";
import { toast } from "react-toastify";
import { useGetEmergencyByIdQuery } from "../../api/emergencyApi";
import {
  useGetEmergencyLogsByEmergencyIdQuery,
  useGetGlobalStatisticsByEmergencyIdQuery,
} from "../../api/logApi";
import LandingMap from "../map/LandingMap";
import { untransformCoordinates } from "../../app/utils/coordinatesTransformations";
import { useGetQuadrantByCoordinatesQuery } from "../../api/quadrantApi";
import { selectToken } from "../user/login/LoginSlice";
import BackButton from "../utils/BackButton";
import formatDate from "../../utils/formatDate";

export default function EmergencyHistoryView() {
  const token = useSelector(selectToken);
  const location = useLocation();
  const navigate = useNavigate();
  const { t } = useTranslation();
  const { i18n } = useTranslation("home");
  const locale = i18n.language;

  const [quadrants, setQuadrants] = useState([]);
  const [selectedStartDate, setSelectedStartDate] = useState(dayjs());
  const [selectedEndDate, setSelectedEndDate] = useState(dayjs());
  const [areDatesValid, setAreDatesValid] = useState(true);

  const emergencyId = location.state.emergencyId;


  const { data: emergencyData, isLoading: isEmergencyLoading, isError: isEmergencyError } = useGetEmergencyByIdQuery({
    token: token,
    emergencyId: emergencyId,
    locale: locale,
  });

  const { data: globalStatistics, isError: isStatsError } = useGetGlobalStatisticsByEmergencyIdQuery({
    token: token,
    emergencyId: emergencyId,
    locale: locale,
  });

  const payload = {
    token: token,
    emergencyId: emergencyId,
    startDate: dayjs(selectedStartDate).format("YYYY-MM-DD"),
    endDate: dayjs(selectedEndDate).format("YYYY-MM-DD"),
    locale: locale
  };

  const { data: emergencyLogs, isError: isLogsError } = useGetEmergencyLogsByEmergencyIdQuery(payload, {
    refetchOnMountOrArgChange: true,
  });

  let quadCoordsForQuery = null;
  if (emergencyData && emergencyData.location) {
    quadCoordsForQuery = { lon: emergencyData.location.lon, lat: emergencyData.location.lat };
  }

  const { data: quadrantByCoordinates } = useGetQuadrantByCoordinatesQuery(
    quadCoordsForQuery ? quadCoordsForQuery : undefined,
    { skip: !quadCoordsForQuery }
  );

  useEffect(() => {
    if (emergencyData) {
      setSelectedStartDate(dayjs(emergencyData.createdAt));
      setSelectedEndDate(dayjs(emergencyData.resolvedAt));
    }
  }, [emergencyData]);


  const handleStartDateChange = (date) => {
    if (date >= selectedEndDate) {
      toast.error(t("start-date-after-end-date"));
      setAreDatesValid(false);
    }
    else {
      setAreDatesValid(true);
      setSelectedStartDate(date);
    }
  };

  const handleEndDateChange = (date) => {
    if (date < selectedStartDate) {
      toast.error(t("end-date-before-start-date"));
      setAreDatesValid(false);
    }
    else {
      setAreDatesValid(true);
      setSelectedEndDate(date);
    }
  };

  useEffect(() => {
    function getQuadrants() {
      const map = new Map();

      function extractFromEntry(entry) {
        if (entry.quadrantInfo) return { quadrant: entry.quadrantInfo, ts: entry.eventAt || entry.linkedAt || entry.assignedAt };
        if (entry.quadrant) return { quadrant: entry.quadrant, ts: entry.eventAt || entry.linkedAt || entry.assignedAt };
        if (entry.assignment && entry.assignment.quadrantInfo) return { quadrant: entry.assignment.quadrantInfo, ts: entry.eventAt || entry.assignment.assignedAt || entry.assignment.acceptedAt };
        if (entry.assignment && entry.assignment.emergencyInfo && Array.isArray(entry.assignment.emergencyInfo.quadrantInfo)) {
          return { quadrant: entry.assignment.emergencyInfo.quadrantInfo, ts: entry.eventAt };
        }
        return null;
      }

      for (let i = 0; i < emergencyLogs.length; i++) {
        const entry = emergencyLogs[i];

        // Filter logs so we only consider entries related to the current emergency.
        // Different log shapes may include the emergency reference in several places.
        // If we detect a different emergency id, skip the entry.
        try {
          if (entry) {
            if (entry.emergency && entry.emergency.id != null && entry.emergency.id !== emergencyId) continue;
            if (entry.emergencyId && entry.emergencyId !== emergencyId) continue;
            if (entry.assignment && entry.assignment.emergencyInfo && entry.assignment.emergencyInfo.id != null && entry.assignment.emergencyInfo.id !== emergencyId) continue;
            if (entry.assignment && entry.assignment.emergency && entry.assignment.emergency.id != null && entry.assignment.emergency.id !== emergencyId) continue;
          }
        } catch (e) {
          // ignore malformed entries and continue
        }

        const extracted = extractFromEntry(entry);
        if (!extracted) continue;

        const ts = extracted.ts || entry.eventAt || null;

        const quadrants = Array.isArray(extracted.quadrant) ? extracted.quadrant : [extracted.quadrant];

        for (const q of quadrants) {
          if (!q || q.id == null) continue;
          const existing = map.get(q.id);
          const coords = q.coordinates || q.coords || q.geometry || null;

          const eventTs = ts ? new Date(ts).toISOString() : null;

          if (!existing) {
            map.set(q.id, {
              quadrant: q,
              linkedAt: eventTs,
              resolvedAt: eventTs,
            });
          } else {
            if (eventTs) {
              if (!existing.linkedAt || eventTs < existing.linkedAt) existing.linkedAt = eventTs;
              if (!existing.resolvedAt || eventTs > existing.resolvedAt) existing.resolvedAt = eventTs;
            }
          }
        }
      }

      const uniqueQuadrants = Array.from(map.values()).map((v) => ({ quadrant: v.quadrant, linkedAt: v.linkedAt, resolvedAt: v.resolvedAt }));
      setQuadrants(uniqueQuadrants);
    }

    if (emergencyLogs) {
      getQuadrants(emergencyLogs);
    }
  }, [emergencyLogs]);



  if (isEmergencyLoading) {
    return (
      <Box sx={{ padding: 3, display: "flex", justifyContent: "center" }}>
        <CircularProgress />
      </Box>
    );
  }

  if (isEmergencyError) {
    return (
      <Box sx={{ padding: 3 }}>
        <BackButton />
        <Alert severity="error">{t("generic-error")}</Alert>
      </Box>
    );
  }

  return (
    <Box sx={{ padding: 3 }}>
      <BackButton />
      <Typography
        variant="h4"
        margin={1}
        sx={{ fontWeight: "bold", color: "primary.light" }}
      >
        {t("emergency-history-title")}
      </Typography>
      {emergencyData && (
        <Typography variant="h6" margin={1}>
          {emergencyData.description} ({"#" + emergencyId})
        </Typography>
      )}
      <Grid
        container
        spacing={{ xs: 2, md: 3 }}
        columns={{ xs: 4, sm: 8, md: 12 }}
      >
        <Grid item xs={12} sm={12} md={6}>
          <Paper
            sx={{
              color: "primary.light",
              padding: 2,
              height: "530px",
            }}
            variant="outlined"
          >
            <Typography variant="h6" sx={{ padding: 2 }}>
              {t("quadrant-map")}
            </Typography>
            <Box sx={{ height: "90%", padding: 1 }}>
              {isLogsError ? (
                <Alert severity="error">{t("generic-error")}</Alert>
              ) : (
                <LandingMap quadrants={quadrants} emergencies={emergencyData && emergencyData.location ? [{ id: emergencyData.id, description: emergencyData.description, location: { lon: emergencyData.location.lon, lat: emergencyData.location.lat }, emergencyTypeName: emergencyData.emergencyTypeName }] : []} />
              )}
            </Box>
          </Paper>
        </Grid>
        <Grid item xs={12} sm={12} md={6}>
          <Paper
            sx={{
              color: "primary.light",
              padding: 2,
              height: "530px",
            }}
            variant="outlined"
          >
            <Typography variant="h6">{t("tittle-date-picker")}</Typography>
            {emergencyData && (
              <div
              >
                <Box
                  sx={{
                    color: "primary.light",
                    padding: 2,
                    display: "flex",
                    transform: "scale(0.9)"
                  }}
                  variant="outlined"
                >
                  <LocalizationProvider dateAdapter={AdapterDayjs}>
                    <MobileDateTimePicker
                      disableToolbar
                      ampm={false}
                      label={t("start-date-picker")}
                      value={selectedStartDate}
                      onChange={handleStartDateChange}
                      format="DD-MM-YYYY HH:mm"
                    />
                  </LocalizationProvider>
                  <LocalizationProvider dateAdapter={AdapterDayjs}>
                    <MobileDateTimePicker
                      ampm={false}
                      label={t("end-date-picker")}
                      value={selectedEndDate}
                      onChange={handleEndDateChange}
                      format="DD-MM-YYYY HH:mm"
                    />
                  </LocalizationProvider>
                </Box>
                {emergencyData && emergencyData.location && (!emergencyData.quadrantInfo || emergencyData.quadrantInfo.length === 0) ? (
                  <Paper variant="outlined" sx={{ p: 2, mt: 2 }}>
                    <Typography variant="subtitle1" sx={{ mb: 1 }}>{t('linked-point-info', 'Linked Point')}</Typography>
                    <Divider sx={{ mb: 1 }} />
                    {(() => {
                      try {
                        const rawX = emergencyData.location.lon;
                        const rawY = emergencyData.location.lat;
                        const geo = untransformCoordinates(rawX, rawY);
                        const quadrantName = quadrantByCoordinates?.nombre || quadrantByCoordinates?.name || t('quadrant-name-unknown');
                        // try to obtain an id for the quadrant from possible response shapes
                        const quadrantIdFromCoords = quadrantByCoordinates?.id || quadrantByCoordinates?.gid || (quadrantByCoordinates?.data && (quadrantByCoordinates.data.id || quadrantByCoordinates.data.gid));
                        return (
                          <List dense disablePadding>
                            <ListItem>
                              <ListItemIcon>
                                <PublicIcon color="primary" />
                              </ListItemIcon>
                              <ListItemText primary={t('geographic-coordinates', 'Geographic coords')} secondary={`${geo.longitude.toFixed(6)}, ${geo.latitude.toFixed(6)}`} />
                            </ListItem>
                            <ListItem>
                              <ListItemIcon>
                                <MapIcon color="primary" />
                              </ListItemIcon>
                              <ListItemText
                                primary={t('quadrant-name', 'Quadrant')}
                                secondary={
                                  quadrantIdFromCoords ? (
                                    // navigate to quadrant-history with the currently selected date range and emergency id
                                    <Button
                                      variant="text"
                                      onClick={() =>
                                        navigate("/quadrant-history", {
                                            state: {
                                              quadrantId: quadrantIdFromCoords,
                                              startDate: selectedStartDate ? selectedStartDate.format("YYYY-MM-DD") : (emergencyData && emergencyData.createdAt ? dayjs(emergencyData.createdAt).format("YYYY-MM-DD") : dayjs().subtract(1, 'year').format("YYYY-MM-DD")),
                                              endDate: selectedEndDate ? selectedEndDate.format("YYYY-MM-DD") : (emergencyData && emergencyData.resolvedAt ? dayjs(emergencyData.resolvedAt).format("YYYY-MM-DD") : dayjs().add(10, 'year').format("YYYY-MM-DD")),
                                              emergencyId: emergencyData && emergencyData.id ? emergencyData.id : emergencyId,
                                            },
                                        })
                                      }
                                      sx={{ textTransform: "none" }}
                                    >
                                      {quadrantName}
                                    </Button>
                                  ) : (
                                    quadrantName
                                  )
                                }
                              />
                            </ListItem>
                          </List>
                        );
                      } catch (e) {
                        return <Typography variant="body2">{t('coordinate-conversion-error', 'Failed to transform coordinates')}</Typography>;
                      }
                    })()}
                  </Paper>
                ) : (
                  areDatesValid && <TableContainer
                    component={Paper}
                    elevation={3}
                    sx={{
                      maxHeight: 350,
                    }}
                  >
                    <Table stickyHeader aria-label="sticky table">
                      <TableHead>
                        <TableRow>
                          <TableCell sx={{ color: "secondary.light" }}>
                            {t("quadrant-id")}
                          </TableCell>
                          <TableCell
                            sx={{ color: "secondary.light" }}
                            align="right"
                          >
                            {t("quadrant-name")}
                          </TableCell>
                          <TableCell
                            sx={{ color: "secondary.light" }}
                            align="right"
                          >
                            {t("quadrant-linkedAt")}
                          </TableCell>
                          <TableCell
                            sx={{ color: "secondary.light" }}
                            align="right"
                          >
                            {t("quadrant-resolvedAt")}
                          </TableCell>
                        </TableRow>
                      </TableHead>
                      <TableBody>
                        {quadrants && quadrants.length > 0 ? (
                          quadrants.map((item, index) => (
                            <TableRow
                              key={(item.quadrant && item.quadrant.id ? item.quadrant.id : index) + "-" + index}
                              hover
                              sx={{
                                "&:last-child td, &:last-child th": {
                                  border: 0,
                                },
                              }}
                            >
                              <TableCell component="th" scope="row">
                                {item.quadrant.id}
                              </TableCell>
                              <TableCell align="right">
                                <Button
                                  onClick={() =>
                                    navigate("/quadrant-history", {
                                        state: {
                                          quadrantId: item.quadrant.id,
                                          // use selected date range from the picker so quadrant-history shows the same time window
                                          startDate: selectedStartDate ? selectedStartDate.format("YYYY-MM-DD") : (emergencyData && emergencyData.createdAt ? dayjs(emergencyData.createdAt).format("YYYY-MM-DD") : (item.linkedAt ? dayjs(item.linkedAt).format("YYYY-MM-DD") : dayjs().subtract(1, 'year').format("YYYY-MM-DD"))),
                                          endDate: selectedEndDate ? selectedEndDate.format("YYYY-MM-DD") : (emergencyData && emergencyData.resolvedAt ? dayjs(emergencyData.resolvedAt).format("YYYY-MM-DD") : (item.resolvedAt ? dayjs(item.resolvedAt).format("YYYY-MM-DD") : dayjs().add(10, 'year').format("YYYY-MM-DD"))),
                                          emergencyId: emergencyData && emergencyData.id ? emergencyData.id : emergencyId,
                                        },
                                    })
                                  }
                                  variant="text"
                                  sx={{ textTransform: "none" }}
                                >
                                  {item.quadrant.nombre}
                                </Button>
                              </TableCell>
                              <TableCell align="right">{item.linkedAt ? formatDate(item.linkedAt, locale) : '-'}</TableCell>
                              <TableCell align="right">
                                {item.resolvedAt ? formatDate(item.resolvedAt, locale) : '-'}
                              </TableCell>
                            </TableRow>
                          ))
                        ) : (
                          // No quadrants affected for this emergency in the selected date range
                          null
                        )}
                      </TableBody>
                    </Table>
                  </TableContainer>)}
                {!areDatesValid && <Typography variant="body" color="error.light">{t("date-invalid-body")}</Typography>
                }
              </div>
            )}
          </Paper>
        </Grid>
        <Grid item xs={12} sm={12} md={12}>
          {isStatsError ? (
            <Alert severity="error">{t("generic-error")}</Alert>
          ) : globalStatistics && (
            <Paper
              sx={{
                padding: 2,
              }}
              variant="outlined"
            >
              <div
                style={{
                  display: "flex",
                  flexDirection: "column",
                  justifyContent: "center",
                  alignItems: "left",
                  padding: "5px",
                }}
              >
                <Typography
                  variant="h6"
                  sx={{
                    display: "flex",
                    justifyContent: "center",
                    alignItems: "center",
                    color: "primary.light",
                  }}
                >
                  {t("global-statistics")}
                </Typography>
                <TableContainer>
                  <Table>
                    <TableBody>
                      <TableRow>
                        <TableCell
                          component="th"
                          scope="row"
                          sx={{ color: "secondary.light", padding: "20px" }}
                        >
                          {t("teams-mobilized")}
                        </TableCell>
                        <TableCell align="center">
                          {globalStatistics.teamsMobilized}
                        </TableCell>
                      </TableRow>
                      <TableRow>
                        <TableCell
                          component="th"
                          scope="row"
                          sx={{ color: "secondary.light", padding: "20px" }}
                        >
                          {t("vehicles-mobilized")}
                        </TableCell>
                        <TableCell align="center">
                          {globalStatistics.vehiclesMobilized}
                        </TableCell>
                      </TableRow>
                      <TableRow>
                        <TableCell
                          component="th"
                          scope="row"
                          sx={{ color: "secondary.light", padding: "20px" }}
                        >
                          {t("affected-quadrants")}
                        </TableCell>
                        <TableCell align="center">
                          {globalStatistics.affectedQuadrants}
                        </TableCell>
                      </TableRow>
                      <TableRow>
                        <TableCell
                          component="th"
                          scope="row"
                          sx={{ color: "secondary.light", padding: "20px" }}
                        >
                          {t("max-burned-hectares")}
                        </TableCell>
                        <TableCell align="center">
                          {globalStatistics.maxBurnedHectares} ha
                        </TableCell>
                      </TableRow>
                    </TableBody>
                  </Table>
                </TableContainer>
              </div>
            </Paper>
          )}
        </Grid>
      </Grid>
    </Box >
  );
}
