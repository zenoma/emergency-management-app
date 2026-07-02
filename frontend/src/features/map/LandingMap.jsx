import PropTypes from "prop-types";
import React, { useCallback, useState, useEffect, useMemo } from "react";
import ReactDOMServer from 'react-dom/server';
import LocalFireDepartmentIcon from '@mui/icons-material/LocalFireDepartment';
import WaterIcon from '@mui/icons-material/Water';
import TerrainIcon from '@mui/icons-material/Terrain';
import DirectionsCarIcon from '@mui/icons-material/DirectionsCar';
import MedicalServicesIcon from '@mui/icons-material/MedicalServices';
import ScienceIcon from '@mui/icons-material/Science';
import FactoryIcon from '@mui/icons-material/Factory';
import StormIcon from '@mui/icons-material/Storm';
import HelpOutlineIcon from '@mui/icons-material/HelpOutline';

import { useSelector } from "react-redux"
import { useTheme } from '@mui/material/styles';
import { useTranslation } from "react-i18next";
import Map, { Layer, NavigationControl, Source, Marker } from "react-map-gl/maplibre";
import 'maplibre-gl/dist/maplibre-gl.css';
import { useLocation, useNavigate } from "react-router-dom";
import { untransformCoordinates } from "../../app/utils/coordinatesTransformations";
import { selectToken } from "../user/login/LoginSlice";
import teamIconSvg from "../../assets/images/team-icon.svg";
import vehicleIconSvg from "../../assets/images/vehicle-icon.svg";
import incendioSvg from "../../assets/images/emergency-icon/incendio.svg";
import tormentaSvg from "../../assets/images/emergency-icon/tormenta.svg";
import montanaSvg from "../../assets/images/emergency-icon/montana.svg";
import inundacionSvg from "../../assets/images/emergency-icon/inundacion.svg";
import cocheSvg from "../../assets/images/emergency-icon/coche.svg";
import medicaSvg from "../../assets/images/emergency-icon/emergencia-medica.svg";
import quimicalSvg from "../../assets/images/emergency-icon/quimical hazard.svg";
import industrialSvg from "../../assets/images/emergency-icon/riesgo-industrial.svg";
import otrosSvg from "../../assets/images/emergency-icon/otros.svg";

