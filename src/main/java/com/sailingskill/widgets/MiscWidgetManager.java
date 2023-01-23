package com.sailingskill.widgets;

import com.sailingskill.SailingPlugin;
import net.runelite.api.*;
import net.runelite.api.widgets.JavaScriptCallback;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.api.widgets.WidgetType;
import net.runelite.client.util.ImageUtil;

import javax.inject.Inject;
import java.awt.image.BufferedImage;

public class MiscWidgetManager
{
    @Inject
    private Client client;

    @Inject
    private SailingPlugin plugin;

    private final BufferedImage cannonPressed = ImageUtil.loadImageResource(getClass(), "/Button_Cannon_Pressed.png");
    private final BufferedImage cannonUnpressed = ImageUtil.loadImageResource(getClass(), "/Button_Cannon_Unpressed.png");
    private final BufferedImage invPressed = ImageUtil.loadImageResource(getClass(), "/Button_Inv_Pressed.png");
    private final BufferedImage invUnpressed = ImageUtil.loadImageResource(getClass(), "/Button_Inv_Unpressed.png");
    private final BufferedImage keyboardUnpressed = ImageUtil.loadImageResource(getClass(), "/Button_Keyboard_Unpressed.png");
    private final BufferedImage keyboardPressed = ImageUtil.loadImageResource(getClass(), "/Button_Keyboard_Pressed.png");
    private final BufferedImage fireImage = ImageUtil.loadImageResource(getClass(), "/Fire_Button.png");

    private Widget parent;
    private Widget keyboardWidget;
    private Widget cannonOverlayWidget;
    private Widget inventoryWidget;
    private Widget fireWidget;

    private final int KEYBOARD_UNPRESSED_SPRITE_ID = SpriteID.MOBILE_CONCEPT_SKETCH_DEVICE;
    private final int KEYBOARD_PRESSED_SPRITE_ID = SpriteID.DEADMAN_TAB_ITEMS_LOST_ON_DEATH_TO_PVM;
    private final int CANNON_UNPRESSED_SPRITE_ID = SpriteID.MOBILE_CONCEPT_SKETCH_UI;
    private final int CANNON_PRESSED_SPRITE_ID = SpriteID.DEADMAN_TAB_ITEMS_LOST_ON_DEATH_TO_PVP;
    private final int INVENTORY_UNPRESSED_SPRITE_ID = SpriteID.MOBILE_FINGER_ON_INTERFACE;
    private final int INVENTORY_PRESSED_SPRITE_ID = SpriteID.DEADMAN_TAB_ITEMS_LOST_ON_DEATH_WHILE_SKULLED;
    private final int FIRE_SPRITE_ID = SpriteID.DEADMAN_BANK_KEYS_2;

    private final int HOVERED_OPACITY = 125;
    private final int BASE_Y = 95;

    public void setParent(Widget parentWidget)
    {
        parent = parentWidget;
    }

    public void createWidgets()
    {
        cannonOverlayWidget = parent.createChild(6, WidgetType.GRAPHIC);
        cannonOverlayWidget.setPos(-55, BASE_Y, 1, 1);
        cannonOverlayWidget.setSize(40, 40);
        cannonOverlayWidget.setOriginalHeight(40);
        cannonOverlayWidget.setOriginalWidth(40);
        cannonOverlayWidget.setSpriteId(CANNON_UNPRESSED_SPRITE_ID);
        cannonOverlayWidget.revalidate();
        cannonOverlayWidget.setHasListener(true);
        cannonOverlayWidget.setOnOpListener((JavaScriptCallback) this::cannonOverlayOpEvent);
        cannonOverlayWidget.setAction(0, "Toggle cannon overlay");

        keyboardWidget = parent.createChild(7, WidgetType.GRAPHIC);
        keyboardWidget.setPos(0, BASE_Y, 1, 1);
        keyboardWidget.setSize(40, 40);
        keyboardWidget.setOriginalHeight(40);
        keyboardWidget.setOriginalWidth(40);
        keyboardWidget.setSpriteId(KEYBOARD_PRESSED_SPRITE_ID);
        keyboardWidget.revalidate();
        keyboardWidget.setHasListener(true);
        keyboardWidget.setOnOpListener((JavaScriptCallback) this::keyboardOpEvent);
        keyboardWidget.setAction(0, "Toggle keyboard control");

        inventoryWidget = parent.createChild(8, WidgetType.GRAPHIC);
        inventoryWidget.setPos(55, BASE_Y, 1, 1);
        inventoryWidget.setSize(40, 40);
        inventoryWidget.setOriginalHeight(40);
        inventoryWidget.setOriginalWidth(40);
        inventoryWidget.setSpriteId(INVENTORY_UNPRESSED_SPRITE_ID);
        inventoryWidget.revalidate();
        inventoryWidget.setHasListener(true);
        inventoryWidget.setOnOpListener((JavaScriptCallback) this::inventoryOpEvent);
        inventoryWidget.setOnReleaseListener((JavaScriptCallback) this::inventoryReleaseEvent);
        inventoryWidget.setAction(0, "Open ship inventory");

        fireWidget = parent.createChild(9, WidgetType.GRAPHIC);
        fireWidget.setPos(0, BASE_Y - 47, 1, 1);
        fireWidget.setSize(150, 26);
        fireWidget.setOriginalHeight(26);
        fireWidget.setOriginalWidth(150);
        fireWidget.setSpriteId(FIRE_SPRITE_ID);
        fireWidget.revalidate();
        fireWidget.setHasListener(true);
        fireWidget.setOnOpListener((JavaScriptCallback) this::fireOpEvent);
        fireWidget.setOnMouseOverListener((JavaScriptCallback) this::fireHoverEvent);
        fireWidget.setOnMouseLeaveListener((JavaScriptCallback) this::fireLeaveEvent);
        fireWidget.setAction(0, "Fire cannon");
    }

