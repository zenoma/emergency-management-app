import { baseApi } from "./baseApi";

export const logApi = baseApi.injectEndpoints({
  endpoints: (build) => ({
    getEmergencyLogsByEmergencyId: build.query({
      query: (payload) => ({
        url:
          "/logs/emergencies/" +
          payload.emergencyId +
          "?startDate=" +
          payload.startDate +
          "&endDate=" +
          payload.endDate,
        headers: {
          Authorization: "Bearer " + payload.token,
          "Accept-Language": payload.locale,
        },
      }),
      transformResponse: (response, meta, arg) => {
        return response;
      },
    }),
    getTeamLogsByQuadrantId: build.query({
      query: (payload) => ({
        url:
          "/logs/teams?quadrantId=" +
          payload.quadrantId +
          "&startDate=" +
          payload.startDate +
          "&endDate=" +
          payload.endDate,
        headers: {
          Authorization: "Bearer " + payload.token,
          "Accept-Language": payload.locale,
        },
      }),
      transformResponse: (response, meta, arg) => {
        return response;
      },
    }),
    getVehicleLogsByQuadrantId: build.query({
      query: (payload) => ({
        url:
          "/logs/vehicles?quadrantId=" +
          payload.quadrantId +
          "&startDate=" +
          payload.startDate +
          "&endDate=" +
          payload.endDate,
        headers: {
          Authorization: "Bearer " + payload.token,
          "Accept-Language": payload.locale,
        },
      }),
      transformResponse: (response, meta, arg) => {
        return response;
      },
    }),
    getGlobalStatisticsByEmergencyId: build.query({
      query: (payload) => ({
        url: "/logs/statistics?emergencyId=" + payload.emergencyId,
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
  useGetEmergencyLogsByEmergencyIdQuery,
  useGetTeamLogsByQuadrantIdQuery,
  useGetVehicleLogsByQuadrantIdQuery,
  useGetGlobalStatisticsByEmergencyIdQuery,
} = logApi;
