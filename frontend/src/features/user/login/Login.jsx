import React, { useState } from "react";
import { useDispatch } from "react-redux";
import { useNavigate } from "react-router-dom";
import { toast } from "react-toastify";

import {
  Button,
  FormControl,
  FormLabel,
  Paper,
  TextField,
  Typography,
} from "@mui/material";

import { useTranslation } from "react-i18next";
import { useLoginMutation } from "../../../api/userApi.js";
import { emailValidation } from "../../../app/utils/validations.js";
import "./Login.css";
import { validLogin } from "./LoginSlice.jsx";

export default function Login() {
  const [email, setEmail] = useState("");
  const [isValidEmail, setIsValidEmail] = useState(false);
  const [password, setPassword] = useState("");

  const { t } = useTranslation();
  const { i18n } = useTranslation("home");
  const locale = i18n.language;

  const dispatch = useDispatch();

  const [login] = useLoginMutation();
  const navigate = useNavigate();

  const handleChange = (event) => {
    if (event.target.id === "email") {
      setEmail(event.target.value);
      setIsValidEmail(emailValidation(event.target.value));
    } else {
      setPassword(event.target.value);
    }
  };

  const handleClick = async (e) => {
    const payload = {
      userName: email,
      password: password,
      locale: locale
    };

    login(payload)
      .unwrap()
      .then((payload) => {
        dispatch(validLogin(payload));
        toast.info(t("succesfully-login"));
        navigate("/profile");
      });
  };

  return (
    <Paper
      sx={{
        maxWidth: 500,
        padding: 3,
        textAlign: "center",
        display: "inline-block",
        boxShadow: "none",
      }}
    >
      <FormControl>
        <FormLabel>
          <Typography variant="h5" sx={{ color: "primary.light" }}>{t("login")}</Typography>
        </FormLabel>
        <TextField
          id="email"
          label={t("email")}
          type="email"
          margin="normal"
          autoComplete="current-email"
          error={!isValidEmail && email !== ""}
          helperText={!isValidEmail && email !== "" ? "Not valid email!" : " "}
          variant="standard"
          value={email}
          onChange={(e) => handleChange(e)}
        />
        <TextField
          id="password"
          label={t("password")}
          type="password"
          autoComplete="current-password"
          margin="normal"
          value={password}
          variant="standard"
          onChange={(e) => handleChange(e)}
        />
        <Button
          type="button"
          variant="contained"
          color="primary"
          className="form-button"
          disabled={!isValidEmail}
          onClick={(e) => handleClick(e)}
        >
          Log in
        </Button>
      </FormControl>
    </Paper>
  );
}
