import { baseApi } from "./baseApi";

export const recommendationRuleApi = baseApi.injectEndpoints({
  endpoints: (build) => ({
    getRecommendationRules: build.query({
      query: ({ token, locale, emergencyTypeId }) => ({
        url: `/recommendation-rules?emergencyTypeId=${emergencyTypeId}`,
        headers: {
          Authorization: "Bearer " + token,
          "Accept-Language": locale,
        },
      }),
      transformResponse: (response) => response || [],
    }),
    updateRecommendationRule: build.mutation({
      query: ({ token, locale, id, priority, ruleJson }) => ({
        url: `/recommendation-rules/${id}`,
        method: "PUT",
        body: { priority, ruleJson },
        headers: {
          Authorization: "Bearer " + token,
          "Accept-Language": locale,
        },
      }),
    }),
  }),
});

export const { useGetRecommendationRulesQuery, useUpdateRecommendationRuleMutation } = recommendationRuleApi;
