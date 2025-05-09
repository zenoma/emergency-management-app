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
import { useGetActiveVehiclesQuery } from "../../api/vehicleApi";
import { selectToken } from "../user/login/LoginSlice";

export default function VehicleDataGrid({ childToParent }) {
  const token = useSelector(selectToken);

  const { t } = useTranslation();
  const { i18n } = useTranslation("home");
  const locale = i18n.language;

  var localeText;

  if (i18n.language === "es") {
    localeText = esES.components.MuiDataGrid.defaultProps.localeText;
  }

  const [pageSize, setPageSize] = React.useState(10);

  const {
    data: vehicles,
    error,
    isLoading,
  } = useGetActiveVehiclesQuery(
    { token: token, locale: locale },
    {
      refetchOnMountOrArgChange: true,
    }
  );

  const initData = (vehicles) => {
    if (vehicles) {
      return vehicles.map((vehicle) => ({
        ...vehicle,
        organizationCode: vehicle.organization.code,
      }));
    }
  };

  const data = {
    columns: [
      {
        field: "vehiclePlate",
        headerName: t("vehicle-plate"),
        width: 150,
        hide: true,
      },
      {
        field: "type",
        headerName: t("type"),
        width: 200,
        hide: true,
      },
      {
        field: "organizationCode",
        headerName: t("team-organization-belong"),
        width: 200,
        hide: true,
      },
    ],
    rows: initData(vehicles),
    initialState: {
      columns: {
        columnVisibilityModel: {
          id: false,
          vehiclePlate: true,
          type: true,
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
      ) : vehicles ? (
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
