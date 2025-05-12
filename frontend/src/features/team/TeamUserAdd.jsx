import {
  Box,
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  Fab,
} from "@mui/material";
import PropTypes from "prop-types";

import AddIcon from "@mui/icons-material/Add";
import React, { useState } from "react";
import { useTranslation } from "react-i18next";
import { useSelector } from "react-redux";
import { toast } from "react-toastify";
import { useAddUserMutation } from "../../api/teamApi";
import { selectToken } from "../user/login/LoginSlice";
import UserDataGrid from "../user/UserDataGrid";

export default function TeamUserAdd(props) {
  const token = useSelector(selectToken);
  const [open, setOpen] = useState(false);
  const [selectedId, setSelectedId] = useState(-1);

  const { t } = useTranslation();
  const { i18n } = useTranslation("home");
  const locale = i18n.language;

  const [addUser] = useAddUserMutation();
  const teamId = props.teamId;

  const handleClickOpen = () => {
    setOpen(true);
  };
  const handleClose = () => {
    setSelectedId("-1");
    setOpen(false);
  };

  const handleClick = () => {
    if (selectedId === "-1") {
      toast.error(t("team-user-add-error"));
    } else {
      const payload = {
        token: token,
        memberId: selectedId,
        teamId: teamId,
        locale: locale
      };

      addUser(payload)
        .unwrap()
        .then(() => {
          toast.success(t("user-added-successfully"));
          setSelectedId(-1);
          setOpen(false);
          props.reloadData();
        })
        .catch((error) => toast.error(t("user-added-error")));
    }
  };

  const childToParent = (childdata) => {
    setSelectedId(childdata.id);
  };

  return (
    <Box m={1}>
      <Fab color="primary" aria-label="add" onClick={() => handleClickOpen()}>
        <AddIcon />
      </Fab>
      <Dialog
        fullWidth={true}
        maxWidth={"md"}
        open={open}
        onClose={handleClose}
        PaperProps={{
          sx: {
            height: 600,
          },
        }}
      >
        <DialogTitle sx={{ color: "primary.light" }}>{t("team-user-add")} </DialogTitle>
        <DialogContent>
          <UserDataGrid childToParent={childToParent} />
        </DialogContent>
        <DialogActions>
          <Button onClick={handleClose}>{t("cancel")}</Button>
          <Button onClick={() => handleClick()}>{t("add")}</Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
}

TeamUserAdd.propTypes = {
  teamId: PropTypes.number.isRequired,
};
