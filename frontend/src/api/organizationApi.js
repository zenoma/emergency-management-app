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
      providesTags: (result, error, arg) =>
        result ? [{ type: "Organization", id: arg.organizationId }] : [],
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
      providesTags: (result) =>
        result
          ? [
              ...result.map((org) => ({ type: "Organization", id: org.id })),
              { type: "Organization", id: "LIST" },
            ]
          : [{ type: "Organization", id: "LIST" }],
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
      invalidatesTags: [{ type: "Organization", id: "LIST" }],
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
      invalidatesTags: (result, error, arg) => [
        { type: "Organization", id: arg.organizationId },
        { type: "Organization", id: "LIST" },
      ],
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
      invalidatesTags: (result, error, arg) => [
        { type: "Organization", id: arg.organizationId },
        { type: "Organization", id: "LIST" },
      ],
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
