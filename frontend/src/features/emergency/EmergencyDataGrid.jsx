import AddIcon from "@mui/icons-material/Add";
import {
  Box,
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  Fab,
  FormControl,
  Grid,
  TextField,
  InputLabel,
  MenuItem,
  Select,
  Typography,
} from "@mui/material";
import {
  DataGrid,
  esES,
  GridToolbarColumnsButton,
  GridToolbarContainer,
  GridToolbarDensitySelector,
  GridToolbarFilterButton,
} from "@mui/x-data-grid";
import { useState } from "react";
import { useTranslation } from "react-i18next";
import { useSelector } from "react-redux";
import { useNavigate } from "react-router-dom";
import { toast } from "react-toastify";
import { Snackbar, Alert } from '@mui/material';
import { useCreateEmergencyMutation, useGetEmergenciesQuery, useGetEmergencyTypesQuery } from "../../api/emergencyApi";
import { useGetQuadrantByCoordinatesQuery } from "../../api/quadrantApi";
import EmergencyTypeIcon from '../../components/EmergencyTypeIcon';
import { selectToken, selectUser } from "../user/login/LoginSlice";
import formatDate from '../../utils/formatDate';

function LocationSummaryCell({ location, quadrantCount, locale, loadingLabel }) {
  const { t } = useTranslation();
  const hasLocation = location != null && location.lon != null && location.lat != null;
  const { data: quadrant, isError } = useGetQuadrantByCoordinatesQuery(
    hasLocation ? { lon: location.lon, lat: location.lat } : { lon: null, lat: null },
    { skip: !hasLocation }
  );

  if (hasLocation) {
    if (quadrant?.nombre) {
      return quadrant.nombre;
    }
    if (isError) {
      return `${location.lon}, ${location.lat}`;
    }
    return loadingLabel;
  }

  if (quadrantCount > 0) {
    return t("quadrants-count", { count: quadrantCount, defaultValue: `${quadrantCount} quadrants` });
  }

  return '-';
}

