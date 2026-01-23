import {
  Box,
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  Fab,
  FormControl,
  Grid,
  InputLabel,
  MenuItem,
  Paper,
  Select,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  TextField,
  Typography,
} from "@mui/material";

import AddIcon from "@mui/icons-material/Add";
import FireExtinguisherIcon from '@mui/icons-material/FireExtinguisher';
import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { useSelector } from "react-redux";
import { useLocation, useNavigate } from "react-router-dom";
import { toast } from "react-toastify";
import {
  useExtinguishFireMutation,
  useExtinguishQuadrantByFireIdMutation,
  useGetFireByIdQuery,
  useUpdateFireMutation,
} from "../../api/fireApi";
import { useLinkFireMutation } from "../../api/quadrantApi";
import LandingMap from "../map/LandingMap";
import QuadrantDataGrid from "../quadrant/QuadrantDataGrid";
import { selectToken } from "../user/login/LoginSlice";
import BackButton from "../utils/BackButton";
import WeatherInfo from "../weather/WeatherInfo";
import { untransformCoordinates } from "../../app/utils/coordinatesTransformations";

const fireIndexSelector = ["CERO", "UNO", "DOS", "TRES"];


export default function FireDetailsView() {
  const token = useSelector(selectToken);

  const location = useLocation();
  const fireId = location.state.fireId;

  const { t } = useTranslation();
  const { i18n } = useTranslation("home");
  const locale = i18n.language;

  const navigate = useNavigate();

  const [open, setOpen] = useState(false);
  const [selectedId, setSelectedId] = useState(true);
  const [quadrantId, setQuadrantId] = useState(true);

  const [description, setDescription] = useState();
  const [type, setType] = useState();
  const [fireIndex, setFireIndex] = useState();

  const [openEdit, setOpenEdit] = useState(false);
  const [openQuadrantExtinguish, setOpenQuadrantExtinguish] = useState(false);
  const [openExtinguish, setOpenExtinguish] = useState(false);


  const [coordinates, setCoordinates] = useState("");

  const payload = { token: token, fireId: fireId, locale: locale };

  const { data, refetch } = useGetFireByIdQuery(payload);

  const [linkFire] = useLinkFireMutation();
  const [extinguishFire] = useExtinguishFireMutation();
  const [extinguishQuadrantByFireId] = useExtinguishQuadrantByFireIdMutation();

  useEffect(() => {
    refetch();

    if (data && data.quadrants && data.quadrants.length > 0) {
      setCoordinates(
        {
          lon: untransformCoordinates(data.quadrants[0].coordinates[0].x, data.quadrants[0].coordinates[0].y).longitude,
          lat: untransformCoordinates(data.quadrants[0].coordinates[0].x, data.quadrants[0].coordinates[0].y).latitude,
        }
      )
    }
  }
    , [refetch, data]);

  const childToParent = (childdata) => {
    setSelectedId(childdata);
  };

  const handleOpenClick = () => {
    setOpen(true);
  };

  const handleClose = () => {
    setOpen(false);
  };

  const handleClick = () => {
    const payload = {
      token: token,
      fireId: fireId,
      quadrantId: selectedId,
      locale: locale
    };

    linkFire(payload)
      .unwrap()
      .then(() => {
        toast.success(t("quadrant-linked-successfully"));
        refetch();
        handleClose();
      })
      .catch((error) => toast.error(t("quadrant-linked-error")));
  };

  const handleExtinguishOpenClick = () => {
    setOpenExtinguish(true);
  };

  const handleExtinguishClose = () => {
    setOpenExtinguish(false);
  };

  const handleExtinguishClick = () => {
    const payload = {
      token: token,
      fireId: fireId,
      locale: locale
    };

    extinguishFire(payload)
      .unwrap()
      .then(() => {
        toast.success(t("fire-extinguished-successfully"));
        setOpenExtinguish(false);
        navigate("/fire-management");
      })
      .catch((error) => toast.error(t("fire-extinguished-error")));
  };


  const handleCloseEdit = () => {
    setOpenEdit(false);
  };

  const handleClickOpenEdit = (data) => {
    setDescription(data.description);
    setType(data.type);
    setFireIndex(data.fireIndex);
    setOpenEdit(true);
  };

  const [updateFire] = useUpdateFireMutation();

  const handleEditClick = () => {
    const payload = {
      fireId: fireId,
      token: token,
      description: description,
      type: type,
      fireIndex: fireIndex,
      locale: locale
    };

    updateFire(payload)
      .unwrap()
      .then((payload) => {
        toast.success(t("fire-updated-successfully"));
      })
      .catch((error) => toast.error(t("fire-updated-error")));

    refetch();
    handleCloseEdit();
  };

  const handleChange = (event) => {
    var id = event.target.id;
    var value = event.target.value;

    switch (id) {
      case "description":
        setDescription(value);
        break;
      case "type":
        setType(value);
        break;
      default:
        setFireIndex(value);
        break;
    }
  };


  const handleExtinguishQuadrantOpenClick = (quadrantId) => {
    setQuadrantId(quadrantId);
    setOpenQuadrantExtinguish(true);
  };

  const handleExtinguishQuadrantClose = () => {
    setOpenQuadrantExtinguish(false);
  };

  const handleExtinguishQuadrantClick = () => {
    const payload = {
      token: token,
      fireId: fireId,
      quadrantId: quadrantId,
      locale: locale
    };

    extinguishQuadrantByFireId(payload)
      .unwrap()
      .then(() => {
        toast.success(t("quadrant-extinguished-successfully"));
        setOpenExtinguish(false);
        refetch();
      })
      .catch((error) => toast.error(t("quadrant-extinguished-error")));
    handleExtinguishQuadrantClose();
  };

  return (
    <Box sx={{ padding: 3 }}>
      <BackButton />
      <Typography
        variant="h4"
        margin={1}
        sx={{ fontWeight: "bold", color: "primary.light" }}
      >
        {t("fire-details-title")}
      </Typography>
      {data && (
        <Typography variant="h6" margin={1}>
          {data.description} ({"#" + fireId})
        </Typography>
      )}
      <Grid
        container
        spacing={{ xs: 2, md: 3 }}
        columns={{ xs: 4, sm: 8, md: 12 }}
      >
        <Grid item xs={4} sm={8} md={6}>
          <Paper
            sx={{
              color: "primary.light",
              padding: 2,
            }}
            variant="outlined"
          >
            <Typography variant="h6">{t("quadrant-map")}</Typography>
            {data && <Box sx={{ height: 450 }}><LandingMap quadrants={data.quadrantInfo || []} /></Box>}
          </Paper>
        </Grid>
        <Grid item xs={4} sm={8} md={3}>
          {data && (
            <Paper
              sx={{
                color: "primary.light",
                padding: 2,
              }}
              variant="outlined"
            >
              <Typography variant="h6">{t("quadrant-list")}</Typography>
              <TableContainer
                component={Paper}
                elevation={3}
                sx={{ maxHeight: 420 }}
              >
                <Table stickyHeader aria-label="sticky table">
                  <TableHead>
                    <TableRow>
                      <TableCell sx={{ color: "secondary.light" }}>
                        {t("quadrant-id")}
                      </TableCell>
                      <TableCell
                        sx={{ color: "secondary.light" }}
                        align="right"
                      >
                        {t("quadrant-name")}
                      </TableCell>
                      <TableCell
                        sx={{ color: "secondary.light" }}
                        align="right"
                      >
                        {t("options")}
                      </TableCell>
                    </TableRow>
                  </TableHead>
                  <TableBody>
                    {data.quadrantInfo && data.quadrantInfo.length > 0 ? (
                      data.quadrantInfo.map((row) => (
                        <TableRow
                          key={row.id}
                          hover
                          sx={{
                            "&:last-child td, &:last-child th": { border: 0 },
                          }}
                          onClick={() =>
                            navigate("/quadrant", {
                              state: { quadrantId: row.id },
                            })}
                        >
                          <TableCell component="th" scope="row">
                            {row.id}
                          </TableCell>
                          <TableCell align="right">{row.nombre}</TableCell>
                          <TableCell>
                            <Button
                              sx={{ color: "red" }}
                              onClick={(e) => {
                                e.stopPropagation();
                                handleExtinguishQuadrantOpenClick(row.id);
                              }}
                            >
                              <FireExtinguisherIcon />
                            </Button>
                          </TableCell>
                        </TableRow>
                      ))
                    ) : (
                      <TableRow>
                        <TableCell colSpan={3} align="center">
                          {t("quadrants-empty-list")}
                        </TableCell>
                      </TableRow>
                    )}
                  </TableBody>
                </Table>
              </TableContainer>
              <Box m={1}>
                <Fab
                  color="primary"
                  aria-label="add"
                  onClick={() => handleOpenClick()}
                >
                  <AddIcon />
                </Fab>
              </Box>
            </Paper>
          )}
        </Grid>
        <Grid item xs={4} sm={8} md={3} >
          {data && (
            <Paper
              sx={{
                color: "primary.light",
                padding: 2,
              }}
              variant="outlined"
            >
              <Typography variant="h6">{t("fire-options")}</Typography>
              <Button
                fullWidth
                variant="contained"
                sx={{ margin: "5px" }}
                onClick={() => handleClickOpenEdit(data)}
              >
                {t("edit")}
              </Button>
              <Button
                variant="contained"
                fullWidth
                sx={{
                  backgroundColor: "error.light",
                  margin: "5px",
                  ":hover": { backgroundColor: "error.dark" },
                }}
                onClick={() => handleExtinguishOpenClick()}
              >
                {t("fire-extinguish")}
              </Button>
              <Dialog
                open={openQuadrantExtinguish}
                onClose={handleExtinguishQuadrantClose}
                aria-labelledby="alert-dialog-title"
                aria-describedby="alert-dialog-description"
              >
                <DialogTitle id="alert-dialog-title" sx={{ color: "primary.light" }}>
                  {t("quadrant-extinguish-dialog")}
                </DialogTitle>
                <DialogContent>
                  <Typography variant="body2">
                    {t("quadrant-extinguish-text")}
                  </Typography>
                </DialogContent>
                <DialogActions>
                  <Button onClick={handleExtinguishQuadrantClose}>{t("cancel")}</Button>
                  <Button
                    onClick={handleExtinguishQuadrantClick}
                    color="error"
                    autoFocus
                  >
                    {t("quadrant-extinguish")}
                  </Button>
                </DialogActions>
              </Dialog>
              <Dialog
                open={openExtinguish}
                onClose={handleExtinguishClose}
                aria-labelledby="alert-dialog-title"
                aria-describedby="alert-dialog-description"
              >
                <DialogTitle id="alert-dialog-title" sx={{ color: "primary.light" }}>
                  {t("fire-extinguish-dialog")}
                </DialogTitle>
                <DialogContent>
                  <Typography variant="body2">
                    {t("fire-extinguish-text")}
                  </Typography>
                </DialogContent>
                <DialogActions>
                  <Button onClick={handleExtinguishClose}>{t("cancel")}</Button>
                  <Button
                    onClick={handleExtinguishClick}
                    color="error"
                    autoFocus
                  >
                    {t("fire-extinguish")}
                  </Button>
                </DialogActions>
              </Dialog>
            </Paper>
          )}
          {coordinates && <WeatherInfo sx={{ padding: 2 }} lat={coordinates.lat} lon={coordinates.lon} />}

        </Grid>
      </Grid>



      <Dialog open={open} fullWidth maxWidth="md">
        <DialogTitle sx={{ color: "primary.light" }}>{t("quadrant-add-title")} </DialogTitle>
        <DialogContent>
          <QuadrantDataGrid childToParent={childToParent} />
        </DialogContent>
        <DialogActions>
          <Button onClick={handleClose}>{t("cancel")}</Button>
          <Button autoFocus variant="contained" onClick={() => handleClick()}>
            {t("add")}
          </Button>
        </DialogActions>
      </Dialog>

      <Dialog maxWidth={"md"} open={openEdit}>
        <DialogTitle sx={{ color: "primary.light" }}>{t("fire-updated-title")}</DialogTitle>
        <DialogContent>
          <FormControl>
            <Grid container spacing={2}>
              <Grid item xs={12}>
                <TextField
                  id="description"
                  label={t("fire-description")}
                  type="text"
                  autoComplete="current-code"
                  margin="normal"
                  value={description}
                  onChange={(e) => handleChange(e)}
                  variant="standard"
                  required
                  sx={{ display: "flex" }}
                />
              </Grid>
              <Grid item xs={6}>
                <TextField
                  id="type"
                  label={t("fire-type")}
                  type="text"
                  autoComplete="current-code"
                  margin="normal"
                  value={type}
                  onChange={(e) => handleChange(e)}
                  variant="standard"
                  required
                  sx={{ display: "flex" }}
                />
              </Grid>
              <Grid item xs={6}>
                <FormControl fullWidth >
                  <InputLabel id="input-label-id" >
                    {t("fire-fireIndex")}
                  </InputLabel>
                  <Select
                    id="fireIndex"
                    label={t("fire-fireIndex")}
                    value={fireIndex}
                    onChange={(e) => handleChange(e)}
                    required
                    sx={{ margin: 2 }}
                  >
                    {fireIndexSelector.map((item, index) => (
                      <MenuItem key={index} value={item}>
                        {item}
                      </MenuItem>
                    ))}
                  </Select>
                </FormControl >
              </Grid>

            </Grid>
          </FormControl>
        </DialogContent>
        <DialogActions>
          <Button onClick={handleCloseEdit}>{t("cancel")}</Button>
          <Button
            autoFocus
            variant="contained"
            onClick={(e) => handleEditClick(e)}
          >
            {t("edit")}
          </Button>
        </DialogActions>
      </Dialog>
    </Box >
  );
}
