import { Paper } from "@mui/material";
import React from "react";
import EmergencyDataGrid from "./EmergencyDataGrid";

export default function EmergencyManagementView() {
  return (
    <Paper
      sx={{
        display: "inline-block",
        padding: "10px",
        minWidth: "1000px",
      }}
    >
      <EmergencyDataGrid />
    </Paper>
  );
}
