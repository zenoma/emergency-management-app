import PropTypes from "prop-types";
import React, { useState } from "react";

import { useSelector } from "react-redux"
import Map, { Layer, NavigationControl, Source } from "react-map-gl/maplibre";
import 'maplibre-gl/dist/maplibre-gl.css';
import { useLocation, useNavigate } from "react-router-dom";
import { untransformCoordinates } from "../../app/utils/coordinatesTransformations";
import { selectToken } from "../user/login/LoginSlice";

export default function LandingMap(props) {

  const token = useSelector(selectToken);
  const navigate = useNavigate();

  const quadrants = props.quadrants;
  const [interactiveLayerIds, setInteractiveLayerIds] = useState([]);

  const MAP_STYLE = "https://api.maptiler.com/maps/topo-v2/style.json?key=3GSLdy5VE4yLq4OhlyYJ"

  const [cursor] = useState("auto");

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
  const isFireDetails = location.pathname === "/fire-details";

  const handleClick = (event) => {
    const feature = event.features && event.features[0];
    if (isFireDetails && feature && token) {
      navigate("/quadrant", {
        state: {
          quadrantId: feature.layer.id,
          quadrantName: feature.layer.name,
        },
      });
    }
  };

  return (
    <Map
      {...viewport}
      onViewportChange={setViewport}
      minZoom={6}
      maxZoom={15}
      initialViewState={INITIAL_VIEW_STATE}
      mapStyle={MAP_STYLE}
      onClick={(e) => handleClick(e)}
      cursor={cursor}
      maxBounds={bounds}
      interactiveLayerIds={interactiveLayerIds}
    >
      <div style={{ position: "absolute", zIndex: 1 }}>
        <NavigationControl />
      </div>

      {quadrants &&
        quadrants.map((item) => {
          if (!interactiveLayerIds.includes(item.id.toString())) {
            setInteractiveLayerIds([
              ...interactiveLayerIds,
              item.id.toString(),
            ]);
          }
          const coord = item.coordinates.map((item) => {
            return [
              untransformCoordinates(item.x, item.y).longitude,
              untransformCoordinates(item.x, item.y).latitude,
            ];
          });

          const teamSize = item.teamDtoList ? item.teamDtoList.length : -1;
          const vehicleSize = item.vehicleDtoList
            ? item.vehicleDtoList.length
            : -1;

          const quadrantLabelStyle = {
            id: item.id.toString() + "-label",
            minzoom: 10,
            type: "symbol",
            source: "label",
            layout: {
              "text-field": "{place-name} #{place-id} ",
              "text-size": 12,
              "text-anchor": "center",
              "text-offset": [0, -2],
            },
            paint: {
              "text-color": "black",
              "text-halo-color": "#fff",
              "text-halo-width": 1,
            },
          };

          const vehiclesLabelStyle = {
            id: item.id.toString() + "-vehicle-label",
            minzoom: 10,
            type: "symbol",
            source: "label",
            layout: {
              "text-field": "{vehicle-size}",
              "text-size": 15,
              "text-anchor": "center",
              "text-offset": [-1.5, 1.8],
              "icon-image": "fire-truck",
              "icon-size": 0.4,
              "icon-anchor": "center",
              "icon-offset": [0, 68],
            },
            paint: {
              "text-color": "white",
              "text-halo-color": "green",
              "text-halo-width": 1,
            },
          };

          const teamLabelStyle = {
            id: item.id.toString() + "-team-label",
            minzoom: 10,
            type: "symbol",
            source: "label",
            layout: {
              "text-field": "{team-size}",
              "text-size": 15,
              "text-anchor": "center",
              "text-offset": [-1.3, 0],
              "icon-image": "team",
              "icon-size": 0.2,
              "icon-anchor": "center",
              "icon-offset": [0, 0],
            },
            paint: {
              "text-color": "white",
              "text-halo-color": "green",
              "text-halo-width": 1,
            },
          };

          const quadrantLayerStyle = {
            id: item.id.toString(),
            type: "fill",
            layout: {},
            paint: {
              "fill-color": "red",
              "fill-opacity": 0.4,
            },
          };

          const quadrantBorderStyle = {
            id: item.id.toString() + "-border",
            type: "line",
            layout: {},
            paint: {
              "line-color": "#000",
              "line-width": 1,
            },
          };

          const geoJson = {
            type: "FeatureCollection",
            features: [
              {
                type: "Feature",
                properties: Object.assign({}, item, {
                  "place-name": item.nombre,
                  "place-id": item.id,
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
            <Source key={item.id.toString()} type="geojson" data={geoJson}>
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
    </Map>
  );
}

LandingMap.propTypes = {
  quadrants: PropTypes.array.isRequired,
};
