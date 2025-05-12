import PropTypes from "prop-types";
import { useState } from "react";

import Box from "@mui/material/Box";
import Container from "@mui/material/Container";
import Typography from "@mui/material/Typography";

import InfoIcon from "@mui/icons-material/Info";
import Button from "@mui/material/Button";
import Dialog from "@mui/material/Dialog";
import { useTranslation } from "react-i18next";
import OrganizationDetailsCard from "../organization/OrganizationDetailsCard";

export default function TeamCard(props) {
  const [open, setOpen] = useState(false);
  const { t } = useTranslation();

  const handleClickToOpen = () => {
    setOpen(true);
  };

  const handleToClose = () => {
    setOpen(false);
  };

  return (
    <Container
      sx={{
        paddingTop: 3,
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
        {t("team-details")}
      </Typography>
      <Box display="flex" alignItems="center" justifyContent="center">
        <Typography
          variant="h6"
          margin={1}
          sx={{
            fontWeight: "bold",
            color: "secondary.light",
          }}
        >
          {t("team-code")}:
        </Typography>
        <Typography variant="h6" margin={1}>
          {props.data["code"]}
        </Typography>
      </Box>{" "}
      <Box display="flex" alignItems="center" justifyContent="center">
        <Typography
          variant="h6"
          margin={1}
          sx={{
            fontWeight: "bold",
            color: "secondary.light",
          }}
        >
          {t("team-organization-belong")}:
        </Typography>
        <Typography variant="h6">
          {props.data["organization"]["name"]}
        </Typography>
        <InfoIcon
          variant="outlined"
          color="primary"
          onClick={handleClickToOpen}
        ></InfoIcon>
      </Box>
      <Dialog open={open} onClose={handleToClose}>
        <OrganizationDetailsCard data={props.data["organization"]} />
        <Button onClick={handleToClose} color="primary" autoFocus>
          t("close")
        </Button>
      </Dialog>
    </Container>
  );
}

TeamCard.propTypes = {
  data: PropTypes.object,
};
