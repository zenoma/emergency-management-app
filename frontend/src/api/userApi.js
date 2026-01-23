import { baseApi } from "./baseApi";


export const userApi = baseApi.injectEndpoints({
  endpoints: (build) => ({
    getUsers: build.query({
      query: (payload) => ({
        url: "/users",
        headers: {
          Authorization: "Bearer " + payload.token,
          "Accept-Language": payload.locale,
        },
      }),
      transformResponse: (response, meta, arg) => {
        return response;
      },
    }),
    login: build.mutation({
      query: (payload) => ({
        url: "/users/login",
        headers: {
          "Accept-Language": payload.locale,
        },
        method: "POST",
        body: payload,
      }),
      transformResponse: (response, meta, arg) => {
        return response;
      },
    }),
    loginFromToken: build.mutation({
      query: (payload) => ({
        url: "/users/loginFromServiceToken",
        method: "POST",
        headers: {
          Authorization: "Bearer " + payload.token,
          "Accept-Language": payload.locale,
        },
        body: payload,
      }),
      transformResponse: (response, meta, arg) => {
        return response;
      },
    }),
    signUp: build.mutation({
      query: (payload) => ({
        url: "/users/signUp",
        headers: {
          "Accept-Language": payload.locale,
        },
        method: "POST",
        body: payload,
      }),
      transformResponse: (response, meta, arg) => {
        return response;
      },
    }),
    changePassword: build.mutation({
      query: (payload) => ({
        url: "/users/" + payload.id + "/changePassword",
        method: "POST",
        body: {
          oldPassword: payload.oldPassword,
          newPassword: payload.newPassword,
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
    updateRole: build.mutation({
      query: (payload) => ({
        url: "/users/" + payload.id + "/updateRole",
        method: "POST",
        body: {
          userRole: payload.userRole,
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
  useGetUsersQuery,
  useLoginMutation,
  useSignUpMutation,
  useChangePasswordMutation,
  useLoginFromTokenMutation,
  useUpdateRoleMutation,
} = userApi;
