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
import { selectToken } from "../user/login/LoginSlice";

import DeleteIcon from "@mui/icons-material/Delete";
import Dialog from "@mui/material/Dialog";
import DialogActions from "@mui/material/DialogActions";
import DialogTitle from "@mui/material/DialogTitle";
import { useState } from "react";
import { toast } from "react-toastify";

import React from "react";
import { useTranslation } from "react-i18next";
import { useRetractTeamMutation } from "../../api/teamApi";

const columns = [
  { id: "code", label: "team-code", minWidth: 150 },
  { id: "organizationCode", label: "team-organization-belong", minWidth: 150 },
  { id: "deployAt", label: "deploy-at", minWidth: 50 },
  { id: "options", label: "options", minWidth: 50 },
];

function createData(id, code, organizationCode, deployAt) {
  return { id, code, organizationCode, deployAt };
}

export default function QuadrantTeamsTable(props) {
  const token = useSelector(selectToken);
  const { t } = useTranslation();
  const { i18n } = useTranslation("home");
  const locale = i18n.language;

  const [page, setPage] = useState(0);
  const [rowsPerPage, setRowsPerPage] = useState(10);
  const [openDelete, setOpenDelete] = useState(false);

  const [teamId, setTeamId] = useState("");

  const teams = props.teams;
  const quadrantId = props.quadrantId;

  const handleChangePage = (event, newPage) => {
    setPage(newPage);
  };

  const handleChangeRowsPerPage = (event) => {
    setRowsPerPage(+event.target.value);
    setPage(0);
  };

  const [retractTeam] = useRetractTeamMutation();

  const handleCloseDelete = () => {
    setOpenDelete(false);
  };

  const handleClickOpenDelete = (teamId) => {
    setTeamId(teamId);
    setOpenDelete(true);
  };

  const handleDeleteClick = () => {
    const payload = {
      token: token,
      gid: quadrantId,
      teamId: teamId,
      locale: locale
    };

    retractTeam(payload)
      .unwrap()
      .then((payload) => {
        toast.success(t("team-retracted-successfully"));
        handleCloseDelete();
        props.reloadData();
      })
      .catch((error) => {
        toast.error(t("team-retracted-error"));
        handleCloseDelete();
      });
  };

  var rows = [];
  if (teams) {
    rows = [];
    teams.forEach((item) => {
      rows.push(
        createData(item.id, item.code, item.organization.code, item.deployAt)
      );
    });
  }

  return (
    <Paper sx={{ overflow: "hidden" }}>
      <TableContainer sx={{ maxHeight: 260 }}>
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
                  <TableRow role="checkbox" tabIndex={-1} key={row.code}>
                    {columns.map((column) => {
                      const value = row[column.id];
                      return (
                        <TableCell key={column.id} align={column.align} sx={{
                          padding: "8px"
                        }}>
                          {column.format && typeof value === "number"
                            ? column.format(value)
                            : value}
                          {column.id === "options" ? (
                            <Box>
                              <Button
                                color="primary"
                                aria-label="add"
                                size="small"
                                onClick={(e) => handleClickOpenDelete(row.id)}
                              >
                                <DeleteIcon />
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
          {t("quadrant-team-retract-dialog")}
        </DialogTitle>
        <DialogActions>
          <Button onClick={handleCloseDelete}>{t("cancel")}</Button>
          <Button onClick={handleDeleteClick} color="error" autoFocus>
            {t("retract")}
          </Button>
        </DialogActions>
      </Dialog>
    </Paper>
  );
}

QuadrantTeamsTable.propTypes = {
  teams: PropTypes.array.isRequired,
  quadrantId: PropTypes.number.isRequired,
};
