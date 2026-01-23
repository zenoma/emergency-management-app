import React, { useState } from "react";
import { useSelector } from "react-redux";
import { Button, TextField, Box } from "@mui/material";
import { transformCoordinates } from "../../app/utils/coordinatesTransformations";
import { useAddImageMutation, useCreateNoticeMutation } from "../../api/noticeApi";
import { selectToken } from "../user/login/LoginSlice";
import { toast } from "react-toastify";
import { useTranslation } from "react-i18next";

export default function Notice() {
  const [body, setBody] = useState("");
  const [coordinates, setCoordinates] = useState("");
  const [image, setImage] = useState("");
  const [bodyError, setBodyError] = useState(false);


  const { t } = useTranslation();
  const { i18n } = useTranslation("home");
  const locale = i18n.language;

  const token = useSelector(selectToken);

  const [createNotice] = useCreateNoticeMutation();
  const [addImage] = useAddImageMutation();

  const handleChange = (event) => {
    navigator.geolocation.getCurrentPosition(function (position) {
      setCoordinates(
        transformCoordinates(
          position.coords.longitude,
          position.coords.latitude
        )
      );
    });

    if (event.target.id === "notice-body") {
      setBody(event.target.value);
    } else if (event.target.id === "notice-image") {
      const selectedFile = event.target.files[0];
      const file = new File([selectedFile], selectedFile.name, {
        type: selectedFile.type,
      });
      setImage(file);
    }
  };

  const handleClick = async (e) => {
    if (!body.trim()) {
      setBodyError(true);
      toast.error(t("notice-body-required"));
      return;
    }

    setBodyError(false);


    const payload = { body: body, coordinates: coordinates, token: token, locale: locale };

    createNotice(payload)
      .unwrap()
      .then((response) => {
        toast.info(t("notice-created-sucessfully"));
        payload.imageFile = image;
        payload.id = response.id;
        if (image) {
          addImage(payload)
            .unwrap()
            .then()
            .catch((error) => toast.error(t("notice-image-error")));
        }
      })
      .catch((error) => toast.error(t("notice-created-error")));

    setBody("");
    setImage("");
  };

  return (
    <Box>
      <TextField
        fullWidth
        id="notice-body"
        placeholder={t("create-notice-placeholder")}
        type="normal"
        margin="normal"
        autoComplete="notice-body"
        value={body}
        variant="standard"
        onChange={(e) => handleChange(e)}
        required
      />
      <Box m={3}>
        <input
          id="notice-image"
          type="file"
          accept="image/*"
          onChange={(e) => handleChange(e)}
        />
      </Box>
      <Button
        type="button"
        variant="contained"
        color="primary"
        className="form-button"
        onClick={(e) => handleClick(e)}
      >
        {t("create-notice")}
      </Button>
    </Box>
  );
}