    public void cannonOverlayOpEvent(ScriptEvent e)
    {
        if (plugin.isShowCannonRange())
        {
            plugin.setShowCannonRange(false);
            cannonOverlayWidget.setSpriteId(CANNON_UNPRESSED_SPRITE_ID);
        }
        else
        {
            plugin.setShowCannonRange(true);
            cannonOverlayWidget.setSpriteId(CANNON_PRESSED_SPRITE_ID);
        }
    }

    public void keyboardOpEvent(ScriptEvent e)
    {
        if (plugin.isEnableKeyboardControl())
        {
            plugin.setEnableKeyboardControl(false);
            keyboardWidget.setSpriteId(KEYBOARD_UNPRESSED_SPRITE_ID);
        }
        else
        {
            plugin.setEnableKeyboardControl(true);
            keyboardWidget.setSpriteId(KEYBOARD_PRESSED_SPRITE_ID);
        }
    }

    public void inventoryOpEvent(ScriptEvent e)
    {
        inventoryWidget.setSpriteId(INVENTORY_PRESSED_SPRITE_ID);
        client.addChatMessage(ChatMessageType.GAMEMESSAGE, "player", "Sailing isn't real so your ship has no inventory.", "game");
    }

    public void inventoryReleaseEvent(ScriptEvent e)
    {
        inventoryWidget.setSpriteId(INVENTORY_UNPRESSED_SPRITE_ID);
    }

    public void fireHoverEvent(ScriptEvent e)
    {
        fireWidget.setOpacity(HOVERED_OPACITY);
    }

    public void fireLeaveEvent(ScriptEvent e)
    {
        fireWidget.setOpacity(0);
    }

    public void fireOpEvent(ScriptEvent e)
    {
        plugin.readyCannon();
    }

    public void setButtonSprites()
    {
        SpritePixels cannonOverlayUnpressedSprite = ImageUtil.getImageSpritePixels(cannonUnpressed, client);
        client.getSpriteOverrides().put(CANNON_UNPRESSED_SPRITE_ID, cannonOverlayUnpressedSprite);

        SpritePixels cannonOverlayPressedSprite = ImageUtil.getImageSpritePixels(cannonPressed, client);
        client.getSpriteOverrides().put(CANNON_PRESSED_SPRITE_ID, cannonOverlayPressedSprite);

        SpritePixels keyboardUnpressedSprite = ImageUtil.getImageSpritePixels(keyboardUnpressed, client);
        client.getSpriteOverrides().put(KEYBOARD_UNPRESSED_SPRITE_ID, keyboardUnpressedSprite);

        SpritePixels keyboardPressedSprite = ImageUtil.getImageSpritePixels(keyboardPressed, client);
        client.getSpriteOverrides().put(KEYBOARD_PRESSED_SPRITE_ID, keyboardPressedSprite);

        SpritePixels inventoryUnpressedSprite = ImageUtil.getImageSpritePixels(invUnpressed, client);
        client.getSpriteOverrides().put(INVENTORY_UNPRESSED_SPRITE_ID, inventoryUnpressedSprite);

        SpritePixels inventoryPressedSprite = ImageUtil.getImageSpritePixels(invPressed, client);
        client.getSpriteOverrides().put(INVENTORY_PRESSED_SPRITE_ID, inventoryPressedSprite);

        SpritePixels fireSprite = ImageUtil.getImageSpritePixels(fireImage, client);
        client.getSpriteOverrides().put(FIRE_SPRITE_ID, fireSprite);
    }

    public void setupManager(Widget parent)
    {
        setParent(parent);
        setButtonSprites();
        createWidgets();
    }
}
