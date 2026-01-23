import PropTypes from "prop-types";
import { Paper, Table, TableBody, TableCell, TableContainer, TableRow } from "@mui/material";
import Typography from "@mui/material/Typography";
import { useTranslation } from "react-i18next";
import { Box } from "@mui/material";

export default function OrganizationDetailsCard({ data }) {
  const { t } = useTranslation();

  if (!data) return null; // protección por si no hay data

  return (
    <Paper
      variant="outlined"
      sx={{
        p: 3,
        maxWidth: 600,
        width: "100%",
        mx: "auto",
      }}
    >
      {/* Título */}
      <Typography
        variant="h6"
        sx={{
          color: "primary.light",
          fontWeight: "bold",
          textAlign: "center",
          mb: 2,
        }}
      >
        {t("organization-details-title")}
      </Typography>

      {/* Tabla de detalles */}
      <TableContainer>
        <Table>
          <TableBody>
            <TableRow>
              <TableCell
                component="th"
                scope="row"
                sx={{ color: "secondary.light", fontWeight: "bold", width: 200 }}
              >
                {t("organization-details-code")}
              </TableCell>
              <TableCell align="center">{data.code}</TableCell>
            </TableRow>

            <TableRow>
              <TableCell component="th" scope="row" sx={{ color: "secondary.light", fontWeight: "bold" }}>
                {t("organization-details-name")}
              </TableCell>
              <TableCell align="center">{data.name}</TableCell>
            </TableRow>

            <TableRow>
              <TableCell component="th" scope="row" sx={{ color: "secondary.light", fontWeight: "bold" }}>
                {t("organization-address")}
              </TableCell>
              <TableCell align="center">{data.headquartersAddress}</TableCell>
            </TableRow>

            <TableRow>
              <TableCell component="th" scope="row" sx={{ color: "secondary.light", fontWeight: "bold" }}>
                {t("organization-type-name")}
              </TableCell>
              <TableCell align="center">{data.organizationTypeName}</TableCell>
            </TableRow>
          </TableBody>
        </Table>
      </TableContainer>
    </Paper>
  );
}

OrganizationDetailsCard.propTypes = {
  data: PropTypes.shape({
    code: PropTypes.string.isRequired,
    name: PropTypes.string.isRequired,
    headquartersAddress: PropTypes.string,
    organizationTypeName: PropTypes.string,
  }).isRequired,
};
