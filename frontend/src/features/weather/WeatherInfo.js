import PropTypes from "prop-types";

import ExpandMoreIcon from '@mui/icons-material/ExpandMore';
import { Accordion, AccordionDetails, AccordionSummary, Box, CardContent, CardHeader, CircularProgress, Paper, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Typography } from "@mui/material";
import React from "react";
import { useTranslation } from "react-i18next";
import { useGetWeatherQuery } from "../../api/weatherApi";


const WeatherInfo = (props) => {

    const { t } = useTranslation();

    const { i18n } = useTranslation("home");
    const locale = i18n.language;


    const { data, isLoading, error } = useGetWeatherQuery({ lat: props.lat, lon: props.lon, locale: locale });

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

    const styles = {
        container: {
            margin: "auto",
            padding: "16px",
        },
        headerCell: {
            color: "secondary.light",
        },
        iconCell: {
            display: "flex",
            justifyContent: "center",
        },
        icon: {
            fontSize: "48px",
        },
    };
    var iconurl = "http://openweathermap.org/img/w/" + data.weather[0].icon + ".png";

    return (
        <TableContainer component={Paper} sx={styles.container}>
            <Typography variant="h5"
                sx={{
                    fontWeight: "bold",
                    color: "primary.light",

                }}>
                {t("weather-info-tittle")}{data.name}
            </Typography>
            <Typography variant="body1" align="center" sx={{
                display: "flex",
                alignItems: "center",
                justifyContent: "center"
            }}>
                {data.weather[0].description.charAt(0).toUpperCase() + data.weather[0].description.slice(1)}

                <img src={iconurl} alt="weather-icon" />
            </Typography>
            <Accordion >
                <AccordionSummary expandIcon={<ExpandMoreIcon />}>
                    <Typography variant="h6" sx={{ color: "primary.light" }}>
                        {t("weather-temperature")}
                    </Typography>
                </AccordionSummary>
                <AccordionDetails>
                    <Box sx={{ display: 'flex', flexDirection: 'column', width: '100%' }}>
                        <Table>
                            <TableHead>
                                <TableRow>
                                    <TableCell sx={styles.headerCell}>{t("weather-temperature")}</TableCell>
                                    <TableCell sx={styles.headerCell}>{t("weather-temperature-min")}</TableCell>
                                    <TableCell sx={styles.headerCell}>{t("weather-temperature-max")}</TableCell>
                                </TableRow>
                            </TableHead>
                            <TableBody>
                                <TableRow>
                                    <TableCell>{data.main.temp}°C</TableCell>
                                    <TableCell>{data.main.temp_min}°C</TableCell>
                                    <TableCell>{data.main.temp_max}°C</TableCell>
                                </TableRow>
                            </TableBody>
                        </Table>
                    </Box>
                </AccordionDetails>
            </Accordion>
            <Accordion >
                <AccordionSummary expandIcon={<ExpandMoreIcon />}>
                    <Typography variant="h6" sx={{ color: "primary.light" }}>
                        {t("weather-humidity-pressure")}
                    </Typography>
                </AccordionSummary>
                <AccordionDetails>
                    <Box sx={{ display: 'flex', flexDirection: 'column', width: '100%' }}>
                        <Table><TableHead>
                            <TableRow>
                                <TableCell sx={styles.headerCell}>{t("weather-humidity")}</TableCell>
                                <TableCell sx={styles.headerCell}>{t("weather-pressure")}</TableCell>
                            </TableRow>
                        </TableHead>
                            <TableBody>
                                <TableRow>
                                    <TableCell >{data.main.humidity}%</TableCell>
                                    <TableCell >{data.main.pressure}hPa</TableCell>
                                </TableRow>
                            </TableBody>
                        </Table>
                    </Box>
                </AccordionDetails>
            </Accordion>
            <Accordion >
                <AccordionSummary expandIcon={<ExpandMoreIcon />}>
                    <Typography variant="h6" sx={{ color: "primary.light" }}>
                        {t("weather-wind")}
                    </Typography>
                </AccordionSummary>
                <AccordionDetails>
                    <Box sx={{ display: 'flex', flexDirection: 'column', width: '100%' }}>
                        <Table>
                            <TableBody>
                                <TableRow>
                                    <TableCell sx={styles.headerCell}>{t("weather-wind-speed")}</TableCell>
                                    <TableCell sx={styles.headerCell}>{t("weather-wind-direction")}</TableCell>
                                </TableRow>
                                <TableRow>
                                    <TableCell>{data.wind.speed}m/s</TableCell>
                                    <TableCell>{data.wind.deg}°</TableCell>
                                </TableRow>
                            </TableBody>
                        </Table>
                    </Box>
                </AccordionDetails>
            </Accordion>
        </TableContainer >
    )
};

export default WeatherInfo;


WeatherInfo.propTypes = {
    lat: PropTypes.number.isRequired,
    lon: PropTypes.number.isRequired,
};