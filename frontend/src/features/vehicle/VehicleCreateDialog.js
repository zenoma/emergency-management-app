import { Box, Fab } from "@mui/material";
import Button from "@mui/material/Button";
import Dialog from "@mui/material/Dialog";
import DialogActions from "@mui/material/DialogActions";
import DialogContent from "@mui/material/DialogContent";
import DialogTitle from "@mui/material/DialogTitle";
import FormControl from "@mui/material/FormControl";
import Grid from "@mui/material/Grid";
import TextField from "@mui/material/TextField";
import PropTypes from "prop-types";
import React, { useEffect, useState } from "react";
import { useSelector } from "react-redux";

import { toast } from "react-toastify";
import { useCreateVehicleMutation } from "../../api/vehicleApi";
import { selectToken } from "../user/login/LoginSlice";

import AddIcon from "@mui/icons-material/Add";
import { useTranslation } from "react-i18next";

export default function VehicleCreateDialog(props) {
  const [vehiclePlate, setVehiclePlate] = useState("");
  const [type, setType] = useState("");
  const [open, setOpen] = useState(false);
  const organizationId = props.organizationId;

  const { t } = useTranslation();
  const { i18n } = useTranslation("home");
  const locale = i18n.language;

  const token = useSelector(selectToken);

  const [createVehicle] = useCreateVehicleMutation();

  const handleClickOpen = () => {
    setOpen(true);
  };

  const handleClose = () => {
    setVehiclePlate("");
    setType("");
    setOpen(false);
  };

  const handleChange = (event) => {
    var id = event.target.id;
    var value = event.target.value;

    switch (id) {
      case "vehiclePlate":
        setVehiclePlate(value);
        break;
      case "type":
        setType(value);
        break;
      default:
        break;
    }
  };

  const handleClick = async (e) => {
    const payload = {
      vehiclePlate: vehiclePlate,
      type: type,
      token: token,
      organizationId: organizationId,
      locale: locale,
    };

    createVehicle(payload)
      .unwrap()
      .then(() => {
        toast.success(t("vehicle-created-successfully"));
        props.reloadData();
        handleClose();
      })
      .catch((error) => toast.error(t("vehicle-created-error")));
  };

  useEffect(() => { }, [open]);

  return (
    <div>
      <Box m={1}>
        <Fab color="primary" aria-label="add" onClick={handleClickOpen}>
          <AddIcon />
        </Fab>
      </Box>
      <Dialog maxWidth={"md"} open={open} onClose={handleClose}>
        <DialogTitle sx={{ color: "primary.light" }}>{t("vehicle-create-title")} </DialogTitle>
        <DialogContent>
          <FormControl>
            <Grid container spacing={2}>
              <Grid item xs={6}>
                <TextField
                  id="vehiclePlate"
                  label={t("vehicle-plate")}
                  type="text"
                  autoComplete="current-vehicle-plate"
                  margin="normal"
                  value={vehiclePlate}
                  onChange={(e) => handleChange(e)}
                  variant="standard"
                  required
                  sx={{ display: "flex" }}
                />
              </Grid>
              <Grid item xs={6}>
                <TextField
                  id="type"
                  label={t("type")}
                  type="text"
                  autoComplete="current-type"
                  margin="normal"
                  value={type}
                  onChange={(e) => handleChange(e)}
                  variant="standard"
                  required
                  sx={{ display: "flex" }}
                />
              </Grid>
            </Grid>
          </FormControl>
        </DialogContent>
        <DialogActions>
          <Button onClick={handleClose}>{t("cancel")}</Button>
          <Button autoFocus variant="contained" onClick={(e) => handleClick(e)}>
            {t("create")}
          </Button>
        </DialogActions>
      </Dialog>
    </div>
  );
}

VehicleCreateDialog.propTypes = {
  organizationId: PropTypes.number.isRequired,
};
