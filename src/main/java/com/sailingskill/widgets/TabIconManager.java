package com.sailingskill.widgets;

import net.runelite.api.Client;
import net.runelite.api.SpriteID;
import net.runelite.api.SpritePixels;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.util.ImageUtil;

import javax.inject.Inject;
import java.awt.image.BufferedImage;

public class TabIconManager
{
    @Inject
    private Client client;


    private final BufferedImage anchorTabImage = ImageUtil.loadImageResource(getClass(), "/Tab_Anchor_Icon.png");
    private final int ANCHOR_SPRITE_ID = SpriteID.DEADMAN_BANK_KEYS_3;

    public void setFixedSailingTab()
    {
        Widget fixedCombatWidget = client.getWidget(WidgetInfo.FIXED_VIEWPORT_COMBAT_ICON);
        if (fixedCombatWidget != null)
        {
            fixedCombatWidget.setSpriteId(ANCHOR_SPRITE_ID);
            fixedCombatWidget.setPos(14, 6);
            fixedCombatWidget.setSize(24, 24);
            fixedCombatWidget.setOriginalHeight(24);
            fixedCombatWidget.setOriginalWidth(24);
            fixedCombatWidget.revalidate();
        }
    }

    public void setResizableSailingTab()
    {
        Widget resizableCombatWidget = client.getWidget(WidgetInfo.RESIZABLE_VIEWPORT_COMBAT_ICON);
        if (resizableCombatWidget != null)
        {
            resizableCombatWidget.setSpriteId(ANCHOR_SPRITE_ID);
            resizableCombatWidget.setPos(8, 6);
            resizableCombatWidget.setSize(24, 24);
            resizableCombatWidget.setOriginalHeight(24);
            resizableCombatWidget.setOriginalWidth(24);
            resizableCombatWidget.revalidate();
        }
    }

    public void setModernSailingTab()
    {
        Widget modernCombatWidget = client.getWidget(WidgetInfo.RESIZABLE_VIEWPORT_BOTTOM_LINE_COMBAT_ICON);
        if (modernCombatWidget != null)
        {
            modernCombatWidget.setSpriteId(ANCHOR_SPRITE_ID);
            modernCombatWidget.setPos(5, 6);
            modernCombatWidget.setSize(24, 24);
            modernCombatWidget.setOriginalHeight(24);
            modernCombatWidget.setOriginalWidth(24);
            modernCombatWidget.revalidate();
        }
    }

    public void unsetFixedSailingTab()
    {
        Widget fixedCombatWidget = client.getWidget(WidgetInfo.FIXED_VIEWPORT_COMBAT_ICON);
        if (fixedCombatWidget != null)
        {
            fixedCombatWidget.setSpriteId(SpriteID.TAB_COMBAT);
            fixedCombatWidget.setPos(10, 0);
            fixedCombatWidget.setSize(33, 36);
            fixedCombatWidget.setOriginalHeight(36);
            fixedCombatWidget.setOriginalWidth(33);
            fixedCombatWidget.revalidate();
        }
    }

    public void unsetResizableSailingTab()
    {
        Widget resizableCombatWidget = client.getWidget(WidgetInfo.RESIZABLE_VIEWPORT_COMBAT_ICON);
        if (resizableCombatWidget != null)
        {
            resizableCombatWidget.setSpriteId(SpriteID.TAB_COMBAT);
            resizableCombatWidget.setPos(4, 0);
            resizableCombatWidget.setSize(33, 36);
            resizableCombatWidget.setOriginalHeight(36);
            resizableCombatWidget.setOriginalWidth(33);
            resizableCombatWidget.revalidate();
        }
    }

    public void unsetModernSailingTab()
    {
        Widget modernCombatWidget = client.getWidget(WidgetInfo.RESIZABLE_VIEWPORT_BOTTOM_LINE_COMBAT_ICON);
        if (modernCombatWidget != null)
        {
            modernCombatWidget.setSpriteId(SpriteID.TAB_COMBAT);
            modernCombatWidget.setPos(0, 0);
            modernCombatWidget.setSize(33, 36);
            modernCombatWidget.setOriginalHeight(36);
            modernCombatWidget.setOriginalWidth(33);
            modernCombatWidget.revalidate();
        }
    }

    public void setAnchorSpriteOverride()
    {
        SpritePixels anchorTabSprite = ImageUtil.getImageSpritePixels(anchorTabImage, client);
        client.getSpriteOverrides().put(ANCHOR_SPRITE_ID, anchorTabSprite);
    }
}
