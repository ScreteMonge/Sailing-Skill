package com.sailingskill.widgets;

import com.sailingskill.SailingPlugin;
import lombok.Getter;
import net.runelite.api.*;
import net.runelite.api.widgets.*;
import net.runelite.client.util.ImageUtil;

import javax.inject.Inject;
import java.awt.image.BufferedImage;

public class BoatWidgetManager
{
    @Inject
    private Client client;

    @Inject
    private SailingPlugin plugin;

    private final BufferedImage wheelLeftUnpressed = ImageUtil.loadImageResource(getClass(), "/Wheel_Left_Unpressed.png");
    private final BufferedImage wheelLeftPressed = ImageUtil.loadImageResource(getClass(), "/Wheel_Left_Pressed.png");
    private final BufferedImage wheelRightUnpressed = ImageUtil.loadImageResource(getClass(), "/Wheel_Right_Unpressed.png");
    private final BufferedImage wheelRightPressed = ImageUtil.loadImageResource(getClass(), "/Wheel_Right_Pressed.png");
    private final BufferedImage anchorPressed = ImageUtil.loadImageResource(getClass(), "/Anchor_Pressed.png");
    private final BufferedImage anchorUnpressed = ImageUtil.loadImageResource(getClass(), "/Anchor_Unpressed.png");
    private final BufferedImage speed1Unpressed = ImageUtil.loadImageResource(getClass(), "/Speed_1_Unpressed.png");
    private final BufferedImage speed1Pressed = ImageUtil.loadImageResource(getClass(), "/Speed_1_Pressed.png");
    private final BufferedImage speed2Unpressed = ImageUtil.loadImageResource(getClass(), "/Speed_2_Unpressed.png");
    private final BufferedImage speed2Pressed = ImageUtil.loadImageResource(getClass(), "/Speed_2_Pressed.png");
    private final BufferedImage speed3Unpressed = ImageUtil.loadImageResource(getClass(), "/Speed_3_Unpressed.png");
    private final BufferedImage speed3Pressed = ImageUtil.loadImageResource(getClass(), "/Speed_3_Pressed.png");

    private Widget parent;
    private Widget wheelRightWidget;
    private Widget wheelLeftWidget;
    private Widget anchorWidget;
    @Getter
    private Widget speed1Widget;
    @Getter
    private Widget speed2Widget;
    @Getter
    private Widget speed3Widget;

    private final int SPEED_1_PRESSED_ID = SpriteID.MOBILE_YELLOW_TOUCH_ANIMATION_1;
    private final int SPEED_1_UNPRESSED_ID = SpriteID.MOBILE_TUTORIAL_FUNCTION_MODE_BUTTON;
    private final int SPEED_2_PRESSED_ID = SpriteID.HEALTHBAR_CYAN_BACK_30PX;
    private final int SPEED_2_UNPRESSED_ID = SpriteID.HEALTHBAR_CYAN_BACK_40PX;
    private final int SPEED_3_PRESSED_ID = SpriteID.HEALTHBAR_CYAN_BACK_50PX;
    private final int SPEED_3_UNPRESSED_ID = SpriteID.HEALTHBAR_CYAN_BACK_60PX;
    private final int WHEEL_LEFT_UNPRESSED_ID = SpriteID.MOBILE_TUTORIAL_MINIMISE_WORLD_MAP;
    private final int WHEEL_LEFT_PRESSED_ID = SpriteID.HEALTHBAR_CYAN_BACK_70PX;
    private final int WHEEL_RIGHT_UNPRESSED_ID = SpriteID.MOBILE_YELLOW_TOUCH_ANIMATION_2;
    private final int WHEEL_RIGHT_PRESSED_ID = SpriteID.HEALTHBAR_CYAN_BACK_80PX;
    private final int ANCHOR_UNPRESSED_ID = SpriteID.MOBILE_FUNCTION_MODE_DISABLED;
    private final int ANCHOR_PRESSED_ID = SpriteID.ABLEGAMERS_PROMO_BANNER;

    private final int HOVERED_OPACITY = 125;
    private final int BASE_Y = -25;

    public void setParent(Widget parentWidget)
    {
        parent = parentWidget;
    }

