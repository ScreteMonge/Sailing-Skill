package com.sailingskill.overlays;

import com.google.inject.Inject;
import com.sailingskill.SailingPlugin;
import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.client.ui.overlay.*;
import net.runelite.client.util.ImageUtil;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class FishOverlay extends Overlay
{
    private final Client client;
    private final SailingPlugin plugin;
    BufferedImage grouperImage = ImageUtil.loadImageResource(getClass(), "/Fish_Grouper.png");
    BufferedImage marlinImage = ImageUtil.loadImageResource(getClass(), "/Fish_Marlin.png");
    BufferedImage sturgeonImage = ImageUtil.loadImageResource(getClass(), "/Fish_Sturgeon.png");
    BufferedImage rodImage = ImageUtil.loadImageResource(getClass(), "/Fishing_rod.png");

    @Inject
    private FishOverlay(Client client, SailingPlugin plugin)
    {
        this.client = client;
        this.plugin = plugin;
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_SCENE);
        setPriority(OverlayPriority.LOW);
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        if (plugin.getShipObject() == null)
        {
            return null;
        }

        if (plugin.isFishing())
        {
            LocalPoint localPoint = plugin.getShipObject().getLocation();

            if (localPoint == null)
            {
                return null;
            }

            Point point = Perspective.getCanvasImageLocation(client, localPoint, marlinImage, 0);

            OverlayUtil.renderImageLocation(graphics, point, rodImage);

            ArrayList<FishDrop> fishList = plugin.getFishDrops();
            for (int i = 0; i < fishList.size(); i++)
            {
                FishDrop fishDrop = fishList.get(i);
                int timer = fishDrop.getTimer();
                int nextTimer = timer - 1;

                if (nextTimer == 0)
                {
                    fishList.remove(fishDrop);
                    continue;
                }

                fishDrop.setTimer(nextTimer);

                BufferedImage image;
                switch (fishDrop.getId())
                {
                    default:
                    case 1:
                        image = marlinImage;
                        break;
                    case 2:
                        image = grouperImage;
                        break;
                    case 3:
                        image = sturgeonImage;
                }

                OverlayUtil.renderImageLocation(graphics, point, image);
            }
        }

        return null;
    }
}
