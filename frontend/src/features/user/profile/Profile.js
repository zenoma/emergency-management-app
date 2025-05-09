import { useDispatch, useSelector } from "react-redux";
import { useNavigate } from "react-router-dom";
import "./Profile.css";

import { Button, Table, TableBody, TableCell, TableContainer, TableRow, Typography } from "@mui/material";
import Avatar from "@mui/material/Avatar";
import Paper from "@mui/material/Paper";

import { useTranslation } from "react-i18next";
import { logout, selectUser } from "../login/LoginSlice";

export default function Profile() {
  const dispatch = useDispatch();
  const navigate = useNavigate();

  const user = useSelector(selectUser);

  const { t } = useTranslation();

  const handleClick = (e) => {
    if (e.target.id === "change-password") {
      navigate("/change-password");
    } else if (e.target.id === "logout") {
      dispatch(logout());
      navigate("/");
    }
  };

  return (
    <Paper
      sx={{
        minWidth: 400,
        maxWidth: 1000,
        padding: 3,
        textAlign: "center",
        display: "inline-block",
        boxShadow: "none",
      }}
    >

      {user && <div
        style={{
          flexDirection: "column",
          justifyContent: "center",
          alignItems: "left",
          padding: "5px",
        }}
      >
        <Typography
          variant="h6"
          sx={{
            display: "flex",
            justifyContent: "center",
            alignItems: "center",
            color: "primary.light",
          }}
        >
          {t("user-profile")}
        </Typography>
        <Avatar alt={user.firstName} src="/static/images/avatar/2.jpg" sx={{ height: 50, width: 50 }} />
        <Typography variant="h4" margin={1}>
          {user.firstName} {user.lastName}
        </Typography>
        <TableContainer>
          <Table >
            <TableBody>
              <TableRow >
                <TableCell
                  component="th"
                  scope="row"
                  sx={{ color: "secondary.light", padding: "20px" }}
                >
                  {t("email")}
                </TableCell>
                <TableCell align="center">
                  {user.email}
                </TableCell>
              </TableRow>
              <TableRow>
                <TableCell
                  component="th"
                  scope="row"
                  sx={{ color: "secondary.light", padding: "20px" }}
                >
                  {t("dni")}
                </TableCell>
                <TableCell align="center">
                  {user.dni}
                </TableCell>
              </TableRow>
              <TableRow>
                <TableCell
                  component="th"
                  scope="row"
                  sx={{ color: "secondary.light", padding: "20px" }}
                >
                  {t("phoneNumber")}
                </TableCell>
                <TableCell align="center">
                  {user.phoneNumber}
                </TableCell>
              </TableRow>
              <TableRow>
                <TableCell
                  component="th"
                  scope="row"
                  sx={{ color: "secondary.light", padding: "20px" }}
                >
                  {t("user-role")}
                </TableCell>
                <TableCell align="center">
                  {user.userRole}
                </TableCell>
              </TableRow>
            </TableBody>
          </Table>
        </TableContainer>
        <Button
          id="change-password"
          type="button"
          color="primary"
          onClick={(e) => handleClick(e)}
          sx={{ margin: "20px" }}
        >
          {t("change-password")}
        </Button>
        <Button
          id="logout"
          type="button"
          variant="contained"
          color="secondary"
          onClick={(e) => handleClick(e)}
        >

          {t("logout")}
        </Button>
      </div>}

      {/* {user && (
        <Box>
          <Avatar alt={user.firstName} src="/static/images/avatar/2.jpg" />
          <Typography variant="h4" margin={1}>
            {user.firstName} {user.lastName}
          </Typography>
          
          <Typography
            variant="h6"
            margin={1}
            color="primary"
            sx={{ fontWeight: "bold" }}
          >
            DNI:
          </Typography>
          <Typography variant="h6" margin={1}>
            {user.dni}
          </Typography>
          <Typography
            variant="h6"
            margin={1}
            color="primary"
            sx={{ fontWeight: "bold" }}
          >
            PhoneNumber:
          </Typography>
          <Typography variant="h6" margin={1}>
            {user.phoneNumber}
          </Typography>
          <Typography
            variant="h6"
            margin={1}
            color="primary"
            sx={{ fontWeight: "bold" }}
          >
            Role:
          </Typography>
          <Typography variant="h6" margin={1}>
            {user.userRole}
          </Typography>
          
        </Box>
      )} */}
    </Paper>
  );
}
