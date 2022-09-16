package com.github.views.map;

import com.github.views.MainLayout;
import com.vaadin.flow.component.map.Map;
import com.vaadin.flow.component.map.configuration.Coordinate;
import com.vaadin.flow.component.map.configuration.View;
import com.vaadin.flow.component.map.configuration.layer.TileLayer;
import com.vaadin.flow.component.map.configuration.source.XYZSource;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;

@PageTitle("Map")
@Route(value = "map", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
public class MapView extends VerticalLayout {

    private final Map map = new Map();

    public MapView() {
        setSizeFull();
        setPadding(false);

        setupXyzSource();

        map.getElement().setAttribute("theme", "borderless");
        View view = map.getView();
        view.setCenter(new Coordinate(10.0, 55.0));
        view.setZoom(4);
        addAndExpand(map);
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
}
