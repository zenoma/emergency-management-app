import Grid from "@mui/material/Grid";
import Typography from "@mui/material/Typography";
import React, { useEffect, useState } from "react";

import { Alert, Card, CardHeader, CardMedia } from "@mui/material";
import LandingMap from "../map/LandingMap";
import Notice from "../notice/Notice";

import Box from "@mui/material/Box";

import Paper from "@mui/material/Paper";
import { useTranslation } from "react-i18next";
import { useGetQuadrantWithActiveFiresQuery, useGetQuadrantByCoordinatesQuery } from "../../api/quadrantApi";
import { transformCoordinates } from "../../app/utils/coordinatesTransformations";
import WeatherInfo from "../weather/WeatherInfo";

export default function Dashboard() {
  const { t } = useTranslation();

  const { data: quadrants, isError } = useGetQuadrantWithActiveFiresQuery();

  const [coordinates, setCoordinates] = useState({
    "lat": 0,
    "lon": 0
  });

  const [utmCoordinates, setUtmCoordinates] = useState(null);


  useEffect(() => {
    navigator.geolocation.getCurrentPosition(function (position) {
      setCoordinates({
        "lat": position.coords.latitude,
        "lon": position.coords.longitude
      });
      const utm = transformCoordinates(position.coords.longitude, position.coords.latitude);
      setUtmCoordinates(utm);
    });
  }, []);

  const { data: currentQuadrant } = useGetQuadrantByCoordinatesQuery(
    utmCoordinates ? { lon: utmCoordinates.longitude, lat: utmCoordinates.latitude } : undefined,
    { skip: !utmCoordinates }
  );


  return (
    <Grid container spacing={2} sx={{ height: "calc(100vh - 100px)", padding: 2 }}>
      <Grid item xs={12} md={6} lg={8} sx={{ display: "flex", flexDirection: "column", height: { xs: "50vh", md: "100%" } }}>
        <Card
          sx={{
            color: "primary.light",
            padding: 2,
            display: "flex",
            flexDirection: "column",
            flex: 1,
            overflow: "hidden",
          }}
          variant="outlined"
        >
          <CardHeader
            title={
              <Typography variant="h4">{t("geographic-map")}</Typography>
            }
          />
          <CardMedia sx={{ flex: 1, minHeight: 0 }}>
            <Box sx={{ height: "100%" }}>
              {isError ? (
                <Alert severity="error">{t("generic-error")}</Alert>
              ) : quadrants ? (
                <LandingMap quadrants={quadrants} />
              ) : (
                <Typography variant="body1">{t("loading")}</Typography>
              )}
            </Box>
          </CardMedia>
        </Card>
      </Grid>

      <Grid item xs={12} md={6} lg={4} sx={{ display: "flex", flexDirection: "column", height: { xs: "auto", md: "100%" } }}>
        <Grid container direction="column" spacing={2} sx={{ flex: 1, minHeight: 0, flexWrap: "nowrap" }}>
          <Grid item sx={{ flexShrink: 0 }}>
            <WeatherInfo lat={coordinates.lat} lon={coordinates.lon} />
          </Grid>
          <Grid item sx={{ flex: 1, minHeight: 200, display: "flex", flexDirection: "column" }}>
            <Paper variant="outlined" sx={{ padding: 2, flex: 1, overflow: "auto", minHeight: 200 }}>
              <Typography variant="h4" color="primary.light">
                {t("notices")}
              </Typography>
              <Notice
                lat={coordinates.lat}
                lon={coordinates.lon}
                quadrantName={currentQuadrant?.nombre}
              />
            </Paper>
          </Grid>
        </Grid>
      </Grid>
    </Grid>
  );

}
