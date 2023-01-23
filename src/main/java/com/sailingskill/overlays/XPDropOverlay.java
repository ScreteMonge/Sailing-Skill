package com.sailingskill.overlays;

import com.google.inject.Inject;
import com.sailingskill.SailingPlugin;
import net.runelite.api.Client;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.ui.overlay.*;
import net.runelite.client.util.ImageUtil;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class XPDropOverlay extends Overlay
{
    private final Client client;
    private final SailingPlugin plugin;
    BufferedImage skillImage = ImageUtil.loadImageResource(getClass(), "/Sailing_skill_icon.png");
    BufferedImage rangedSkillImage = ImageUtil.loadImageResource(getClass(), "/ranged.png");
    BufferedImage fishingSkillImage = ImageUtil.loadImageResource(getClass(), "/Fishing_icon.png");

    @Inject
    private XPDropOverlay(Client client, SailingPlugin plugin)
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
        Widget xpDisplay = client.getWidget(WidgetInfo.EXPERIENCE_TRACKER_WIDGET);

        ArrayList<XPDrop> xpSailingDrops = plugin.getXpSailingDrops();
        ArrayList<XPDrop> xpCannonDrops = plugin.getXpCannonDrops();
        ArrayList<XPDrop> xpFishDrops = plugin.getXpFishDrops();

        if (xpDisplay == null)
        {
            xpSailingDrops.clear();
            xpCannonDrops.clear();
            return null;
        }

        int x = xpDisplay.getRelativeX();
        int y = xpDisplay.getRelativeY();

        for (int i = 0; i < xpSailingDrops.size(); i++)
        {
            XPDrop xpDrop = xpSailingDrops.get(i);
            int timer = xpDrop.getTimer();
            xpDrop.setTimer(timer - 1);
            int yPos = xpDrop.getYPosition();
            xpDrop.setYPosition(yPos - 1);

            if (timer == 0)
            {
                xpSailingDrops.remove(xpDrop);
                continue;
            }

            graphics.drawImage(skillImage, x + 85, y + yPos, null);
        }

        for (int i = 0; i < xpCannonDrops.size(); i++)
        {
            XPDrop xpDrop = xpCannonDrops.get(i);
            int timer = xpDrop.getTimer();
            xpDrop.setTimer(timer - 1);
            int yPos = xpDrop.getYPosition();
            xpDrop.setYPosition(yPos - 1);

            if (timer == 0)
            {
                xpCannonDrops.remove(xpDrop);
                continue;
            }

            graphics.drawImage(skillImage, x + 85, y + yPos, null);
            graphics.drawImage(rangedSkillImage, x + 85, y + yPos + 30, null);
        }

        for (int i = 0; i < xpFishDrops.size(); i++)
        {
            XPDrop xpDrop = xpFishDrops.get(i);
            int timer = xpDrop.getTimer();
            xpDrop.setTimer(timer - 1);
            int yPos = xpDrop.getYPosition();
            xpDrop.setYPosition(yPos - 1);

            if (timer == 0)
            {
                xpFishDrops.remove(xpDrop);
                continue;
            }

            graphics.drawImage(skillImage, x + 85, y + yPos, null);
            graphics.drawImage(fishingSkillImage, x + 85, y + yPos + 30, null);
        }

        return null;
    }
}
