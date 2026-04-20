import {
  Alert,
  Box,
  Button,
  CircularProgress,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  Fab,
  FormControl,
  Grid,
  InputLabel,
  MenuItem,
  Paper,
  Select,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  TextField,
  Typography,
} from "@mui/material";

import AddIcon from "@mui/icons-material/Add";
import DeleteIcon from '@mui/icons-material/Delete';
import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { useSelector } from "react-redux";
import { useLocation, useNavigate } from "react-router-dom";
import { toast } from "react-toastify";
import {
  useResolveEmergencyMutation,
  useRemoveQuadrantByEmergencyIdMutation,
  useGetEmergencyByIdQuery,
  useUpdateEmergencyMutation,
  useLinkEmergencyToPointMutation,
} from "../../api/emergencyApi";
import { useGetQuadrantByCoordinatesQuery } from "../../api/quadrantApi";
import { useLinkQuadrantsMutation } from "../../api/emergencyApi";
import LandingMap from "../map/LandingMap";
import CoordinatesMap from "../map/CoordinatesMap";
import QuadrantDataGrid from "../quadrant/QuadrantDataGrid";
import { selectToken } from "../user/login/LoginSlice";
import BackButton from "../utils/BackButton";
import WeatherInfo from "../weather/WeatherInfo";
import { untransformCoordinates } from "../../app/utils/coordinatesTransformations";
import Radio from '@mui/material/Radio';
import RadioGroup from '@mui/material/RadioGroup';
import FormControlLabel from '@mui/material/FormControlLabel';
import FormLabel from '@mui/material/FormLabel';
import EmergencyTypeIcon from "../../components/EmergencyTypeIcon";
import { useGetEmergencyTypesQuery } from "../../api/emergencyApi";

const emergencyIndexSelector = ["CERO", "UNO", "DOS", "TRES"];


