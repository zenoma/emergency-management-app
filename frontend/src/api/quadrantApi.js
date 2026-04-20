import { baseApi } from "./baseApi";

export const quadrantApi = baseApi.injectEndpoints({
  endpoints: (build) => ({
    getQuadrantsByScale: build.query({
      query: (payload) => ({
        url: "/quadrants?scale=" + payload.scale,
        headers: {
          Authorization: "Bearer " + payload.token,
          "Accept-Language": payload.locale,
        },
      }),
      transformResponse: (response, meta, arg) => {
        return response;
      },
    }),
    getQuadrantById: build.query({
      query: (payload) => ({
        url: "/quadrants/" + payload.quadrantId,
        headers: {
          Authorization: "Bearer " + payload.token,
          "Accept-Language": payload.locale,
        },
      }),
      transformResponse: (response, meta, arg) => {
        return response;
      },
    }),
    getQuadrantWithActiveEmergencies: build.query({
      query: () => ({
        url: "/quadrants/active",
      }),
      transformResponse: (response, meta, arg) => {
        return response;
      },
    }),

    getQuadrantByCoordinates: build.query({
      query: (payload) => ({
        // Backend expects query as lat=...&lon=... (use lat first to match examples)
        url: "/quadrants/findByCoordinates?lat=" + payload.lat + "&lon=" + payload.lon,
      }),
      transformResponse: (response, meta, arg) => {
        return response;
      },
    }),

    linkEmergency: build.mutation({
      query: (payload) => ({
        url: "/quadrants/" + payload.quadrantId + "/linkEmergency",
        method: "POST",
        body: {
          emergencyId: payload.emergencyId,
        },
        headers: {
          Authorization: "Bearer " + payload.token,
          "Accept-Language": payload.locale,
        },
      }),
      transformResponse: (response, meta, arg) => {
        return response;
      },
    }),
  }),
});

export const {
  useGetQuadrantsByScaleQuery,
  useGetQuadrantByIdQuery,
  useGetQuadrantWithActiveEmergenciesQuery,
  useGetQuadrantByCoordinatesQuery,
  useLinkEmergencyMutation,
} = quadrantApi;
