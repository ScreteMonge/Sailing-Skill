package com.sailingskill.widgets;

import com.sailingskill.BoatTool;
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

public class ToolsWidgetManager
{
    @Inject
    private Client client;

    @Inject
    private SailingPlugin plugin;

    private final BufferedImage borderImage = ImageUtil.loadImageResource(getClass(), "/Buttons_Border.png");
    private final BufferedImage fireCannonImage = ImageUtil.loadImageResource(getClass(), "/Button_Cannon.png");
    private final BufferedImage rodImage = ImageUtil.loadImageResource(getClass(), "/Button_Fish.png");
    private final BufferedImage harpoonImage = ImageUtil.loadImageResource(getClass(), "/Button_Harpoon.png");
    private final BufferedImage coinsImage = ImageUtil.loadImageResource(getClass(), "/Button_Plunder.png");
    private final BufferedImage spyglassImage = ImageUtil.loadImageResource(getClass(), "/Button_Spyglass.png");
    private final BufferedImage netImage = ImageUtil.loadImageResource(getClass(), "/Button_Net.png");

    private final BufferedImage cannonballUnpressed = ImageUtil.loadImageResource(getClass(), "/Cannonball_UnPressed.png");
    private final BufferedImage cannonballPressed = ImageUtil.loadImageResource(getClass(), "/Cannonball_Pressed.png");
    private final BufferedImage harpoonUnpressed = ImageUtil.loadImageResource(getClass(), "/Harpoon_UnPressed.png");
    private final BufferedImage harpoonPressed = ImageUtil.loadImageResource(getClass(), "/Harpoon_Pressed.png");
    private final BufferedImage rodUnpressed = ImageUtil.loadImageResource(getClass(), "/Rod_UnPressed.png");
    private final BufferedImage rodPressed = ImageUtil.loadImageResource(getClass(), "/Rod_Pressed.png");
    private final BufferedImage netUnpressed = ImageUtil.loadImageResource(getClass(), "/Net_UnPressed.png");
    private final BufferedImage netPressed = ImageUtil.loadImageResource(getClass(), "/Net_Pressed.png");
    private final BufferedImage scopeUnpressed = ImageUtil.loadImageResource(getClass(), "/Scope_UnPressed.png");
    private final BufferedImage scopePressed = ImageUtil.loadImageResource(getClass(), "/Scope_Pressed.png");
    private final BufferedImage coinsUnpressed = ImageUtil.loadImageResource(getClass(), "/Coins_UnPressed.png");
    private final BufferedImage coinsPressed = ImageUtil.loadImageResource(getClass(), "/Coins_Pressed.png");

    private Widget parent;
    private Widget borderWidget;
    private Widget fireWidget;
    private Widget cannonballWidget;
    private Widget rodWidget;
    private Widget harpoonWidget;
    private Widget netWidget;
    private Widget spyglassWidget;
    private Widget coinsWidget;

    private final int BORDER_SPRITE_ID = SpriteID.EMOTE_PENGUIN_FLAP;
    private final int BUTTON_CANNON_ID = SpriteID.EMOTE_PENGUIN_PREEN;
    private final int BUTTON_FISH_ID = SpriteID.EMOTE_PREMIER_SHIELD_LOCKED;
    private final int BUTTON_HARPOON_ID = SpriteID.EMOTE_PENGUIN_SPIN;
    private final int BUTTON_NET_ID = SpriteID.EMOTE_PENGUIN_WAVE;
    private final int BUTTON_PLUNDER_ID = SpriteID.CYRISUS_CHEST;
    private final int BUTTON_SPYGLASS_ID = SpriteID.PAYPAL_DONATE_BUTTON;
    private final int CANNONBALL_UNPRESSED_SPRITE_ID = SpriteID.DEADMAN_EXCLAMATION_MARK_SKULLED_WARNING;
    private final int CANNONBALL_PRESSED_SPRITE_ID = SpriteID.DEADMAN_TAB_ITEMS_LOST_ON_DEATH_WHILE_SKULLED_IN_SAFE_ZONE;
    private final int HARPOON_UNPRESSED_SPRITE_ID = SpriteID.DEADMAN_BANK_KEYS_1;
    private final int HARPOON_PRESSED_SPRITE_ID = SpriteID.DEADMAN_BANK_KEYS_2;
    private final int ROD_UNPRESSED_SPRITE_ID = SpriteID.DEADMAN_BANK_KEYS_3;
    private final int ROD_PRESSED_SPRITE_ID = SpriteID.DEADMAN_BANK_KEYS_4;
    private final int NET_UNPRESSED_SPRITE_ID = SpriteID.DEADMAN_BANK_KEYS_5;
    private final int NET_PRESSED_SPRITE_ID = SpriteID.DONATEGAMES_PROMO_BANNER;
    private final int SPYGLASS_UNPRESSED_SPRITE_ID = SpriteID.GAMEBLAST15_PROMO_BANNER;
    private final int SPYGLASS_PRESSED_SPRITE_ID = SpriteID.EMOTE_PENGUIN_BOW;
    private final int COINS_UNPRESSED_SPRITE_ID = SpriteID.EMOTE_PENGUIN_CHEER;
    private final int COINS_PRESSED_SPRITE_ID = SpriteID.EMOTE_PENGUIN_CLAP;