export default function EmergencyDetailsView() {
  const token = useSelector(selectToken);

  const location = useLocation();
  const emergencyId = location.state.emergencyId || location.state.emergencyId;

  const { t } = useTranslation();
  const { i18n } = useTranslation("home");
  const locale = i18n.language;

  const navigate = useNavigate();

  const [open, setOpen] = useState(false);
  const [selectedId, setSelectedId] = useState(true);
  const [quadrantId, setQuadrantId] = useState(true);

  const [description, setDescription] = useState();
  const [type, setType] = useState();
  const [emergencyIndex, setEmergencyIndex] = useState();
  const [emergencyTypeId, setEmergencyTypeId] = useState();

  const [openEdit, setOpenEdit] = useState(false);
  const [openQuadrantResolve, setOpenQuadrantResolve] = useState(false);
  const [openResolve, setOpenResolve] = useState(false);
  const [linkMode, setLinkMode] = useState(''); // '' means not preselected; can be 'QUADRANT' or 'POINT'
  const [pointLon, setPointLon] = useState('');
  const [pointLat, setPointLat] = useState('');
  const [openPointPicker, setOpenPointPicker] = useState(false);
  const [openPointConfirm, setOpenPointConfirm] = useState(false);
  const [pendingProjPoint, setPendingProjPoint] = useState(null); // { x, y }


  const [coordinates, setCoordinates] = useState(null);

  const payload = { token: token, emergencyId: emergencyId, locale: locale };

  const { data, refetch, isLoading, isError } = useGetEmergencyByIdQuery(payload);

  // Local error boundary to catch unexpected render errors from child components
  class WeatherErrorBoundary extends React.Component {
    constructor(props) {
      super(props);
      this.state = { hasError: false };
    }
    static getDerivedStateFromError() {
      return { hasError: true };
    }
    componentDidCatch(error, info) {
      console.error('WeatherErrorBoundary caught error', error, info);
      toast.error(t('generic-error'));
    }
    render() {
      if (this.state.hasError) {
        return null;
      }
      return this.props.children;
    }
  }

  const [linkQuadrants] = useLinkQuadrantsMutation ? useLinkQuadrantsMutation() : [null];
  const [resolveEmergency] = useResolveEmergencyMutation();
  const [removeQuadrantByEmergencyId] = useRemoveQuadrantByEmergencyIdMutation();
  const [linkEmergencyToPoint] = useLinkEmergencyToPointMutation();
  // fetch quadrant by projected coordinates (backend expects projected coords)
  // Prefer using data.location (already in projected coords) when available to avoid roundtrip transforms
  let quadCoordsForQuery = null;
  if (data && data.location) {
    // data.location has lon/lat in projected coordinates
    quadCoordsForQuery = { lon: data.location.lon, lat: data.location.lat };
  } else if (coordinates) {
    try {
      const proj = transformCoordinates(coordinates.lon, coordinates.lat);
      quadCoordsForQuery = { lon: proj.longitude, lat: proj.latitude };
    } catch (e) {
      console.error('Failed to transform geographic to projected for quadrant query', e);
    }
  }

  const { data: quadrantByCoordinates } = useGetQuadrantByCoordinatesQuery(
    quadCoordsForQuery ? quadCoordsForQuery : undefined,
    { skip: !quadCoordsForQuery }
  );

  const quadrantName = quadrantByCoordinates?.nombre || quadrantByCoordinates?.name || quadrantByCoordinates?.data?.nombre || quadrantByCoordinates?.data?.name || t('quadrant-name-unknown');


  useEffect(() => {
    refetch();

    if (data) {
      const hasQuadrants = data.quadrantInfo && data.quadrantInfo.length > 0;
      const hasLocation = data.location != null;


      // If emergency already has quadrants or location, lock the mode to that value. Otherwise allow user to choose.
      if (hasQuadrants) setLinkMode('QUADRANT');
      else if (hasLocation) setLinkMode('POINT');
      else setLinkMode('');

      // compute coordinates for WeatherInfo: prefer quadrant centroid, otherwise emergency.location
      if (hasQuadrants) {
        try {
          const q = data.quadrantInfo[0];
          // quadrant coordinates array contains objects with x/y in DB projection
          const coord = q.coordinates && q.coordinates[0];
          if (coord) {
            const geo = untransformCoordinates(coord.x, coord.y);
            setCoordinates({ lon: geo.longitude, lat: geo.latitude });
          }
        } catch (e) {
          console.error('Failed to compute coords from quadrant', e);
          setCoordinates(null);
        }
      } else if (hasLocation) {
        try {
          // location stored in DB may be in projected coordinates (easting/northing)
          const rawX = data.location.lon; // easting in DB projection
          const rawY = data.location.lat; // northing in DB projection
          const geo = untransformCoordinates(rawX, rawY);
          setCoordinates({ lon: geo.longitude, lat: geo.latitude });
          // keep projected string values for display
          setPointLon(String(rawX));
          setPointLat(String(rawY));
        } catch (e) {
          console.error('Failed to compute coords from location', e);
          setCoordinates(null);
        }
      } else {
        setCoordinates(null);
      }

    }
  }
    , [refetch, data]);

  useEffect(() => {
    console.debug('EmergencyDetailsView raw data', data);
  }, [data]);

  const childToParent = (childdata) => {
    setSelectedId(childdata);
  };

  const handleOpenClick = () => {
    setOpen(true);
  };

  const handleClose = () => {
    setOpen(false);
  };

  const handleClick = () => {
    const payload = {
      token: token,
      emergencyId: emergencyId,
      quadrantGids: Array.isArray(selectedId) ? selectedId : [selectedId],
      locale: locale,
    };


    if (!linkQuadrants) {
      console.error('linkQuadrants mutation not available');
      return;
    }

    linkQuadrants(payload)
      .unwrap()
      .then(() => {
        toast.success(t("quadrant-linked-successfully"));
        refetch();
        handleClose();
      })
      .catch((error) => {
        console.error('Failed to link quadrants', error);
        toast.error(t("quadrant-linked-error"));
      });
  };

  const handleResolveOpenClick = () => {
    setOpenResolve(true);
  };

  const handleResolveClose = () => {
    setOpenResolve(false);
  };

  const handleResolveClick = () => {
    const payload = {
      token: token,
      emergencyId: emergencyId,
      locale: locale
    };

    resolveEmergency(payload)
      .unwrap()
      .then(() => {
        toast.success(t("emergency-resolved-successfully"));
        setOpenResolve(false);
        navigate("/emergency-management");
      })
      .catch((error) => toast.error(t("emergency-resolved-error")));
  };


  const handleCloseEdit = () => {
    setOpenEdit(false);
  };

  const handleClickOpenEdit = (data) => {
    setDescription(data.description);
    setType(data.type);
    setEmergencyIndex(data.emergencyIndex);
    // try to set emergencyTypeId from several possible fields
    if (data.emergencyTypeId) setEmergencyTypeId(Number(data.emergencyTypeId));
    else if (data.emergencyType && data.emergencyType.id) setEmergencyTypeId(Number(data.emergencyType.id));
    else {
      // attempt to resolve by name if emergencyTypes already loaded
      const nameToMatch = data.emergencyTypeName || data.type || data.name || '';
      if (nameToMatch && emergencyTypes && emergencyTypes.length > 0) {
        const matched = emergencyTypes.find((et) => (et.name || '').toLowerCase() === nameToMatch.toLowerCase());
        if (matched) {
          setEmergencyTypeId(Number(matched.id));
        } else {
          setEmergencyTypeId(undefined);
        }
      } else {
        setEmergencyTypeId(undefined);
      }
    }
    setOpenEdit(true);
  };

  const [updateEmergency] = useUpdateEmergencyMutation();
  const { data: emergencyTypes } = useGetEmergencyTypesQuery({ token: token, locale: locale });

  const handleEditClick = () => {
    // ensure we have an emergencyTypeId; try to derive from type name if missing
    let resolvedEmergencyTypeId = emergencyTypeId;
    if (!resolvedEmergencyTypeId && type && emergencyTypes && emergencyTypes.length > 0) {
      const matched = emergencyTypes.find((et) => (et.name || '').toLowerCase() === (type || '').toLowerCase());
      if (matched) resolvedEmergencyTypeId = Number(matched.id);
    }

    if (!resolvedEmergencyTypeId) {
      // do not submit invalid payload
      toast.error(t('Please select an emergency type'));
      console.error('Cannot update emergency: emergencyTypeId is missing', { emergencyTypeId, type });
      return;
    }

    const payload = {
      emergencyId: emergencyId,
      token: token,
      description: description,
      type: type,
      emergencyTypeId: resolvedEmergencyTypeId,
      emergencyIndex: emergencyIndex,
      locale: locale,
    };

    updateEmergency(payload)
      .unwrap()
      .then((payload) => {
        toast.success(t("emergency-updated-successfully"));
      })
      .catch((error) => toast.error(t("emergency-updated-error")));

    refetch();
    handleCloseEdit();
  };

  const handleChange = (event) => {
    var id = event.target.id;
    var value = event.target.value;

    switch (id) {
      case "description":
        setDescription(value);
        break;
      case "type":
        setType(value);
        break;
      default:
        setEmergencyIndex(value);
        break;
    }
  };

  const handleEmergencyTypeChange = (event) => {
    const value = event.target.value;
    const valueNum = typeof value === 'string' && value.trim() !== '' ? Number(value) : value;
    setEmergencyTypeId(valueNum);
    const selected = emergencyTypes ? emergencyTypes.find((et) => et.id === valueNum) : null;
    if (selected) setType(selected.name);
  };


  const handleResolveQuadrantOpenClick = (quadrantId) => {
    setQuadrantId(quadrantId);
    setOpenQuadrantResolve(true);
  };

  const handleResolveQuadrantClose = () => {
    setOpenQuadrantResolve(false);
  };

  // legacy/alternate name used in the template for the 'resolve' button
  const handleExtinguishOpenClick = () => {
    setOpenResolve(true);
  };

  const handleResolveQuadrantClick = () => {
    const payload = {
      token: token,
      emergencyId: emergencyId,
      quadrantId: quadrantId,
      locale: locale
    };

    removeQuadrantByEmergencyId(payload)
      .unwrap()
      .then(() => {
        toast.success(t("quadrant-resolved-successfully"));
        setOpenResolve(false);
        refetch();
      })
      .catch((error) => toast.error(t("quadrant-resolved-error")));
    handleResolveQuadrantClose();
  };

  if (isLoading) {
    return (
      <Box sx={{ padding: 3, display: "flex", justifyContent: "center" }}>
        <CircularProgress />
      </Box>
    );
  }

  if (isError) {
    return (
      <Box sx={{ padding: 3 }}>
        <BackButton />
        <Alert severity="error">{t("generic-error")}</Alert>
      </Box>
    );
  }

  return (
    <Box sx={{ padding: 3 }}>
      {/* determine if emergency already has quadrants or a linked point (computed inline where needed) */}
      <BackButton />
      <Typography
        variant="h4"
        margin={1}
        sx={{ fontWeight: "bold", color: "primary.light" }}
      >
        {t("emergency-details-title")}
      </Typography>
      {data && (
        <Paper
          sx={{
            color: "primary.light",
            padding: 2,
            marginBottom: 2,
            marginTop: 1,
            marginLeft: "auto",
            marginRight: "auto",
            backgroundColor: "rgba(0, 0, 0, 0.02)",
            display: "flex",
            justifyContent: "center",
          }}
          variant="outlined"
        >
          <Box sx={{ display: "flex", gap: 3, flexWrap: "wrap", justifyContent: "center" }}>
            <Box>
              <Typography variant="overline" sx={{ color: "secondary.light" }} display="block">
                {t("emergency-id")}
              </Typography>
              <Typography variant="h6" fontWeight="bold">
                #{data.id}
              </Typography>
            </Box>
            <Box>
              <Typography variant="overline" sx={{ color: "secondary.light" }} display="block">
                {t("emergency-description")}
              </Typography>
              <Typography variant="h6" fontWeight="bold">
                {data.description}
              </Typography>
            </Box>
            <Box>
              <Typography variant="overline" sx={{ color: "secondary.light" }} display="block">
                {t("emergency-type")}
              </Typography>
              <Typography variant="h6" fontWeight="bold" sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                <EmergencyTypeIcon
                  name={data.emergencyTypeName || (data.emergencyType && data.emergencyType.name) || data.type}
                  showLabel={true}
                />
              </Typography>
            </Box>
            <Box>
              <Typography variant="overline" sx={{ color: "secondary.light" }} display="block">
                {t("emergency-emergencyIndex")}
              </Typography>
              <Typography variant="h6" fontWeight="bold">
                {data.emergencyIndex}
              </Typography>
            </Box>
          </Box>
        </Paper>
      )
      }
      <Grid
        container
        spacing={{ xs: 2, md: 3 }}
        columns={{ xs: 4, sm: 8, md: 12 }}
      >
        <Grid item xs={4} sm={8} md={6}>
          <Paper
            sx={{
              color: "primary.light",
              padding: 2,
            }}
            variant="outlined"
          >
            <Typography variant="h6">{t("quadrant-map")}</Typography>
            {data && <Box sx={{ height: 450 }}>
              <LandingMap
                quadrants={data.quadrantInfo || []}
                emergencies={data.location ? [{
                  id: data.id,
                  description: data.description,
                  location: { lon: data.location.lon, lat: data.location.lat },
                  // include type fields so LandingMap can select the correct icon
                  type: data.type,
                  emergencyTypeName: data.emergencyTypeName || data.type,
                }] : []}
              />
            </Box>}
          </Paper>
        </Grid>
        <Grid item xs={4} sm={8} md={3}>
          {data && (
            <Paper
              sx={{
                color: "primary.light",
                padding: 2,
              }}
              variant="outlined"
            >
              <Typography variant="h6">{t("quadrant-link")}</Typography>
              <Box sx={{ mt: 1, mb: 2 }}>
                <FormLabel component="legend">{t('link-mode-label') || 'Link mode'}</FormLabel>
                {(() => {
                  const hasQuadrants = data && data.quadrantInfo && data.quadrantInfo.length > 0;
                  const hasLocation = data && data.location != null;
                  return (
                    <RadioGroup
                      row
                      value={linkMode}
                      onChange={(e) => setLinkMode(e.target.value)}
                    >
                      <FormControlLabel
                        value="QUADRANT"
                        control={<Radio />}
                        label={t('link-mode-quadrant') || 'Quadrant'}
                        disabled={hasLocation}
                      />
                      <FormControlLabel
                        value="POINT"
                        control={<Radio />}
                        label={t('link-mode-point') || 'Point'}
                        disabled={hasQuadrants}
                      />
                    </RadioGroup>
                  );
                })()}
              </Box>

              {linkMode === 'QUADRANT' ? (
                <>
                  <Typography variant="subtitle1">{t("quadrant-list")}</Typography>
                  <TableContainer
                    component={Paper}
                    elevation={3}
                    sx={{ maxHeight: 320 }}
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
                            {t("options")}
                          </TableCell>
                        </TableRow>
                      </TableHead>
                      <TableBody>
                        {data.quadrantInfo && data.quadrantInfo.length > 0 ? (
                          data.quadrantInfo.map((row) => (
                            <TableRow
                              key={row.id}
                              hover
                              sx={{
                                "&:last-child td, &:last-child th": { border: 0 },
                              }}
                              onClick={() =>
                                navigate("/quadrant", {
                                  state: { quadrantId: row.id, emergencyId: emergencyId },
                                })}
                            >
                              <TableCell component="th" scope="row">
                                {row.id}
                              </TableCell>
                              <TableCell align="right">{row.nombre}</TableCell>
                              <TableCell>
                                <Button
                                  sx={{ color: "red" }}
                                  onClick={(e) => {
                                    e.stopPropagation();
                                    handleResolveQuadrantOpenClick(row.id);
                                  }}
                                >
                                  <DeleteIcon />
                                </Button>
                              </TableCell>
                            </TableRow>
                          ))
                        ) : (
                          <TableRow>
                            <TableCell colSpan={3} align="center">
                              {t("quadrants-empty-list")}
                            </TableCell>
                          </TableRow>
                        )}
                      </TableBody>
                    </Table>
                  </TableContainer>
                  <Box m={1}>
                    <Fab
                      color="primary"
                      aria-label="add"
                      onClick={() => handleOpenClick()}
                    >
                      <AddIcon />
                    </Fab>
                  </Box>
                </>
              ) : (
                <>
                  <Typography variant="subtitle1">{t('point-link-title') || 'Link to point'}</Typography>
                  <Box sx={{ display: 'flex', flexDirection: 'column', gap: 1, mt: 1 }}>
                    {data && data.location == null ? (
                      <>
                        <Button variant="contained" onClick={() => setOpenPointPicker(true)}>{t('select-point-on-map') || 'Select point on map'}</Button>
                      </>
                    ) : (
                      <Box>
                        <Typography variant="body2">{quadrantByCoordinates?.name || quadrantByCoordinates?.nombre || t('quadrant-name-unknown') || '-'}</Typography>
                      </Box>
                    )}

                  </Box>
                </>
              )}
            </Paper>
          )}
        </Grid>
        <Grid item xs={4} sm={8} md={3} >
          {data && (
            <Paper
              sx={{
                color: "primary.light",
                padding: 2,
              }}
              variant="outlined"
            >
              <Typography variant="h6">{t("emergency-options")}</Typography>
              <Button
                fullWidth
                variant="contained"
                sx={{ margin: "5px" }}
                onClick={() => handleClickOpenEdit(data)}
              >
                {t("edit")}
              </Button>
              <Button
                variant="contained"
                fullWidth
                sx={{
                  backgroundColor: "error.light",
                  margin: "5px",
                  ":hover": { backgroundColor: "error.dark" },
                }}
                onClick={() => handleResolveOpenClick()}
              >
                {t("emergency-resolve")}
              </Button>
              <Dialog
                open={openQuadrantResolve}
                onClose={handleResolveQuadrantClose}
                aria-labelledby="alert-dialog-title"
                aria-describedby="alert-dialog-description"
              >
                <DialogTitle id="alert-dialog-title" sx={{ color: "primary.light" }}>
                  {t("quadrant-resolve-dialog")}
                </DialogTitle>
                <DialogContent>
                  <Typography variant="body2">
                    {t("quadrant-resolve-text")}
                  </Typography>
                </DialogContent>
                <DialogActions>
                  <Button onClick={handleResolveQuadrantClose}>{t("cancel")}</Button>
                  <Button
                    onClick={handleResolveQuadrantClick}
                    color="error"
                    autoFocus
                  >
                    {t("quadrant-resolve")}
                  </Button>
                </DialogActions>
              </Dialog>
              <Dialog
                open={openResolve}
                onClose={handleResolveClose}
                aria-labelledby="alert-dialog-title"
                aria-describedby="alert-dialog-description"
              >
                <DialogTitle id="alert-dialog-title" sx={{ color: "primary.light" }}>
                  {t("emergency-resolve-dialog")}
                </DialogTitle>
                <DialogContent>
                  <Typography variant="body2">
                    {t("emergency-resolve-text")}
                  </Typography>
                </DialogContent>
                <DialogActions>
                  <Button onClick={handleResolveClose}>{t("cancel")}</Button>
                  <Button
                    onClick={handleResolveClick}
                    color="error"
                    autoFocus
                  >
                    {t("emergency-resolve")}
                  </Button>
                </DialogActions>
              </Dialog>
            </Paper>
          )}

          {(() => {
            if (coordinates && coordinates.lat != null && coordinates.lon != null) {
              return <WeatherErrorBoundary><WeatherInfo sx={{ padding: 2 }} lat={coordinates.lat} lon={coordinates.lon} /></WeatherErrorBoundary>;
            }
            return null;
          })()}

        </Grid>
      </Grid>



      <Dialog open={open} fullWidth maxWidth="md">
        <DialogTitle sx={{ color: "primary.light" }}>{t("quadrant-add-title")} </DialogTitle>
        <DialogContent>
          <QuadrantDataGrid
            childToParent={childToParent}
            excludedQuadrantIds={data?.quadrantInfo ? data.quadrantInfo.map((q) => q.id) : []}
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={handleClose}>{t("cancel")}</Button>
          <Button autoFocus variant="contained" onClick={() => handleClick()}>
            {t("add")}
          </Button>
        </DialogActions>
      </Dialog>

      {/* Point picker dialog (reuse CoordinatesMap) */}
      <Dialog fullWidth={true} maxWidth={"md"} open={openPointPicker} onClose={() => setOpenPointPicker(false)}>
        <DialogTitle sx={{ color: "primary.light" }}>{t('select-point-on-map') || 'Select point on map'}</DialogTitle>
        <DialogContent sx={{ height: '60vh', width: '100%' }}>
          <Box sx={{ height: '100%', width: '100%' }}>
            <CoordinatesMap childToParent={(childdata) => {
              // childdata is [projectedX, projectedY]
              const projX = childdata[0];
              const projY = childdata[1];
              // store projected values as strings (same as existing textfields)
              setPointLon(String(projX));
              setPointLat(String(projY));

              // compute geographic coords for WeatherInfo
              try {
                const geo = untransformCoordinates(projX, projY);
                setCoordinates({ lon: geo.longitude, lat: geo.latitude });
              } catch (e) {
                console.error('Failed to compute coords from point picker', e);
              }

              // open confirmation dialog before linking
              setPendingProjPoint({ x: projX, y: projY });
              setOpenPointPicker(false);
              setOpenPointConfirm(true);
            }} />
          </Box>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setOpenPointPicker(false)}>{t('cancel') || 'Cancel'}</Button>
        </DialogActions>
      </Dialog>

      {/* Confirmation dialog before linking the selected point */}
      <Dialog open={openPointConfirm} onClose={() => { setOpenPointConfirm(false); setPendingProjPoint(null); }}>
        <DialogTitle>{t('confirm-link-point-title') || 'Confirm link point'}</DialogTitle>
        <DialogContent>
          <Typography>{t('confirm-link-point-text') || 'Do you want to link the emergency to the selected point?'}</Typography>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => { setOpenPointConfirm(false); setPendingProjPoint(null); }}>{t('cancel') || 'Cancel'}</Button>
          <Button variant="contained" onClick={() => {
            if (!pendingProjPoint) return;
            const projX = pendingProjPoint.x;
            const projY = pendingProjPoint.y;
            const payloadPoint = { token: token, emergencyId: emergencyId, lon: projX, lat: projY, locale: locale };
            linkEmergencyToPoint(payloadPoint)
              .unwrap()
              .then(() => {
                toast.success(t('quadrant-linked-successfully'));
                refetch();
              })
              .catch(() => {
                toast.error(t('quadrant-linked-error'));
              })
              .finally(() => {
                setOpenPointConfirm(false);
                setPendingProjPoint(null);
              });
          }}>{t('confirm') || 'Confirm'}</Button>
        </DialogActions>
      </Dialog>

      <Dialog maxWidth={"md"} open={openEdit}>
        <DialogTitle sx={{ color: "primary.light" }}>{t("emergency-updated-title")}</DialogTitle>
        <DialogContent>
          <FormControl>
            <Grid container spacing={2}>
              <Grid item xs={12}>
                <TextField
                  id="description"
                  label={t("emergency-description")}
                  type="text"
                  autoComplete="current-code"
                  margin="normal"
                  value={description}
                  onChange={(e) => handleChange(e)}
                  variant="standard"
                  required
                  sx={{ display: "flex" }}
                />
              </Grid>
              <Grid item xs={6}>
                <FormControl fullWidth variant="outlined">
                  <InputLabel id="emergency-type-label">{t("emergency-type")}</InputLabel>
                  <Select
                    id="emergencyTypeId"
                    labelId="emergency-type-label"
                    label={t("emergency-type")}
                    value={emergencyTypeId || ''}
                    onChange={handleEmergencyTypeChange}
                    required
                  >
                    {emergencyTypes && emergencyTypes.map((et) => (
                      <MenuItem key={et.id} value={et.id}>
                        {et.name}
                      </MenuItem>
                    ))}
                  </Select>
                </FormControl>
              </Grid>
              <Grid item xs={6}>
                <FormControl fullWidth >
                  <InputLabel id="input-label-id" >
                    {t("emergency-emergencyIndex")}
                  </InputLabel>
                  <Select
                    id="emergencyIndex"
                    label={t("emergency-emergencyIndex")}
                    value={emergencyIndex}
                    onChange={(e) => handleChange(e)}
                    required
                    sx={{ margin: 2 }}
                  >
                    {emergencyIndexSelector.map((item, index) => (
                      <MenuItem key={index} value={item}>
                        {item}
                      </MenuItem>
                    ))}
                  </Select>
                </FormControl >
              </Grid>

            </Grid>
          </FormControl>
        </DialogContent>
        <DialogActions>
          <Button onClick={handleCloseEdit}>{t("cancel")}</Button>
          <Button
            autoFocus
            variant="contained"
            onClick={(e) => handleEditClick(e)}
          >
            {t("edit")}
          </Button>
        </DialogActions>
      </Dialog>
    </Box >
  );
}
