import { useMemo, useState } from "react";
import { useSelector } from "react-redux";
import {
  Alert,
  Box,
  Button,
  CircularProgress,
  Divider,
  FormControl,
  InputLabel,
  List,
  ListItemButton,
  ListItemText,
  MenuItem,
  Paper,
  Stack,
  TextField,
  Typography,
} from "@mui/material";
import { useTranslation } from "react-i18next";
import { toast } from "react-toastify";
import { selectToken, selectUser } from "../user/login/LoginSlice";
import { useGetEmergencyTypesQuery } from "../../api/emergencyApi";
import { useGetOrganizationTypesQuery } from "../../api/organizationTypeApi";
import { useGetRecommendationRulesQuery, useUpdateRecommendationRuleMutation } from "../../api/recommendationRuleApi";
import EmergencyTypeIcon from "../../components/EmergencyTypeIcon";

export default function RecommendationRulesView() {
  const { t, i18n } = useTranslation();
  const token = useSelector(selectToken);
  const userRole = useSelector(selectUser)?.userRole;
  const locale = i18n.language;
  const priorityOptions = [
    { label: t("priority-0", "CERO"), value: 0 },
    { label: t("priority-1", "UNO"), value: 1 },
    { label: t("priority-2", "DOS"), value: 2 },
    { label: t("priority-3", "TRES"), value: 3 },
  ];
  const [selectedTypeId, setSelectedTypeId] = useState("");
  const [selectedRuleId, setSelectedRuleId] = useState("");
  const [priority, setPriority] = useState(0);
  const [ruleWhenType, setRuleWhenType] = useState("default");
  const [ruleWhenValue, setRuleWhenValue] = useState("");
  const [ruleTeams, setRuleTeams] = useState(1);
  const [ruleVehicles, setRuleVehicles] = useState(0);
  const [ruleMaxDistanceKm, setRuleMaxDistanceKm] = useState(25);
  const [rulePreferredOrganizationType, setRulePreferredOrganizationType] = useState("");

  const { data: emergencyTypes = [], isLoading: loadingTypes } = useGetEmergencyTypesQuery({ token, locale });
  const { data: organizationTypes = [], isLoading: loadingOrganizationTypes } = useGetOrganizationTypesQuery({ token, locale });
  const { data: rules = [], isLoading: loadingRules, refetch } = useGetRecommendationRulesQuery(
    { token, locale, emergencyTypeId: selectedTypeId },
    { skip: !selectedTypeId }
  );
  const [updateRule, { isLoading: saving }] = useUpdateRecommendationRuleMutation();

  const selectedRule = useMemo(
    () => rules.find((rule) => String(rule.id) === String(selectedRuleId)),
    [rules, selectedRuleId]
  );

  const buildRuleJson = () => {
    const payload = {
      when: {
        type: ruleWhenType,
        ...(ruleWhenValue ? { value: ruleWhenValue } : {}),
      },
      then: {
        teams: Number(ruleTeams),
        vehicles: Number(ruleVehicles),
        maxDistanceKm: Number(ruleMaxDistanceKm),
        ...(rulePreferredOrganizationType ? { preferredOrganizationType: rulePreferredOrganizationType } : {}),
      },
    };

    return JSON.stringify(payload);
  };

  const loadRuleToForm = (rule) => {
    setPriority(rule.priority ?? 0);
    try {
      const parsed = rule.ruleJson ? JSON.parse(rule.ruleJson) : {};
      setRuleWhenType(parsed?.when?.type || "default");
      setRuleWhenValue(parsed?.when?.value || "");
      setRuleTeams(parsed?.then?.teams ?? 1);
      setRuleVehicles(parsed?.then?.vehicles ?? 0);
      setRuleMaxDistanceKm(parsed?.then?.maxDistanceKm ?? 25);
      setRulePreferredOrganizationType(parsed?.then?.preferredOrganizationType || "");
    } catch (_error) {
      setRuleWhenType("default");
      setRuleWhenValue("");
      setRuleTeams(1);
      setRuleVehicles(0);
      setRuleMaxDistanceKm(25);
      setRulePreferredOrganizationType("");
    }
  };

  if (userRole !== "COORDINATOR") {
    return <Alert severity="error">{t("generic-error")}</Alert>;
  }

  const handleSelectType = (value) => {
    setSelectedTypeId(value);
    setSelectedRuleId("");
    setPriority(0);
    setRuleWhenType("default");
    setRuleWhenValue("");
    setRuleTeams(1);
    setRuleVehicles(0);
    setRuleMaxDistanceKm(25);
    setRulePreferredOrganizationType("");
  };

  const handleSelectRule = (value) => {
    setSelectedRuleId(value);
    const rule = rules.find((item) => String(item.id) === String(value));
    if (rule) {
      loadRuleToForm(rule);
    }
  };

  const handleSave = async () => {
    try {
      const ruleJson = buildRuleJson();
      await updateRule({ token, locale, id: selectedRuleId, priority: Number(priority), ruleJson }).unwrap();
      toast.success(t("saved-successfully", "Saved successfully"));
      refetch();
    } catch (error) {
      toast.error(error?.data?.errorMessage || t("generic-error"));
    }
  };

  return (
    <Paper
      variant="outlined"
      sx={{
        p: 2,
        m: 1,
        minWidth: 900,
        borderRadius: 3,
        overflow: "hidden",
        background: "linear-gradient(180deg, rgba(255,255,255,0.96) 0%, rgba(248,250,252,0.98) 100%)",
      }}
    >
      <Box sx={{ mb: 2, textAlign: "center" }}>
        <Typography variant="h4" sx={{ fontWeight: 800, color: "primary.light" }}>
          {t("recommendation-rules", "Recommendation rules")}
        </Typography>
      </Box>

      {loadingTypes ? (
        <CircularProgress />
      ) : (
        <Box sx={{ display: "grid", gridTemplateColumns: { xs: "1fr", lg: "360px 1fr" }, gap: 2, alignItems: "start" }}>
          <Paper variant="outlined" sx={{ p: 2, borderRadius: 2, minHeight: 420 }}>
            <Typography variant="subtitle1" sx={{ fontWeight: 700, mb: 1 }}>
              {t("emergency-type", "Emergency type")}
            </Typography>
            <TextField
              select
              fullWidth
              size="small"
              value={selectedTypeId}
              onChange={(e) => handleSelectType(e.target.value)}
            >
              {emergencyTypes.map((type) => (
                <MenuItem key={type.id} value={type.id}>
                  <Box sx={{ display: "flex", alignItems: "center", gap: 1 }}>
                    <EmergencyTypeIcon name={type.name} />
                    <span>{type.name}</span>
                  </Box>
                </MenuItem>
              ))}
            </TextField>

            <Divider sx={{ my: 2 }} />

            <Typography variant="subtitle2" sx={{ fontWeight: 700, mb: 1 }}>
              {t("rules", "Rules")}
            </Typography>
            {selectedTypeId ? (
              loadingRules ? (
                <CircularProgress size={24} />
              ) : (
                <List dense sx={{ bgcolor: "background.paper", borderRadius: 2, border: 1, borderColor: "divider" }}>
                  {rules.map((rule) => (
                    <ListItemButton
                      key={rule.id}
                      selected={String(rule.id) === String(selectedRuleId)}
                      onClick={() => handleSelectRule(rule.id)}
                    >
                      <ListItemText
                        primary={`#${rule.id}`}
                        secondary={`${t("priority")} ${rule.priority}`}
                      />
                    </ListItemButton>
                  ))}
                  {!rules.length && (
                    <Box sx={{ p: 2 }}>
                      <Typography variant="body2" color="text.secondary">
                        {t("no-rules", "No rules available")}
                      </Typography>
                    </Box>
                  )}
                </List>
              )
            ) : (
              <Alert severity="info" sx={{ mt: 1 }}>
                {t("select-emergency-type", "Select an emergency type to edit its rules.")}
              </Alert>
            )}
          </Paper>

          <Paper variant="outlined" sx={{ p: 2, borderRadius: 2, minHeight: 420 }}>
            <Typography variant="subtitle1" sx={{ fontWeight: 700, mb: 2 }}>
              {selectedRule ? `#${selectedRule.id}` : t("rule-editor", "Rule editor")}
            </Typography>

            {selectedRule ? (
              <Stack spacing={2}>
                <Box sx={{ display: "grid", gridTemplateColumns: { xs: "1fr", sm: "1fr 1fr" }, gap: 2 }}>
                  <TextField
                    label={t("emergency-type", "Emergency type")}
                    value={selectedRule.emergencyTypeName || ""}
                    disabled
                    size="small"
                  />
                  <TextField
                    select
                    label={t("priority", "Priority")}
                    value={priority}
                    onChange={(e) => setPriority(Number(e.target.value))}
                    size="small"
                  >
                    {priorityOptions.map((option) => (
                      <MenuItem key={option.value} value={option.value}>
                        {option.label}
                      </MenuItem>
                    ))}
                  </TextField>
                </Box>

                <Box sx={{ display: "grid", gap: 2 }}>
                  <Box sx={{ display: "grid", gridTemplateColumns: { xs: "1fr", sm: "repeat(3, 1fr)" }, gap: 2 }}>
                    <TextField
                      type="number"
                      label={t("teams", "Teams")}
                      value={ruleTeams}
                      onChange={(e) => setRuleTeams(e.target.value)}
                      size="small"
                      inputProps={{ min: 0, step: 1 }}
                    />
                    <TextField
                      type="number"
                      label={t("vehicles", "Vehicles")}
                      value={ruleVehicles}
                      onChange={(e) => setRuleVehicles(e.target.value)}
                      size="small"
                      inputProps={{ min: 0, step: 1 }}
                    />
                    <TextField
                      type="number"
                      label={t("max-distance-km", "Max distance (km)")}
                      value={ruleMaxDistanceKm}
                      onChange={(e) => setRuleMaxDistanceKm(e.target.value)}
                      size="small"
                      inputProps={{ min: 0, step: 1 }}
                    />
                  </Box>

                  <TextField
                    select
                    label={t("preferred-organization-type", "Preferred organization type")}
                    value={rulePreferredOrganizationType}
                    onChange={(e) => setRulePreferredOrganizationType(e.target.value)}
                    size="small"
                    disabled={loadingOrganizationTypes}
                  >
                    <MenuItem value="">
                      <em>{t("none", "None")}</em>
                    </MenuItem>
                    {organizationTypes.map((item) => (
                      <MenuItem key={item.id} value={item.name}>
                        {item.name}
                      </MenuItem>
                    ))}
                  </TextField>
                </Box>

                <Box sx={{ display: "flex", justifyContent: "space-between", alignItems: "center", gap: 2, flexWrap: "wrap" }}>
                  <Button variant="contained" disabled={saving} onClick={handleSave}>
                    {t("save", "Save")}
                  </Button>
                </Box>
              </Stack>
            ) : (
              <Alert severity="info">
                {t("select-rule-to-edit", "Choose a rule from the list to edit it.")}
              </Alert>
            )}
          </Paper>
        </Box>
      )}
    </Paper>
  );
}
