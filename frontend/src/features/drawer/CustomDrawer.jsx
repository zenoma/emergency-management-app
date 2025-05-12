import { useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import { useNavigate } from "react-router-dom";

import ArticleIcon from "@mui/icons-material/Article";
import ChevronLeftIcon from "@mui/icons-material/ChevronLeft";
import ChevronRightIcon from "@mui/icons-material/ChevronRight";
import GroupsIcon from "@mui/icons-material/Groups";
import GroupWorkIcon from "@mui/icons-material/GroupWork";
import HomeIcon from "@mui/icons-material/Home";
import MenuIcon from "@mui/icons-material/Menu";
import WhatshotIcon from "@mui/icons-material/Whatshot";
import People from "@mui/icons-material/People";
import MuiAppBar from "@mui/material/AppBar";
import Box from "@mui/material/Box";
import CssBaseline from "@mui/material/CssBaseline";
import Divider from "@mui/material/Divider";
import Drawer from "@mui/material/Drawer";
import IconButton from "@mui/material/IconButton";
import List from "@mui/material/List";
import ListItem from "@mui/material/ListItem";
import ListItemButton from "@mui/material/ListItemButton";
import ListItemIcon from "@mui/material/ListItemIcon";
import ListItemText from "@mui/material/ListItemText";
import { styled, useTheme } from "@mui/material/styles";
import Toolbar from "@mui/material/Toolbar";
import Typography from "@mui/material/Typography";

import LoginIcon from "@mui/icons-material/Login";
import { Avatar, Menu, MenuItem, Tooltip } from "@mui/material";
import { useTranslation } from "react-i18next";
import { logout, selectToken, selectUser } from "../user/login/LoginSlice";
import { SwitchLanguajeDropdown } from "./SwitchLanguajeDropdown";
import { SwitchThemeButton } from "./SwitchThemeButton";

const drawerWidth = 240;
const loggedSettingsMenu = ["profile", "logout"];
const notLoggedSettingsMenu = ["login", "sign-up"];

const pages = [
  {
    name: "organizations",
    icon: <GroupsIcon />,
    role: ["COORDINATOR", "MANAGER", "USER"],

  },
  {
    name: "my-team",
    icon: <GroupWorkIcon />,
    role: ["COORDINATOR", "MANAGER", "USER"],
  },
  {
    name: "my-notices",
    icon: <ArticleIcon />,
    role: ["COORDINATOR", "MANAGER", "USER"],
  },
  {
    name: "fire-management",
    icon: <WhatshotIcon />,
    role: ["COORDINATOR", "MANAGER"],
  },
  {
    name: "user-management",
    icon: <People />,
    role: ["COORDINATOR"],
  },
  {
    name: "notice-management",
    icon: <ArticleIcon />,
    role: ["COORDINATOR"],
  },
];

const DrawerHeader = styled("div")(({ theme }) => ({
  display: "flex",
  alignItems: "center",
  padding: theme.spacing(0, 1),
  ...theme.mixins.toolbar,
  justifyContent: "flex-end",
}));

export default function PersistentDrawerLeft() {
  const { t } = useTranslation();

  const theme = useTheme();
  const [open, setOpen] = useState(false);
  const [anchorElUser, setAnchorElUser] = useState(null);

  const token = useSelector(selectToken);
  const userRole = useSelector(selectUser).userRole;

  const dispatch = useDispatch();
  const navigate = useNavigate();

  const handleOpenUserMenu = (event) => {
    setAnchorElUser(event.currentTarget);
  };

  const handleCloseUserMenu = () => {
    setAnchorElUser(null);
  };

  const handleClickUserMenu = (id) => {
    switch (id) {
      case "sign-up":
        navigate("/sign-up");
        break;
      case "login":
        navigate("/login");
        break;
      case "profile":
        navigate("/profile");
        break;
      case "logout":
        dispatch(logout());
        navigate("/");
        break;
      default:
        break;
    }
  };

  const handleClickItemList = (e, text) => {
    switch (text) {
      case "my-team":
        navigate("/my-team");
        break;
      case "organizations":
        navigate("/organizations");
        break;
      case "my-notices":
        navigate("/my-notices");
        break;
      case "fire-management":
        navigate("/fire-management");
        break;
      case "user-management":
        navigate("/user-management");
        break;
      case "notice-management":
        navigate("/notice-management");
        break;
      default:
        navigate("/");
        break;
    }
    handleDrawerClose();
  };

  const handleDrawerOpen = () => {
    setOpen(true);
  };

  const handleDrawerClose = () => {
    setOpen(false);
  };

  return (
    <Box mt={10} sx={{ display: "flex", bgcolor: "secondary.light" }}>
      <CssBaseline />
      <MuiAppBar position="fixed" open={open} sx={{ bgcolor: "primary.main" }}>
        <Toolbar>
          <IconButton
            color="inherit"
            aria-label="open drawer"
            onClick={handleDrawerOpen}
            edge="start"
            sx={{ mr: 2, ...(open && { display: "none" }) }}
          >
            <MenuIcon />
          </IconButton>
          <Typography variant="h5" noWrap component="div" sx={{ flexGrow: 1 }}>
            {t("title")}
          </Typography>
          {!token && (
            <Box sx={{ flexGrow: 0 }}>
              <Tooltip title={t("open-settings")}>
                <IconButton onClick={handleOpenUserMenu} sx={{ p: 0 }}>
                  <LoginIcon />
                </IconButton>
              </Tooltip>
              <Menu
                sx={{ mt: "45px" }}
                id="menu-appbar"
                anchorEl={anchorElUser}
                anchorOrigin={{
                  vertical: "top",
                  horizontal: "right",
                }}
                keepMounted
                transformOrigin={{
                  vertical: "top",
                  horizontal: "right",
                }}
                open={Boolean(anchorElUser)}
                onClose={handleCloseUserMenu}
              >
                {notLoggedSettingsMenu.map((setting) => (
                  <MenuItem key={setting} onClick={handleCloseUserMenu}>
                    <Typography
                      id={setting}
                      textAlign="center"
                      onClick={(e) => handleClickUserMenu(setting)}
                    >
                      {t(setting)}
                    </Typography>
                  </MenuItem>
                ))}
              </Menu>
            </Box>
          )}
          {token && (
            <Box sx={{ flexGrow: 0 }}>
              <Tooltip title="Open settings">
                <IconButton onClick={handleOpenUserMenu} sx={{ p: 0 }}>
                  <Avatar alt="Remy Sharp" src="/static/images/avatar/2.jpg" />
                </IconButton>
              </Tooltip>
              <Menu
                sx={{ mt: "45px" }}
                id="menu-appbar"
                anchorEl={anchorElUser}
                anchorOrigin={{
                  vertical: "top",
                  horizontal: "right",
                }}
                keepMounted
                transformOrigin={{
                  vertical: "top",
                  horizontal: "right",
                }}
                open={Boolean(anchorElUser)}
                onClose={handleCloseUserMenu}
              >
                {loggedSettingsMenu.map((setting) => (
                  <MenuItem key={setting} onClick={handleCloseUserMenu}>
                    <Typography
                      id={setting}
                      textAlign="center"
                      onClick={(e) => handleClickUserMenu(setting)}
                    >
                      {t(setting)}
                    </Typography>
                  </MenuItem>
                ))}
              </Menu>
            </Box>
          )}
        </Toolbar>
      </MuiAppBar>
      <Drawer
        sx={{
          width: drawerWidth,
          flexShrink: 0,
          "& .MuiDrawer-paper": {
            width: drawerWidth,
            boxSizing: "border-box",
          },
        }}
        variant="persistent"
        anchor="left"
        open={open}
      >
        <DrawerHeader sx={{ display: "flex" }}>
          <List sx={{ flexGrow: 10 }}>
            <ListItem
              key="Home"
              disablePadding
              onClick={(e) => handleClickItemList(e, "")}
            >
              <ListItemButton>
                <ListItemIcon>
                  <HomeIcon />
                </ListItemIcon>
                <ListItemText primary="Home" />
              </ListItemButton>
            </ListItem>
          </List>
          <IconButton sx={{ flexGrow: 10 }} onClick={handleDrawerClose}>
            {theme.direction === "ltr" ? (
              <ChevronLeftIcon />
            ) : (
              <ChevronRightIcon />
            )}
          </IconButton>
        </DrawerHeader>

        <Divider />
        <List>
          {pages
            .filter((item) => item.role.includes(userRole))
            .map((item) => (
              <ListItem
                key={item.name}
                disablePadding
                onClick={(e) => handleClickItemList(e, item.name)}
              >
                <ListItemButton>
                  <ListItemIcon>{item.icon}</ListItemIcon>
                  <ListItemText primary={t(item.name)} />
                </ListItemButton>
              </ListItem>
            ))}
        </List>
        <Divider />
        <SwitchLanguajeDropdown />
        <SwitchThemeButton />
      </Drawer>
    </Box>
  );
}
