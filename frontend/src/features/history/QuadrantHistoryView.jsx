import { Alert, Box, CircularProgress, Grid, Typography } from "@mui/material";
import dayjs from "dayjs";
import React from "react";
import { useTranslation } from "react-i18next";
import { useSelector } from "react-redux";
import { useLocation } from "react-router-dom";
import {
  useGetTeamLogsByQuadrantIdQuery,
  useGetVehicleLogsByQuadrantIdQuery,
} from "../../api/logApi";
import { selectToken } from "../user/login/LoginSlice";
import BackButton from "../utils/BackButton";
import QuadrantHistoryTeamsTable from "./QuadrantHistoryTeamsTable";
import QuadrantHistoryVehiclesTable from "./QuadrantHistoryVehiclesTable";
import { useGetQuadrantByIdQuery } from "../../api/quadrantApi";

import teamImage from "../../assets/images/team-banner.jpg";
import vehicleImage from "../../assets/images/vehicle-banner.jpg"


export default function QuadrantHistoryView() {
  const location = useLocation();
  const token = useSelector(selectToken);


  const quadrantId = location.state.quadrantId;
  const startDate = dayjs(location.state.startDate);
  const endDate = dayjs(location.state.endDate);

  const { t } = useTranslation();
  const { i18n } = useTranslation("home");
  const locale = i18n.language;

  const payload = {
    token: token,
    quadrantId: quadrantId,
    startDate: dayjs(startDate).format("YYYY-MM-DD"),
    endDate: dayjs(endDate).format("YYYY-MM-DD"),
    locale: locale
  };


  const { data: teamLogs, isError: isTeamLogsError } = useGetTeamLogsByQuadrantIdQuery(payload);
  const { data: quadrantInfo, isLoading, isError: isQuadrantError } = useGetQuadrantByIdQuery(payload);
  const { data: vehicleLogs, isError: isVehicleLogsError } = useGetVehicleLogsByQuadrantIdQuery(payload);

  if (isLoading) {
    return (
      <Box sx={{ padding: 3, display: "flex", justifyContent: "center" }}>
        <CircularProgress />
      </Box>
    );
  }

  if (isQuadrantError) {
    return (
      <Box sx={{ padding: 3 }}>
        <BackButton />
        <Alert severity="error">{t("generic-error")}</Alert>
      </Box>
    );
  }

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
            {t("quadrant-history-details")}
          </Typography>
          {quadrantInfo && startDate && endDate && (
            <div >
              <Typography variant="h6" margin={1}>
                {quadrantInfo.nombre} ({"#" + quadrantId})
              </Typography>
              <Box display="flex" sx={{ alingItems: "center", justifyContent: "center" }} >
                <Typography variant="body1" color="textSecondary" m={1}>
                  <div style={{ fontWeight: 600 }}>
                    {t("start-date-picker")}
                  </div>
                  {dayjs(startDate).format("DD-MM-YYYY HH:mm")}
                </Typography>
                <Typography variant="body1" color="textSecondary" m={1}>
                  <div style={{ fontWeight: 600 }}>
                    {t("end-date-picker")}
                  </div>
                  {dayjs(endDate).format("DD-MM-YYYY HH:mm")}
                </Typography>
              </Box>
            </div>
          )}
        </Grid>
        <Grid item xs={4} sm={8} md={6}>
          <Box>
            <Typography
              variant="h6"
              margin={1}
              sx={{
                fontWeight: "bold",
                color: "primary.light",
                backgroundImage: `url(${teamImage})`,
                minHeight: 75,
                display: "flex",
                justifyContent: "center",
                alignItems: "center",
                textShadow: "2px 2px 3px #000",
                backgroundBlendMode: "screen",
              }}
            >
              {t("quadrant-teams-deployed")}
            </Typography>
            {isTeamLogsError ? (
              <Alert severity="error">{t("generic-error")}</Alert>
            ) : teamLogs && <QuadrantHistoryTeamsTable teamLogs={teamLogs} />}
          </Box>
        </Grid>
        <Grid item xs={4} sm={8} md={6}>
          <Box>
            <Typography
              variant="h6"
              margin={1}
              sx={{
                fontWeight: "bold",
                color: "primary.light",
                backgroundImage: `url(${vehicleImage})`,
                minHeight: 75,
                display: "flex",
                justifyContent: "center",
                alignItems: "center",
                textShadow: "2px 2px 3px #000",
                backgroundBlendMode: "screen",
              }}
            >
              {t("quadrant-vehicles-deployed")}
            </Typography>
            {isVehicleLogsError ? (
              <Alert severity="error">{t("generic-error")}</Alert>
            ) : vehicleLogs && (
              <QuadrantHistoryVehiclesTable vehicleLogs={vehicleLogs} />
            )}
          </Box>
        </Grid>
      </Grid>
    </Box>
  );
}
