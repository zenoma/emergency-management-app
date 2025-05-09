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
    getQuadrantWithActiveFires: build.query({
      query: () => ({
        url: "/quadrants/active",
      }),
      transformResponse: (response, meta, arg) => {
        return response;
      },
    }),

    linkFire: build.mutation({
      query: (payload) => ({
        url: "/quadrants/" + payload.quadrantId + "/linkFire",
        method: "POST",
        body: {
          fireId: payload.fireId,
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
  useGetQuadrantWithActiveFiresQuery,
  useLinkFireMutation,
} = quadrantApi;
