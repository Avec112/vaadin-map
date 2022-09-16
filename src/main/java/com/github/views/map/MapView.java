package com.github.views.map;

import com.github.views.MainLayout;
import com.vaadin.flow.component.map.Map;
import com.vaadin.flow.component.map.configuration.Coordinate;
import com.vaadin.flow.component.map.configuration.Extent;
import com.vaadin.flow.component.map.configuration.Feature;
import com.vaadin.flow.component.map.configuration.View;
import com.vaadin.flow.component.map.configuration.feature.MarkerFeature;
import com.vaadin.flow.component.map.configuration.layer.TileLayer;
import com.vaadin.flow.component.map.configuration.source.XYZSource;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;

import java.util.HashMap;
import java.util.List;

@PageTitle("Map")
@Route(value = "map", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
public class MapView extends VerticalLayout {

    private final Map map = new Map();

    private static final City BERLIN = new City("Berlin", new Coordinate(13.404954, 52.520008));
    private static final City HONG_KONG = new City("Hong Kong", new Coordinate(114.162813, 22.279328));
    private static final City MOSCOW = new City("Moscow", new Coordinate(37.617298, 55.755825));
    private static final City NEW_YORK = new City("New York", new Coordinate(-74.005974, 40.712776));
    private static final City RIO = new City("Rio de Janeiro", new Coordinate(-43.2093727, -22.9110137));
    private static final List<City> CITIES = List.of(BERLIN, HONG_KONG, MOSCOW, NEW_YORK, RIO);


    public MapView() {
        setSizeFull();
        setSpacing(false);
        setPadding(false);

        setupXyzSource();

        map.getElement().setAttribute("theme", "borderless");
        View view = map.getView();
        view.setCenter(new Coordinate(10.0, 55.0));
        view.setZoom(4);
        addAndExpand(map);

        // Setup text areas for logging event data
        TextArea viewEventInfo = new TextArea("View Move End Event");
        viewEventInfo.addClassName("monospace");
        viewEventInfo.setReadOnly(true);
        viewEventInfo.setWidthFull();
        viewEventInfo.addThemeName("monospace");
        TextArea mapClickInfo = new TextArea("Map Click Event");
        mapClickInfo.addClassName("monospace");
        mapClickInfo.setReadOnly(true);
        mapClickInfo.setWidthFull();
        mapClickInfo.addThemeName("monospace");
        TextArea featureClickInfo = new TextArea("Feature Click Event");
        featureClickInfo.addClassName("monospace");
        featureClickInfo.setReadOnly(true);
        featureClickInfo.setWidthFull();
        featureClickInfo.addThemeName("monospace");
        VerticalLayout eventsLayout = new VerticalLayout();
        eventsLayout.setSpacing(false);
        eventsLayout.add(viewEventInfo, mapClickInfo, featureClickInfo);
        add(eventsLayout);

        // Add markers for cities
        HashMap<Feature, City> cityLookup = new HashMap<>();
        CITIES.forEach(city -> {
            MarkerFeature cityMarker = new MarkerFeature(city.coordinates);
            map.getFeatureLayer().addFeature(cityMarker);
            // Store relation between cities and markers in a hash map
            cityLookup.put(cityMarker, city);
        });

        map.addViewMoveEndEventListener(e -> {
            Coordinate center = e.getCenter();
            Extent extent = e.getExtent();
            String info = "";
            info += String.format("Center = { x: %s, y: %s }%n", center.getX(), center.getY());
            info += String.format("Zoom   = %s%n", e.getZoom());
            info += String.format("Extent = { left: %s, top: %s,%n", extent.getMinX(), extent.getMinY());
            info += String.format("           right: %s, bottom: %s }", extent.getMaxX(), extent.getMaxY());
            viewEventInfo.setValue(info);
        });

        map.addClickEventListener(e -> {
            Coordinate coordinates = e.getCoordinate();
            String info = String.format("Coordinates = { x: %s, y: %s }", coordinates.getX(), coordinates.getY());
            mapClickInfo.setValue(info);
        });

        map.addFeatureClickListener(e -> {
            MarkerFeature feature = (MarkerFeature) e.getFeature();
            Coordinate coordinates = feature.getCoordinates();
            // Get city entity for event marker,
            // see remaining example on how the markers are set up
            City city = cityLookup.get(feature);
            String info = "";
            info += String.format("City        = %s%n", city.getName());
            info += String.format("Coordinates = { x: %s, y: %s }", coordinates.getX(), coordinates.getY());
            featureClickInfo.setValue(info);
        });
    }

    private void setupXyzSource() {
        XYZSource.Options sourceOptions = new XYZSource.Options();
        sourceOptions.setUrl("https://{a-c}.tile.openstreetmap.org/{z}/{x}/{y}.png");
        sourceOptions.setAttributionsCollapsible(false);
        XYZSource source = new XYZSource(sourceOptions);
        TileLayer tileLayer = new TileLayer();
        tileLayer.setSource(source);
        map.setBackgroundLayer(tileLayer);
    }

    private static class City {
        private final String name;
        private final Coordinate coordinates;

        public String getName() {
            return name;
        }

        public Coordinate getCoordinates() {
            return coordinates;
        }

        public City(String name, Coordinate coordinates) {
            this.name = name;
            this.coordinates = coordinates;
        }
    }
}
