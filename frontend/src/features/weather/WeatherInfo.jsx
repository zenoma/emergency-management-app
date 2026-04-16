import PropTypes from "prop-types";

import { Box, CardContent, CardHeader, CircularProgress, Divider, Paper, Typography } from "@mui/material";
import React from "react";
import { useTranslation } from "react-i18next";
import { useGetWeatherQuery } from "../../api/weatherApi";


const WeatherInfo = (props) => {

    const { t } = useTranslation();

    const { i18n } = useTranslation("home");
    const locale = i18n.language;


    const latNum = Number(props.lat);
    const lonNum = Number(props.lon);
    const isNumber = !Number.isNaN(latNum) && !Number.isNaN(lonNum);
    const inRange = (latNum >= -90 && latNum <= 90 && lonNum >= -180 && lonNum <= 180);
    const shouldFetch = isNumber && inRange;

    // If coordinates are out of numeric range, show a friendly message instead of calling the API
    if (isNumber && !inRange) {
        return (
            <Paper>
                <CardHeader title={t("navigator-geolocation-tittle")} />
                <CardContent>
                    <Typography variant="body1" color="error.light" >
                        {t("invalid-coordinates")}
                    </Typography>
                </CardContent>
            </Paper>
        );
    }

    // Ensure hook is always called in the same order to avoid hooks mismatch
    const { data, isLoading, error } = useGetWeatherQuery(
        { lat: latNum, lon: lonNum, locale: locale },
        { skip: !shouldFetch }
    );

    // Log coordinates used for the weather API to help debugging
    if (shouldFetch) console.debug('WeatherInfo fetching with coords', { lat: latNum, lon: lonNum, locale });

    if (!isNumber) {
        // no valid numeric coordinates provided
        return null;
    }

    if (isLoading) {
        return <CircularProgress />;
    }

    if (error) {
        return <Paper >
            <CardHeader title={t("navigator-geolocation-tittle")} />
            <CardContent>
                <Typography variant="body1" color="error.light" >
                    {t("navigator-geolocation-error")}
                </Typography>
            </CardContent>
        </Paper>
            ;
    }

    if (!data) {
        return null;
    }

    var iconurl = "http://openweathermap.org/img/w/" + data.weather[0].icon + ".png";

    return (
        <Paper sx={{ margin: "auto", padding: 2 }}>
            <Typography variant="h5"
                sx={{
                    fontWeight: "bold",
                    color: "primary.light",
                    mb: 1,
                }}>
                {t("weather-info-tittle")}{data.name}
            </Typography>
            <Typography variant="body1" align="center" sx={{
                display: "flex",
                alignItems: "center",
                justifyContent: "center",
                mb: 1,
            }}>
                {data.weather[0].description.charAt(0).toUpperCase() + data.weather[0].description.slice(1)}
                <img src={iconurl} alt="weather-icon" />
            </Typography>

            <Divider sx={{ mb: 1.5 }} />

            {/* Temperatura */}
            <Typography variant="subtitle1" sx={{ fontWeight: "bold", color: "primary.light", mb: 0.5 }}>
                {t("weather-temperature")}
            </Typography>
            <Box sx={{ display: "flex", justifyContent: "space-between", mb: 1.5 }}>
                <Box sx={{ textAlign: "center", flex: 1 }}>
                    <Typography variant="caption" color="text.secondary">{t("weather-temperature")}</Typography>
                    <Typography variant="body1" sx={{ fontWeight: "medium" }}>{data.main.temp}°C</Typography>
                </Box>
                <Box sx={{ textAlign: "center", flex: 1 }}>
                    <Typography variant="caption" color="text.secondary">{t("weather-temperature-min")}</Typography>
                    <Typography variant="body1" sx={{ fontWeight: "medium" }}>{data.main.temp_min}°C</Typography>
                </Box>
                <Box sx={{ textAlign: "center", flex: 1 }}>
                    <Typography variant="caption" color="text.secondary">{t("weather-temperature-max")}</Typography>
                    <Typography variant="body1" sx={{ fontWeight: "medium" }}>{data.main.temp_max}°C</Typography>
                </Box>
            </Box>

            <Divider sx={{ mb: 1.5 }} />

            {/* Humedad y presión */}
            <Typography variant="subtitle1" sx={{ fontWeight: "bold", color: "primary.light", mb: 0.5 }}>
                {t("weather-humidity-pressure")}
            </Typography>
            <Box sx={{ display: "flex", justifyContent: "space-between", mb: 1.5 }}>
                <Box sx={{ textAlign: "center", flex: 1 }}>
                    <Typography variant="caption" color="text.secondary">{t("weather-humidity")}</Typography>
                    <Typography variant="body1" sx={{ fontWeight: "medium" }}>{data.main.humidity}%</Typography>
                </Box>
                <Box sx={{ textAlign: "center", flex: 1 }}>
                    <Typography variant="caption" color="text.secondary">{t("weather-pressure")}</Typography>
                    <Typography variant="body1" sx={{ fontWeight: "medium" }}>{data.main.pressure} hPa</Typography>
                </Box>
            </Box>

            <Divider sx={{ mb: 1.5 }} />

            {/* Viento */}
            <Typography variant="subtitle1" sx={{ fontWeight: "bold", color: "primary.light", mb: 0.5 }}>
                {t("weather-wind")}
            </Typography>
            <Box sx={{ display: "flex", justifyContent: "space-between" }}>
                <Box sx={{ textAlign: "center", flex: 1 }}>
                    <Typography variant="caption" color="text.secondary">{t("weather-wind-speed")}</Typography>
                    <Typography variant="body1" sx={{ fontWeight: "medium" }}>{data.wind.speed} m/s</Typography>
                </Box>
                <Box sx={{ textAlign: "center", flex: 1 }}>
                    <Typography variant="caption" color="text.secondary">{t("weather-wind-direction")}</Typography>
                    <Typography variant="body1" sx={{ fontWeight: "medium" }}>{data.wind.deg}°</Typography>
                </Box>
            </Box>
        </Paper>
    )
};

export default WeatherInfo;


WeatherInfo.propTypes = {
    lat: PropTypes.number.isRequired,
    lon: PropTypes.number.isRequired,
};
