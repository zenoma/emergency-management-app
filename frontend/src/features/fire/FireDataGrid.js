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
import * as React from "react";
import { useState } from "react";
import { useTranslation } from "react-i18next";
import { useSelector } from "react-redux";
import { useNavigate } from "react-router-dom";
import { toast } from "react-toastify";
import { useCreateFireMutation, useGetFiresQuery } from "../../api/fireApi";
import { selectToken, selectUser } from "../user/login/LoginSlice";

export default function FireDataGrid() {
  const token = useSelector(selectToken);
  const userRole = useSelector(selectUser).userRole;

  const { t } = useTranslation();
  const { i18n } = useTranslation("home");
  const [open, setOpen] = useState(false);
  const [pageSize, setPageSize] = useState(10);

  const [description, setDescription] = useState("");
  const [fireType, setFireType] = useState("");

  const handleClickOpen = () => {
    setOpen(true);
  };

  const handleClose = () => {
    setDescription("");
    setFireType("");
    setOpen(false);
  };

  const navigate = useNavigate();

  var localeText;

  if (i18n.language === "es") {
    localeText = esES.components.MuiDataGrid.defaultProps.localeText;
  }

  const locale = i18n.language;

  const {
    data: fires,
    error,
    isLoading,
    refetch,
  } = useGetFiresQuery(
    { token: token, locale: locale },
    {
      refetchOnMountOrArgChange: true,
    }
  );

  const [createFire] = useCreateFireMutation();

  const data = {
    columns: [
      {
        field: "id",
        headerName: t("fire-id"),
        groupable: false,
        aggregable: false,
      },
      {
        field: "description",
        headerName: t("fire-description"),
        minWidth: 200,
        hide: true,
      },
      {
        field: "type",
        headerName: t("fire-type"),
        groupable: false,
        minWidth: 100,
        aggregable: false,
      },
      {
        field: "fireIndex",
        headerName: t("fire-index"),
        groupable: false,
        minWidth: 150,
        aggregable: false,
      },

      {
        field: "createdAt",
        headerName: t("fire-created-at"),
        minWidth: 200,
        aggregable: false,
      },
      {
        field: "extinguishedAt",
        headerName: t("fire-extinguished-at"),
        minWidth: 200,
        aggregable: false,
      },
    ],
    rows: fires,
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

  const handleChange = (event) => {
    var id = event.target.id;
    var value = event.target.value;

    if (id === "description") {
      setDescription(value);
    }
    if (id === "fireType") {
      setFireType(value);
    }
  };

  const handleRowClick = (row) => {
    navigate("/fire-details", { state: { fireId: row.id } });
  };

  const handleDisabledRowClick = (row) => {
    navigate("/fire-history", { state: { fireId: row.id } });
  };

  const handleClick = () => {
    const payload = {
      token: token,
      description: description,
      type: fireType,
      locale: locale,
    };

    createFire(payload)
      .unwrap()
      .then(() => {
        toast.success(t("fire-created-successfully"));
        refetch();
        handleClose();
      })
      .catch((error) => toast.error(t("fire-created-error")));
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
    items: [{ columnField: "fireIndex", operatorValue: 'isAnyOf', value: ['CERO', 'UNO', 'DOS', 'TRES'], label: 'Extinguido' }]
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
      ) : fires ? (
        <Box
          sx={{
            height: 490,
            width: "100%",
            "& .disabled": {
              backgroundColor: "lightgrey",
              "&:hover": {
                backgroundColor: "darkgrey",
              },
            },
            "& .active": {
              backgroundColor: "error.light",
              "&:hover": {
                backgroundColor: "error.dark",
              },
            },
          }}
        >
          <Typography
            variant="h4"
            margin={1}
            sx={{ fontWeight: "bold", color: "primary.light" }}
          >
            {t("fire-list")}
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
              if (params.row.fireIndex === "EXTINGUIDO") {
                return "disabled";
              } else {
                return "active";
              }
            }}
            onRowClick={(e) =>
              e.row.fireIndex === "EXTINGUIDO"
                ? handleDisabledRowClick(e.row)
                : handleRowClick(e.row)
            }
          />
          {userRole === "COORDINATOR" && <Box m={1}>
            <Fab color="primary" aria-label="add" onClick={handleClickOpen}>
              <AddIcon />
            </Fab>
          </Box>}
          <Dialog fullWidth open={open} onClose={handleClose}>
            <DialogTitle sx={{ color: "primary.light" }}>{t("fire-create-title")} </DialogTitle>
            <DialogContent>
              <FormControl>
                <Grid container spacing={2}>
                  <Grid item xs={6}>
                    <TextField
                      id="description"
                      label={t("fire-description")}
                      type="text"
                      autoComplete="current-description"
                      margin="normal"
                      value={description}
                      onChange={(e) => handleChange(e)}
                      required
                      variant="standard"
                    />
                  </Grid>
                  <Grid item xs={6}>
                    <TextField
                      id="fireType"
                      label={t("fire-type")}
                      type="text"
                      autoComplete="current-type"
                      margin="normal"
                      value={fireType}
                      onChange={(e) => handleChange(e)}
                      required
                      variant="standard"
                    />
                  </Grid>
                </Grid>
              </FormControl>
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
