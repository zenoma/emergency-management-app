import PropTypes from "prop-types";
import { useState } from "react";

import { Box, Container, Typography, Button, Dialog } from "@mui/material";
import InfoIcon from "@mui/icons-material/Info";

import { useTranslation } from "react-i18next";
import OrganizationDetailsCard from "../organization/OrganizationDetailsCard";

export default function TeamCard({ data }) {
  const [open, setOpen] = useState(false);
  const { t } = useTranslation();

  if (!data) return null;

  const handleClickToOpen = () => setOpen(true);
  const handleToClose = () => setOpen(false);

  return (
    <Container
      sx={{
        paddingTop: 3,
        textAlign: "center",
        display: "inline-block",
        boxShadow: "none",
      }}
    >
      <Typography
        variant="h4"
        sx={{ fontWeight: "bold", color: "primary.light", mb: 2 }}
      >
        {t("team-details")}
      </Typography>

      <Box display="flex" justifyContent="center" alignItems="center" mb={1}>
        <Typography
          variant="h6"
          sx={{ fontWeight: "bold", color: "secondary.light", mr: 1 }}
        >
          {t("team-code")}:
        </Typography>
        <Typography variant="h6">{data.code}</Typography>
      </Box>

      <Box display="flex" justifyContent="center" alignItems="center" mb={2}>
        <Typography
          variant="h6"
          sx={{ fontWeight: "bold", color: "secondary.light", mr: 1 }}
        >
          {t("team-organization-belong")}:
        </Typography>
        <Typography variant="h6">{data.organization.name}</Typography>
        <InfoIcon
          color="primary"
          sx={{ ml: 1, cursor: "pointer" }}
          onClick={handleClickToOpen}
        />
      </Box>

      <Dialog open={open} onClose={handleToClose} maxWidth="sm" fullWidth>
        <Box p={2}>
          <OrganizationDetailsCard data={data.organization} />
          <Box display="flex" justifyContent="center" mt={2}>
            <Button variant="contained" onClick={handleToClose} color="primary">
              {t("close")}
            </Button>
          </Box>
        </Box>
      </Dialog>
    </Container>
  );
}

TeamCard.propTypes = {
  data: PropTypes.shape({
    code: PropTypes.string.isRequired,
    organization: PropTypes.shape({
      name: PropTypes.string.isRequired,
    }).isRequired,
  }).isRequired,
};
