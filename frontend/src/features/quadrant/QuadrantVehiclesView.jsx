// Quadrant vehicles view: show vehicles assigned to the quadrant via assignments endpoint
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
import QuadrantVehicleTable from "./QuadrantVehicleTable";
import vehicleImage from "../../assets/images/vehicle-banner.jpg";

export default function QuadrantVehiclesView(props) {
  const token = useSelector(selectToken);

  const { t } = useTranslation();
  const { i18n } = useTranslation("home");
  const locale = i18n.language;

  const [unused] = useState(0);

  const quadrantId = props.quadrantId;
  const emergencyIdProp = props.emergencyId || null;

  const payload = {
    token: token,
    quadrantId: quadrantId,
    locale: locale,
  };

  const {
    data: quadrantInfo,
    isFetching: isFetchingQuadrant,
    isError: isErrorQuadrant,
    refetch,
  } = useGetQuadrantByIdQuery(payload, {
    refetchOnMountOrArgChange: true,
  });

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
        {(isFetchingQuadrant || isFetchingAssignments) ? (
          <CircularProgress />
        ) : (isErrorQuadrant || isErrorAssignments) ? (
          <Alert severity="error">{t("generic-error")}</Alert>
        ) : (
          <QuadrantVehicleTable
            reloadData={reloadData}
            vehicles={(assignments || []).filter(a => a.vehicleInfo).map(a => ({
              assignmentId: a.id,
              resourceId: a.vehicleInfo.id,
              vehiclePlate: a.vehicleInfo.plate || a.vehicleInfo.vehiclePlate || '',
              assignedAt: a.assignedAt || null,
              assignmentStatus: a.status || null,
              organizationCode: a.vehicleInfo.organization ? a.vehicleInfo.organization.code : '',
              resourceStatus: a.vehicleInfo.status || null,
              resourceDeployAt: a.vehicleInfo.deployAt || a.assignedAt || null,
            }))}
            quadrantId={quadrantId}
          />
        )}
      </Paper>
    </Box>
  );
}

QuadrantVehiclesView.propTypes = {
  quadrantId: PropTypes.number.isRequired,
};
