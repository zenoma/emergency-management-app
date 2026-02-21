import PropTypes from "prop-types";

import Paper from "@mui/material/Paper";
import Table from "@mui/material/Table";
import TableBody from "@mui/material/TableBody";
import TableCell from "@mui/material/TableCell";
import TableContainer from "@mui/material/TableContainer";
import TableHead from "@mui/material/TableHead";
import TablePagination from "@mui/material/TablePagination";
import TableRow from "@mui/material/TableRow";

import { useEffect, useState } from "react";

import React from "react";
import { useTranslation } from "react-i18next";

const columns = [
  { id: "code", label: "team-code", minWidth: 100 },
  { id: "organizationCode", label: "team-organization-belong", minWidth: 100 },
  { id: "deployAt", label: "deploy-at", minWidth: 50 },
  { id: "retractAt", label: "retract-at", minWidth: 50 },
];

function createData(id, code, organizationCode, deployAt, retractAt) {
  return { id, code, organizationCode, deployAt, retractAt };
}

export default function QuadrantHistoryTeamsTable(props) {
  const { t } = useTranslation();

  const [page, setPage] = useState(0);
  const [rowsPerPage, setRowsPerPage] = useState(10);
  const [teams, setTeams] = useState([]);

  const teamLogs = props.teamLogs;

  const handleChangePage = (event, newPage) => {
    setPage(newPage);
  };

  const handleChangeRowsPerPage = (event) => {
    setRowsPerPage(+event.target.value);
    setPage(0);
  };

  useEffect(() => {
    var teams_aux = [];
    if (teamLogs) {
      teamLogs.forEach((teamLog) => {
        teams_aux.push(
          createData(
            teamLog.teamInfo.id,
            teamLog.teamInfo.code,
            teamLog.teamInfo.organization.code,
            teamLog.deployAt,
            teamLog.retractAt
          )
        );
      });
      setTeams(teams_aux);
    }
  }, [teamLogs]);

  return (
    <Paper sx={{ overflow: "hidden" }}>
      <TableContainer sx={{ maxHeight: 400 }}>
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
            {teams
              .slice(page * rowsPerPage, page * rowsPerPage + rowsPerPage)
              .map((item) => {
                return (
                  <TableRow role="checkbox" tabIndex={-1} key={item.code}>
                    {columns.map((column) => {
                      const value = item[column.id];
                      return (
                        <TableCell key={column.id} align={column.align}>
                          {column.format && typeof value === "number"
                            ? column.format(value)
                            : value}
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
        count={teams.length}
        rowsPerPage={rowsPerPage}
        page={page}
        onPageChange={handleChangePage}
        onRowsPerPageChange={handleChangeRowsPerPage}
        labelRowsPerPage={t("rows-per-page")}
      />
    </Paper>
  );
}

QuadrantHistoryTeamsTable.propTypes = {
  teamLogs: PropTypes.array.isRequired,
};
