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
        // allow logs where the assignment object is present OR the eventType explicitly marks completion
        if (!a && !(entry.eventType && String(entry.eventType).toUpperCase().includes('ASSIGNMENT_COMPLETED'))) continue;
        // Determine whether this entry represents a completed assignment. Some log shapes mark completion
        // via assignment.status === 'COMPLETED', others via entry.eventType === 'ASSIGNMENT_COMPLETED'. Accept both.
        const entryEventType = entry.eventType ? String(entry.eventType).toUpperCase() : null;
        const assignmentStatus = a && a.status ? String(a.status).toUpperCase() : null;
        const isCompleted = (assignmentStatus === 'COMPLETED') || (entryEventType && entryEventType.includes('ASSIGNMENT_COMPLETED'));
        if (!isCompleted) continue;
        // determine the quadrant id for the assignment: prefer embedded quadrant id
        const aqid = a && a.quadrantInfo && a.quadrantInfo.id ? a.quadrantInfo.id : (entry.quadrant && entry.quadrant.id ? entry.quadrant.id : (a && a.emergencyQuadrantId ? a.emergencyQuadrantId : null));

        // Determine whether this entry belongs to the requested quadrant.
        // Accept cases where the assignment/quadrant explicitly references the quadrant (aqid),
        // or when the navigation included an emergencyId and the assignment references that emergency
        // (this covers point-linked emergencies where assignments may not carry quadrant info).
        const payloadEmergencyId = location.state && location.state.emergencyId ? location.state.emergencyId : undefined;
        let matchesQuadrant = false;
        if (aqid) {
          matchesQuadrant = (Number(aqid) === Number(quadrantId));
        } else if (payloadEmergencyId) {
          const aEmergencyId = a && a.emergencyInfo && a.emergencyInfo.id ? a.emergencyInfo.id : (entry.emergency && entry.emergency.id ? entry.emergency.id : (entry.emergencyId || null));
          if (aEmergencyId && Number(aEmergencyId) === Number(payloadEmergencyId)) {
            matchesQuadrant = true;
          }
        }
        if (!matchesQuadrant) continue;

        // normalize resource info: prefer assignment.* fields, fall back to top-level entry.* fields
        const teamInfo = (a && a.teamInfo) ? a.teamInfo : (entry.teamInfo ? entry.teamInfo : null);
        const vehicleInfo = (a && a.vehicleInfo) ? a.vehicleInfo : (entry.vehicleInfo ? entry.vehicleInfo : null);

        const resourceDeploy = (a && (a.acceptedAt || a.assignedAt)) || (teamInfo && teamInfo.deployAt) || (vehicleInfo && vehicleInfo.deployAt) || entry.assignedAt || entry.acceptedAt || null;
        const resourceRetract = (a && a.completedAt) || entry.completedAt || entry.eventAt || null;

        if (teamInfo) {
          const id = teamInfo.id;
          const existing = tMap.get(id);
          if (!existing) {
            tMap.set(id, { teamInfo: teamInfo, deployAt: resourceDeploy, retractAt: resourceRetract });
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

        if (vehicleInfo) {
          const id = vehicleInfo.id;
          const existing = vMap.get(id);
          if (!existing) {
            vMap.set(id, { vehicleInfo: vehicleInfo, deployAt: resourceDeploy, retractAt: resourceRetract });
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
