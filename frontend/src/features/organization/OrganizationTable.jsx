import PropTypes from "prop-types";
import { useSelector } from "react-redux";

import Paper from "@mui/material/Paper";
import Table from "@mui/material/Table";
import TableBody from "@mui/material/TableBody";
import TableCell from "@mui/material/TableCell";
import TableContainer from "@mui/material/TableContainer";
import TableHead from "@mui/material/TableHead";
import TablePagination from "@mui/material/TablePagination";
import TableRow from "@mui/material/TableRow";

import { Box, Button, IconButton } from "@mui/material";
import {
  useDeleteOrganizationByIdMutation,
  useUpdateOrganizationMutation
} from "../../api/organizationApi";
import { selectToken, selectUser } from "../user/login/LoginSlice";

import DeleteIcon from "@mui/icons-material/Delete";
import EditIcon from "@mui/icons-material/Edit";
import Dialog from "@mui/material/Dialog";
import DialogActions from "@mui/material/DialogActions";
import DialogTitle from "@mui/material/DialogTitle";
import { useState } from "react";
import { toast } from "react-toastify";

import DialogContent from "@mui/material/DialogContent";
import FormControl from "@mui/material/FormControl";
import Grid from "@mui/material/Grid";
import TextField from "@mui/material/TextField";
import React from "react";
import { useTranslation } from "react-i18next";
import { useNavigate } from "react-router-dom";
import CoordinatesMap from "../map/CoordinatesMap";

const columns = [
  { id: "code", label: "organization-code", minWidth: 50 },
  { id: "name", label: "organization-name", minWidth: 250 },
  { id: "address", label: "organization-address", minWidth: 250 },
  { id: "options", label: "options", minWidth: 50 },
];

function createData(id, code, name, address, lon, lat) {
  return { id, code, name, address, lon, lat };
}

