package com.sailingskill.overlays;

import com.google.inject.Provides;
import com.sailingskill.SailingConfig;
import com.sailingskill.SailingPlugin;
import net.runelite.api.Client;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.TitleComponent;

import javax.inject.Inject;
import java.awt.*;

public class GuideOverlay extends OverlayPanel
{
    private final Client client;
    private final SailingPlugin plugin;
    private final SailingConfig config;

    @Inject
    private GuideOverlay(Client client, SailingPlugin plugin, SailingConfig config)
    {
        super(plugin);
        setPosition(OverlayPosition.ABOVE_CHATBOX_RIGHT);
        this.config = config;
        this.client = client;
        this.plugin = plugin;
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        if (config.disableGuideOverlay())
        {
            return null;
        }

        String message = "Right click over water and select 'Build boat' to build your boat.";
        String message2 = "Make sure you're standing in front of a water tile!";
        String message3 = "Look at the config to toggle keyboard controls. By default, controls are:";
        String message4 = config.turnLeftHotkey().charAt(0) + " = turn Boat left; " + config.turnRightHotkey().charAt(0) + " = turn Boat right.";
        String message5 = config.sailLeftHotkey().charAt(0) + " = turn Sail left; " + config.sailRightHotkey().charAt(0) + " = turn Sail right.";
        String message6 = "Shift = toggle Anchor; Space = fire cannon.";
        String message7 = "Use Detached Camera plugin to zoom out further.";
        String message8 = "";
        String message9 = "Sailing Trials: southwest Lumbridge Swamp and near the Lighthouse";
        String message10 = "Offshore fishing: Catherby, Mudskipper Point, or south of Kourend Woodland";
        String message11 = "Try the Auto Camera Rotation feature in the config menu";

        panelComponent.getChildren().add(TitleComponent.builder().text(message).color(Color.WHITE).build());
        panelComponent.getChildren().add(TitleComponent.builder().text(message2).color(Color.WHITE).build());
        panelComponent.getChildren().add(TitleComponent.builder().text(message3).color(Color.YELLOW).build());
        panelComponent.getChildren().add(TitleComponent.builder().text(message4).color(Color.YELLOW).build());
        panelComponent.getChildren().add(TitleComponent.builder().text(message5).color(Color.YELLOW).build());
        panelComponent.getChildren().add(TitleComponent.builder().text(message6).color(Color.YELLOW).build());
        panelComponent.getChildren().add(TitleComponent.builder().text(message7).color(Color.CYAN).build());
        panelComponent.getChildren().add(TitleComponent.builder().text(message8).color(Color.WHITE).build());
        panelComponent.getChildren().add(TitleComponent.builder().text(message9).color(Color.WHITE).build());
        panelComponent.getChildren().add(TitleComponent.builder().text(message10).color(Color.WHITE).build());
        panelComponent.getChildren().add(TitleComponent.builder().text(message11).color(Color.WHITE).build());

        panelComponent.setPreferredSize(new Dimension(
                graphics.getFontMetrics().stringWidth(message10) + 15,
                0));

        return super.render(graphics);
    }
}