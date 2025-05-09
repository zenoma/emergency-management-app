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
  useDeleteTeambyIdMutation,
  useUpdateTeamMutation,
} from "../../api/teamApi";
import { useNavigate } from "react-router-dom";

const columns = [
  { id: "code", label: "team-code", minWidth: 150 },
  { id: "createdAt", label: "created-at", minWidth: 50 },
  { id: "options", label: "options", minWidth: 50 },
];

function createData(id, code, createdAt) {
  return { id, code, createdAt };
}

export default function TeamsTable(props) {
  const token = useSelector(selectToken);
  const userRole = useSelector(selectUser).userRole;

  const { t } = useTranslation();
  const { i18n } = useTranslation("home");
  const locale = i18n.language;

  const navigate = useNavigate();

  const [page, setPage] = useState(0);
  const [rowsPerPage, setRowsPerPage] = useState(10);
  const [openDelete, setOpenDelete] = useState(false);
  const [openEdit, setOpenEdit] = useState(false);

  const [teamId, setTeamId] = useState("");
  const [code, setCode] = useState("");

  const teams = props.teams;

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
    setTeamId(row.id);
    setCode(row.code);
    setOpenEdit(true);
  };

  const [updateTeam] = useUpdateTeamMutation();

  const handleEditClick = () => {
    const payload = {
      teamId: teamId,
      code: code,
      token: token,
      locale: locale
    };
    updateTeam(payload)
      .unwrap()
      .then((payload) => {
        toast.success(t("team-updated-successfully"));
      })
      .catch((error) => toast.error(t("team-updated-error")));

    props.reloadData();
    handleCloseEdit();
  };

  const handleChange = (event) => {
    var id = event.target.id;
    var value = event.target.value;

    switch (id) {
      case "code":
        setCode(value);
        break;
      default:
        break;
    }
  };

  const [deleteTeambyId] = useDeleteTeambyIdMutation();

  const handleCloseDelete = () => {
    setOpenDelete(false);
  };
  const handleClickOpenDelete = (organizationId) => {
    setTeamId(organizationId);
    setOpenDelete(true);
  };

  const handleDeleteClick = () => {
    const payload = {
      token: token,
      teamId: teamId,
    };

    deleteTeambyId(payload)
      .unwrap()
      .then((payload) => {
        toast.success(t("team-dismantled-successfully"));
      })
      .catch((error) => toast.error(t("team-dismantled-error")));
    handleCloseDelete();
    props.reloadData();
  };

  var rows = [];
  if (teams) {
    rows = [];
    teams.forEach((item, index) => {
      rows.push(createData(item.id, item.code, item.createdAt));
    });
  }

  return (
    <Paper sx={{ overflow: "hidden" }}>
      <TableContainer sx={{ maxHeight: 270 }}>
        <Table stickyHeader aria-label="sticky table">
          <TableHead>
            <TableRow>
              {columns.map((column) =>  (
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
                  <TableRow hover role="checkbox" tabIndex={-1} key={row.code}>
                    {columns.map((column) => {
                      const value = row[column.id];
                      return (
                        <TableCell
                          sx={{
                            padding: "7px",
                            "&:hover": {
                              cursor: "pointer",
                            },
                          }}
                          key={column.id}
                          align={column.align}
                          onClick={
                            column.id !== "options"
                              ? () =>
                                navigate("/teams", {
                                  state: { teamId: row.id },
                                })
                              : undefined
                          }
                        >
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
          {t("team-dismantled-dialog")}
        </DialogTitle>
        <DialogActions>
          <Button onClick={handleCloseDelete}>{t("cancel")}</Button>
          <Button onClick={handleDeleteClick} color="error" autoFocus>
            {t("dismantle")}
          </Button>
        </DialogActions>
      </Dialog>

      <Dialog maxWidth={"md"} open={openEdit}>
        <DialogTitle sx={{ color: "primary.light" }}>{t("team-updated-title")}</DialogTitle>
        <DialogContent>
          <FormControl>
            <Grid container spacing={2}>
              <Grid item xs={12}>
                <TextField
                  id="code"
                  label={t("team-code")}
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

TeamsTable.propTypes = {
  teams: PropTypes.array.isRequired,
};
