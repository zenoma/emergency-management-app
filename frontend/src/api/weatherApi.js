import { createApi, fetchBaseQuery } from '@reduxjs/toolkit/query/react';

const API_KEY = import.meta.env.VITE_REACT_APP_OPEN_WEATHER_TOKEN;
const API_BASE_URL = "/api/weather";

export const weatherApi = createApi({
    reducerPath: 'weatherApi',
    baseQuery: fetchBaseQuery({
        baseUrl: API_BASE_URL,
        prepareHeaders: (headers) => {
            headers.set('Content-Type', 'application/json');
            return headers;
        },
    }),
    endpoints: (builder) => ({
        getWeather: builder.query({
            query: ({ lat, lon, locale }) => ({
                url: `weather?lat=${lat}&lon=${lon}&APPID=${API_KEY}&units=metric&lang=${locale}`,
                method: 'GET'
            }),
        }),
    }),
});

export const { useGetWeatherQuery } = weatherApi;