    public void createWidgets()
    {
        wheelLeftWidget = parent.createChild(1, WidgetType.GRAPHIC);
        wheelLeftWidget.setPos(-50, BASE_Y - 37, 1, 1);
        wheelLeftWidget.setSize(53, 88);
        wheelLeftWidget.setOriginalHeight(88);
        wheelLeftWidget.setOriginalWidth(53);
        wheelLeftWidget.setSpriteId(WHEEL_LEFT_UNPRESSED_ID);
        wheelLeftWidget.revalidate();
        wheelLeftWidget.setHasListener(true);
        wheelLeftWidget.setOnMouseRepeatListener((JavaScriptCallback) this::wheelLeftHoverEvent);
        wheelLeftWidget.setOnMouseLeaveListener((JavaScriptCallback) this::wheelLeftLeaveEvent);
        wheelLeftWidget.setOnOpListener((JavaScriptCallback) this::wheelLeftClickEvent);
        wheelLeftWidget.setAction(0, "Rotate left");

        wheelRightWidget = parent.createChild(2, WidgetType.GRAPHIC);
        wheelRightWidget.setPos(51, BASE_Y - 37, 1, 1);
        wheelRightWidget.setSize(53, 88);
        wheelRightWidget.setOriginalHeight(88);
        wheelRightWidget.setOriginalWidth(53);
        wheelRightWidget.setSpriteId(WHEEL_RIGHT_UNPRESSED_ID);
        wheelRightWidget.revalidate();
        wheelRightWidget.setHasListener(true);
        wheelRightWidget.setOnMouseRepeatListener((JavaScriptCallback) this::wheelRightHoverEvent);
        wheelRightWidget.setOnMouseLeaveListener((JavaScriptCallback) this::wheelRightLeaveEvent);
        wheelRightWidget.setOnOpListener((JavaScriptCallback) this::wheelRightClickEvent);
        wheelRightWidget.setAction(0, "Rotate right");

        anchorWidget = parent.createChild(3, WidgetType.GRAPHIC);
        anchorWidget.setPos(0, BASE_Y, 1, 1);
        anchorWidget.setSize(40, 26);
        anchorWidget.setOriginalHeight(26);
        anchorWidget.setOriginalWidth(40);
        anchorWidget.setSpriteId(ANCHOR_PRESSED_ID);
        anchorWidget.revalidate();
        anchorWidget.setNoClickThrough(true);
        anchorWidget.setHasListener(true);
        anchorWidget.setOnOpListener((JavaScriptCallback) this::anchorOpEvent);
        anchorWidget.setOnTimerListener((JavaScriptCallback) this::anchorOnClientTick);
        anchorWidget.setOnMouseOverListener((JavaScriptCallback) this::anchorHoverEvent);
        anchorWidget.setOnMouseLeaveListener((JavaScriptCallback) this::anchorLeaveEvent);
        anchorWidget.setAction(0, "Toggle anchor");

        speed1Widget = parent.createChild(4, WidgetType.GRAPHIC);
        speed1Widget.setPos(0, BASE_Y - 25, 1, 1);
        speed1Widget.setSize(40, 26);
        speed1Widget.setOriginalHeight(26);
        speed1Widget.setOriginalWidth(40);
        speed1Widget.setSpriteId(SPEED_1_PRESSED_ID);
        speed1Widget.revalidate();
        speed1Widget.setHasListener(true);
        speed1Widget.setOnOpListener((JavaScriptCallback) this::speed1ClickEvent);
        speed1Widget.setOnMouseOverListener((JavaScriptCallback) this::speed1HoverEvent);
        speed1Widget.setOnMouseLeaveListener((JavaScriptCallback) this::speed1LeaveEvent);
        speed1Widget.setAction(0, "Sail speed 1");

        speed2Widget = parent.createChild(5, WidgetType.GRAPHIC);
        speed2Widget.setPos(0, BASE_Y - 50, 1, 1);
        speed2Widget.setSize(40, 26);
        speed2Widget.setOriginalHeight(26);
        speed2Widget.setOriginalWidth(40);
        speed2Widget.setSpriteId(SPEED_2_UNPRESSED_ID);
        speed2Widget.revalidate();
        speed2Widget.setHasListener(true);
        speed2Widget.setOnOpListener((JavaScriptCallback) this::speed2ClickEvent);
        speed2Widget.setOnMouseOverListener((JavaScriptCallback) this::speed2HoverEvent);
        speed2Widget.setOnMouseLeaveListener((JavaScriptCallback) this::speed2LeaveEvent);
        speed2Widget.setAction(0, "Sail speed 2");

        speed3Widget = parent.createChild(6, WidgetType.GRAPHIC);
        speed3Widget.setPos(0, BASE_Y - 75, 1, 1);
        speed3Widget.setSize(40, 26);
        speed3Widget.setOriginalHeight(26);
        speed3Widget.setOriginalWidth(40);
        speed3Widget.setSpriteId(SPEED_3_UNPRESSED_ID);
        speed3Widget.revalidate();
        speed3Widget.setHasListener(true);
        speed3Widget.setOnOpListener((JavaScriptCallback) this::speed3ClickEvent);
        speed3Widget.setOnMouseOverListener((JavaScriptCallback) this::speed3HoverEvent);
        speed3Widget.setOnMouseLeaveListener((JavaScriptCallback) this::speed3LeaveEvent);
        speed3Widget.setAction(0, "Sail speed 3");
    }

