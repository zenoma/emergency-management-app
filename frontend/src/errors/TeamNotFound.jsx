import React from "react";
import TeamNotFound from "../app/assets/images/TeamNotFound.png";
import { Link, Typography } from "@mui/material";
import { useTranslation } from "react-i18next";

export default function TeamNotFoundPage() {
  const { t } = useTranslation();

  return (
    <div style={{ display: "block", textAlign: "center" }}>
      <Typography
        variant="h4"
        margin={1}
        sx={{ fontWeight: "bold", color: "error.light" }}
      >
        {t("my-team-not-found-tittle")}
      </Typography>
      <Typography variant="body" margin={1} sx={{ color: "error.light" }}>
        {t("my-team-not-found-body")}
      </Typography>
      <img src={TeamNotFound} alt="TeamNotFound" />
      <div></div>
      <Link to="/">Go to Home </Link>
    </div>
  );
}
