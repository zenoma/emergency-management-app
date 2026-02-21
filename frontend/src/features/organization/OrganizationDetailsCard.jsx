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
          mb: 3,
        }}
      >
        {t("organization-details-title")}
      </Typography>

      <Box
        sx={{
          display: "flex",
          gap: 3,
          flexWrap: "wrap",
          justifyContent: "center",
        }}
      >
        <Box>
          <Typography
            variant="overline"
            sx={{ color: "secondary.light" }}
            display="block"
          >
            {t("organization-details-code")}
          </Typography>
          <Typography variant="h6" fontWeight="bold">
            {data.code}
          </Typography>
        </Box>

        <Box>
          <Typography
            variant="overline"
            sx={{ color: "secondary.light" }}
            display="block"
          >
            {t("organization-details-name")}
          </Typography>
          <Typography variant="h6" fontWeight="bold">
            {data.name}
          </Typography>
        </Box>

        <Box>
          <Typography
            variant="overline"
            sx={{ color: "secondary.light" }}
            display="block"
          >
            {t("organization-address")}
          </Typography>
          <Typography variant="h6" fontWeight="bold">
            {data.headquartersAddress}
          </Typography>
        </Box>

        <Box>
          <Typography
            variant="overline"
            sx={{ color: "secondary.light" }}
            display="block"
          >
            {t("organization-type-name")}
          </Typography>
          <Typography variant="h6" fontWeight="bold">
            {data.organizationTypeName}
          </Typography>
        </Box>
      </Box>
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
