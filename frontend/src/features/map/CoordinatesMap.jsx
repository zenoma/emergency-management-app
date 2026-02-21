import { useState } from "react";

import Map, { Layer, NavigationControl, Source } from "react-map-gl/maplibre";
import { Marker } from '@vis.gl/react-maplibre';
import 'maplibre-gl/dist/maplibre-gl.css';
import Icon from "../../app/assets/images/pin.png";
import { transformCoordinates } from "../../app/utils/coordinatesTransformations";


export default function CoordinatesMap({ childToParent }) {
  const [cursor] = useState("auto");

  const MAP_STYLE = "https://api.maptiler.com/maps/topo-v2/style.json?key=3GSLdy5VE4yLq4OhlyYJ"

  // Viewport settings
  const INITIAL_VIEW_STATE = {
    longitude: -7.787,
    latitude: 43.0,
    zoom: 6,
    pitch: 0,
    bearing: 0,
  };

  const [viewport] = useState({
    width: "100%",
    height: "100%",
  });

  const [mouseCoords, setMouseCoords] = useState({
    lng: 0,
    lat: 0,
  });

  const bounds = [
    [-10.353521, 40.958984], // northeastern corner of the bounds
    [-4.615985, 44.50585], // southwestern corner of the bounds
  ];

  const handleClick = (event) => {
    setMouseCoords(event.lngLat);

    childToParent([
      transformCoordinates(event.lngLat.lng, event.lngLat.lat).longitude,
      transformCoordinates(event.lngLat.lng, event.lngLat.lat).latitude,
    ]);
  };

  return (
    <Map
      {...viewport}
      minZoom={6}
      maxZoom={15}
      initialViewState={INITIAL_VIEW_STATE}
      mapStyle={MAP_STYLE}
      onClick={(e) => handleClick(e)}
      cursor={cursor}
      maxBounds={bounds}
    >
      <div style={{ position: "absolute", zIndex: 1 }}>
        <NavigationControl />
      </div>
      <Marker
        latitude={mouseCoords.lat}
        longitude={mouseCoords.lng}
        anchor="bottom"
      >
        <img src={Icon} alt="map icon" width="20" />
      </Marker>
    </Map>
  );
}
