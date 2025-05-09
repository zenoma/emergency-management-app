import { baseApi } from "./baseApi";

export const noticeApi = baseApi.injectEndpoints({
  endpoints: (build) => ({
    createNotice: build.mutation({
      query: (payload) => ({
        url: `/notices`,
        method: "POST",
        body: {
          body: payload.body,
          coordinates: { lon: payload.coordinates.longitude, lat: payload.coordinates.latitude },
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
    getNotices: build.query({
      query: (payload) => ({
        url: `/notices?id=` + payload.id,
        headers: {
          Authorization: "Bearer " + payload.token,
          "Accept-Language": payload.locale,
        },
      }),
      transformResponse: (response, meta, arg) => {
        return response;
      },
    }),
    updateNotice: build.mutation({
      query: (payload) => ({
        url: `/notices/` + payload.id + '/status ',
        method: "PUT",
        body: {
          status: payload.status,
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
    addImage: build.mutation({
      query: (payload) => {
        const formData = new FormData();
        formData.append('image', payload.imageFile);
        return {
          url: `/notices/${payload.id}/images`,
          method: 'POST',
          body: formData,
          headers: {
            Authorization: `Bearer ${payload.token}`,
            'Accept-Language': payload.locale,
          },
        };
      },
      transformResponse: (response, meta, arg) => {
        return response;
      },
    }),
    deleteNotice: build.mutation({
      query: (payload) => ({
        url: `/notices/` + payload.id,
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
});

export const { useCreateNoticeMutation, useGetNoticesQuery, useAddImageMutation, useUpdateNoticeMutation, useDeleteNoticeMutation } = noticeApi;
