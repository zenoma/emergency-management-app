import { Alert, Box, Button } from "@mui/material";
import {
  DataGrid,
  esES,
  GridToolbarColumnsButton,
  GridToolbarContainer,
  GridToolbarDensitySelector,
  GridToolbarFilterButton,
} from "@mui/x-data-grid";
import * as React from "react";
import { useState } from "react";
import { useTranslation } from "react-i18next";
import { useSelector } from "react-redux";
import { useGetUsersQuery, useUpdateRoleMutation } from "../../api/userApi";
import { selectToken, selectUser } from "./login/LoginSlice";
import { useLocation } from "react-router-dom";
import ArrowUpwardIcon from '@mui/icons-material/ArrowUpward';
import ArrowDownwardIcon from '@mui/icons-material/ArrowDownward';
import { toast } from "react-toastify";

export default function UserDataGrid({ childToParent }) {
  const token = useSelector(selectToken);
  const user = useSelector(selectUser);

  const { t } = useTranslation();
  const { i18n } = useTranslation("home");
  const locale = i18n.language;


  const location = useLocation();
  const isUserManagement = location.pathname === "/user-management";

  const [showWarning, setShowWarning] = useState(false);

  var localeText;

  if (i18n.language === "es") {
    localeText = esES.components.MuiDataGrid.defaultProps.localeText;
  }

  const UserRole = {
    COORDINATOR: { value: 0, name: 'COORDINATOR' },
    MANAGER: { value: 1, name: 'MANAGER' },
    USER: { value: 2, name: 'USER' },
  };

  const [pageSize, setPageSize] = useState(10);


  const {
    data: users,
    error,
    isLoading,
    refetch
  } = useGetUsersQuery(
    { token: token, locale: locale },
    {
      refetchOnMountOrArgChange: true,
    }
  );


  const [updateRole] = useUpdateRoleMutation();


  function getNextSuperiorRole(role) {
    const currentRole = UserRole[role];
    if (!currentRole) {
      throw new Error(`Invalid role: ${role}`);
    }
    const nextValue = currentRole.value - 1;
    if (nextValue < UserRole.COORDINATOR.value) {
      throw new Error(`Role ${role} has no superior role`);
    }
    const nextRole = Object.values(UserRole).find(role => role.value === nextValue);
    return nextRole.name;
  }

  function getNextInferiorRole(role) {
    const currentRole = UserRole[role];
    if (!currentRole) {
      throw new Error(`Invalid role: ${role}`);
    }
    const prevValue = currentRole.value + 1;
    if (prevValue > UserRole.USER.value) {
      throw new Error(`Role ${role} has no inferior role`);
    }
    const nextRole = Object.values(UserRole).find(role => role.value === prevValue);
    return nextRole.name;
  }


  const handleClickRoleUp = (e, row) => {
    e.stopPropagation();
    const userRole = getNextSuperiorRole(row.userRole);
    const payload = {
      userRole: userRole,
      id: row.id,
      token: token,
      locale: locale,
    };


    updateRole(payload)
      .unwrap()
      .then((payload) => {
        refetch();
      })
      .catch((error) =>
        toast.error(error.data.globalError)
      );

  }

  const handleClickRoleDown = (e, row) => {
    e.stopPropagation();
    const userRole = getNextInferiorRole(row.userRole);

    const payload = {
      userRole: userRole,
      id: row.id,
      token: token,
    };

    updateRole(payload)
      .unwrap()
      .then((payload) => {
        refetch();
      })
      .catch((error) =>
        toast.error(error.data.globalError)
      );
  }

  const initData = (users) => {
    if (users) {
      return users.map((user) => ({
        ...user,
        hasTeam: user.teamId !== undefined,
      }));
    }
  };


  const data = {
    columns: [
      {
        field: "dni",
        headerName: t("dni"),
        groupable: false,
        aggregable: false,
      },
      {
        field: "email",
        headerName: t("email"),
        width: 150,
        hide: true,
      },
      {
        field: "firstName",
        headerName: t("first-name"),
        groupable: false,
        width: 100,
        aggregable: false,
      },
      {
        field: "lastName",
        headerName: t("last-name"),
        groupable: false,
        width: 100,
        aggregable: false,
      },

      {
        field: "phoneNumber",
        headerName: t("phone-number"),
        width: 150,
        hide: true,
      },
      {
        field: "hasTeam",
        headerName: t("has-team?"),
        type: "boolean",
        width: 200,
      },
      {
        field: "userRole",
        headerName: t("user-role"),
        width: 200,
      },
      {
        field: "role-permissions",
        headerName: t("role-permissions"),
        width: 200,
        renderCell: (params) => (
          <Box onClick={(e) => e.stopPropagation()} sx={{ width: "100%", height: "100%", display: "flex", alignItems: "center", justifyContent: "center" }} >
            <Button
              sx={{ borderRadius: "20px" }}
              variant="contained"
              color="primary"
              disabled={params.row.userRole === UserRole.COORDINATOR.name || user.id === params.row.id}
              onClick={(e) => handleClickRoleUp(e, params.row)}
            >
              <ArrowUpwardIcon />
            </Button>
            <Button
              sx={{ borderRadius: "20px" }}
              variant="contained"
              color="error"
              disabled={params.row.userRole === UserRole.USER.name || user.id === params.row.id}
              onClick={(e) => handleClickRoleDown(e, params.row)}
            >
              <ArrowDownwardIcon />
            </Button>
          </Box >
        ),
      },
    ],
    rows: initData(users),
    initialState: {
      columns: {
        columnVisibilityModel: {
          id: false,
          email: true,
          firstName: true,
          lastName: true,
          dni: true,
          phoneNumber: true,
          userRole: isUserManagement,
          hasTeam: true,
          "role-permissions": isUserManagement,
        },
      },
    },
  };

  function handleRowClick(e) {
    setShowWarning(e.row.hasTeam);
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
    <Box sx={{ height: 500 }}>
      {showWarning && !isUserManagement && (
        <Alert severity="warning">{t("user-selected-has-team-warning")}</Alert>
      )}
      {error ? (
        <h1>{t("generic-error")}</h1>
      ) : isLoading ? (
        <div>Loading</div>
      ) : users ? (
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
