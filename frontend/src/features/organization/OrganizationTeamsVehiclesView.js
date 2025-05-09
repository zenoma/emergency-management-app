import InfoIcon from "@mui/icons-material/Info";
import { Box, Button, Dialog, Grid, Typography } from "@mui/material";
import { useState } from "react";
import { useTranslation } from "react-i18next";
import { useSelector } from "react-redux";
import { useLocation } from "react-router-dom";
import { useGetOrganizationByIdQuery } from "../../api/organizationApi";
import TeamsView from "../team/TeamsView";
import { selectToken } from "../user/login/LoginSlice";
import BackButton from "../utils/BackButton";
import VehiclesView from "../vehicle/VehiclesView";
import OrganizationDetailsCard from "./OrganizationDetailsCard";

export default function OrganizationTeamsVehiclesView() {
  const token = useSelector(selectToken);

  const location = useLocation();
  const organizationId = location.state.organizationId;

  const { t } = useTranslation();
  const { i18n } = useTranslation("home");
  const locale = i18n.language;

  const [open, setOpen] = useState(false);
  const payload = {
    token: token,
    organizationId: organizationId,
    locale: locale
  };
  const { data: organizationData } = useGetOrganizationByIdQuery(payload);

  const handleInfoClickOpen = () => {
    setOpen(true);
  };

  const handleClose = () => {
    setOpen(false);
  };

  return (
    <Box sx={{ padding: 3 }}>
      <BackButton />
      <Grid
        container
        spacing={{ xs: 3, md: 3 }}
        columns={{ xs: 4, sm: 8, md: 12 }}
      >
        <Grid item xs={4} sm={8} md={12}>
          <Typography
            variant="h4"
            margin={1}
            sx={{ fontWeight: "bold", color: "primary.light" }}
          >
            {t("organization-details-title")}
          </Typography>
          {organizationData && (
            <Box display="flex" alignItems="center" justifyContent="center">
              <Typography variant="h6" color="secondary.dark">
                {organizationData.name}
              </Typography>
              <InfoIcon
                onClick={() => handleInfoClickOpen()}
                htmlColor="grey"
                fontSize="small"
              />
            </Box>
          )}
        </Grid>
        <Grid item xs={4} sm={8} md={6}>
          <Box>
            <TeamsView organizationId={organizationId} />
          </Box>
        </Grid>
        <Grid item xs={4} sm={8} md={6}>
          <Box>
            <VehiclesView organizationId={organizationId} />
          </Box>
        </Grid>
      </Grid>
      <Dialog open={open} onClose={handleClose}>
        <OrganizationDetailsCard data={organizationData} />
        <Button onClick={handleClose} color="primary" autoFocus>
          {t("close")}
        </Button>
      </Dialog>
    </Box>
  );
}
