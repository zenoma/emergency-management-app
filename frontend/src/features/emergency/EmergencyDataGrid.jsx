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
import { useCreateEmergencyMutation, useGetEmergenciesQuery, useGetEmergencyTypesQuery } from "../../api/emergencyApi";
import EmergencyTypeIcon from '../../components/EmergencyTypeIcon';
import { selectToken, selectUser } from "../user/login/LoginSlice";

export default function EmergencyDataGrid() {
  const token = useSelector(selectToken);
  const userRole = useSelector(selectUser).userRole;

  const { t } = useTranslation();
  const { i18n } = useTranslation("home");
  const [open, setOpen] = useState(false);
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
        minWidth: 200,
        hide: true,
      },
      {
        field: "type",
        headerName: t("emergency-type"),
        groupable: false,
        minWidth: 140,
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
        field: "emergencyIndex",
        headerName: t("emergency-index"),
        groupable: false,
        minWidth: 150,
        aggregable: false,
      },

      {
        field: "createdAt",
        headerName: t("emergency-created-at"),
        minWidth: 200,
        aggregable: false,
        valueGetter: (params) => {
          const v = params.row ? params.row.createdAt : null;
          if (!v) return "";
          try {
            return new Date(v).toLocaleString(locale);
          } catch (e) {
            return v;
          }
        },
      },
      {
        field: "resolvedAt",
        headerName: t("emergency-resolved-at"),
        minWidth: 200,
        aggregable: false,
        valueGetter: (params) => {
          const v = params.row ? params.row.resolvedAt : null;
          if (!v) return "";
          try {
            return new Date(v).toLocaleString(locale);
          } catch (e) {
            return v;
          }
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

    createEmergency(payload)
      .unwrap()
      .then(() => {
        toast.success(t("emergency-created-successfully"));
        refetch();
        handleClose();
      })
      .catch((error) => toast.error(t("emergency-created-error")));
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
    items: [{ columnField: "emergencyIndex", operatorValue: 'isAnyOf', value: ['CERO', 'UNO', 'DOS', 'TRES'], label: 'Extinguido' }]
  };

  const [filterModel, setFilterModel] = useState(statusFilterModel);

  const handleFilterModelChange = (model) => {
    setFilterModel(model);
  };

  return (
    <Box style={{ height: 600 }}>
      {error ? (
        <h1>{t("generic-error")}</h1>
      ) : isLoading ? (
        <div>Loading</div>
      ) : emergencies ? (
        <Box
          sx={{
            height: 490,
            width: "100%",
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
              if (params.row.emergencyIndex === "EXTINGUIDO") {
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
              e.row.emergencyIndex === "EXTINGUIDO"
                ? handleDisabledRowClick(e.row)
                : handleRowClick(e.row)
            }
          />
          {userRole === "COORDINATOR" && <Box m={1}>
            <Fab color="primary" aria-label="add" onClick={handleClickOpen}>
              <AddIcon />
            </Fab>
          </Box>}
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
        </Box>
      ) : null}
    </Box>
  );
}