    public void speed1ClickEvent(ScriptEvent e)
    {
        plugin.setSailLength(1);
    }

    public void speed1HoverEvent(ScriptEvent e) {speed1Widget.setOpacity(HOVERED_OPACITY); }

    public void speed1LeaveEvent(ScriptEvent e) {speed1Widget.setOpacity(0); }

    public void speed2ClickEvent(ScriptEvent e)
    {
        plugin.setSailLength(2);
    }

    public void speed2HoverEvent(ScriptEvent e) {speed2Widget.setOpacity(HOVERED_OPACITY); }

    public void speed2LeaveEvent(ScriptEvent e) {speed2Widget.setOpacity(0); }

    public void speed3ClickEvent(ScriptEvent e)
    {
        plugin.setSailLength(3);
    }

    public void speed3HoverEvent(ScriptEvent e) {speed3Widget.setOpacity(HOVERED_OPACITY); }

    public void speed3LeaveEvent(ScriptEvent e) {speed3Widget.setOpacity(0); }

    public void wheelLeftClickEvent(ScriptEvent e)
    {
        plugin.rotateBoatCCW();
    }

    public void wheelRightClickEvent(ScriptEvent e)
    {
        plugin.rotateBoatCW();
    }

    public void wheelLeftHoverEvent(ScriptEvent e)
    {
        wheelLeftWidget.setOpacity(HOVERED_OPACITY);
    }

    public void wheelRightHoverEvent(ScriptEvent e)
    {
        wheelRightWidget.setOpacity(HOVERED_OPACITY);
    }

    public void wheelLeftLeaveEvent(ScriptEvent e)
    {
        wheelLeftWidget.setOpacity(0);
    }

    public void wheelRightLeaveEvent(ScriptEvent e)
    {
        wheelRightWidget.setOpacity(0);
    }

    public void anchorOpEvent(ScriptEvent e)
    {
        plugin.setSailLength(0);
    }

    public void anchorHoverEvent(ScriptEvent e)
    {
        anchorWidget.setOpacity(HOVERED_OPACITY);
    }

    public void anchorLeaveEvent(ScriptEvent e)
    {
        anchorWidget.setOpacity(0);
    }

    public void anchorOnClientTick(ScriptEvent e)
    {
        updateSailLengthSprite(plugin.getSailLength());
        updateBoatRotationSprite();
    }

    public void updateBoatRotationSprite()
    {
        int rotationQueue = plugin.getBoatRotationQueue();
        if (rotationQueue > 0)
        {
            wheelLeftWidget.setSpriteId(WHEEL_LEFT_UNPRESSED_ID);
            wheelRightWidget.setSpriteId(WHEEL_RIGHT_PRESSED_ID);
            return;
        }

        if (rotationQueue < 0)
        {
            wheelLeftWidget.setSpriteId(WHEEL_LEFT_PRESSED_ID);
            wheelRightWidget.setSpriteId(WHEEL_RIGHT_UNPRESSED_ID);
            return;
        }

        wheelLeftWidget.setSpriteId(WHEEL_LEFT_UNPRESSED_ID);
        wheelRightWidget.setSpriteId(WHEEL_RIGHT_UNPRESSED_ID);
    }

