import { Paper } from "@mui/material";
import React from "react";
import EmergencyDataGrid from "./EmergencyDataGrid";

export default function EmergencyManagementView() {
  return (
    <Paper
      sx={{
        display: "flex",
        flexDirection: "column",
        padding: "10px",
        minWidth: "1000px",
        height: "calc(100vh - 120px)",
        boxSizing: "border-box",
        overflow: "hidden",
      }}
    >
      <EmergencyDataGrid />
    </Paper>
  );
}
