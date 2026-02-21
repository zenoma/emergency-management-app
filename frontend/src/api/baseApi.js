import { createApi, fetchBaseQuery } from "@reduxjs/toolkit/query/react";

var URL = import.meta.env.VITE_REACT_APP_BACKEND_URL;

export const baseApi = createApi({
  baseQuery: fetchBaseQuery({ baseUrl: URL }),
  tagTypes: ["Organization"],
  endpoints: () => ({}),
});
