package com.sailingskill.overlays;

import com.sailingskill.SailingConfig;
import com.sailingskill.SailingPlugin;
import com.sailingskill.RotationTranslator;
import com.sailingskill.widgets.BoatWidgetManager;
import net.runelite.api.Client;
import net.runelite.api.SpriteID;
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

    BufferedImage sailImage = ImageUtil.loadImageResource(getClass(), "/Sail.png");
    BufferedImage sailImageCW = ImageUtil.loadImageResource(getClass(), "/Sail_CW.png");
    BufferedImage sailImageCCW = ImageUtil.loadImageResource(getClass(), "/Sail_CCW.png");
    BufferedImage sailImageError = ImageUtil.loadImageResource(getClass(), "/Sail_Error.png");

    BufferedImage sailSettingsImage = ImageUtil.loadImageResource(getClass(), "/Sail_Settings.png");
    BufferedImage sailSettingsGhost = ImageUtil.loadImageResource(getClass(), "/Sail_Settings_Ghost.png");
    BufferedImage sailSettingsPoint = ImageUtil.loadImageResource(getClass(), "/Sail_Settings_Point.png");
    BufferedImage sailSettingsBlank = ImageUtil.loadImageResource(getClass(), "/Sail_Settings_Blank.png");

    private final int wheelFullL0SpriteID = SpriteID.MOBILE_YELLOW_TOUCH_ANIMATION_1;
    private final int wheelFullL1SpriteID = SpriteID.MOBILE_TUTORIAL_FUNCTION_MODE_BUTTON;
    private final int wheelFullL2SpriteID = SpriteID.MOBILE_TUTORIAL_MINIMISE_WORLD_MAP;
    private final int wheelFullL3SpriteID = SpriteID.MOBILE_TUTORIAL_GESTURES_TAP_AND_PRESS;
    private final int wheelFullR0SpriteID = SpriteID.MOBILE_YELLOW_TOUCH_ANIMATION_2;
    private final int wheelFullR1SpriteID = SpriteID.MOBILE_TUTORIAL_NPC_GESTURE_PRESS;
    private final int wheelFullR2SpriteID = SpriteID.MOBILE_TUTORIAL_NPC_GESTURE_TAP;
    private final int wheelFullR3SpriteID = SpriteID.MOBILE_TUTORIAL_CAMERA_MOVEMENT;

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


        int sailOptionRotation = RotationTranslator.translateOrientation(plugin.getAbsoluteBoatOrientation());
        int ghostRotation = sailOptionRotation + (plugin.getBoatRotationQueue() * 256);

        graphics.rotate(jUnitToRad * sailOptionRotation, windCenterX, windCenterY);
        graphics.drawImage(sailSettingsImage, 0, 0, null);
        graphics.rotate(jUnitToRad * sailOptionRotation * -1, windCenterX, windCenterY);


        if (BoatWidgetManager.getWheelLeftWidget() != null)
        {
            int idRight = BoatWidgetManager.getWheelRightWidget().getSpriteId();
            int idLeft = BoatWidgetManager.getWheelLeftWidget().getSpriteId();
            int blankRotation = sailOptionRotation;

            if (idRight != wheelFullR0SpriteID || idLeft != wheelFullL0SpriteID)
            {
                switch (idLeft)
                {
                    case wheelFullL1SpriteID:
                        blankRotation -= 256;
                        break;
                    case wheelFullL2SpriteID:
                        blankRotation -= 512;
                        break;
                    case wheelFullL3SpriteID:
                        blankRotation -= 768;
                }

                switch (idRight)
                {
                    case wheelFullR1SpriteID:
                        blankRotation += 256;
                        break;
                    case wheelFullR2SpriteID:
                        blankRotation += 512;
                        break;
                    case wheelFullR3SpriteID:
                        blankRotation += 768;
                }
            }

            graphics.rotate(jUnitToRad * blankRotation, windCenterX, windCenterY);
            graphics.drawImage(sailSettingsBlank, 0, 0, null);
            graphics.rotate(jUnitToRad * blankRotation * -1, windCenterX, windCenterY);
        }

        graphics.rotate(jUnitToRad * ghostRotation, windCenterX, windCenterY);
        graphics.drawImage(sailSettingsGhost, 0, 0, null);
        graphics.rotate(jUnitToRad * ghostRotation * -1, windCenterX, windCenterY);

        graphics.rotate(jUnitToRad * sailOptionRotation, windCenterX, windCenterY);
        graphics.drawImage(sailSettingsPoint, 0, 0, null);
        graphics.rotate(jUnitToRad * sailOptionRotation * -1, windCenterX, windCenterY);

        int sailTimer = plugin.getSailTimer();
        int sailErrorTimer = plugin.getSailErrorTimer();
        BufferedImage sailDirectionImage;

        if (sailErrorTimer > 0)
        {
            plugin.setSailErrorTimer(sailErrorTimer - 1);
            sailDirectionImage = sailImageError;
        }
        else if (sailTimer == 0)
        {
            sailDirectionImage = sailImage;
        }
        else if (sailTimer > 0)
        {
            sailDirectionImage = sailImageCW;
            plugin.setSailTimer(sailTimer - 1);
        }
        else
        {
            sailDirectionImage = sailImageCCW;
            plugin.setSailTimer(sailTimer + 1);
        }

        int sailRotation = RotationTranslator.translateOrientation(plugin.getAbsoluteSailOrientation());
        graphics.rotate(jUnitToRad * sailRotation, windCenterX, windCenterY);
        graphics.drawImage(sailDirectionImage, 0, 0, null);
        graphics.rotate(jUnitToRad * sailRotation * -1, windCenterX, windCenterY);

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
