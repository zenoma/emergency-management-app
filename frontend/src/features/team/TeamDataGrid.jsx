import { Box } from "@mui/material";
import {
  DataGrid,
  esES,
  GridToolbarColumnsButton,
  GridToolbarContainer,
  GridToolbarDensitySelector,
  GridToolbarFilterButton,
} from "@mui/x-data-grid";
import * as React from "react";
import { useTranslation } from "react-i18next";
import { useSelector } from "react-redux";
import { useGetActiveTeamsQuery } from "../../api/teamApi";
import { selectToken } from "../user/login/LoginSlice";

export default function TeamDataGrid({ childToParent }) {
  const token = useSelector(selectToken);

  const { t } = useTranslation();
  const { i18n } = useTranslation("home");
  const locale = i18n.language;

  var localeText;

  if (i18n.language === "es") {
    localeText = esES.components.MuiDataGrid.defaultProps.localeText;
  }

  const [pageSize, setPageSize] = React.useState(10);

  const payload = {
    token: token,
    locale: locale
  }

  const {
    data: teams,
    error,
    isLoading,
  } = useGetActiveTeamsQuery(payload, { refetchOnMountOrArgChange: true, }
  );

  const initData = (teams) => {
    if (teams) {
      return teams.map((team) => ({
        ...team,
        organizationCode: team.organization.code,
      }));
    }
  };

  const data = {
    columns: [
      {
        field: "code",
        headerName: t("team-code"),
        width: 250,
        hide: true,
      },
      {
        field: "organizationCode",
        headerName: t("team-organization-belong"),
        width: 250,
        hide: true,
      },
    ],
    rows: initData(teams),
    initialState: {
      columns: {
        columnVisibilityModel: {
          id: false,
          code: true,
        },
      },
    },
  };

  function handleRowClick(e) {
    return childToParent(e.row);
  }

  function CustomToolbar() {
    return (
      <GridToolbarContainer>
        <GridToolbarColumnsButton />
        <GridToolbarFilterButton />
        <GridToolbarDensitySelector />
      </GridToolbarContainer>
    );
  }

  return (
    <Box style={{ height: 400 }}>
      {error ? (
        <h1>{t("generic-error")}</h1>
      ) : isLoading ? (
        <div>Loading</div>
      ) : teams ? (
        <DataGrid
          {...data}
          components={{ Toolbar: CustomToolbar }}
          componentsProps={{
            pagination: {
              labelRowsPerPage: t("rows-per-page"),
            },
          }}
          pageSize={pageSize}
          onPageSizeChange={(newPageSize) => setPageSize(newPageSize)}
          rowsPerPageOptions={[10, 25, 50]}
          pagination
          localeText={localeText}
          onRowClick={(e) => handleRowClick(e)}
        />
      ) : null}
    </Box>
  );
}
