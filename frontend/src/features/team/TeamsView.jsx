import PropTypes from "prop-types";
import { useSelector } from "react-redux";

import { CircularProgress } from "@mui/material";

import Paper from "@mui/material/Paper";
import Typography from "@mui/material/Typography";

import { useTranslation } from "react-i18next";
import { useGetActiveTeamsByOrganizationIdQuery } from "../../api/teamApi";
import { selectToken, selectUser } from "../user/login/LoginSlice";
import TeamCreateDialog from "./TeamCreateDialog";
import TeamsTable from "./TeamsTable";

import teamImage from "../../assets/images/team-banner.jpg";

export default function TeamsView(props) {
  const token = useSelector(selectToken);
  const userRole = useSelector(selectUser).userRole;

  const { t } = useTranslation();
  const { i18n } = useTranslation("home");
  const locale = i18n.language;

  const organizationId = props.organizationId;

  const payload = {
    token: token,
    organizationId: organizationId,
    locale: locale,
  };

  const {
    data: teamsList,
    isFetching,
    refetch,
  } = useGetActiveTeamsByOrganizationIdQuery(payload, {
    refetchOnMountOrArgChange: true,
  });

  const reloadData = () => {
    refetch();
  };

  return (
    <Paper
      sx={{
        padding: "10px",
        borderRadius: "10px",
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
        {t("teams-list")}
      </Typography>
      {isFetching ? (
        <CircularProgress />
      ) : teamsList ? (
        <TeamsTable reloadData={reloadData} teams={teamsList} />
      ) : null}

      {userRole !== "USER" && <TeamCreateDialog
        reloadData={reloadData}
        organizationId={organizationId}
      />}

    </Paper>
  );
}

TeamsView.propTypes = {
  organizationId: PropTypes.number.isRequired,
};