export default function OrganizationTable(props) {
  const token = useSelector(selectToken);
  const userRole = useSelector(selectUser).userRole;
  const navigate = useNavigate();
  const { t } = useTranslation();
  const { i18n } = useTranslation("home");
  const locale = i18n.language;

  const [page, setPage] = useState(0);
  const [rowsPerPage, setRowsPerPage] = useState(10);
  const [openDelete, setOpenDelete] = useState(false);
  const [openEdit, setOpenEdit] = useState(false);

  const [organizationId, setOrganizationId] = useState("");
  const [code, setCode] = useState("");
  const [name, setName] = useState("");
  const [headquartersAddress, setHeadquartersAddress] = useState("");
  const [data, setData] = useState("");

  const organizations = props.organizations;

  const handleChangePage = (event, newPage) => {
    setPage(newPage);
  };

  const handleChangeRowsPerPage = (event) => {
    setRowsPerPage(+event.target.value);
    setPage(0);
  };

  const handleCloseEdit = () => {
    setOpenEdit(false);
  };

  const handleClickOpenEdit = (row) => {
    setOrganizationId(row.id);
    setCode(row.code);
    setName(row.name);
    setHeadquartersAddress(row.address);
    setOpenEdit(true);
    setData({ lat: row.lat, lng: row.lon });
  };

  const [updateOrganization] = useUpdateOrganizationMutation();

  const handleEditClick = () => {
    const payload = {
      organizationId: organizationId,
      code: code,
      name: name,
      headquartersAddress: headquartersAddress,
      coordinates: data,
      token: token,
      locale: locale
    };
    updateOrganization(payload)
      .unwrap()
      .then(() => {
        toast.success("Organización actualizada satisfactoriamente");
      })
      .catch((error) =>
        toast.error("No se ha podido actualizar la organización")
      );

    handleCloseEdit();
  };

  const childToParent = (childdata) => {
    setData({ lat: childdata[0], lng: childdata[1] });
  };

  const handleChange = (event) => {
    var id = event.target.id;
    var value = event.target.value;

    switch (id) {
      case "code":
        setCode(value);
        break;
      case "name":
        setName(value);
        break;
      case "headquartersAddress":
        setHeadquartersAddress(value);
        break;
      default:
        break;
    }
  };

  const [deleteOrganizationById] = useDeleteOrganizationByIdMutation();

  const handleCloseDelete = () => {
    setOpenDelete(false);
  };
  const handleClickOpenDelete = (organizationId) => {
    setOrganizationId(organizationId);
    setOpenDelete(true);
  };

  const handleDeleteClick = () => {
    const payload = {
      token: token,
      organizationId: organizationId,
      locale: locale
    };

    deleteOrganizationById(payload)
      .unwrap()
      .then(() => {
        toast.success("Organización borrada satisfactoriamente");
      })
      .catch((error) =>
        toast.error("No se ha podido borrar la organización")
      );

    handleCloseDelete();
  };

  const handleClickOrganization = (organizationId) => {
    navigate("/organizations/teams", {
      state: { organizationId: organizationId },
    });
  };

  var rows = [];
  if (organizations) {
    rows = [];
    organizations.forEach((item, index) => {
      rows.push(
        createData(
          item.id,
          item.code,
          item.name,
          item.headquartersAddress,
          item.coordinates.lon,
          item.coordinates.lat
        )
      );
    });
  }

  return (
    <Paper sx={{ overflow: "hidden" }}>
      <TableContainer sx={{ minWidth: 600, maxHeight: 500 }}>
        <Table stickyHeader aria-label="sticky table">
          <TableHead>
            <TableRow>
              {columns.map((column) => (
                <TableCell
                  key={column.id}
                  align={column.align}
                  style={{ minWidth: column.minWidth }}
                  sx={{
                    color: "secondary.light",
                    fontWeight: "bold",
                  }}
                >
                  {t(column.label)}
                </TableCell>
              ))}
            </TableRow>
          </TableHead>
          <TableBody>
            {rows
              .slice(page * rowsPerPage, page * rowsPerPage + rowsPerPage)
              .map((row) => {
                return (
                  <TableRow hover tabIndex={-1} key={row.code}>
                    {columns.map((column) => {
                      const value = row[column.id];
                      return (
                        <TableCell
                          key={column.id}
                          align={column.align}
                          onClick={
                            column.id !== "options"
                              ? () => handleClickOrganization(row.id)
                              : undefined
                          }
                          sx={{
                            "&:hover": {
                              cursor: "pointer",
                            },
                          }}
                        >
                          {column.format && typeof value === "number"
                            ? column.format(value)
                            : value}
                          {column.id === "options" && userRole === "COORDINATOR" ? (
                            <Box>
                              <IconButton
                                color="primary"
                                aria-label="add"
                                size="small"
                                onClick={(e) => handleClickOpenEdit(row)}
                              >
                                <EditIcon />
                              </IconButton>
                              <IconButton
                                color="primary"
                                aria-label="add"
                                size="small"
                                onClick={(e) => handleClickOpenDelete(row.id)}
                              >
                                <DeleteIcon />
                              </IconButton>
                            </Box>
                          ) : null}
                        </TableCell>
                      );
                    })}
                  </TableRow>
                );
              })}
          </TableBody>
        </Table>
      </TableContainer>
      <TablePagination
        rowsPerPageOptions={[10, 25, 100]}
        component="div"
        count={rows.length}
        rowsPerPage={rowsPerPage}
        page={page}
        onPageChange={handleChangePage}
        onRowsPerPageChange={handleChangeRowsPerPage}
        labelRowsPerPage={t("rows-per-page")}
      />
      <Dialog
        open={openDelete}
        onClose={handleCloseDelete}
        aria-labelledby="alert-dialog-title"
        aria-describedby="alert-dialog-description"
      >
        <DialogTitle id="alert-dialog-title" sx={{ color: "primary.light" }}>
          {t("organization-deleted-dialog")}
        </DialogTitle>
        <DialogActions>
          <Button onClick={handleCloseDelete}>Cancelar</Button>
          <Button onClick={handleDeleteClick} color="error" autoFocus>
            Aceptar
          </Button>
        </DialogActions>
      </Dialog>

      <Dialog fullWidth={true} maxWidth={"md"} open={openEdit}>
        <DialogTitle sx={{ color: "primary.light" }}>{t("organization-edit")} </DialogTitle>
        <DialogContent>
          <FormControl>
            <Grid container spacing={2}>
              <Grid item xs={6}>
                <TextField
                  id="code"
                  label="Código"
                  type="text"
                  autoComplete="current-code"
                  margin="normal"
                  value={code}
                  onChange={(e) => handleChange(e)}
                  variant="standard"
                  required
                  sx={{ display: "flex" }}
                />
              </Grid>
              <Grid item xs={6}>
                <TextField
                  id="name"
                  label="Nombre"
                  type="text"
                  autoComplete="current-name"
                  margin="normal"
                  value={name}
                  onChange={(e) => handleChange(e)}
                  variant="standard"
                  required
                  sx={{ display: "flex" }}
                />
              </Grid>

              <Grid item xs={12}>
                <TextField
                  id="headquartersAddress"
                  label="Dirección de la organización"
                  type="text"
                  autoComplete="current-name"
                  margin="normal"
                  value={headquartersAddress}
                  onChange={(e) => handleChange(e)}
                  variant="standard"
                  required
                  sx={{ display: "flex" }}
                />
              </Grid>
            </Grid>
            <CoordinatesMap childToParent={childToParent} />
          </FormControl>
        </DialogContent>
        <DialogActions>
          <Button onClick={handleCloseEdit}>Cancelar</Button>
          <Button onClick={(e) => handleEditClick(e)}>Editar</Button>
        </DialogActions>
      </Dialog>
    </Paper>
  );
}

OrganizationTable.propTypes = {
  organizations: PropTypes.array.isRequired,
};
