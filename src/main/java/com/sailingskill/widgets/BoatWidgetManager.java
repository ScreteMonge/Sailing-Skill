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

    private final BufferedImage wheelFullL0 = ImageUtil.loadImageResource(getClass(), "/Left_Wheel_0.png");
    private final BufferedImage wheelFullL1 = ImageUtil.loadImageResource(getClass(), "/Left_Wheel_1.png");
    private final BufferedImage wheelFullL2 = ImageUtil.loadImageResource(getClass(), "/Left_Wheel_2.png");
    private final BufferedImage wheelFullL3 = ImageUtil.loadImageResource(getClass(), "/Left_Wheel_3.png");
    private final BufferedImage wheelFullR0 = ImageUtil.loadImageResource(getClass(), "/Right_Wheel_0.png");
    private final BufferedImage wheelFullR1 = ImageUtil.loadImageResource(getClass(), "/Right_Wheel_1.png");
    private final BufferedImage wheelFullR2 = ImageUtil.loadImageResource(getClass(), "/Right_Wheel_2.png");
    private final BufferedImage wheelFullR3 = ImageUtil.loadImageResource(getClass(), "/Right_Wheel_3.png");
    private final BufferedImage anchorImage = ImageUtil.loadImageResource(getClass(), "/Wheel_Anchor.png");
    private final BufferedImage anchorPressedImage = ImageUtil.loadImageResource(getClass(), "/Wheel_Anchor_Pressed.png");
    private final BufferedImage wheelHeadImage = ImageUtil.loadImageResource(getClass(), "/Wheel_Head.png");

    private Widget parent;
    @Getter
    private static Widget wheelRightWidget;
    @Getter
    private static Widget wheelLeftWidget;
    private Widget anchorWidget;
    private Widget headWidget;
    private Widget headShadowWidget;

    private final int LEFT_0_SPRITE_ID = SpriteID.MOBILE_YELLOW_TOUCH_ANIMATION_1;
    private final int LEFT_1_SPRITE_ID = SpriteID.MOBILE_TUTORIAL_FUNCTION_MODE_BUTTON;
    private final int LEFT_2_SPRITE_ID = SpriteID.MOBILE_TUTORIAL_MINIMISE_WORLD_MAP;
    private final int LEFT_3_SPRITE_ID = SpriteID.MOBILE_TUTORIAL_GESTURES_TAP_AND_PRESS;
    private final int RIGHT_0_SPRITE_ID = SpriteID.MOBILE_YELLOW_TOUCH_ANIMATION_2;
    private final int RIGHT_1_SPRITE_ID = SpriteID.MOBILE_TUTORIAL_NPC_GESTURE_PRESS;
    private final int RIGHT_2_SPRITE_ID = SpriteID.MOBILE_TUTORIAL_NPC_GESTURE_TAP;
    private final int RIGHT_3_SPRITE_ID = SpriteID.MOBILE_TUTORIAL_CAMERA_MOVEMENT;
    private final int ANCHOR_SPRITE_ID = SpriteID.MOBILE_FUNCTION_MODE_DISABLED;
    private final int ANCHOR_PRESSED_SPRITE_ID = SpriteID.ABLEGAMERS_PROMO_BANNER;
    private final int HEAD_SPRITE_ID = SpriteID.MOBILE_FUNCTION_MODE_ENABLED;

    private final int HOVERED_OPACITY = 125;
    private final int BASE_Y = 10;

    public void setParent(Widget parentWidget)
    {
        parent = parentWidget;
    }

    public void createWidgets()
    {
        wheelLeftWidget = parent.createChild(1, WidgetType.GRAPHIC);
        wheelLeftWidget.setPos(-35, BASE_Y - 19, 1, 1);
        wheelLeftWidget.setSize(71, 66);
        wheelLeftWidget.setOriginalHeight(66);
        wheelLeftWidget.setOriginalWidth(71);
        wheelLeftWidget.setSpriteId(LEFT_0_SPRITE_ID);
        wheelLeftWidget.revalidate();
        wheelLeftWidget.setHasListener(true);
        wheelLeftWidget.setOnMouseRepeatListener((JavaScriptCallback) this::wheelLeftHoverEvent);
        wheelLeftWidget.setOnMouseLeaveListener((JavaScriptCallback) this::wheelLeftLeaveEvent);
        wheelLeftWidget.setOnOpListener((JavaScriptCallback) this::wheelLeftClickEvent);
        wheelLeftWidget.setAction(0, "Rotate left");


        wheelRightWidget = parent.createChild(2, WidgetType.GRAPHIC);
        wheelRightWidget.setPos(36, BASE_Y - 19, 1, 1);
        wheelRightWidget.setSize(71, 66);
        wheelRightWidget.setOriginalHeight(66);
        wheelRightWidget.setOriginalWidth(71);
        wheelRightWidget.setSpriteId(RIGHT_0_SPRITE_ID);
        wheelRightWidget.revalidate();
        wheelRightWidget.setHasListener(true);
        wheelRightWidget.setOnMouseRepeatListener((JavaScriptCallback) this::wheelRightHoverEvent);
        wheelRightWidget.setOnMouseLeaveListener((JavaScriptCallback) this::wheelRightLeaveEvent);
        wheelRightWidget.setOnOpListener((JavaScriptCallback) this::wheelRightClickEvent);
        wheelRightWidget.setAction(0, "Rotate right");


        anchorWidget = parent.createChild(3, WidgetType.GRAPHIC);
        anchorWidget.setPos(0, BASE_Y, 1, 1);
        anchorWidget.setSize(36, 36);
        anchorWidget.setOriginalHeight(36);
        anchorWidget.setOriginalWidth(36);
        anchorWidget.setSpriteId(ANCHOR_PRESSED_SPRITE_ID);
        anchorWidget.revalidate();
        anchorWidget.setNoClickThrough(true);
        anchorWidget.setHasListener(true);
        anchorWidget.setOnOpListener((JavaScriptCallback) this::anchorOpEvent);
        anchorWidget.setOnTimerListener((JavaScriptCallback) this::anchorOnClientTick);
        anchorWidget.setOnMouseOverListener((JavaScriptCallback) this::anchorHoverEvent);
        anchorWidget.setOnMouseLeaveListener((JavaScriptCallback) this::anchorLeaveEvent);
        anchorWidget.setAction(0, "Toggle anchor");


        headShadowWidget = parent.createChild(4, WidgetType.GRAPHIC);
        headShadowWidget.setPos(0, BASE_Y - 44, 1, 1);
        headShadowWidget.setSize(36, 53);
        headShadowWidget.setOriginalHeight(53);
        headShadowWidget.setOriginalWidth(36);
        headShadowWidget.setSpriteId(HEAD_SPRITE_ID);
        headShadowWidget.setOpacity(50);
        headShadowWidget.revalidate();
        headShadowWidget.setNoClickThrough(true);


        headWidget = parent.createChild(5, WidgetType.GRAPHIC);
        headWidget.setPos(0, BASE_Y - 44, 1, 1);
        headWidget.setSize(36, 53);
        headWidget.setOriginalHeight(53);
        headWidget.setOriginalWidth(36);
        headWidget.setSpriteId(HEAD_SPRITE_ID);
        headWidget.revalidate();
        headWidget.setNoClickThrough(true);
        headWidget.setAction(0, "Stop rotation");
        headWidget.setAction(1, "Drag rotation");
        headWidget.setHasListener(true);
        headWidget.setDragParent(parent);
        headWidget.setDragDeadTime(0);
        headWidget.setDragDeadZone(0);
        headWidget.setOnOpListener((JavaScriptCallback) this::headOpEvent);
        headWidget.setOnDragCompleteListener((JavaScriptCallback) this::headDragCompleteEvent);
        headWidget.setOnDragListener((JavaScriptCallback) this::headDragEvent);
        headWidget.setOnMouseOverListener((JavaScriptCallback) this::headHoverEvent);
        headWidget.setOnMouseLeaveListener((JavaScriptCallback) this::headLeaveEvent);
    }

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
        plugin.setAnchorMode(!plugin.isAnchorMode());
        anchorWidget.setSpriteId(plugin.isAnchorMode() ? ANCHOR_PRESSED_SPRITE_ID : ANCHOR_SPRITE_ID);
    }

    public void anchorHoverEvent(ScriptEvent e)
    {
        anchorWidget.setOpacity(HOVERED_OPACITY);
        wheelRightWidget.setOpacity(0);
        wheelLeftWidget.setOpacity(0);
    }

    public void anchorLeaveEvent(ScriptEvent e)
    {
        anchorWidget.setOpacity(0);
    }

    public void anchorOnClientTick(ScriptEvent e)
    {
        if (plugin.isAnchorMode())
        {
            anchorWidget.setSpriteId(ANCHOR_PRESSED_SPRITE_ID);
        }
        else
        {
            anchorWidget.setSpriteId(ANCHOR_SPRITE_ID);
        }
    }

    public void newHeadDragEvent(ScriptEvent e)
    {
        if (plugin.getShipObject() == null)
        {
            return;
        }

        wheelLeftWidget.setOpacity(0);
        wheelRightWidget.setOpacity(0);
        headWidget.setOpacity(HOVERED_OPACITY);

        int movementSpace = parent.getWidth() - headWidget.getWidth();
        int midline = movementSpace / 2;
        int deadZone = 30;

        int x = e.getMouseX();

        if (x > midline - deadZone && x < midline + deadZone)
        {
            wheelLeftWidget.setSpriteId(LEFT_0_SPRITE_ID);
            wheelRightWidget.setSpriteId(RIGHT_0_SPRITE_ID);
            plugin.setBoatRotationQueue(0);
        }
        else if (x < midline - deadZone)
        {
            wheelLeftWidget.setSpriteId(LEFT_3_SPRITE_ID);
            plugin.rotateBoatCCW();
        }
        else if (x > midline + deadZone)
        {
            wheelRightWidget.setSpriteId(RIGHT_3_SPRITE_ID);
            plugin.rotateBoatCW();
        }
    }

    public void newHeadDragCompleteEvent(ScriptEvent e)
    {
        if (plugin.getShipObject() == null)
        {
            return;
        }

        wheelLeftWidget.setSpriteId(LEFT_0_SPRITE_ID);
        wheelRightWidget.setSpriteId(RIGHT_0_SPRITE_ID);
        headWidget.setOpacity(0);

        plugin.setBoatRotationQueue(0);
    }

    public void headDragEvent(ScriptEvent e)
    {
        if (plugin.getShipObject() == null)
        {
            return;
        }

        wheelLeftWidget.setOpacity(0);
        wheelRightWidget.setOpacity(0);
        headWidget.setOpacity(HOVERED_OPACITY);

        int movementSpace = parent.getWidth() - headWidget.getWidth();
        int midline = movementSpace / 2;
        int deadZone = 5;
        int singleMove = movementSpace / 4;

        int x = e.getMouseX();

        if (x > midline - deadZone && x < midline + deadZone)
        {
            wheelLeftWidget.setSpriteId(LEFT_0_SPRITE_ID);
            wheelRightWidget.setSpriteId(RIGHT_0_SPRITE_ID);
        }
        else if (x < midline - deadZone)
        {
            if (x == 0)
            {
                wheelLeftWidget.setSpriteId(LEFT_3_SPRITE_ID);
                wheelRightWidget.setSpriteId(RIGHT_0_SPRITE_ID);
            }
            else if (x > midline - singleMove)
            {
                wheelLeftWidget.setSpriteId(LEFT_1_SPRITE_ID);
                wheelRightWidget.setSpriteId(RIGHT_0_SPRITE_ID);
            }
            else
            {
                wheelLeftWidget.setSpriteId(LEFT_2_SPRITE_ID);
                wheelRightWidget.setSpriteId(RIGHT_0_SPRITE_ID);
            }
        }
        else if (x > midline + deadZone)
        {
            if (x == movementSpace)
            {
                wheelRightWidget.setSpriteId(RIGHT_3_SPRITE_ID);
                wheelLeftWidget.setSpriteId(LEFT_0_SPRITE_ID);
            }
            else if (x < midline + singleMove)
            {
                wheelRightWidget.setSpriteId(RIGHT_1_SPRITE_ID);
                wheelLeftWidget.setSpriteId(LEFT_0_SPRITE_ID);
            }
            else
            {
                wheelRightWidget.setSpriteId(RIGHT_2_SPRITE_ID);
                wheelLeftWidget.setSpriteId(LEFT_0_SPRITE_ID);
            }
        }
    }

    public void headDragCompleteEvent(ScriptEvent e)
    {
        if (plugin.getShipObject() == null)
        {
            return;
        }

        wheelLeftWidget.setSpriteId(LEFT_0_SPRITE_ID);
        wheelRightWidget.setSpriteId(RIGHT_0_SPRITE_ID);
        headWidget.setOpacity(0);

        int movementSpace = parent.getWidth() - headWidget.getWidth();
        int midline = movementSpace / 2;
        int deadZone = 5;
        int singleMove = movementSpace / 4;

        int x = e.getMouseX();


        if (x > midline - deadZone && x < midline + deadZone)
        {
            return;
        }
        else if (x < midline - deadZone)
        {
            if (x == 0)
            {
                plugin.setBoatRotationQueue(-3);
            }
            else if (x > midline - singleMove)
            {
                plugin.setBoatRotationQueue(-1);
            }
            else
            {
                plugin.setBoatRotationQueue(-2);
            }
        }
        else if (x > midline + deadZone)
        {
            if (x == movementSpace)
            {
                plugin.setBoatRotationQueue(3);
            }
            else if (x < midline + singleMove)
            {
                plugin.setBoatRotationQueue(1);
            }
            else
            {
                plugin.setBoatRotationQueue(2);
            }
        }
    }

    public void headHoverEvent(ScriptEvent e)
    {
        headWidget.setOpacity(HOVERED_OPACITY);
        headShadowWidget.setHidden(true);
        wheelRightWidget.setOpacity(0);
        wheelLeftWidget.setOpacity(0);
    }

    public void headLeaveEvent(ScriptEvent e)
    {
        headShadowWidget.setHidden(false);
        headWidget.setOpacity(0);
    }

    public void headOpEvent(ScriptEvent e)
    {
        if (e.getOp() == 1)
        {
            plugin.setBoatRotationQueue(0);
        }

        if (e.getOp() == 2)
        {
            client.addChatMessage(ChatMessageType.GAMEMESSAGE, "player", "Drag this widget to queue rotations.", "game");
        }
    }

    public void setWheelSprite()
    {
        SpritePixels wheelFullL0Sprite = ImageUtil.getImageSpritePixels(wheelFullL0, client);
        client.getSpriteOverrides().put(LEFT_0_SPRITE_ID, wheelFullL0Sprite);

        SpritePixels wheelFullL1Sprite = ImageUtil.getImageSpritePixels(wheelFullL1, client);
        client.getSpriteOverrides().put(LEFT_1_SPRITE_ID, wheelFullL1Sprite);

        SpritePixels wheelFullL2Sprite = ImageUtil.getImageSpritePixels(wheelFullL2, client);
        client.getSpriteOverrides().put(LEFT_2_SPRITE_ID, wheelFullL2Sprite);

        SpritePixels wheelFullL3Sprite = ImageUtil.getImageSpritePixels(wheelFullL3, client);
        client.getSpriteOverrides().put(LEFT_3_SPRITE_ID, wheelFullL3Sprite);

        SpritePixels wheelFullR0Sprite = ImageUtil.getImageSpritePixels(wheelFullR0, client);
        client.getSpriteOverrides().put(RIGHT_0_SPRITE_ID, wheelFullR0Sprite);

        SpritePixels wheelFullR1Sprite = ImageUtil.getImageSpritePixels(wheelFullR1, client);
        client.getSpriteOverrides().put(RIGHT_1_SPRITE_ID, wheelFullR1Sprite);

        SpritePixels wheelFullR2Sprite = ImageUtil.getImageSpritePixels(wheelFullR2, client);
        client.getSpriteOverrides().put(RIGHT_2_SPRITE_ID, wheelFullR2Sprite);

        SpritePixels wheelFullR3Sprite = ImageUtil.getImageSpritePixels(wheelFullR3, client);
        client.getSpriteOverrides().put(RIGHT_3_SPRITE_ID, wheelFullR3Sprite);

        SpritePixels anchorSprite = ImageUtil.getImageSpritePixels(anchorImage, client);
        client.getSpriteOverrides().put(ANCHOR_SPRITE_ID, anchorSprite);

        SpritePixels anchorPressedSprite = ImageUtil.getImageSpritePixels(anchorPressedImage, client);
        client.getSpriteOverrides().put(ANCHOR_PRESSED_SPRITE_ID, anchorPressedSprite);

        SpritePixels wheelHead = ImageUtil.getImageSpritePixels(wheelHeadImage, client);
        client.getSpriteOverrides().put(HEAD_SPRITE_ID, wheelHead);
    }

    public void setupManager(Widget parent)
    {
        setParent(parent);
        setWheelSprite();
        createWidgets();
    }

}
