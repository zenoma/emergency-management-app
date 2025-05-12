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

import { Box, Button } from "@mui/material";
import { selectToken, selectUser } from "../user/login/LoginSlice";

import DismantleIcon from "@mui/icons-material/Cancel";
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
import {
  useDeleteVehiclebyIdMutation,
  useUpdateVehicleMutation,
} from "../../api/vehicleApi";

const columns = [
  { id: "vehiclePlate", label: "vehicle-plate", minWidth: 150 },
  { id: "type", label: "type", minWidth: 150 },
  { id: "createdAt", label: "created-at", minWidth: 50 },
  { id: "options", label: "options", minWidth: 50 },
];

function createData(id, vehiclePlate, type, createdAt) {
  return { id, vehiclePlate, type, createdAt };
}

export default function VehicleTable(props) {
  const token = useSelector(selectToken);
  const userRole = useSelector(selectUser).userRole;

  const { t } = useTranslation();
  const { i18n } = useTranslation("home");
  const locale = i18n.language;

  const [page, setPage] = useState(0);
  const [rowsPerPage, setRowsPerPage] = useState(10);
  const [openDelete, setOpenDelete] = useState(false);
  const [openEdit, setOpenEdit] = useState(false);

  const [vehicleId, setVehicleId] = useState("");
  const [vehiclePlate, setVehiclePlate] = useState("");
  const [type, setType] = useState("");

  const vehicles = props.vehicles;

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
    setVehicleId(row.id);
    setVehiclePlate(row.vehiclePlate);
    setType(row.type);
    setOpenEdit(true);
  };

  const [updateVehicle] = useUpdateVehicleMutation();

  const handleEditClick = () => {
    const payload = {
      vehicleId: vehicleId,
      vehiclePlate: vehiclePlate,
      type: type,
      token: token,
      locale: locale
    };
    updateVehicle(payload)
      .unwrap()
      .then((payload) => {
        toast.success(t("vehicle-updated-successfully"));
      })
      .catch((error) => toast.error(t("vehicle-updated-error")));

    props.reloadData();
    handleCloseEdit();
  };

  const handleChange = (event) => {
    var id = event.target.id;
    var value = event.target.value;

    switch (id) {
      case "vehiclePlate":
        setVehiclePlate(value);
        break;
      case "type":
        setType(value);
        break;
      default:
        break;
    }
  };

  const [deleteVehicleById] = useDeleteVehiclebyIdMutation();

  const handleCloseDelete = () => {
    setOpenDelete(false);
  };
  const handleClickOpenDelete = (vehicleId) => {
    setVehicleId(vehicleId);
    setOpenDelete(true);
  };

  const handleDeleteClick = () => {
    const payload = {
      token: token,
      vehicleId: vehicleId,
      locale: locale
    };

    deleteVehicleById(payload)
      .unwrap()
      .then((payload) => {
        toast.success(t("vehicle-dismantled-successfully"));
      })
      .catch((error) => toast.error(t("vehicle-dismantled-error")));
    handleCloseDelete();
    props.reloadData();
  };

  var rows = [];
  if (vehicles) {
    rows = [];
    vehicles.forEach((item, index) => {
      rows.push(createData(item.id, item.vehiclePlate, item.type, item.createdAt));
    });
  }

  return (
    <Paper sx={{ overflow: "hidden" }}>
      <TableContainer sx={{ maxHeight: 270 }}>
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
                  <TableRow
                    hover
                    role="checkbox"
                    tabIndex={-1}
                    key={row.vehiclePlate}
                  >
                    {columns.map((column) => {
                      const value = row[column.id];
                      return (
                        <TableCell key={column.id} align={column.align}
                          sx={{
                            padding: "7px",
                          }}>
                          {column.format && typeof value === "number"
                            ? column.format(value)
                            : value}
                          {column.id === "options" && userRole !== "USER" ? (
                            <Box>
                              <Button
                                color="primary"
                                aria-label="add"
                                size="small"
                                onClick={(e) => handleClickOpenEdit(row)}
                              >
                                <EditIcon />
                              </Button>
                              <Button
                                color="primary"
                                aria-label="add"
                                size="small"
                                onClick={(e) => handleClickOpenDelete(row.id)}
                              >
                                <DismantleIcon />
                              </Button>
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
          {t("vehicle-dismantled-dialog")}
        </DialogTitle>
        <DialogActions>
          <Button onClick={handleCloseDelete}>{t("cancel")}</Button>
          <Button onClick={handleDeleteClick} color="error" autoFocus>
            {t("dismantle")}
          </Button>
        </DialogActions>
      </Dialog>

      <Dialog maxWidth={"md"} open={openEdit}>
        <DialogTitle sx={{ color: "primary.light" }}>{t("vehicle-updated-title")} </DialogTitle>
        <DialogContent>
          <FormControl>
            <Grid container spacing={2}>
              <Grid item xs={6}>
                <TextField
                  id="vehiclePlate"
                  label={t("vehicle-plate")}
                  type="text"
                  autoComplete="current-vehicle-plate"
                  margin="normal"
                  value={vehiclePlate}
                  onChange={(e) => handleChange(e)}
                  variant="standard"
                  required
                  sx={{ display: "flex" }}
                />
              </Grid>
              <Grid item xs={6}>
                <TextField
                  id="type"
                  label={t("type")}
                  type="text"
                  autoComplete="current-type"
                  margin="normal"
                  value={type}
                  onChange={(e) => handleChange(e)}
                  variant="standard"
                  required
                  sx={{ display: "flex" }}
                />
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
    </Paper>
  );
}

VehicleTable.propTypes = {
  vehicles: PropTypes.array.isRequired,
};
