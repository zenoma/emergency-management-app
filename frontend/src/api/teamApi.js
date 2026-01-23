import { baseApi } from "./baseApi";

export const teamApi = baseApi.injectEndpoints({
  endpoints: (build) => ({
    getTeams: build.query({
      query: (payload) => ({
        url: "/teams?code=",
        headers: {
          Authorization: "Bearer " + payload.token,
          "Accept-Language": payload.locale,
        },
      }),
      transformResponse: (response, meta, arg) => {
        return response;
      },
    }),
    getActiveTeams: build.query({
      query: (payload) => ({
        url: "/teams/active",
        headers: {
          Authorization: "Bearer " + payload.token,
          "Accept-Language": payload.locale,
        },
      }),
      transformResponse: (response, meta, arg) => {
        return response;
      },
    }),
    getActiveTeamsByOrganizationId: build.query({
      query: (payload) => ({
        url: "/teams/active?organizationId=" + payload.organizationId,
        headers: {
          Authorization: "Bearer " + payload.token,
          "Accept-Language": payload.locale,
        },
      }),
      transformResponse: (response, meta, arg) => {
        return response;
      },
    }),
    getTeamsById: build.query({
      query: (payload) => ({
        url: "/teams/" + payload.teamId,
        headers: {
          Authorization: "Bearer " + payload.token,
          "Accept-Language": payload.locale,
        },
      }),
      transformResponse: (response, meta, arg) => {
        return response;
      },
    }),
    getUsersById: build.query({
      query: (payload) => ({
        url: "/teams/" + payload.teamId + "/users",
        headers: {
          Authorization: "Bearer " + payload.token,
          "Accept-Language": payload.locale,
        },
      }),
      transformResponse: (response, meta, arg) => {
        return response;
      },
    }),
    getMyTeam: build.query({
      query: (payload) => ({
        url: "/teams/myTeam",
        headers: {
          Authorization: "Bearer " + payload.token,
          "Accept-Language": payload.locale,
        },
      }),
      transformErrorResponse: (response) => {
        if (response.status === 404) {
          return { noTeam: true };
        }
        return response;
      },
    }),
    createTeam: build.mutation({
      query: (payload) => ({
        url: "/teams",
        method: "POST",
        body: {
          code: payload.code,
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
    updateTeam: build.mutation({
      query: (payload) => ({
        url: "/teams/" + payload.teamId,
        method: "PUT",
        body: {
          code: payload.code,
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
    deleteTeambyId: build.mutation({
      query: (payload) => ({
        url: "/teams/" + payload.teamId,
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
    addUser: build.mutation({
      query: (payload) => ({
        url: "/teams/" + payload.teamId + "/addUser",
        method: "POST",
        body: {
          memberId: payload.memberId,
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
    deleteUser: build.mutation({
      query: (payload) => ({
        url: "/teams/" + payload.teamId + "/deleteUser",
        method: "POST",
        body: {
          memberId: payload.memberId,
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
    deployTeam: build.mutation({
      query: (payload) => ({
        url: "/teams/" + payload.teamId + "/deploy",
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
    retractTeam: build.mutation({
      query: (payload) => ({
        url: "/teams/" + payload.teamId + "/retract",
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
  }),
});

export const {
  useGetTeamsQuery,
  useGetActiveTeamsQuery,
  useGetActiveTeamsByOrganizationIdQuery,
  useGetTeamsByIdQuery,
  useGetUsersByIdQuery,
  useGetMyTeamQuery,
  useCreateTeamMutation,
  useUpdateTeamMutation,
  useDeleteTeambyIdMutation,
  useAddUserMutation,
  useDeleteUserMutation,
  useDeployTeamMutation,
  useRetractTeamMutation,
} = teamApi;
