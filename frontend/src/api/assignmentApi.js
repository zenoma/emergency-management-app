import { baseApi } from "./baseApi";

export const assignmentApi = baseApi.injectEndpoints({
  endpoints: (build) => ({
    createAssignment: build.mutation({
      query: (payload) => ({
        url: "/assignments",
        method: "POST",
        body: {
          emergencyId: payload.emergencyId,
          quadrantId: payload.quadrantId,
          resourceId: payload.resourceId,
          notes: payload.notes,
        },
        headers: {
          Authorization: "Bearer " + payload.token,
          "Accept-Language": payload.locale,
        },
      }),
      transformResponse: (response, meta, arg) => {
        return response;
      },
      invalidatesTags: (result, error, arg) => {
        const tags = [{ type: 'Assignment', id: 'LIST' }];
        if (result) {
          if (result.emergencyInfo && result.emergencyInfo.id) tags.push({ type: 'Emergency', id: result.emergencyInfo.id });
          if (result.teamInfo && result.teamInfo.id) tags.push({ type: 'Team', id: result.teamInfo.id });
          if (result.vehicleInfo && result.vehicleInfo.id) tags.push({ type: 'Vehicle', id: result.vehicleInfo.id });
        }
        tags.push({ type: 'Emergency', id: 'LIST' });
        tags.push({ type: 'Team', id: 'LIST' });
        tags.push({ type: 'Vehicle', id: 'LIST' });
        return tags;
      },
    }),
    getAssignments: build.query({
      query: (filters) => {
        const params = new URLSearchParams();
        const token = filters?.token;
        const locale = filters?.locale;
        if (filters) {
          if (filters.emergencyId) params.append('emergencyId', filters.emergencyId);
          if (filters.quadrantId) params.append('quadrantId', filters.quadrantId);
          if (filters.resourceId) params.append('resourceId', filters.resourceId);
          if (filters.status) params.append('status', filters.status);
          if (filters.from) params.append('from', filters.from);
          if (filters.to) params.append('to', filters.to);
        }
        const qs = params.toString();
        return {
          url: '/assignments' + (qs ? `?${qs}` : ''),
          method: 'GET',
          headers: {
            Authorization: token ? `Bearer ${token}` : undefined,
            'Accept-Language': locale || undefined,
          },
        };
      },
      transformResponse: (response) => response || [],
    }),
    getAssignment: build.query({
      query: ({ id, token, locale }) => ({
        url: `/assignments/${id}`,
        method: 'GET',
        headers: {
          Authorization: token ? `Bearer ${token}` : undefined,
          'Accept-Language': locale || undefined,
        },
      }),
      transformResponse: (response) => response || null,
      providesTags: (result, error, arg) => result ? [{ type: 'Assignment', id: result.id }] : [{ type: 'Assignment', id: 'LIST' }],
    }),
    deleteAssignment: build.mutation({
      query: ({ id, token, locale }) => ({
        url: `/assignments/${id}`,
        method: 'DELETE',
        headers: {
          Authorization: token ? `Bearer ${token}` : undefined,
          'Accept-Language': locale || undefined,
        },
      }),
      invalidatesTags: (result, error, arg) => [{ type: 'Assignment', id: 'LIST' }, { type: 'Assignment', id: arg.id }],
    }),
    updateAssignmentStatus: build.mutation({
      query: ({ id, status, token, locale }) => ({
        url: `/assignments/${id}/status`,
        method: 'PUT',
        body: { status },
        headers: {
          Authorization: token ? `Bearer ${token}` : undefined,
          'Accept-Language': locale || undefined,
        },
      }),
      invalidatesTags: (result, error, arg) => [{ type: 'Assignment', id: arg.id }, { type: 'Assignment', id: 'LIST' }],
    }),
  }),
});

export const { useCreateAssignmentMutation, useGetAssignmentsQuery, useGetAssignmentQuery, useDeleteAssignmentMutation, useUpdateAssignmentStatusMutation } = assignmentApi;