    private final int HOVERED_OPACITY = 125;
    private final int BASE_Y = 45;

    public void setParent(Widget parentWidget)
    {
        parent = parentWidget;
    }

    public void createWidgets()
    {
        borderWidget = parent.createChild(10, WidgetType.GRAPHIC);
        borderWidget.setPos(0, BASE_Y - 15, 1, 1);
        borderWidget.setSize(160, 68);
        borderWidget.setOriginalWidth(160);
        borderWidget.setOriginalHeight(68);
        borderWidget.setSpriteId(BORDER_SPRITE_ID);
        borderWidget.revalidate();
        borderWidget.setHasListener(false);

        fireWidget = parent.createChild(11, WidgetType.GRAPHIC);
        fireWidget.setPos(0, BASE_Y - 29, 1, 1);
        fireWidget.setSize(150, 27);
        fireWidget.setOriginalWidth(150);
        fireWidget.setOriginalHeight(27);
        fireWidget.setSpriteId(BUTTON_CANNON_ID);
        fireWidget.revalidate();
        fireWidget.setHasListener(true);
        fireWidget.setOnOpListener((JavaScriptCallback) this::fireOpEvent);
        fireWidget.setOnMouseOverListener((JavaScriptCallback) this::fireHoverEvent);
        fireWidget.setOnMouseLeaveListener((JavaScriptCallback) this::fireLeaveEvent);
        fireWidget.setAction(0, "Fire cannon");

        cannonballWidget = parent.createChild(12, WidgetType.GRAPHIC);
        cannonballWidget.setPos(-62, BASE_Y, 1, 1);
        cannonballWidget.setSize(23, 23);
        cannonballWidget.setOriginalWidth(23);
        cannonballWidget.setOriginalHeight(23);
        cannonballWidget.setSpriteId(CANNONBALL_PRESSED_SPRITE_ID);
        cannonballWidget.revalidate();
        cannonballWidget.setHasListener(true);
        cannonballWidget.setOnOpListener((JavaScriptCallback) this::onCannonballPressed);
        cannonballWidget.setAction(0, "Switch-to cannon");

        rodWidget = parent.createChild(13, WidgetType.GRAPHIC);
        rodWidget.setPos(-37, BASE_Y, 1, 1);
        rodWidget.setSize(23, 23);
        rodWidget.setOriginalWidth(23);
        rodWidget.setOriginalHeight(23);
        rodWidget.setSpriteId(ROD_UNPRESSED_SPRITE_ID);
        rodWidget.revalidate();
        rodWidget.setHasListener(true);
        rodWidget.setOnOpListener((JavaScriptCallback) this::onRodPressed);
        rodWidget.setAction(0, "Switch-to rod");

        harpoonWidget = parent.createChild(14, WidgetType.GRAPHIC);
        harpoonWidget.setPos(-12, BASE_Y, 1, 1);
        harpoonWidget.setSize(23, 23);
        harpoonWidget.setOriginalWidth(23);
        harpoonWidget.setOriginalHeight(23);
        harpoonWidget.setSpriteId(HARPOON_UNPRESSED_SPRITE_ID);
        harpoonWidget.revalidate();
        harpoonWidget.setHasListener(true);
        harpoonWidget.setOnOpListener((JavaScriptCallback) this::onHarpoonPressed);
        harpoonWidget.setAction(0, "Switch-to harpoon");

        netWidget = parent.createChild(15, WidgetType.GRAPHIC);
        netWidget.setPos(12, BASE_Y, 1, 1);
        netWidget.setSize(23, 23);
        netWidget.setOriginalWidth(23);
        netWidget.setOriginalHeight(23);
        netWidget.setSpriteId(NET_UNPRESSED_SPRITE_ID);
        netWidget.revalidate();
        netWidget.setHasListener(true);
        netWidget.setOnOpListener((JavaScriptCallback) this::onNetPressed);
        netWidget.setAction(0, "Switch-to net");

        spyglassWidget = parent.createChild(16, WidgetType.GRAPHIC);
        spyglassWidget.setPos(37, BASE_Y, 1, 1);
        spyglassWidget.setSize(23, 23);
        spyglassWidget.setOriginalWidth(23);
        spyglassWidget.setOriginalHeight(23);
        spyglassWidget.setSpriteId(SPYGLASS_UNPRESSED_SPRITE_ID);
        spyglassWidget.revalidate();
        spyglassWidget.setHasListener(true);
        spyglassWidget.setOnOpListener((JavaScriptCallback) this::onSpyglassPressed);
        spyglassWidget.setAction(0, "Switch-to spyglass");

        coinsWidget = parent.createChild(17, WidgetType.GRAPHIC);
        coinsWidget.setPos(62, BASE_Y, 1, 1);
        coinsWidget.setSize(23, 23);
        coinsWidget.setOriginalWidth(23);
        coinsWidget.setOriginalHeight(23);
        coinsWidget.setSpriteId(COINS_UNPRESSED_SPRITE_ID);
        coinsWidget.revalidate();
        coinsWidget.setHasListener(true);
        coinsWidget.setOnOpListener((JavaScriptCallback) this::onCoinsPressed);
        coinsWidget.setAction(0, "Switch-to plunder");
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
        plugin.useCurrentTool();
    }