export default function EmergencyDataGrid() {
  const token = useSelector(selectToken);
  const userRole = useSelector(selectUser).userRole;

  const { t } = useTranslation();
  const { i18n } = useTranslation("home");
  const [open, setOpen] = useState(false);
  const [snackbarOpen, setSnackbarOpen] = useState(false);
  const [snackbarMsg, setSnackbarMsg] = useState("");
  const [snackbarSeverity, setSnackbarSeverity] = useState("success");
  const [pageSize, setPageSize] = useState(10);

  const [description, setDescription] = useState("");
  const [emergencyType, setEmergencyType] = useState("");
  const [emergencyTypeId, setEmergencyTypeId] = useState("");
  const emergencyIndexSelector = ["CERO", "UNO", "DOS", "TRES"];
  const [emergencyIndex, setEmergencyIndex] = useState("");

  const handleClickOpen = () => {
    setOpen(true);
  };

  const handleClose = () => {
    setDescription("");
    setEmergencyType("");
    setEmergencyTypeId("");
    setEmergencyIndex("");
    setOpen(false);
  };

  const navigate = useNavigate();

  var localeText;

  if (i18n.language === "es") {
    localeText = esES.components.MuiDataGrid.defaultProps.localeText;
  }

  const locale = i18n.language;

  const {
    data: emergencies,
    error,
    isLoading,
    refetch,
  } = useGetEmergenciesQuery(
    { token: token, locale: locale },
    {
      refetchOnMountOrArgChange: true,
    }
  );

  const [createEmergency] = useCreateEmergencyMutation();

  const data = {
    columns: [
      {
        field: "id",
        headerName: t("emergency-id"),
        groupable: false,
        aggregable: false,
      },
      {
        field: "description",
        headerName: t("emergency-description"),
        minWidth: 320,
        flex: 1,
        valueGetter: (params) => params.row?.description || '-',
      },
      {
        field: "type",
        headerName: t("emergency-type"),
        groupable: false,
        minWidth: 240,
        flex: 1,
        aggregable: false,
        // valueGetter provides the plain string used for sorting/filtering
        valueGetter: (params) => {
          const row = params.row || {};
          return row.emergencyTypeName || (row.emergencyType && row.emergencyType.name) || row.type || "";
        },
        renderCell: (params) => {
          const name = params.value || "";
          return <EmergencyTypeIcon name={name} showLabel={true} />;
        },
      },
      {
        field: "locationSummary",
        headerName: t("emergency-location"),
        minWidth: 220,
        flex: 1,
        aggregable: false,
        renderCell: (params) => {
          const row = params.row || {};
          const quadrantCount = Array.isArray(row.quadrantInfo) ? row.quadrantInfo.length : 0;
          return (
            <LocationSummaryCell
              location={row.location}
              quadrantCount={quadrantCount}
              locale={locale}
              loadingLabel={t('loading', 'Loading')}
            />
          );
        },
      },
      {
        field: "emergencyIndex",
        headerName: t("emergency-index"),
        groupable: false,
        minWidth: 160,
        aggregable: false,
      },

      {
        field: "createdAt",
        headerName: t("emergency-created-at"),
        minWidth: 190,
        flex: 1,
        aggregable: false,
        valueGetter: (params) => {
          const v = params.row ? params.row.createdAt : null;
          if (!v) return "";
          return formatDate(v, locale);
        },
      },
      {
        field: "resolvedAt",
        headerName: t("emergency-resolved-at"),
        minWidth: 190,
        flex: 1,
        aggregable: false,
        valueGetter: (params) => {
          const v = params.row ? params.row.resolvedAt : null;
          if (!v) return "";
          return formatDate(v, locale);
        },
      },
    ],
    rows: emergencies,
    initialState: {
      columns: {
        columnVisibilityModel: {
          id: true,
          email: true,
          firstName: true,
          lastName: true,
          dni: true,
          phoneNumber: true,
          userRole: true,
          hasTeam: true,
        },
      },
    },
  };

  const { data: emergencyTypes } = useGetEmergencyTypesQuery({ token: token, locale: locale });

  const handleChange = (event) => {
    var id = event.target.id;
    var value = event.target.value;

    if (id === "description") {
      setDescription(value);
    }
    if (id === "type") {
      setEmergencyType(value);
    }
  };

  const handleEmergencyTypeChange = (event) => {
    const value = event.target.value;
    const valueNum = typeof value === 'string' && value.trim() !== '' ? Number(value) : value;
    setEmergencyTypeId(valueNum);
    const selected = emergencyTypes ? emergencyTypes.find((et) => et.id === valueNum) : null;
    if (selected) setEmergencyType(selected.name);
  };

  const handleRowClick = (row) => {
    navigate("/emergency-details", { state: { emergencyId: row.id } });
  };

  const handleDisabledRowClick = (row) => {
    navigate("/emergency-history", { state: { emergencyId: row.id } });
  };

  const handleClick = () => {
    if (!description.trim() || (!emergencyTypeId && !emergencyType.trim()) || !emergencyIndex) {
      toast.error(t("emergency-required-fields"));
      return;
    }

    const payload = {
      token: token,
      description: description,
      type: emergencyType,
      emergencyTypeId: emergencyTypeId,
      emergencyIndex: emergencyIndex,
      locale: locale,
    };

    console.log('EmergencyDataGrid: creating emergency with payload', payload);
    createEmergency(payload)
      .unwrap()
      .then((res) => {
        console.log('EmergencyDataGrid: createEmergency success', res);
        // keep toast for global notifications
        toast.success(t("emergency-created-successfully"));
        // also show local Snackbar to ensure visibility in this view
        setSnackbarMsg(t("emergency-created-successfully"));
        setSnackbarSeverity('success');
        setSnackbarOpen(true);
        const emergencyId = res?.id;
        if (emergencyId) {
          handleClose();
          navigate("/emergency-details/", { state: { emergencyId } });
          return;
        }

        refetch();
        handleClose();
      })
      .catch((error) => {
        console.error('EmergencyDataGrid: createEmergency error', error);
        const errMsg = error?.data?.errorMessage || error?.data || error?.message || t('emergency-created-error');
        toast.error(typeof errMsg === 'string' ? errMsg : t('emergency-created-error'));
        setSnackbarMsg(typeof errMsg === 'string' ? errMsg : t('emergency-created-error'));
        setSnackbarSeverity('error');
        setSnackbarOpen(true);
      });
  };

  function CustomToolbar() {
    return (
      <GridToolbarContainer>
        <GridToolbarColumnsButton />
        <GridToolbarFilterButton />
        <GridToolbarDensitySelector />
      </GridToolbarContainer>
    );
  }

  const statusFilterModel = {
    items: [{ columnField: "emergencyIndex", operatorValue: 'isAnyOf', value: ['CERO', 'UNO', 'DOS', 'TRES'] }]
  };

  const [filterModel, setFilterModel] = useState(statusFilterModel);

  const handleFilterModelChange = (model) => {
    setFilterModel(model);
  };

  return (
    <Box sx={{ height: '100%', display: 'flex', flexDirection: 'column', minHeight: 0 }}>
      {error ? (
        <h1>{t("generic-error")}</h1>
      ) : isLoading ? (
        <div>{t("loading")}</div>
      ) : emergencies ? (
        <Box
          sx={{
            flex: 1,
            minHeight: 0,
            width: "100%",
            display: 'flex',
            flexDirection: 'column',
            "& .extinguido": {
              backgroundColor: "lightgrey",
              "&:hover": {
                backgroundColor: "darkgrey",
              },
            },
            "& .cero": {
              backgroundColor: "#ffcdd2",
              "&:hover": {
                backgroundColor: "#ef9a9a",
              },
            },
            "& .uno": {
              backgroundColor: "#ef9a9a",
              "&:hover": {
                backgroundColor: "#e57373",
              },
            },
            "& .dos": {
              backgroundColor: "#e57373",
              "&:hover": {
                backgroundColor: "#ef5350",
              },
            },
            "& .tres": {
              backgroundColor: "#ef5350",
              "&:hover": {
                backgroundColor: "#e53935",
              },
            },
          }}
        >
          <Typography
            variant="h4"
            margin={1}
            sx={{ fontWeight: "bold", color: "primary.light" }}
          >
            {t("emergency-list")}
          </Typography>
          <DataGrid
            {...data}
            components={{ Toolbar: CustomToolbar }}
            componentsProps={{
              pagination: {
                labelRowsPerPage: t("rows-per-page"),
              },
            }}
            pageSize={pageSize}
            onPageSizeChange={(newPageSize) => setPageSize(newPageSize)}
            rowsPerPageOptions={[10, 25, 50]}
            pagination
            localeText={localeText}
            filterModel={filterModel}
            onFilterModelChange={handleFilterModelChange}
            getRowClassName={(params) => {
              if (params.row.emergencyIndex === "EXTINGUIDO" || params.row.emergencyIndex === "RESUELTO") {
                return "extinguido";
              } else if (params.row.emergencyIndex === "CERO") {
                return "cero";
              } else if (params.row.emergencyIndex === "UNO") {
                return "uno";
              } else if (params.row.emergencyIndex === "DOS") {
                return "dos";
              } else if (params.row.emergencyIndex === "TRES") {
                return "tres";
              }
            }}
            onRowClick={(e) =>
              (e.row.emergencyIndex === "EXTINGUIDO" || e.row.emergencyIndex === "RESUELTO")
                ? handleDisabledRowClick(e.row)
                : handleRowClick(e.row)
            }
            sx={{ flex: 1, minHeight: 0 }}
          />
          {userRole === "COORDINATOR" && (
            <Box sx={{ display: 'flex', justifyContent: 'flex-end', mt: 1, flexShrink: 0 }}>
              <Button variant="contained" color="secondary" onClick={handleClickOpen}>{t('create-emergency','Create emergency')}</Button>
            </Box>
          )}
          <Dialog open={open} onClose={handleClose} maxWidth="sm" fullWidth>
            <DialogTitle sx={{ color: "primary.light" }}>{t("emergency-create-title")} </DialogTitle>
            <DialogContent>
              <Grid container spacing={2} sx={{ mt: 1 }}>
                <Grid item xs={12}>
                  <TextField
                    id="description"
                    label={t("emergency-description")}
                    type="text"
                    autoComplete="current-description"
                    value={description}
                    onChange={(e) => handleChange(e)}
                    required
                    variant="outlined"
                    fullWidth
                  />
                </Grid>
                <Grid item xs={12}>
                  <FormControl fullWidth variant="outlined">
                    <InputLabel id="emergency-type-label">{t("emergency-type")}</InputLabel>
                    <Select
                      id="emergencyTypeId"
                      labelId="emergency-type-label"
                      label={t("emergency-type")}
                      value={emergencyTypeId}
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
                <Grid item xs={12}>
                  <FormControl fullWidth variant="outlined">
                    <InputLabel id="emergency-index-label">
                      {t("emergency-emergencyIndex")}
                    </InputLabel>
                    <Select
                      id="emergencyIndex"
                      labelId="emergency-index-label"
                      label={t("emergency-emergencyIndex")}
                      value={emergencyIndex}
                      onChange={(e) => setEmergencyIndex(e.target.value)}
                      required
                    >
                      {emergencyIndexSelector.map((item, index) => (
                        <MenuItem key={index} value={item}>
                          {item}
                        </MenuItem>
                      ))}
                    </Select>
                  </FormControl>
                </Grid>
              </Grid>
            </DialogContent>
          <DialogActions>
            <Button onClick={handleClose}>{t("cancel")}</Button>
            <Button
              autoFocus
              variant="contained"
              onClick={() => handleClick()}
            >
              {t("create")}
            </Button>
          </DialogActions>
        </Dialog>
        <Snackbar open={snackbarOpen} autoHideDuration={4000} onClose={() => setSnackbarOpen(false)} anchorOrigin={{ vertical: 'bottom', horizontal: 'center' }}>
          <Alert onClose={() => setSnackbarOpen(false)} severity={snackbarSeverity} sx={{ width: '100%' }}>
            {snackbarMsg}
          </Alert>
        </Snackbar>
        </Box>
      ) : null}
    </Box>
  );
}
