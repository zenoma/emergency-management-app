import { Alert, Box, CircularProgress, Grid, Typography } from "@mui/material";
import React from "react";
import { useTranslation } from "react-i18next";
import { useSelector } from "react-redux";
import { useLocation } from "react-router-dom";
import { selectToken } from "../user/login/LoginSlice";
import BackButton from "../utils/BackButton";
import QuadrantTeamsView from "../quadrant/QuadrantTeamsView";
import QuadrantVehiclesView from "../quadrant/QuadrantVehiclesView";

export default function EmergencyPointView() {
  const location = useLocation();
  const emergencyId = location.state?.emergencyId;

  const { t } = useTranslation();

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
            {t("quadrant-details")}
          </Typography>
          {emergencyId && (
            <Typography variant="h6" margin={1}>
              {t('emergency-id')}: {"#" + emergencyId}
            </Typography>
          )}
        </Grid>
        <Grid item xs={4} sm={8} md={12}>
          <Box>
            <QuadrantTeamsView emergencyId={emergencyId} />
          </Box>
        </Grid>
        <Grid item xs={4} sm={8} md={12}>
          <Box sx={{ mt: 2 }}>
            <QuadrantVehiclesView emergencyId={emergencyId} />
          </Box>
        </Grid>
      </Grid>
    </Box>
  );
}
