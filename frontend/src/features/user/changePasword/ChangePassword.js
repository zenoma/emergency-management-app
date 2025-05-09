import Paper from "@mui/material/Paper";
import Typography from "@mui/material/Typography";
import React, { useState } from "react";
import { useSelector } from "react-redux";
import { useNavigate } from "react-router-dom";

import { selectToken, selectUser } from "../login/LoginSlice";

import { Button, FormControl, FormLabel, TextField } from "@mui/material";
import { useTranslation } from "react-i18next";
import { useChangePasswordMutation } from "../../../api/userApi";
import { toast } from "react-toastify";

export default function ChangePassword() {
  const [newPassword, setNewPassword] = useState("");
  const [oldPassword, setOldPassword] = useState("");

  const { t } = useTranslation();
  const { i18n } = useTranslation("home");
  const locale = i18n.language;

  const user = useSelector(selectUser);
  const token = useSelector(selectToken);
  const navigate = useNavigate();

  const [changePassword] = useChangePasswordMutation();

  const handleChange = (event) => {
    if (event.target.id === "old-password") {
      setOldPassword(event.target.value);
    } else if (event.target.id === "new-password") {
      setNewPassword(event.target.value);
    }
  };

  const handleClick = async (e) => {
    const payload = {
      oldPassword: oldPassword,
      newPassword: newPassword,
      id: user.id,
      token: token,
      locale: locale,
    };

    changePassword(payload)
      .unwrap()
      .then((payload) => {
        toast.success(t("change-password-successfully"));
        navigate("/profile");
      })
      .catch((error) => toast.error(t("change-password-error")));

  };

  return (
    <Paper
      sx={{
        minWidth: 400,
        maxWidth: 1000,
        padding: 3,
        textAlign: "center",
        display: "inline-block",
        boxShadow: "none",
      }}
    >
      <form>
        <FormControl>
          <FormLabel>
            <Typography variant="h5" sx={{ color: "primary.light" }}>{t("change-password")}</Typography>
          </FormLabel>
          <TextField
            id="old-password"
            label="Contraseña Vieja"
            type="password"
            margin="normal"
            autoComplete="current-password"
            variant="standard"
            value={oldPassword}
            onChange={(e) => handleChange(e)}
          />
          <TextField
            id="new-password"
            label="Nueva Contraseña"
            type="password"
            autoComplete="current-password"
            variant="standard"
            margin="normal"
            value={newPassword}
            onChange={(e) => handleChange(e)}
          />
          <Button
            type="button"
            color="primary"
            className="form-button"
            onClick={(e) => handleClick(e)}
            variant="contained"
          >
            Enviar
          </Button>
        </FormControl>
      </form>
    </Paper>
  );
}
