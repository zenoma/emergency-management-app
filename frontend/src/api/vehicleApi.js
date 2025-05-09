import { baseApi } from "./baseApi";

export const vehicleApi = baseApi.injectEndpoints({
  endpoints: (build) => ({
    getVehicles: build.query({
      query: (payload) => ({
        url: "/vehicles",
        headers: {
          Authorization: "Bearer " + payload.token,
          "Accept-Language": payload.locale,
        },
      }),
      transformResponse: (response, meta, arg) => {
        return response;
      },
    }),
    getActiveVehicles: build.query({
      query: (payload) => ({
        url: "/vehicles/active",
        headers: {
          Authorization: "Bearer " + payload.token,
          "Accept-Language": payload.locale,
        },
      }),
      transformResponse: (response, meta, arg) => {
        return response;
      },
    }),
    getActiveVehiclesByOrganizationId: build.query({
      query: (payload) => ({
        url: "/vehicles/active?organizationId=" + payload.organizationId,
        headers: {
          Authorization: "Bearer " + payload.token,
          "Accept-Language": payload.locale,
        },
      }),
      transformResponse: (response, meta, arg) => {
        return response;
      },
    }),
    getVehiclesById: build.query({
      query: (payload) => ({
        url: "/vehicles/" + payload.vehicleId,
        headers: {
          Authorization: "Bearer " + payload.token,
          "Accept-Language": payload.locale,
        },
      }),
      transformResponse: (response, meta, arg) => {
        return response;
      },
    }),
    createVehicle: build.mutation({
      query: (payload) => ({
        url: "/vehicles",
        method: "POST",
        body: {
          vehiclePlate: payload.vehiclePlate,
          type: payload.type,
          organizationId: payload.organizationId,
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
    updateVehicle: build.mutation({
      query: (payload) => ({
        url: "/vehicles/" + payload.vehicleId,
        method: "PUT",
        body: {
          vehiclePlate: payload.vehiclePlate,
          type: payload.type,
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
    deleteVehiclebyId: build.mutation({
      query: (payload) => ({
        url: "/vehicles/" + payload.vehicleId,
        method: "DELETE",
        headers: {
          Authorization: "Bearer " + payload.token,
          "Accept-Language": payload.locale,
        },
      }),
      transformResponse: (response, meta, arg) => {
        return response;
      },
    }),
    deployVehicle: build.mutation({
      query: (payload) => ({
        url: "/vehicles/" + payload.vehicleId + "/deploy",
        method: "POST",
        body: {
          gid: payload.gid,
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
    retractVehicle: build.mutation({
      query: (payload) => ({
        url: "/vehicles/" + payload.vehicleId + "/retract",
        method: "POST",
        body: {
          gid: payload.gid,
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
  useGetVehiclesQuery,
  useGetActiveVehiclesQuery,
  useGetActiveVehiclesByOrganizationIdQuery,
  useGetVehiclesByIdQuery,
  useCreateVehicleMutation,
  useUpdateVehicleMutation,
  useDeleteVehiclebyIdMutation,
  useDeployVehicleMutation,
  useRetractVehicleMutation,
} = vehicleApi;
