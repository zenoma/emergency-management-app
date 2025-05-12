import { Button } from "@mui/material";
import React from "react";
import { useNavigate } from "react-router-dom";
import ArrowBackIcon from "@mui/icons-material/ArrowBack";

export default function BackButton() {
  const navigate = useNavigate();

  return (
    <Button
      onClick={() => navigate(-1)}
      color="secondary"
      sx={{
        position: "fixed",
        top: 100,
        left: 10,
        "z-index": 100,
      }}
    >
      <ArrowBackIcon fontSize="large" />
    </Button>
  );
}