    public void updateSailLengthSprite(int sailLength)
    {
        switch (sailLength)
        {
            case 0:
                speed1Widget.setSpriteId(SPEED_1_UNPRESSED_ID);
                speed2Widget.setSpriteId(SPEED_2_UNPRESSED_ID);
                speed3Widget.setSpriteId(SPEED_3_UNPRESSED_ID);
                anchorWidget.setSpriteId(ANCHOR_PRESSED_ID);
                return;
            case 1:
                speed1Widget.setSpriteId(SPEED_1_PRESSED_ID);
                speed2Widget.setSpriteId(SPEED_2_UNPRESSED_ID);
                speed3Widget.setSpriteId(SPEED_3_UNPRESSED_ID);
                anchorWidget.setSpriteId(ANCHOR_UNPRESSED_ID);
                return;
            case 2:
                speed1Widget.setSpriteId(SPEED_1_UNPRESSED_ID);
                speed2Widget.setSpriteId(SPEED_2_PRESSED_ID);
                speed3Widget.setSpriteId(SPEED_3_UNPRESSED_ID);
                anchorWidget.setSpriteId(ANCHOR_UNPRESSED_ID);
                return;
            case 3:
                speed1Widget.setSpriteId(SPEED_1_UNPRESSED_ID);
                speed2Widget.setSpriteId(SPEED_2_UNPRESSED_ID);
                speed3Widget.setSpriteId(SPEED_3_PRESSED_ID);
                anchorWidget.setSpriteId(ANCHOR_UNPRESSED_ID);
        }
    }

    public void setWheelSprite()
    {
        SpritePixels sp1 = ImageUtil.getImageSpritePixels(speed1Unpressed, client);
        client.getSpriteOverrides().put(SPEED_1_UNPRESSED_ID, sp1);

        SpritePixels sp2 = ImageUtil.getImageSpritePixels(speed1Pressed, client);
        client.getSpriteOverrides().put(SPEED_1_PRESSED_ID, sp2);

        SpritePixels sp3 = ImageUtil.getImageSpritePixels(speed2Unpressed, client);
        client.getSpriteOverrides().put(SPEED_2_UNPRESSED_ID, sp3);

        SpritePixels sp4 = ImageUtil.getImageSpritePixels(speed2Pressed, client);
        client.getSpriteOverrides().put(SPEED_2_PRESSED_ID, sp4);

        SpritePixels sp5 = ImageUtil.getImageSpritePixels(speed3Unpressed, client);
        client.getSpriteOverrides().put(SPEED_3_UNPRESSED_ID, sp5);

        SpritePixels sp6 = ImageUtil.getImageSpritePixels(speed3Pressed, client);
        client.getSpriteOverrides().put(SPEED_3_PRESSED_ID, sp6);

        SpritePixels sp7 = ImageUtil.getImageSpritePixels(wheelLeftUnpressed, client);
        client.getSpriteOverrides().put(WHEEL_LEFT_UNPRESSED_ID, sp7);

        SpritePixels sp8 = ImageUtil.getImageSpritePixels(wheelRightUnpressed, client);
        client.getSpriteOverrides().put(WHEEL_RIGHT_UNPRESSED_ID, sp8);

        SpritePixels sp9 = ImageUtil.getImageSpritePixels(wheelLeftPressed, client);
        client.getSpriteOverrides().put(WHEEL_LEFT_PRESSED_ID, sp9);

        SpritePixels sp10 = ImageUtil.getImageSpritePixels(wheelRightPressed, client);
        client.getSpriteOverrides().put(WHEEL_RIGHT_PRESSED_ID, sp10);

        SpritePixels anchorSprite = ImageUtil.getImageSpritePixels(anchorUnpressed, client);
        client.getSpriteOverrides().put(ANCHOR_UNPRESSED_ID, anchorSprite);

        SpritePixels anchorPressedSprite = ImageUtil.getImageSpritePixels(anchorPressed, client);
        client.getSpriteOverrides().put(ANCHOR_PRESSED_ID, anchorPressedSprite);
    }

    public void setupManager(Widget parent)
    {
        setParent(parent);
        setWheelSprite();
        createWidgets();
    }
}