    public void onCannonballPressed(ScriptEvent e)
    {
        swapToolIcons(cannonballWidget, CANNONBALL_PRESSED_SPRITE_ID);
        swapFireButton(BUTTON_CANNON_ID);
        plugin.setCurrentBoatTool(BoatTool.CANNON);
    }

    public void onRodPressed(ScriptEvent e)
    {
        swapToolIcons(rodWidget, ROD_PRESSED_SPRITE_ID);
        swapFireButton(BUTTON_FISH_ID);
        plugin.setCurrentBoatTool(BoatTool.FISHING_ROD);
    }

    public void onHarpoonPressed(ScriptEvent e)
    {
        swapToolIcons(harpoonWidget, HARPOON_PRESSED_SPRITE_ID);
        swapFireButton(BUTTON_HARPOON_ID);
        plugin.setCurrentBoatTool(BoatTool.HARPOON);
    }

    public void onNetPressed(ScriptEvent e)
    {
        swapToolIcons(netWidget, NET_PRESSED_SPRITE_ID);
        swapFireButton(BUTTON_NET_ID);
        plugin.setCurrentBoatTool(BoatTool.NET);
    }

    public void onSpyglassPressed(ScriptEvent e)
    {
        swapToolIcons(spyglassWidget, SPYGLASS_PRESSED_SPRITE_ID);
        swapFireButton(BUTTON_SPYGLASS_ID);
        plugin.setCurrentBoatTool(BoatTool.SPYGLASS);
    }

    public void onCoinsPressed(ScriptEvent e)
    {
        swapToolIcons(coinsWidget, COINS_PRESSED_SPRITE_ID);
        swapFireButton(BUTTON_PLUNDER_ID);
        plugin.setCurrentBoatTool(BoatTool.PLUNDER);
    }

    public void swapToolIcons(Widget updatedWidget, int nextSpriteID)
    {
        cannonballWidget.setSpriteId(CANNONBALL_UNPRESSED_SPRITE_ID);
        rodWidget.setSpriteId(ROD_UNPRESSED_SPRITE_ID);
        harpoonWidget.setSpriteId(HARPOON_UNPRESSED_SPRITE_ID);
        netWidget.setSpriteId(NET_UNPRESSED_SPRITE_ID);
        spyglassWidget.setSpriteId(SPYGLASS_UNPRESSED_SPRITE_ID);
        coinsWidget.setSpriteId(COINS_UNPRESSED_SPRITE_ID);
        updatedWidget.setSpriteId(nextSpriteID);
    }

