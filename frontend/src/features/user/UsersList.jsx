import PropTypes from "prop-types";
import { useState } from "react";
import { useSelector } from "react-redux";

import { selectToken, selectUser } from "./login/LoginSlice";

import {
  Alert,
  Button,
  Dialog,
  DialogActions,
  DialogTitle,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow
} from "@mui/material";
import Box from "@mui/material/Box";
import List from "@mui/material/List";
import Paper from "@mui/material/Paper";
import Typography from "@mui/material/Typography";
import { useTranslation } from "react-i18next";
import { useDeleteUserMutation } from "../../api/teamApi";

import TablePagination from "@mui/material/TablePagination";

import DeleteIcon from "@mui/icons-material/Delete";

import React from "react";
import { toast } from "react-toastify";

const columns = [
  { id: "dni", label: "dni", minWidth: 50 },
  { id: "firstName", label: "firstName", minWidth: 100 },
  { id: "lastName", label: "lastName", minWidth: 100 },
  { id: "email", label: "email", minWidth: 100 },
  { id: "phoneNumber", label: "phoneNumber", minWidth: 150 },
  { id: "options", label: "options", minWidth: 50 },
];

function createData(id, dni, email, firstName, lastName, phoneNumber) {
  return { id, dni, email, firstName, lastName, phoneNumber };
}

export default function TeamItem(props) {
  const token = useSelector(selectToken);
  const user = useSelector(selectUser);
  const [memberId, setMemberId] = useState("-1");
  const [openDelete, setOpenDelete] = useState(false);

  const [page, setPage] = useState(0);
  const [rowsPerPage, setRowsPerPage] = useState(10);

  const { t } = useTranslation();
  const { i18n } = useTranslation("home");
  const locale = i18n.language;

  const [deleteUser] = useDeleteUserMutation();

  const handleCloseDelete = () => {
    setOpenDelete(false);
  };

  const payload = {
    token: token,
    teamId: props.teamId,
    locale: locale
  };

  const handleClickOpenDelete = (id) => {
    setMemberId(id);
    setOpenDelete(true);
  };

  const handleDeleteClick = () => {
    payload.memberId = memberId;

    deleteUser(payload)
      .unwrap()
      .then(() => {
        toast.success(t("user-deleted-successfully"));
        setOpenDelete(false);
        props.reloadData();
      })
      .catch((error) => toast.error(t("user-deleted-error")));
  };

  const users = props.users;

  var rows = [];
  if (users) {
    rows = [];
    users.forEach((item, index) => {
      rows.push(
        createData(
          item.id,
          item.dni,
          item.email,
          item.firstName,
          item.lastName,
          item.phoneNumber
        )
      );
    });
  }

  const handleChangePage = (event, newPage) => {
    setPage(newPage);
  };

  const handleChangeRowsPerPage = (event) => {
    setRowsPerPage(+event.target.value);
    setPage(0);
  };

  return (
    <Paper sx={{ padding: 1, minWidth: 900, display: "inline-block" }} elevation={3}>
      <Typography
        variant="h6"
        sx={{ padding: 3 }}
        fontWeight="bold"
        color={"primary.light"}
      >
        {t("user-list")}
      </Typography>
      <List component="div" disablePadding>
        {users.length === 0 ? (
          <Box
            sx={{
              padding: "10px",
              display: "flex",
              justifyContent: "center",
            }}
          >
            <Alert severity="warning">{t("user-no-user-list")}</Alert>
          </Box>
        ) : users.length !== 0 ? (
          <Box>
            <TableContainer sx={{ maxHeight: 250 }}>
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
                          fontWeight: "bold", fontSize: "0.8rem",
                          padding: "8px 16px",
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
                          key={row.id}
                        >
                          {columns.map((column) => {
                            const value = row[column.id];
                            return (
                              <TableCell
                                sx={{
                                  fontSize: "0.8rem",
                                  padding: "8px 16px",
                                  "&:hover": {
                                    cursor: "pointer",
                                  },
                                }}
                                key={column.id}
                                align={column.align}
                              >
                                {column.format && typeof value === "number"
                                  ? column.format(value)
                                  : value}
                                {column.id === "options" && user.userRole !== "USER" ? (
                                  <Button
                                    color="primary"
                                    aria-label="add"
                                    size="small"
                                    onClick={() =>
                                      handleClickOpenDelete(row.id)
                                    }
                                  >
                                    <DeleteIcon />
                                  </Button>
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
                {t("user-deleted-dialog")}
              </DialogTitle>
              <DialogActions>
                <Button onClick={handleCloseDelete}>{t("cancel")}</Button>
                <Button onClick={handleDeleteClick} color="error" autoFocus>
                  {t("delete")}
                </Button>
              </DialogActions>
            </Dialog>
          </Box>
        ) : null}
      </List>
    </Paper>
  );
}

TeamItem.propTypes = {
  name: PropTypes.string.isRequired,
  teamId: PropTypes.number.isRequired,
};
