import { Alert, Box, CircularProgress, Grid, Typography } from "@mui/material";
import dayjs from "dayjs";
import React from "react";
import { useTranslation } from "react-i18next";
import { useSelector } from "react-redux";
import { useLocation } from "react-router-dom";
import { useGetEmergencyLogsByEmergencyIdQuery } from "../../api/logApi";
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
    emergencyId: location.state && location.state.emergencyId ? location.state.emergencyId : undefined,
    startDate: dayjs(startDate).format("YYYY-MM-DD"),
    endDate: dayjs(endDate).format("YYYY-MM-DD"),
    locale: locale,
  };

  const { data: emergencyLogs, isError: isLogsError, isLoading: isLogsLoading } = useGetEmergencyLogsByEmergencyIdQuery(payload, { refetchOnMountOrArgChange: true });
  const { data: quadrantInfo, isLoading, isError: isQuadrantError } = useGetQuadrantByIdQuery({ token, quadrantId, locale });

  // local derived logs for the selected quadrant
  const [teamLogs, setTeamLogs] = React.useState([]);
  const [vehicleLogs, setVehicleLogs] = React.useState([]);

  React.useEffect(() => {
    console.debug('QuadrantHistoryView logs payload', emergencyLogs);
    if (!emergencyLogs || !Array.isArray(emergencyLogs)) {
      setTeamLogs([]);
      setVehicleLogs([]);
      return;
    }

    // Use maps to deduplicate resources (assignments may appear multiple times: created/accepted/completed)
    const tMap = new Map();
    const vMap = new Map();

    for (const entry of emergencyLogs) {
      try {
        const a = entry.assignment;
        if (!a) continue;
        // Only consider assignments that reached COMPLETED status
        if (!a.status || String(a.status).toUpperCase() !== 'COMPLETED') continue;
        // determine the quadrant id for the assignment: prefer embedded quadrant id
        const aqid = a.quadrantInfo && a.quadrantInfo.id ? a.quadrantInfo.id : (entry.quadrant && entry.quadrant.id ? entry.quadrant.id : (a.emergencyQuadrantId || null));
        if (!aqid) continue;
        if (Number(aqid) !== Number(quadrantId)) continue;

        const resourceDeploy = a.acceptedAt || a.assignedAt || (a.teamInfo && a.teamInfo.deployAt) || (a.vehicleInfo && a.vehicleInfo.deployAt) || null;
        const resourceRetract = a.completedAt || null;

        if (a.teamInfo) {
          const id = a.teamInfo.id;
          const existing = tMap.get(id);
          if (!existing) {
            tMap.set(id, { teamInfo: a.teamInfo, deployAt: resourceDeploy, retractAt: resourceRetract });
          } else {
            // update times: keep earliest deployAt and latest retractAt
            const dCandidates = [existing.deployAt, resourceDeploy].filter(Boolean).map((s) => new Date(s));
            const rCandidates = [existing.retractAt, resourceRetract].filter(Boolean).map((s) => new Date(s));
            const earliestD = dCandidates.length ? new Date(Math.min(...dCandidates)) : null;
            const latestR = rCandidates.length ? new Date(Math.max(...rCandidates)) : null;
            existing.deployAt = earliestD ? earliestD.toISOString() : existing.deployAt;
            existing.retractAt = latestR ? latestR.toISOString() : existing.retractAt;
            tMap.set(id, existing);
          }
        }

        if (a.vehicleInfo) {
          const id = a.vehicleInfo.id;
          const existing = vMap.get(id);
          if (!existing) {
            vMap.set(id, { vehicleInfo: a.vehicleInfo, deployAt: resourceDeploy, retractAt: resourceRetract });
          } else {
            const dCandidates = [existing.deployAt, resourceDeploy].filter(Boolean).map((s) => new Date(s));
            const rCandidates = [existing.retractAt, resourceRetract].filter(Boolean).map((s) => new Date(s));
            const earliestD = dCandidates.length ? new Date(Math.min(...dCandidates)) : null;
            const latestR = rCandidates.length ? new Date(Math.max(...rCandidates)) : null;
            existing.deployAt = earliestD ? earliestD.toISOString() : existing.deployAt;
            existing.retractAt = latestR ? latestR.toISOString() : existing.retractAt;
            vMap.set(id, existing);
          }
        }
      } catch (e) {
        // ignore malformed entries
      }
    }

    setTeamLogs(Array.from(tMap.values()));
    setVehicleLogs(Array.from(vMap.values()));
  }, [emergencyLogs, quadrantId]);

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
                  {dayjs(startDate).format("DD-MM-YYYY HH:mm:ss")}
                </Typography>
                <Typography variant="body1" color="textSecondary" m={1}>
                  <div style={{ fontWeight: 600 }}>
                    {t("end-date-picker")}
                  </div>
                  {dayjs(endDate).format("DD-MM-YYYY HH:mm:ss")}
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
            {isLogsError ? (
              <Alert severity="error">{t("generic-error")}</Alert>
            ) : (isLogsLoading ? <CircularProgress /> : <QuadrantHistoryTeamsTable teamLogs={teamLogs} />)}
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
            {isLogsError ? (
              <Alert severity="error">{t("generic-error")}</Alert>
            ) : (isLogsLoading ? <CircularProgress /> : <QuadrantHistoryVehiclesTable vehicleLogs={vehicleLogs} />)}
          </Box>
        </Grid>
      </Grid>
    </Box>
  );
}
