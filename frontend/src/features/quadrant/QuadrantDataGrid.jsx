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
import { useGetQuadrantsByScaleQuery } from "../../api/quadrantApi";
import { selectToken } from "../user/login/LoginSlice";

export default function QuadrantDataGrid({ childToParent }) {
  const token = useSelector(selectToken);

  const { t } = useTranslation();
  const { i18n } = useTranslation("home");
  const locale = i18n.language;  

  var localeText;

  if (i18n.language === "es") {
    localeText = esES.components.MuiDataGrid.defaultProps.localeText;
  }

  const [pageSize, setPageSize] = React.useState(25);

  const {
    data: quadrants,
    error,
    isLoading,
  } = useGetQuadrantsByScaleQuery(
    { token: token, scale: "5.0", locale: locale },
    {
      refetchOnMountOrArgChange: true,
    }
  );

  const data = {
    columns: [
      {
        field: "id",
        headerName: t("quadrant-id"),
        groupable: false,
        width: 150,
        aggregable: false,
      },
      {
        field: "folla5",
        headerName: t("quadrant-folla5"),
        width: 200,
      },
      {
        field: "nombre",
        headerName: t("quadrant-name"),
        width: 200,
      },
      {
        field: "fireId",
        headerName: t("quadrant-fire-id"),
        width: 200,
      },
    ],
    rows: quadrants,
    initialState: {
      columns: {
        columnVisibilityModel: {
          id: true,
          name: true,
        },
      },
    },
  };

  function handleRowClick(e) {
    return childToParent(e.row.id);
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
    <Box
      style={{ height: 500, minWidth: 700 }}
      sx={{
        "& .disabled": {
          backgroundColor: "lightgrey",
          color: "grey",
          "pointer-events": "none",
        },
      }}
    >
      {error ? (
        <h1>{t("generic-error")}</h1>
      ) : isLoading ? (
        <div>Loading</div>
      ) : quadrants ? (
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
          rowsPerPageOptions={[25, 50, 100]}
          pagination
          localeText={localeText}
          getRowClassName={(params) => {
            if (params.row.fireId !== undefined) {
              return "disabled";
            }
          }}
          onRowClick={(e) => handleRowClick(e)}
        />
      ) : null}
    </Box>
  );
}
