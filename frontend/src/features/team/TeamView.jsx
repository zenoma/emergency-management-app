import { Box, CircularProgress, Grid } from "@mui/material";
import { useTranslation } from "react-i18next";
import { useSelector } from "react-redux";
import { useLocation } from "react-router-dom";
import { useGetTeamsByIdQuery } from "../../api/teamApi";
import { selectToken } from "../user/login/LoginSlice";
import UsersList from "../user/UsersList";
import BackButton from "../utils/BackButton";
import TeamCard from "./TeamCard";
import TeamUserAdd from "./TeamUserAdd";

export default function TeamView() {
  const token = useSelector(selectToken);

  const { t } = useTranslation();
  const { i18n } = useTranslation("home");
  const locale = i18n.language;

  const location = useLocation();
  const teamId = location.state.teamId;

  const payload = {
    token: token,
    teamId: teamId,
    locale: locale
  };


  const { data, error, isLoading, refetch } = useGetTeamsByIdQuery(payload, {
    refetchOnMountOrArgChange: true,
  });

  const reloadData = () => {
    refetch();
  };

  return (
    <Box >
      <BackButton />
      {error ? (
        <h1>{t("generic-error")}</h1>
      ) : isLoading ? (
        <CircularProgress />
      ) : data ? (
        <Grid
          container
          spacing={{ xs: 3, md: 3 }}
          columns={{ xs: 4, sm: 8, md: 12 }}
        >
          <Grid item xs={4} sm={8} md={12}>
            <TeamCard data={data} />
          </Grid>
          <Grid item xs={3} sm={7} md={12}>
            <UsersList
              teamId={data.id}
              name={data.code}
              users={data.userList}
              reloadData={reloadData}
            />
            <TeamUserAdd teamId={data.id} reloadData={reloadData} />
          </Grid>
        </Grid>
      ) : null}
    </Box>
  );
}
