import Grid from "@mui/material/Grid";
import Typography from "@mui/material/Typography";
import React, { useEffect, useState } from "react";

import { Card, CardHeader, CardMedia } from "@mui/material";
import LandingMap from "../map/LandingMap";
import Notice from "../notice/Notice";

import Box from "@mui/material/Box";

import Paper from "@mui/material/Paper";
import { useTranslation } from "react-i18next";
import { useGetQuadrantWithActiveFiresQuery } from "../../api/quadrantApi";
import WeatherInfo from "../weather/WeatherInfo";

export default function Dashboard() {
  const { t } = useTranslation();

  const { data: quadrants } = useGetQuadrantWithActiveFiresQuery();

  const [coordinates, setCoordinates] = useState({
    "lat": 0,
    "lon": 0
  });


  useEffect(() => {
    navigator.geolocation.getCurrentPosition(function (position) {
      setCoordinates({
        "lat": position.coords.latitude,
        "lon": position.coords.longitude
      });
    });
  }, []);


  return (
    <Grid container spacing={2} >
      <Grid item xs={12} md={6} lg={8}>
        <Card
          sx={{
            color: "primary.light",
            padding: 2,
            minHeight: 500,
          }}
          variant="outlined"
        >
          <CardHeader
            title={
              <Typography variant="h4">{t("geographic-map")}</Typography>
            }
          />
          <CardMedia>
            <Box sx={{ height: 500 }}>
              {quadrants ? (
                <LandingMap quadrants={quadrants} />
              ) : (
                <Typography variant="body1">{t("loading")}</Typography>
              )}
            </Box>
          </CardMedia>
        </Card>
      </Grid>

      <Grid item xs={12} md={6} lg={4}>
        <Grid container direction="column" spacing={2}>
          <Grid item xs={12} md={6} lg={8}>
            <WeatherInfo lat={coordinates.lat} lon={coordinates.lon} />
          </Grid>
          <Grid item>
            <Paper variant="outlined" sx={{ padding: 2 }}>
              <Typography variant="h4" color="primary.light">
                {t("notices")}
              </Typography>
              <Notice lat={coordinates.lat} lon={coordinates.lon} />
            </Paper>
          </Grid>
        </Grid>
      </Grid>
    </Grid>
  );

}
