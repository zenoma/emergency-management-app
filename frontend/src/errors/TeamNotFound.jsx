import { Box, Typography } from "@mui/material";
import { useTranslation } from "react-i18next";
import TeamNotFound from "../app/assets/images/TeamNotFound.png";


export default function TeamNotFoundPage() {
  const { t } = useTranslation();

  return (
    <Box
      display="flex"
      flexDirection="column"
      alignItems="center"
      justifyContent="center"
      textAlign="center"
      sx={{
        width: "100%",
        minHeight: 300,
        gap: 2,
        padding: 2,
      }}
    >
      <Typography
        variant="h4"
        sx={{ fontWeight: "bold", color: "error.main" }}
      >
        {t("my-team-not-found-tittle")}
      </Typography>
      <Box
        component="img"
        src={TeamNotFound}
        alt="Team not found"
        sx={{
          maxWidth: 500,
          width: "100%",
          marginTop: 2,
          opacity: 0.9,
        }}
      />

      <Typography
        variant="body1"
        sx={{ color: "error.main", maxWidth: 500 }}
      >
        {t("my-team-not-found-body")}
      </Typography>

    </Box>
  );
}
