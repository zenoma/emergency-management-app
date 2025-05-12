import { Box, Fab, InputLabel, MenuItem, Select } from "@mui/material";
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
import { useCreateOrganizationMutation } from "../../api/organizationApi";
import CoordinatesMap from "../map/CoordinatesMap";
import { selectToken } from "../user/login/LoginSlice";

import AddIcon from "@mui/icons-material/Add";
import { useTranslation } from "react-i18next";

export default function OrganizationCreateDialog(props) {
  const [code, setCode] = useState("");
  const [name, setName] = useState("");
  const [headquartersAddress, setHeadquartersAddress] = useState("");
  const [open, setOpen] = useState(false);
  const [data, setData] = useState("");
  const [organizationTypeId, setOrganizationTypeId] = useState();

  const { t } = useTranslation();
  const { i18n } = useTranslation("home");
  const locale = i18n.language;

  const childToParent = (childdata) => {
    setData({ lat: childdata[0], lng: childdata[1] });
  };

  const token = useSelector(selectToken);
  const organizationTypesList = props.organizationTypes;

  const [createOrganization] = useCreateOrganizationMutation();

  const handleClickOpen = () => {
    setOpen(true);
  };

  const handleClose = () => {
    setCode("");
    setName("");
    setHeadquartersAddress("");
    setData("");
    setOrganizationTypeId("");
    setOpen(false);
  };

  const handleChange = (event) => {
    var id = event.target.id;
    var value = event.target.value;

    switch (id) {
      case "code":
        setCode(value);
        break;
      case "name":
        setName(value);
        break;
      case "headquartersAddress":
        setHeadquartersAddress(value);
        break;
      default:
        setOrganizationTypeId(value);
        break;
    }
  };

  const handleClick = async (e) => {
    const payload = {
      code: code,
      name: name,
      headquartersAddress: headquartersAddress,
      coordinates: data,
      token: token,
      organizationTypeId: organizationTypeId,
      locale: locale
    };

    if (data === "") {
      toast.warning(
        "Por favor indique la dirección de la organización en el mapa."
      );
    }
    if (!organizationTypeId) {
      toast.warning("Por favor indique el tipo de la organización");
    }

    createOrganization(payload)
      .unwrap()
      .then(() => {
        toast.success("Organización creada satisfactoriamente");
        handleClose();
        props.reloadData();
      })
      .catch((error) => toast.error("No se ha podido crear la organización"));
  };

  useEffect(() => { }, [open]);


  return (
    <div>
      <Box m={1}>
        <Fab color="primary" aria-label="add" onClick={handleClickOpen}>
          <AddIcon />
        </Fab>
      </Box>
      <Dialog
        fullWidth={true}
        maxWidth={"md"}
        open={open}
        onClose={handleClose}
      >
        <DialogTitle sx={{ color: "primary.light" }}>{t("organization-create-new")} </DialogTitle>
        <DialogContent>
          <FormControl fullWidth>
            <Grid container spacing={2} sx={{ padding: 1 }}>
              <Grid item xs={3} >
                <TextField
                  id="code"
                  label={t("organization-code")}
                  type="text"
                  autoComplete="current-code"
                  value={code}
                  onChange={(e) => handleChange(e)}
                  required
                  sx={{ display: "flex" }}
                  variant="standard"
                />
              </Grid>
              <Grid item xs={3}>
                <TextField
                  id="name"
                  label={t("organization-name")}
                  type="text"
                  autoComplete="current-name"
                  value={name}
                  onChange={(e) => handleChange(e)}
                  required
                  sx={{ display: "flex" }}
                  variant="standard"
                />
              </Grid>
              <Grid item xs={6}>
                <FormControl fullWidth>
                  <InputLabel id="input-label-id">
                    {t("organization-type-name")}
                  </InputLabel>
                  <Select
                    id="organizationTypes"
                    labelId="input-label-id"
                    label={t("organization-type-name")}
                    value={organizationTypeId}
                    onChange={(e) => handleChange(e)}
                    required
                  >
                    {organizationTypesList.map((item, index) => (
                      <MenuItem key={index} value={item.id}>
                        {item.name}
                      </MenuItem>
                    ))}
                  </Select>
                </FormControl>
              </Grid>
              <Grid item xs={12}>
                <TextField
                  id="headquartersAddress"
                  label={t("organization-address")}
                  type="text"
                  autoComplete="current-headquartersAddress"
                  margin="normal"
                  value={headquartersAddress}
                  onChange={(e) => handleChange(e)}
                  helperText=" "
                  required
                  sx={{ display: "flex" }}
                  variant="standard"
                />
              </Grid>
            </Grid>
            <Box sx={{ height: 300 }}>
              <CoordinatesMap childToParent={childToParent} /></Box>
          </FormControl>
        </DialogContent>
        <DialogActions>
          <Button onClick={handleClose}>{t("cancel")}</Button>
          <Button onClick={(e) => handleClick(e)}>{t("create")}</Button>
        </DialogActions>
      </Dialog>
    </div>
  );
}

OrganizationCreateDialog.propTypes = {
  organizationTypes: PropTypes.array.isRequired,
};
