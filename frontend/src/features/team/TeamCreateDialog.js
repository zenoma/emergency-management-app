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
import { useCreateTeamMutation } from "../../api/teamApi";
import { selectToken } from "../user/login/LoginSlice";

import AddIcon from "@mui/icons-material/Add";
import { useTranslation } from "react-i18next";

export default function TeamCreateDialog(props) {
  const [code, setCode] = useState("");
  const [open, setOpen] = useState(false);
  const organizationId = props.organizationId;

  const { t } = useTranslation();
  const { i18n } = useTranslation("home");
  const locale = i18n.language;

  const token = useSelector(selectToken);

  const [createTeam] = useCreateTeamMutation();

  const handleClickOpen = () => {
    setOpen(true);
  };

  const handleClose = () => {
    setCode("");
    setOpen(false);
  };

  const handleChange = (event) => {
    var id = event.target.id;
    var value = event.target.value;

    if (id === "code") {
      setCode(value);
    }
  };

  const handleClick = async (e) => {
    const payload = {
      code: code,
      token: token,
      organizationId: organizationId,
      locale: locale,
    };

    createTeam(payload)
      .unwrap()
      .then(() => {
        toast.success(t("team-created-successfully"));
        props.reloadData();
        handleClose();
      })
      .catch((error) => toast.error(t("team-created-error")));
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
        <DialogTitle sx={{ color: "primary.light" }}>{t("team-created-title")} </DialogTitle>
        <DialogContent>
          <FormControl>
            <Grid container spacing={2}>
              <Grid item xs={12}>
                <TextField
                  id="code"
                  label={t("team-code")}
                  type="text"
                  autoComplete="current-code"
                  margin="normal"
                  value={code}
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

TeamCreateDialog.propTypes = {
  organizationId: PropTypes.number.isRequired,
};
