import React from "react";
import { Link } from "react-router-dom";
import { useTranslation } from "react-i18next";
import PageNotFoundImage from "../app/assets/images/PageNotFound.png";

export default function NotFoundPage() {
  const { t } = useTranslation();

  return (
    <div>
      <img src={PageNotFoundImage} alt="PageNotFound" />
      <p style={{ textAlign: "center" }}>
        <Link to="/">{t("go-to-home") || "Go to Home"}</Link>
      </p>
    </div>
  );
}
