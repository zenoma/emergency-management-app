import PropTypes from "prop-types";
import { useSelector } from "react-redux";

import { Box, CircularProgress } from "@mui/material";

import Paper from "@mui/material/Paper";
import Typography from "@mui/material/Typography";

import { useTranslation } from "react-i18next";
import { useGetActiveVehiclesByOrganizationIdQuery } from "../../api/vehicleApi";
import { selectToken, selectUser } from "../user/login/LoginSlice";
import VehicleCreateDialog from "./VehicleCreateDialog";
import VehicleTable from "./VehicleTable";


import vehicleImage from "../../assets/images/vehicle-banner.jpg"

export default function VehiclesView(props) {
  const token = useSelector(selectToken);
  const userRole = useSelector(selectUser).userRole;
  const { t } = useTranslation();
  const { i18n } = useTranslation("home");
  const locale = i18n.language;

  const organizationId = props.organizationId;

  const payload = {
    token: token,
    organizationId: organizationId,
    locale: locale
  };

  const {
    data: vehicleList,
    isFetching,
    refetch,
  } = useGetActiveVehiclesByOrganizationIdQuery(payload, {
    refetchOnMountOrArgChange: true,
  });

  const reloadData = () => {
    refetch();
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
          {t("vehicle-list")}
        </Typography>
        {isFetching ? (
          <CircularProgress />
        ) : vehicleList ? (
          <VehicleTable reloadData={reloadData} vehicles={vehicleList} />
        ) : null}


        {userRole !== "USER" && <VehicleCreateDialog
          reloadData={reloadData}
          organizationId={organizationId}
        />}

      </Paper>
    </Box>
  );
}

VehiclesView.propTypes = {
  organizationId: PropTypes.number.isRequired,
};
