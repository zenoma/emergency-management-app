import PropTypes from "prop-types";
import { useState } from "react";

import Container from "@mui/material/Container";
import Typography from "@mui/material/Typography";

import InfoIcon from "@mui/icons-material/Info";
import Button from "@mui/material/Button";
import Dialog from "@mui/material/Dialog";
import DialogActions from "@mui/material/DialogActions";
import DialogContent from "@mui/material/DialogContent";
import OrganizationDetailsCard from "../organization/OrganizationDetailsCard";

export default function VehicleCard(props) {
  const [open, setOpen] = useState(false);

  const handleClickToOpen = () => {
    setOpen(true);
  };

  const handleToClose = () => {
    setOpen(false);
  };

  return (
    <Container
      sx={{
        padding: 3,
        textAlign: "center",
        display: "inline-block",
        boxShadow: "none",
      }}
    >
      <Typography
        variant="h4"
        margin={1}
        sx={{ fontWeight: "bold", color: "primary.light" }}
      >
        Detalles de equipo
      </Typography>
      <Typography
        variant="h6"
        margin={1}
        sx={{
          fontWeight: "bold",
          display: "inline-block",
          color: "secondary.light",
        }}
      >
        Código de equipo:
      </Typography>
      <Typography variant="h6" margin={1}  sx={{ display: "inline" }}>
        {props.data["code"]}
      </Typography>
      <Typography
        variant="h6"
        margin={1}
        sx={{
          fontWeight: "bold",
          display: "block",
          color: "secondary.light",
        }}
      >
        Organización asociada :
      </Typography>
      <Typography variant="h6" margin={1} sx={{ display: "inline-block" }}>
        {props.data["organization"]["name"]}
      </Typography>
      <InfoIcon
        variant="outlined"
        color="primary"
        onClick={handleClickToOpen}
      ></InfoIcon>
      <Dialog open={open} onClose={handleToClose}>
        <DialogContent sx={{ background: "#FAFAFA" }}>
          <OrganizationDetailsCard data={props.data["organization"]} />
        </DialogContent>
        <DialogActions sx={{ background: "#FAFAFA" }}>
          <Button onClick={handleToClose} color="primary" autoFocus>
            Close
          </Button>
        </DialogActions>
      </Dialog>
    </Container>
  );
}

VehicleCard.propTypes = {
  data: PropTypes.object,
};
