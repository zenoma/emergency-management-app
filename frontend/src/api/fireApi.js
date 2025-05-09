import { baseApi } from "./baseApi";

export const fireApi = baseApi.injectEndpoints({
  endpoints: (build) => ({
    getFires: build.query({
      query: (payload) => ({
        url: "/fires",
        headers: {
          Authorization: "Bearer " + payload.token,
          "Accept-Language": payload.locale,
        },
      }),
      transformResponse: (response, meta, arg) => {
        return response;
      },
    }),
    getFireById: build.query({
      query: (payload) => ({
        url: "/fires/" + payload.fireId,
        headers: {
          Authorization: "Bearer " + payload.token,
          "Accept-Language": payload.locale,
        },
      }),
      transformResponse: (response, meta, arg) => {
        return response;
      },
    }),
    createFire: build.mutation({
      query: (payload) => ({
        url: "/fires",
        method: "POST",
        body: {
          description: payload.description,
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
    updateFire: build.mutation({
      query: (payload) => ({
        url: "/fires/" + payload.fireId,
        method: "PUT",
        body: {
          description: payload.description,
          type: payload.type,
          fireIndex: payload.fireIndex,
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
    extinguishFire: build.mutation({
      query: (payload) => ({
        url: "/fires/" + payload.fireId + "/extinguishFire",
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
    extinguishQuadrantByFireId: build.mutation({
      query: (payload) => ({
        url: "/fires/" + payload.fireId + "/extinguishQuadrant?quadrantId=" + payload.quadrantId,
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
  }),
});

export const {
  useGetFiresQuery,
  useGetFireByIdQuery,
  useCreateFireMutation,
  useUpdateFireMutation,
  useExtinguishFireMutation,
  useExtinguishQuadrantByFireIdMutation
} = fireApi;
