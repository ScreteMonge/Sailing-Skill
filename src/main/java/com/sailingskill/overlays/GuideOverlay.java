package com.sailingskill.overlays;

import com.sailingskill.SailingConfig;
import com.sailingskill.SailingPlugin;
import net.runelite.api.Client;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.TitleComponent;

import javax.inject.Inject;
import java.awt.*;

public class GuideOverlay extends OverlayPanel
{
    private final SailingConfig config;

    @Inject
    private GuideOverlay(SailingPlugin plugin, SailingConfig config)
    {
        super(plugin);
        setPosition(OverlayPosition.ABOVE_CHATBOX_RIGHT);
        this.config = config;
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        if (config.disableGuideOverlay())
        {
            return null;
        }

        String message = "Right click over water and select 'Build boat' to build your boat";
        String message2 = "Make sure you're standing in front of a water tile";
        String message16 = "";
        String message3 = "Keyboard controls can be changed in the config";
        String message15 = "If toggled on, they are currently:";
        String message4 = config.turnLeftHotkey().charAt(0) + " = turn Boat left; " + config.turnRightHotkey().charAt(0) + " = turn Boat right";
        String message5 = config.sailIncreaseHotkey().charAt(0) + " = increase Sail speed; " + config.sailDecreaseHotkey().charAt(0) + " = decrease Sail speed";
        String message6 = "Space = use currently selected tool (cannon, fishing rod, etc)";
        String message7 = "Use the Detached Camera external plugin to zoom out further";
        String message8 = "";
        String message9 = "Sailing trials: southwest Lumbridge Swamp and near the Lighthouse";
        String message10 = "Offshore fishing: Catherby, Mudskipper Point, or south of Kourend Woodland";
        String message11 = "NPC spawns: Catherby/Kourend Woodland, Brimhaven, Rimmington";
        String message12 = "Do not go to Corsair Cove";
        String message13 = "";
        String message14 = "You can toggle this menu off in the config";

        panelComponent.getChildren().add(TitleComponent.builder().text(message).color(Color.WHITE).build());
        panelComponent.getChildren().add(TitleComponent.builder().text(message2).color(Color.WHITE).build());
        panelComponent.getChildren().add(TitleComponent.builder().text(message16).color(Color.WHITE).build());
        panelComponent.getChildren().add(TitleComponent.builder().text(message3).color(Color.YELLOW).build());
        panelComponent.getChildren().add(TitleComponent.builder().text(message15).color(Color.YELLOW).build());
        panelComponent.getChildren().add(TitleComponent.builder().text(message4).color(Color.YELLOW).build());
        panelComponent.getChildren().add(TitleComponent.builder().text(message5).color(Color.YELLOW).build());
        panelComponent.getChildren().add(TitleComponent.builder().text(message6).color(Color.YELLOW).build());
        panelComponent.getChildren().add(TitleComponent.builder().text(message7).color(Color.YELLOW).build());
        panelComponent.getChildren().add(TitleComponent.builder().text(message8).color(Color.WHITE).build());
        panelComponent.getChildren().add(TitleComponent.builder().text(message9).color(Color.WHITE).build());
        panelComponent.getChildren().add(TitleComponent.builder().text(message10).color(Color.WHITE).build());
        panelComponent.getChildren().add(TitleComponent.builder().text(message11).color(Color.WHITE).build());
        panelComponent.getChildren().add(TitleComponent.builder().text(message12).color(Color.RED).build());
        panelComponent.getChildren().add(TitleComponent.builder().text(message13).color(Color.ORANGE).build());
        panelComponent.getChildren().add(TitleComponent.builder().text(message14).color(Color.CYAN).build());

        panelComponent.setPreferredSize(new Dimension(
                graphics.getFontMetrics().stringWidth(message10) + 15,
                0));

        return super.render(graphics);
    }
}