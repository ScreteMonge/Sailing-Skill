package com.sailingskill.widgets;

import com.sailingskill.SailingPlugin;
import net.runelite.api.Client;
import net.runelite.api.ScriptEvent;
import net.runelite.api.SpriteID;
import net.runelite.api.SpritePixels;
import net.runelite.api.widgets.JavaScriptCallback;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetType;
import net.runelite.client.util.ImageUtil;

import javax.inject.Inject;
import java.awt.image.BufferedImage;

public class SailWidgetManager
{
    @Inject
    private Client client;

    @Inject
    private SailingPlugin plugin;

    private final BufferedImage sailLeftImage = ImageUtil.loadImageResource(getClass(), "/Sail_Button_Left.png");
    private final BufferedImage sailRightImage = ImageUtil.loadImageResource(getClass(), "/Sail_Button_Right.png");
    private final BufferedImage poleImage = ImageUtil.loadImageResource(getClass(), "/Sail_Button_Pole.png");

    private Widget parent;
    private Widget leftSailWidget;
    private Widget rightSailWidget;
    private Widget poleWidget;

    private final int SAIL_LEFT_SPRITE_ID = SpriteID.DEADMAN_EXCLAMATION_MARK_SKULLED_WARNING;
    private final int SAIL_RIGHT_SPRITE_ID = SpriteID.DEADMAN_TAB_ITEMS_LOST_ON_DEATH_WHILE_SKULLED_IN_SAFE_ZONE;
    private final int POLE_SPRITE_ID = SpriteID.DEADMAN_BANK_KEYS_1;

    private final int HOVERED_OPACITY = 125;
    private final int BASE_Y = -95;

    public void setParent(Widget parentWidget)
    {
        parent = parentWidget;
    }

    public void createWidgets()
    {
        leftSailWidget = parent.createChild(10, WidgetType.GRAPHIC);
        leftSailWidget.setPos(-33, BASE_Y, 1, 1);
        leftSailWidget.setSize(66, 45);
        leftSailWidget.setOriginalHeight(45);
        leftSailWidget.setOriginalWidth(66);
        leftSailWidget.revalidate();
        leftSailWidget.setSpriteId(SAIL_LEFT_SPRITE_ID);
        leftSailWidget.setHasListener(true);
        leftSailWidget.setOnMouseOverListener((JavaScriptCallback) this::leftHover);
        leftSailWidget.setOnMouseLeaveListener((JavaScriptCallback) this::leftLeave);
        leftSailWidget.setOnOpListener((JavaScriptCallback) this::leftOnOpEvent);
        leftSailWidget.setAction(0, "Rotate left");

        rightSailWidget = parent.createChild(11, WidgetType.GRAPHIC);
        rightSailWidget.setPos(33, BASE_Y, 1, 1);
        rightSailWidget.setSize(66, 45);
        rightSailWidget.setOriginalHeight(45);
        rightSailWidget.setOriginalWidth(66);
        rightSailWidget.setSpriteId(SAIL_RIGHT_SPRITE_ID);
        rightSailWidget.revalidate();
        rightSailWidget.setHasListener(true);
        rightSailWidget.setOnMouseOverListener((JavaScriptCallback) this::rightHover);
        rightSailWidget.setOnMouseLeaveListener((JavaScriptCallback) this::rightLeave);
        rightSailWidget.setOnOpListener((JavaScriptCallback) this::rightOnOpEvent);
        rightSailWidget.setAction(0, "Rotate right");

        poleWidget = parent.createChild(12, WidgetType.GRAPHIC);
        poleWidget.setPos(0, BASE_Y + 14, 1, 1);
        poleWidget.setSize(20, 30);
        poleWidget.setOriginalHeight(30);
        poleWidget.setOriginalWidth(20);
        poleWidget.setSpriteId(POLE_SPRITE_ID);
        poleWidget.revalidate();
        poleWidget.setNoClickThrough(true);

    }

    public void leftHover(ScriptEvent e)
    {
        leftSailWidget.setOpacity(HOVERED_OPACITY);
    }

    public void rightHover(ScriptEvent e)
    {
        rightSailWidget.setOpacity(HOVERED_OPACITY);
    }

    public void leftLeave(ScriptEvent e)
    {
        leftSailWidget.setOpacity(0);
    }

    public void rightLeave(ScriptEvent e)
    {
        rightSailWidget.setOpacity(0);
    }

    public void leftOnOpEvent(ScriptEvent e)
    {
        plugin.rotateSailCCW();
    }

    public void rightOnOpEvent(ScriptEvent e)
    {
        plugin.rotateSailCW();
    }

    public void setSailSprites()
    {
        SpritePixels poleSprite = ImageUtil.getImageSpritePixels(poleImage, client);
        client.getSpriteOverrides().put(POLE_SPRITE_ID, poleSprite);

        SpritePixels leftSprite = ImageUtil.getImageSpritePixels(sailLeftImage, client);
        client.getSpriteOverrides().put(SAIL_LEFT_SPRITE_ID, leftSprite);

        SpritePixels rightSprite = ImageUtil.getImageSpritePixels(sailRightImage, client);
        client.getSpriteOverrides().put(SAIL_RIGHT_SPRITE_ID, rightSprite);
    }

    public void setupManager(Widget parent)
    {
        setParent(parent);
        setSailSprites();
        createWidgets();
    }
}
