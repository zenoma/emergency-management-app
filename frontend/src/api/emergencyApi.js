import { baseApi } from "./baseApi";

export const emergencyApi = baseApi.injectEndpoints({
  endpoints: (build) => ({
    getEmergencies: build.query({
      query: (payload) => ({
        url: "/emergencies",
        headers: {
          Authorization: "Bearer " + payload.token,
          "Accept-Language": payload.locale,
        },
      }),
      transformResponse: (response, meta, arg) => {
        return response;
      },
    }),
    getEmergencyTypes: build.query({
      query: (payload) => ({
        url: "/emergencies/types",
        headers: {
          Authorization: "Bearer " + payload.token,
          "Accept-Language": payload.locale,
        },
      }),
      transformResponse: (response, meta, arg) => {
        return response;
      },
    }),
    getEmergencyById: build.query({
      query: (payload) => ({
        url: "/emergencies/" + payload.emergencyId,
        headers: {
          Authorization: "Bearer " + payload.token,
          "Accept-Language": payload.locale,
        },
      }),
      transformResponse: (response, meta, arg) => {
        return response;
      },
    }),
    createEmergency: build.mutation({
      query: (payload) => ({
        url: "/emergencies",
        method: "POST",
        body: {
          description: payload.description,
          type: payload.type,
          emergencyTypeId: payload.emergencyTypeId,
          emergencyIndex: payload.emergencyIndex,
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
    updateEmergency: build.mutation({
      query: (payload) => ({
        url: "/emergencies/" + payload.emergencyId,
        method: "PUT",
        body: {
          description: payload.description,
          type: payload.type,
          emergencyIndex: payload.emergencyIndex,
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
    resolveEmergency: build.mutation({
      query: (payload) => ({
        url: "/emergencies/" + payload.emergencyId + "/resolveEmergency",
        method: "POST",
        headers: {
          Authorization: "Bearer " + payload.token,
          "Accept-Language": payload.locale,
        },
      }),
      transformResponse: (response, meta, arg) => {
        return response;
      },
    }),
    linkEmergencyToPoint: build.mutation({
      query: (payload) => ({
        url: "/emergencies/" + payload.emergencyId + "/linkPoint",
        method: "POST",
        body: {
          lon: payload.lon,
          lat: payload.lat,
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
    removeQuadrantByEmergencyId: build.mutation({
      query: (payload) => ({
        url: "/emergencies/" + payload.emergencyId + "/removeQuadrant",
        method: "POST",
        body: {
          quadrantId: payload.quadrantId,
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
    linkQuadrants: build.mutation({
      query: (payload) => ({
        url: "/emergencies/" + payload.emergencyId + "/linkQuadrants",
        method: "POST",
        body: {
          quadrantGids: payload.quadrantGids,
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
  useGetEmergenciesQuery,
  useGetEmergencyByIdQuery,
  useGetEmergencyTypesQuery,
  useCreateEmergencyMutation,
  useUpdateEmergencyMutation,
  useLinkEmergencyToPointMutation,
  useResolveEmergencyMutation,
  useRemoveQuadrantByEmergencyIdMutation,
  useLinkQuadrantsMutation,
} = emergencyApi;
