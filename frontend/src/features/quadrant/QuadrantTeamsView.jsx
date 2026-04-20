// Quadrant teams view: show teams assigned to the quadrant via assignments endpoint
import PropTypes from "prop-types";
import { useSelector } from "react-redux";
import { Alert, Box, CircularProgress } from "@mui/material";
import Paper from "@mui/material/Paper";
import Typography from "@mui/material/Typography";
import { useState } from "react";
import { useTranslation } from "react-i18next";
import { useGetQuadrantByIdQuery } from "../../api/quadrantApi";
import { useGetAssignmentsQuery } from '../../api/assignmentApi';
import { selectToken } from "../user/login/LoginSlice";
import QuadrantTeamsTable from "./QuadrantTeamsTable";
import teamImage from "../../assets/images/team-banner.jpg";

export default function QuadrantTeamsView(props) {
  const token = useSelector(selectToken);

  const { t } = useTranslation();
  const { i18n } = useTranslation("home");
  const locale = i18n.language;

  const [unusedState] = useState(0);

  const quadrantId = props.quadrantId;
  // allowed to pass emergencyId from parent (EmergencyDetailsView) to scope assignments to that emergency
  const emergencyIdProp = props.emergencyId || null;

  const payload = quadrantId ? { token: token, quadrantId: quadrantId, locale: locale } : null;

  const {
    data: quadrantInfo,
    isFetching: isFetchingQuadrant,
    isError: isErrorQuadrant,
    refetch,
  } = useGetQuadrantByIdQuery(payload, {
    skip: !payload,
    refetchOnMountOrArgChange: true,
  });

  // prefer prop emergency id, otherwise explicit emergency id from quadrantInfo if present
  const emergencyId = emergencyIdProp || quadrantInfo?.emergencyId || quadrantInfo?.emergency?.id || null;

  const {
    data: assignments = [],
    isFetching: isFetchingAssignments,
    isError: isErrorAssignments,
    refetch: refetchAssignments,
  } = useGetAssignmentsQuery({ token, locale, quadrantId, emergencyId }, { refetchOnMountOrArgChange: true });

  const reloadData = () => {
    refetch();
    refetchAssignments();
  };

  return (
    <Box>
      <Paper
        sx={{
          padding: "10px",
        }}
      >
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
        {(isFetchingQuadrant || isFetchingAssignments) ? (
          <CircularProgress />
        ) : (isErrorQuadrant || isErrorAssignments) ? (
          <Alert severity="error">{t("generic-error")}</Alert>
        ) : (
          <QuadrantTeamsTable
            reloadData={reloadData}
              teams={(assignments || []).filter(a => a.teamInfo).map(a => ({
              assignmentId: a.id,
              resourceId: a.teamInfo.id,
              code: a.teamInfo.code || a.teamInfo.name || '',
              assignedAt: a.assignedAt || null,
              assignmentStatus: a.status || null,
              organizationCode: a.teamInfo.organization ? a.teamInfo.organization.code : '',
              resourceStatus: a.teamInfo.status || null,
              // Show the actual deployAt from the nested teamInfo when present
              resourceDeployAt: a.teamInfo.deployAt || null,
            }))}
            quadrantId={quadrantId}
          />
        )}
      </Paper>
    </Box>
  );
}

QuadrantTeamsView.propTypes = {
  quadrantId: PropTypes.number,
};
