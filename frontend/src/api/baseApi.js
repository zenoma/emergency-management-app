import { createApi, fetchBaseQuery } from "@reduxjs/toolkit/query/react";

var URL = import.meta.env.VITE_REACT_APP_BACKEND_URL;

export const baseApi = createApi({
  baseQuery: fetchBaseQuery({
    baseUrl: URL,
    prepareHeaders: (headers, { getState }) => {
      try {
        const state = getState();
        const token = state?.login?.token;
        const locale = state?.i18n?.language || state?.i18n?.lng;
        if (token) {
          headers.set('Authorization', `Bearer ${token}`);
        }
        if (locale) {
          headers.set('Accept-Language', locale);
        }
      } catch (e) {
      }
      return headers;
    }
  }),
  tagTypes: ["Organization", "Team", "Vehicle", "Assignment", "Emergency"],
  endpoints: () => ({}),
});
