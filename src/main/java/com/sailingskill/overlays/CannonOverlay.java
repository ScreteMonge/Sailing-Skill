package com.sailingskill.overlays;

import com.google.inject.Inject;
import com.sailingskill.SailingConfig;
import com.sailingskill.SailingPlugin;
import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.Tile;
import net.runelite.api.coords.LocalPoint;
import net.runelite.client.ui.overlay.*;

import java.awt.*;

public class CannonOverlay extends Overlay
{
    private final Client client;
    private final SailingConfig config;
    private final SailingPlugin plugin;


    @Inject
    private CannonOverlay(Client client, SailingConfig config, SailingPlugin plugin) {
        this.client = client;
        this.config = config;
        this.plugin = plugin;
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_SCENE);
        setPriority(OverlayPriority.NONE);
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        if (plugin.getShipObject() == null)
        {
            return null;
        }

        if (plugin.isShowCannonRange())
        {
            if (plugin.getCannonTiles() != null)
            {
                for (Tile tile : plugin.getCannonTiles())
                {
                    LocalPoint localPoint = tile.getLocalLocation();
                    renderTile(graphics, localPoint, Color.RED, 0, new Color(255, 0, 0, 20));
                }
            }
        }
        return null;
    }

    private void renderTile(final Graphics2D graphics, final LocalPoint dest, final Color color, final double borderWidth, final Color fillColor) {
        if (dest == null) {
            return;
        }

        final Polygon poly = Perspective.getCanvasTilePoly(client, dest);

        if (poly == null) {
            return;
        }

        OverlayUtil.renderPolygon(graphics, poly, fillColor, fillColor, new BasicStroke((float) borderWidth));
    }
}