    public void swapFireButton(int nextFireButtonSprite)
    {
        fireWidget.setSpriteId(nextFireButtonSprite);
    }

    public void setSailSprites()
    {
        SpritePixels sp0 = ImageUtil.getImageSpritePixels(borderImage, client);
        client.getSpriteOverrides().put(BORDER_SPRITE_ID, sp0);

        SpritePixels sp1 = ImageUtil.getImageSpritePixels(cannonballUnpressed, client);
        client.getSpriteOverrides().put(CANNONBALL_UNPRESSED_SPRITE_ID, sp1);

        SpritePixels sp2 = ImageUtil.getImageSpritePixels(cannonballPressed, client);
        client.getSpriteOverrides().put(CANNONBALL_PRESSED_SPRITE_ID, sp2);

        SpritePixels sp3 = ImageUtil.getImageSpritePixels(rodUnpressed, client);
        client.getSpriteOverrides().put(ROD_UNPRESSED_SPRITE_ID, sp3);

        SpritePixels sp4 = ImageUtil.getImageSpritePixels(rodPressed, client);
        client.getSpriteOverrides().put(ROD_PRESSED_SPRITE_ID, sp4);

        SpritePixels sp5 = ImageUtil.getImageSpritePixels(harpoonUnpressed, client);
        client.getSpriteOverrides().put(HARPOON_UNPRESSED_SPRITE_ID, sp5);

        SpritePixels sp6 = ImageUtil.getImageSpritePixels(harpoonPressed, client);
        client.getSpriteOverrides().put(HARPOON_PRESSED_SPRITE_ID, sp6);

        SpritePixels sp7 = ImageUtil.getImageSpritePixels(netUnpressed, client);
        client.getSpriteOverrides().put(NET_UNPRESSED_SPRITE_ID, sp7);

        SpritePixels sp8 = ImageUtil.getImageSpritePixels(netPressed, client);
        client.getSpriteOverrides().put(NET_PRESSED_SPRITE_ID, sp8);

        SpritePixels sp9 = ImageUtil.getImageSpritePixels(scopeUnpressed, client);
        client.getSpriteOverrides().put(SPYGLASS_UNPRESSED_SPRITE_ID, sp9);

        SpritePixels sp10 = ImageUtil.getImageSpritePixels(scopePressed, client);
        client.getSpriteOverrides().put(SPYGLASS_PRESSED_SPRITE_ID, sp10);

        SpritePixels sp11 = ImageUtil.getImageSpritePixels(coinsUnpressed, client);
        client.getSpriteOverrides().put(COINS_UNPRESSED_SPRITE_ID, sp11);

        SpritePixels sp12 = ImageUtil.getImageSpritePixels(coinsPressed, client);
        client.getSpriteOverrides().put(COINS_PRESSED_SPRITE_ID, sp12);

        SpritePixels sp13 = ImageUtil.getImageSpritePixels(fireCannonImage, client);
        client.getSpriteOverrides().put(BUTTON_CANNON_ID, sp13);

        SpritePixels sp14 = ImageUtil.getImageSpritePixels(rodImage, client);
        client.getSpriteOverrides().put(BUTTON_FISH_ID, sp14);

        SpritePixels sp15 = ImageUtil.getImageSpritePixels(harpoonImage, client);
        client.getSpriteOverrides().put(BUTTON_HARPOON_ID, sp15);

        SpritePixels sp16 = ImageUtil.getImageSpritePixels(netImage, client);
        client.getSpriteOverrides().put(BUTTON_NET_ID, sp16);

        SpritePixels sp17 = ImageUtil.getImageSpritePixels(coinsImage, client);
        client.getSpriteOverrides().put(BUTTON_PLUNDER_ID, sp17);

        SpritePixels sp18 = ImageUtil.getImageSpritePixels(spyglassImage, client);
        client.getSpriteOverrides().put(BUTTON_SPYGLASS_ID, sp18);
    }

    public void setupManager(Widget parent)
    {
        setParent(parent);
        setSailSprites();
        createWidgets();
    }
}