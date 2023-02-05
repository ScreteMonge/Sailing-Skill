package com.sailingskill.overlays;

import com.sailingskill.SailingConfig;
import com.sailingskill.SailingPlugin;
import com.sailingskill.BoatMaths;
import net.runelite.api.Client;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.ImageComponent;
import net.runelite.client.util.ImageUtil;

import javax.inject.Inject;
import java.awt.*;
import java.awt.image.BufferedImage;

public class WindOverlay extends Overlay
{
    private final Client client;
    private final SailingPlugin plugin;
    private final SailingConfig config;

    private int animationRotation = 0;

    BufferedImage windArrowBlank = ImageUtil.loadImageResource(getClass(), "/Windy_Blank.png");
    BufferedImage windArrow0 = ImageUtil.loadImageResource(getClass(), "/Windy_0.png");
    BufferedImage windArrow1 = ImageUtil.loadImageResource(getClass(), "/Windy_1.png");
    BufferedImage windArrow2 = ImageUtil.loadImageResource(getClass(), "/Windy_2.png");
    BufferedImage windArrow3 = ImageUtil.loadImageResource(getClass(), "/Windy_3.png");
    BufferedImage windArrow4 = ImageUtil.loadImageResource(getClass(), "/Windy_4.png");
    BufferedImage windArrow5 = ImageUtil.loadImageResource(getClass(), "/Windy_5.png");
    BufferedImage boatIndicator = ImageUtil.loadImageResource(getClass(), "/Wind_Boat_Indicator.png");

    @Inject
    private WindOverlay(Client client, SailingPlugin plugin, SailingConfig config)
    {
        super(plugin);
        setPosition(OverlayPosition.ABOVE_CHATBOX_RIGHT);
        setLayer(OverlayLayer.ABOVE_WIDGETS);
        this.config = config;
        this.client = client;
        this.plugin = plugin;
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        graphics.scale(config.overlayScale(), config.overlayScale());

        BufferedImage windImage = getWindImage();
        ImageComponent windImageComponent = new ImageComponent(windImage);
        ImageComponent windBlankImageComponent = new ImageComponent(windArrowBlank);
        double windCenterX = (double) windImage.getWidth() / 2;
        double windCenterY = (double) windImage.getHeight() / 2;

        int globalRotation = client.getCameraYaw() - 2048;
        double jUnitToRad = Math.PI / 1024;
        graphics.rotate(jUnitToRad * globalRotation, windCenterX, windCenterY);

        int sailOptionRotation = BoatMaths.translateOrientation(plugin.getAbsoluteBoatOrientation());

        graphics.rotate(jUnitToRad * sailOptionRotation, windCenterX, windCenterY);
        graphics.drawImage(boatIndicator, 0, 0, null);
        graphics.rotate(jUnitToRad * sailOptionRotation * -1, windCenterX, windCenterY);

        int windRotation = plugin.getWindDirection();
        graphics.rotate(windRotation * jUnitToRad, windCenterX, windCenterY);

        int windChangeCountdown = config.windChangeRate() - plugin.getWindChangeTimer();
        if (windChangeCountdown < 16)
        {
            if (windChangeCountdown % 3 == 0)
            {
                return windBlankImageComponent.render(graphics);
            }
        }

        return windImageComponent.render(graphics);
    }

    private BufferedImage getWindImage()
    {
        animationRotation++;

        if (animationRotation < 40)
        {
            return windArrow0;
        }

        if (animationRotation < 50)
        {
            return windArrow1;
        }

        if (animationRotation < 60)
        {
            return  windArrow2;
        }

        if (animationRotation < 70)
        {
            return  windArrow3;
        }

        if (animationRotation < 80)
        {
            return  windArrow4;
        }

        if (animationRotation < 100)
        {
            return  windArrow5;
        }

        animationRotation = 0;
        return windArrow0;
    }
}
