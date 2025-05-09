import { baseApi } from "./baseApi";

export const organizationApi = baseApi.injectEndpoints({
  endpoints: (build) => ({
    getOrganizationById: build.query({
      query: (payload) => ({
        url: "/organizations/" + payload.organizationId,
        headers: {
          Authorization: "Bearer " + payload.token,
          "Accept-Language": payload.locale,
        },
      }),
      transformResponse: (response, meta, arg) => {
        return response;
      },
    }),
    getOrganizationsByOrganizationType: build.query({
      query: (payload) => ({
        url:
          "/organizations?organizationTypeName=" + payload.organizationTypeName,
        headers: {
          Authorization: "Bearer " + payload.token,
          "Accept-Language": payload.locale,
        },
      }),
      transformResponse: (response, meta, arg) => {
        return response;
      },
    }),
    createOrganization: build.mutation({
      query: (payload) => ({
        url: "/organizations",
        method: "POST",
        body: {
          code: payload.code,
          name: payload.name,
          headquartersAddress: payload.headquartersAddress,
          coordinates: {
            lon: payload.coordinates.lng,
            lat: payload.coordinates.lat,
          },
          organizationTypeId: payload.organizationTypeId,
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
    updateOrganization: build.mutation({
      query: (payload) => ({
        url: "/organizations/" + payload.organizationId,
        method: "PUT",
        body: {
          code: payload.code,
          name: payload.name,
          headquartersAddress: payload.headquartersAddress,
          coordinates: {
            lon: payload.coordinates.lng,
            lat: payload.coordinates.lat,
          },
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
    deleteOrganizationById: build.mutation({
      query: (payload) => ({
        url: "/organizations/" + payload.organizationId,
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
  }),
  refetchOnMountOrArgChange: true,
});

export const {
  useGetOrganizationByIdQuery,
  useCreateOrganizationMutation,
  useUpdateOrganizationMutation,
  useGetOrganizationsByOrganizationTypeQuery,
  useDeleteOrganizationByIdMutation,
} = organizationApi;
