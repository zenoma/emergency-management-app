import { Paper } from "@mui/material";
import React from "react";
import FireDataGrid from "./FireDataGrid";

export default function FireManagementView() {
  return (
    <Paper
      sx={{
        display: "inline-block",
        padding: "10px",
        minWidth: "1000px",
      }}
    >
      <FireDataGrid />
    </Paper>
  );
}
