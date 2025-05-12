import PropTypes from "prop-types";

import { Paper, Table, TableBody, TableCell, TableContainer, TableRow } from "@mui/material";
import Typography from "@mui/material/Typography";
import { useTranslation } from "react-i18next";

export default function OrganizationDetailsCard(props) {
  const { t } = useTranslation();

  return (
    <Paper
      sx={{
        padding: 2,
      }}
      variant="outlined"
    >
      <div
        style={{
          display: "flex",
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
          {t("organization-details-title")}
        </Typography>
        <TableContainer>
          <Table>
            <TableBody>
              <TableRow>
                <TableCell
                  component="th"
                  scope="row"
                  sx={{ color: "secondary.light", padding: "20px" }}
                >
                  {t("organization-details-code")}
                </TableCell>
                <TableCell align="center">
                  {props.data["code"]}
                </TableCell>
              </TableRow>
              <TableRow>
                <TableCell
                  component="th"
                  scope="row"
                  sx={{ color: "secondary.light", padding: "20px" }}
                >
                  {t("organization-details-name")}
                </TableCell>
                <TableCell align="center">
                  {props.data["name"]}
                </TableCell>
              </TableRow>
              <TableRow>
                <TableCell
                  component="th"
                  scope="row"
                  sx={{ color: "secondary.light", padding: "20px" }}
                >
                  {t("organization-address")}
                </TableCell>
                <TableCell align="center">
                  {props.data["headquartersAddress"]}
                </TableCell>
              </TableRow>
              <TableRow>
                <TableCell
                  component="th"
                  scope="row"
                  sx={{ color: "secondary.light", padding: "20px" }}
                >
                  {t("organization-type-name")}
                </TableCell>
                <TableCell align="center">
                  {props.data["organizationType"]}
                </TableCell>
              </TableRow>

            </TableBody>
          </Table>
        </TableContainer>
      </div>
    </Paper>
  )

}

OrganizationDetailsCard.propTypes = {
  data: PropTypes.object,
};
