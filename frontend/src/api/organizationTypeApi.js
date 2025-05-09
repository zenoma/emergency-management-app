import { baseApi } from "./baseApi";

export const organizationTypeApi = baseApi.injectEndpoints({
  endpoints: (build) => ({
    getOrganizationTypes: build.query({
      query: (payload) => ({
        url: "/organizationTypes",
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

export const { useGetOrganizationTypesQuery } = organizationTypeApi;
