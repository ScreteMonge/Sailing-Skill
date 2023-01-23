package com.sailingskill.overlays;

import com.google.inject.Inject;
import com.sailingskill.SailingPlugin;
import com.sailingskill.combat.HitSplat;
import com.sailingskill.npcs.NPCCharacter;
import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.api.RuneLiteObject;
import net.runelite.api.coords.LocalPoint;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.overlay.*;
import net.runelite.client.util.ImageUtil;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class HitsplatOverlay extends Overlay
{
    private final Client client;
    private final SailingPlugin plugin;
    BufferedImage hitsplatImage = ImageUtil.loadImageResource(getClass(), "/Damage_hitsplat.png");
    BufferedImage healthBarRed = ImageUtil.loadImageResource(getClass(), "/Healthbar Red.png");
    BufferedImage healthBarGreen = ImageUtil.loadImageResource(getClass(), "/Healthbar Green.png");

    @Inject
    private HitsplatOverlay(Client client, SailingPlugin plugin)
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
        for (NPCCharacter npcDummy : plugin.getNpcCharacters())
        {
            RuneLiteObject runeLiteObject = npcDummy.getRuneLiteObject();

            int barTimer = npcDummy.getHealthBarTimer();
            if (barTimer > 0)
            {
                npcDummy.setHealthBarTimer(barTimer - 1);
                double healthRatio = (double) npcDummy.getCurrentHealth() / npcDummy.getMaxHealth();
                drawHealthBar(graphics, runeLiteObject, healthRatio);
            }

            int hitTimer = npcDummy.getHitTimer();
            if (hitTimer > 0)
            {
                npcDummy.setHitTimer(hitTimer - 1);

                drawHitsplat(graphics, runeLiteObject, npcDummy.getLastHit(), 1);
            }
        }

        RuneLiteObject shipObject = plugin.getShipObject();

        int healthBarTimer = plugin.getHealthBarTimer();
        if (healthBarTimer > 0)
        {
            plugin.setHealthBarTimer(healthBarTimer - 1);
            double healthRatio = (double) plugin.getCurrentHealth() / plugin.getMaxHealth();
            drawHealthBar(graphics, shipObject, healthRatio);
        }

        ArrayList<HitSplat> hitSplats = plugin.getHitSplats();
        for (int i = hitSplats.size() - 1; i >= 0; i--)
        {
            HitSplat hitSplat = hitSplats.get(i);
            int hitTimer = hitSplat.getHitTimer();
            hitSplat.setHitTimer(hitTimer - 1);
            drawHitsplat(graphics, plugin.getShipObject(), hitSplat.getHitValue(), hitSplat.getSplatPosition());
        }

        return null;
    }

    public void drawHitsplat(Graphics2D graphics, RuneLiteObject runeLiteObject, int lastHit, int position)
    {
        LocalPoint localPoint = runeLiteObject.getLocation();
        Point centralPoint = Perspective.getCanvasImageLocation(client, localPoint, hitsplatImage, 90);
        int splatWidth = hitsplatImage.getWidth();

        int positionModX = 0;
        int positionModY = 0;
        switch (position)
        {
            default:
            case 1:
                break;
            case 2:
                positionModY = 20;
                break;
            case 3:
                positionModX = -15;
                positionModY = 10;
                break;
            case 4:
                positionModX = 15;
                positionModY = 10;
        }

        Point healthPoint = new Point(centralPoint.getX() + positionModX, centralPoint.getY() + positionModY);
        OverlayUtil.renderImageLocation(graphics, healthPoint, hitsplatImage);

        FontMetrics fontMetrics = graphics.getFontMetrics(FontManager.getRunescapeSmallFont());
        String text = String.valueOf(lastHit);
        int fontWidth = fontMetrics.stringWidth(text);
        Point textPoint = new Point((centralPoint.getX() + positionModX + (splatWidth / 2) - ((fontWidth + 1) / 2)), centralPoint.getY() + positionModY + 17);
        OverlayUtil.renderTextLocation(graphics, textPoint, text, Color.WHITE);
    }

    public void drawHealthBar(Graphics2D graphics, RuneLiteObject runeLiteObject, double healthRatio)
    {
        LocalPoint localPoint = runeLiteObject.getLocation();
        Point centralPoint = Perspective.getCanvasImageLocation(client, localPoint, hitsplatImage, 175);

        int splatWidth = hitsplatImage.getWidth();
        int barWidth = healthBarRed.getWidth();
        Point endPoint = new Point(centralPoint.getX() - barWidth / 2 + splatWidth / 2, centralPoint.getY());
        OverlayUtil.renderImageLocation(graphics, endPoint, healthBarRed);

        int greenBarWidth = (int) (30 * healthRatio);
        if (greenBarWidth > 0)
        {
            BufferedImage greenBar = healthBarGreen.getSubimage(0, 0, greenBarWidth, 5);
            OverlayUtil.renderImageLocation(graphics, endPoint, greenBar);
        }
    }
}