export default function LandingMap(props) {

  const token = useSelector(selectToken);
  const navigate = useNavigate();
  const theme = useTheme();
  const { t } = useTranslation();

  const quadrants = props.quadrants;
  const [interactiveLayerIds, setInteractiveLayerIds] = useState([]);

  const MAP_STYLE = "https://api.maptiler.com/maps/topo-v2/style.json?key=3GSLdy5VE4yLq4OhlyYJ"

  const [cursor] = useState("auto");
  const [iconsLoaded, setIconsLoaded] = useState(false);

  const loadImage = (map, url, id, size) => {
    return new Promise((resolve, reject) => {
      const img = new Image(size, size);
      img.crossOrigin = "anonymous";
      img.onload = () => {
        if (!map.hasImage(id)) {
          map.addImage(id, img);
        }
        resolve();
      };
      img.onerror = reject;
      img.src = url;
    });
  };

  const onMapLoad = useCallback((event) => {
    const map = event.target;

    const makeSvgDataUrl = (IconComp, color = theme.palette.status.busy.main, size = 48) => {
      const svgString = ReactDOMServer.renderToStaticMarkup(
        React.createElement(IconComp, { style: { color: color, width: size, height: size } })
      );
      return 'data:image/svg+xml;charset=utf-8,' + encodeURIComponent(svgString);
    };

    const emergencyIconsToLoad = [
      { id: 'emergency-fire', uri: makeSvgDataUrl(LocalFireDepartmentIcon, theme.palette.status.busy.main, 48) },
      { id: 'emergency-water', uri: makeSvgDataUrl(WaterIcon, '#2196f3', 48) },
      { id: 'emergency-terrain', uri: makeSvgDataUrl(TerrainIcon, '#6d4c41', 48) },
      { id: 'emergency-car', uri: makeSvgDataUrl(DirectionsCarIcon, '#9e9e9e', 48) },
      { id: 'emergency-medical', uri: makeSvgDataUrl(MedicalServicesIcon, '#4caf50', 48) },
      { id: 'emergency-science', uri: makeSvgDataUrl(ScienceIcon, '#9c27b0', 48) },
      { id: 'emergency-factory', uri: makeSvgDataUrl(FactoryIcon, '#ff9800', 48) },
      { id: 'emergency-storm', uri: makeSvgDataUrl(StormIcon, '#607d8b', 48) },
      { id: 'emergency-default', uri: makeSvgDataUrl(HelpOutlineIcon, '#607d8b', 48) },
    ];

    const loads = [
      loadImage(map, teamIconSvg, "team-icon", 64),
      loadImage(map, vehicleIconSvg, "vehicle-icon", 64),
      ...emergencyIconsToLoad.map(item => loadImage(map, item.uri, item.id, 64))
    ];

    Promise.all(loads).then(() => {
      setIconsLoaded(true);
    }).catch((e) => {
      console.error('Failed to load map icons', e);
      setIconsLoaded(true);
    });
  }, []);

  // Viewport settings
  const INITIAL_VIEW_STATE = {
    longitude: -7.787,
    latitude: 43.0,
    zoom: 6,
    pitch: 0,
    bearing: 0,
  };

  const [viewport, setViewport] = useState({
    width: "100%",
    height: "100%",
  });


  const bounds = [
    [-10.353521, 40.958984], // northeastern corner of the bounds
    [-4.615985, 44.50585], // southwestern corner of the bounds
  ];

  const location = useLocation();
  const isEmergencyDetails = location.pathname === "/emergency-details";


  const emergencyFeatures = useMemo(() => {
    if (!props.emergencies || !Array.isArray(props.emergencies)) return [];
    const feats = props.emergencies.map((e) => {
      if (!e || !e.location) return null;
      const lx = Number(e.location.lon);
      const ly = Number(e.location.lat);
      if (Number.isNaN(lx) || Number.isNaN(ly)) return null;
      let lon = null;
      let lat = null;
      try {
        // Heuristic: if values are large (>> 1e6) they are projected coordinates
        if (Math.abs(lx) > 1000000 || Math.abs(ly) > 1000000) {
          const geo = untransformCoordinates(lx, ly);
          lon = geo.longitude;
          lat = geo.latitude;
        } else {
          // assume already geographic
          lon = lx;
          lat = ly;
        }
      } catch (err) {
        console.error('Failed to compute emergency geo coordinates', err, e);
        return null;
      }
      // validate range
      if (Math.abs(lon) > 180 || Math.abs(lat) > 90) {
        console.warn('Emergency coords out of geographic range', { lon, lat, raw: e.location });
        return null;
      }
      const rawTypeKey = (e.emergencyTypeName || (e.emergencyType && e.emergencyType.name) || e.type || e.name || '');
      return {
        type: "Feature",
        properties: { id: e.id, title: e.description, typeKey: rawTypeKey },
        geometry: { type: "Point", coordinates: [lon, lat] },
      };
    }).filter(Boolean);
    return feats;
  }, [props.emergencies]);

  const iconDataUrlMap = useMemo(() => {
    const map = {};
    const iconEntries = {
      'emergency-fire': LocalFireDepartmentIcon,
      'emergency-water': WaterIcon,
      'emergency-terrain': TerrainIcon,
      'emergency-car': DirectionsCarIcon,
      'emergency-medical': MedicalServicesIcon,
      'emergency-science': ScienceIcon,
      'emergency-factory': FactoryIcon,
      'emergency-storm': StormIcon,
      'emergency-default': HelpOutlineIcon,
    };
    Object.entries(iconEntries).forEach(([key, Comp]) => {
      try {
        const svgString = ReactDOMServer.renderToStaticMarkup(
          React.createElement(Comp, { style: { color: theme.palette.status.busy.main, width: 28, height: 28 } })
        );
        map[key] = 'data:image/svg+xml;charset=utf-8,' + encodeURIComponent(svgString);
      } catch (e) {
        console.error('Failed to render icon to SVG for', key, e);
        map[key] = null;
      }
    });
    return map;
  }, [theme]);

  useEffect(() => {
    const ids = [];
    if (quadrants && Array.isArray(quadrants)) {
      quadrants.forEach((q) => {
        const id = q && q.id != null ? q.id : (q && q.quadrant && q.quadrant.id != null ? q.quadrant.id : null);
        if (id != null) ids.push(id.toString());
      });
    }
    if (emergencyFeatures && emergencyFeatures.length > 0) {
      ids.push('emergency-symbols');
    }
    // dedupe
    const uniq = Array.from(new Set(ids));
    setInteractiveLayerIds(uniq);
  }, [quadrants, emergencyFeatures]);

  return (
    <Map
      {...viewport}
      onViewportChange={setViewport}
      minZoom={6}
      maxZoom={15}
      initialViewState={INITIAL_VIEW_STATE}
      mapStyle={MAP_STYLE}
      onLoad={onMapLoad}
      cursor={cursor}
      maxBounds={bounds}
      interactiveLayerIds={interactiveLayerIds}
    >
      <div style={{ position: "absolute", zIndex: 1 }}>
        <NavigationControl />
      </div>

      {quadrants &&
      quadrants.map((item) => {
          // support two shapes: item can be a quadrant object or { quadrant, linkedAt, resolvedAt }
          const quad = item && item.quadrant ? item.quadrant : item;
          if (!quad || !quad.coordinates) return null;

          const coord = quad.coordinates.map((c) => {
            return [
              untransformCoordinates(c.x, c.y).longitude,
              untransformCoordinates(c.x, c.y).latitude,
            ];
          });

          const teamSize = quad.teamList ? quad.teamList.length : -1;
          const vehicleSize = quad.vehicleList ? quad.vehicleList.length : -1;

          const quadrantLabelStyle = {
            id: quad.id.toString() + "-label",
            minzoom: 11.5,
            type: "symbol",
            source: "label",
            layout: {
              "text-field": "{place-name} (#{place-id})",
              "text-size": 15,
              "text-anchor": "center",
              "text-offset": [0, -2],
              "text-allow-overlap": true,
            },
            paint: {
              "text-color": "black",
              "text-halo-color": "#fff",
              "text-halo-width": 1,
            },
          };

          const teamLabelStyle = {
            id: quad.id.toString() + "-team-label",
            minzoom: 11.5,
            type: "symbol",
            source: "label",
            layout: {
              "text-field": "{team-size}",
              "text-size": 15,
              "text-anchor": "left",
              "text-offset": [1.2, 0.5],
              "icon-image": "team-icon",
              "icon-size": 0.35,
              "icon-anchor": "center",
              "icon-offset": [0, 30],
              "icon-allow-overlap": true,
              "text-allow-overlap": true,
            },
              paint: {
               "text-color": "white",
               "text-halo-color": theme.palette.status.available.main,
               "text-halo-width": 1,
             },
          };

          const vehiclesLabelStyle = {
            id: quad.id.toString() + "-vehicle-label",
            minzoom: 11.5,
            type: "symbol",
            source: "label",
            layout: {
              "text-field": "{vehicle-size}",
              "text-size": 15,
              "text-anchor": "left",
              "text-offset": [1.2, 3],
              "icon-image": "vehicle-icon",
              "icon-size": 0.35,
              "icon-anchor": "center",
              "icon-offset": [0, 120],
              "icon-allow-overlap": true,
              "text-allow-overlap": true,
            },
              paint: {
               "text-color": "white",
               "text-halo-color": theme.palette.status.available.main,
               "text-halo-width": 1,
             },
          };

          const quadrantLayerStyle = {
            id: quad.id.toString(),
            type: "fill",
            layout: {},
            paint: {
              // use status busy colors for quadrant highlight
              "fill-color": theme.palette.status.busy.main,
              "fill-opacity": 0.5,
            },
          };

          const quadrantBorderStyle = {
            id: quad.id.toString() + "-border",
            type: "line",
            layout: {},
            paint: {
              // darker busy border
              "line-color": theme.palette.status.busy.dark,
              "line-width": 2,
            },
          };

          const geoJson = {
            type: "FeatureCollection",
            features: [
              {
                type: "Feature",
                properties: Object.assign({}, quad, {
                  "place-name": quad.nombre,
                  "place-id": quad.id,
                  "team-size": teamSize,
                  "vehicle-size": vehicleSize,
                }),
                geometry: {
                  type: "MultiPolygon",
                  coordinates: [[coord]],
                },
              },
            ],
          };
          return (
            <Source key={quad.id.toString()} type="geojson" data={geoJson}>
              <Layer {...quadrantBorderStyle} />
              <Layer {...quadrantLayerStyle} />
              {teamSize === -1 ? undefined : <Layer {...teamLabelStyle} />}
              {vehicleSize === -1 ? undefined : (
                <Layer {...vehiclesLabelStyle} />
              )}
              <Layer {...quadrantLabelStyle} />
            </Source>
          );
        })}
      {emergencyFeatures && emergencyFeatures.length > 0 && iconsLoaded && (
        <Source id="emergencies" type="geojson" data={{ type: 'FeatureCollection', features: emergencyFeatures }}>
        </Source>
      )}

      {emergencyFeatures && emergencyFeatures.length > 0 && (
        emergencyFeatures.map((f) => {
          const [lon, lat] = f.geometry.coordinates;
          const typeKey = f.properties.typeKey || '';

          const chooseAsset = (tk) => {
            const n = (tk || '').toLowerCase().normalize('NFD').replace(/\p{Diacritic}/gu, '');
            if (n.includes('incend') || n.includes('fire')) return incendioSvg;
            // inundación -> inundacionSvg
            if (n.includes('inund') || n.includes('flood') || n.includes('water') || n.includes('inundacion')) return inundacionSvg;
            // temporal / meteorológico -> tormentaSvg
            if (n.includes('torment') || n.includes('temporal') || n.includes('storm') || n.includes('meteor') || n.includes('evento')) return tormentaSvg;
            if (n.includes('derrum') || n.includes('desprend') || n.includes('land') || n.includes('mont')) return montanaSvg;
            if (n.includes('accident') || n.includes('vial') || n.includes('car')) return cocheSvg;
            if (n.includes('sanit') || n.includes('salud') || n.includes('medical')) return medicaSvg;
            if (n.includes('quim') || n.includes('chemical')) return quimicalSvg;
            if (n.includes('industrial') || n.includes('factory') || n.includes('industr')) return industrialSvg;
            return otrosSvg;
          };

          const asset = chooseAsset(typeKey);
          return (
            <Marker key={`em-${f.properties.id}`} longitude={lon} latitude={lat} anchor="center">
              <div style={{ width: 28, height: 28, display: 'flex', alignItems: 'center', justifyContent: 'center', pointerEvents: 'auto' }}>
                <img src={asset} alt={t("emergency-marker")} style={{ width: 24, height: 24 }} />
              </div>
            </Marker>
          );
        })
      )}
      {props.showDebug && emergencyFeatures && emergencyFeatures.length > 0 && (
        <Source id="emergencies-debug" type="geojson" data={{ type: 'FeatureCollection', features: emergencyFeatures }}>
          <Layer id="emergency-debug-circles" type="circle" paint={{ 'circle-radius': 8, 'circle-color': theme.palette.status.busy.main }} />
        </Source>
      )}
    </Map>
  );
}

LandingMap.propTypes = {
  quadrants: PropTypes.array.isRequired,
  showDebug: PropTypes.bool,
};
