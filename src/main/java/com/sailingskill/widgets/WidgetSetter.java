package com.sailingskill.widgets;

import com.sailingskill.SailingPlugin;
import net.runelite.api.Client;
import net.runelite.api.widgets.Widget;

import javax.inject.Inject;

public class WidgetSetter
{
    @Inject
    private BoatWidgetManager boatWidgetManager;

    @Inject
    private MiscWidgetManager miscWidgetManager;

    @Inject
    private ToolsWidgetManager toolsWidgetManager;

    @Inject
    private TabIconManager tabIconManager;

    @Inject
    private Client client;

    @Inject
    private SailingPlugin plugin;

    private final int CLASSIC_COMBAT_MENU = 35913807;
    private final int MODERN_COMBAT_MENU = 10747976;
    private final int CLASSIC_RESIZED_COMBAT_MENU = 10551371;

    public void setupUnknownTab()
    {
        Widget widget = client.getWidget(CLASSIC_COMBAT_MENU);
        if (widget != null)
        {
            setupFixedTab();
            return;
        }

        widget = client.getWidget(CLASSIC_RESIZED_COMBAT_MENU);
        if (widget != null)
        {
            setupResizableTab();
            return;
        }

        widget = client.getWidget(MODERN_COMBAT_MENU);
        if (widget != null)
        {
            setupModernTab();
        }
    }

    public void setupFixedTab()
    {
        Widget widget = client.getWidget(CLASSIC_COMBAT_MENU);
        if (widget == null)
        {
            return;
        }

        setupGeneralTab(widget);
        tabIconManager.setFixedSailingTab();
    }

    public void setupResizableTab()
    {
        Widget widget = client.getWidget(CLASSIC_RESIZED_COMBAT_MENU);
        if (widget == null)
        {
            return;
        }

        setupGeneralTab(widget);
        tabIconManager.setResizableSailingTab();
    }

    public void setupModernTab()
    {
        Widget widget = client.getWidget(MODERN_COMBAT_MENU);
        if (widget == null)
        {
            return;
        }

        setupGeneralTab(widget);
        tabIconManager.setModernSailingTab();
    }

    public void setupGeneralTab(Widget widget)
    {
        Widget[] children = widget.getNestedChildren();
        if (children != null)
        {
            for (int i = 0; i < children.length; i++)
            {
                Widget child = children[i];
                child.setHidden(i == 0);
            }
        }

        boatWidgetManager.setupManager(widget);
        miscWidgetManager.setupManager(widget);
        toolsWidgetManager.setupManager(widget);
        tabIconManager.setAnchorSpriteOverride();
    }

    public void unsetFixedTab()
    {
        Widget widget = client.getWidget(CLASSIC_COMBAT_MENU);
        if (widget == null)
        {
            return;
        }

        unsetTab(widget);
        tabIconManager.unsetFixedSailingTab();
    }

    public void unsetResizableTab()
    {
        Widget widget = client.getWidget(CLASSIC_RESIZED_COMBAT_MENU);
        if (widget == null)
        {
            return;
        }

        unsetTab(widget);
        tabIconManager.unsetResizableSailingTab();
    }

    public void unsetModernTab()
    {
        Widget widget = client.getWidget(MODERN_COMBAT_MENU);
        if (widget == null)
        {
            return;
        }

        unsetTab(widget);
        tabIconManager.unsetModernSailingTab();
    }

    public void unsetLastTab()
    {
        Widget widget;
        switch(plugin.getCurrentGameClientLayout())
        {
            case CLASSIC:
                widget = client.getWidget(CLASSIC_COMBAT_MENU);
                if (widget != null)
                {
                    unsetFixedTab();
                }
                return;
            case RESIZED:
                widget = client.getWidget(CLASSIC_RESIZED_COMBAT_MENU);
                if (widget != null)
                {
                    unsetResizableTab();
                }
                return;
            case MODERN:
                widget = client.getWidget(MODERN_COMBAT_MENU);
                if (widget != null)
                {
                    unsetModernTab();
                }
        }
    }

    public void unsetTab(Widget widget)
    {
        Widget[] children = widget.getNestedChildren();
        for (int i = 0; i < children.length; i++)
        {
            Widget child = children[i];
            if (i == 0)
            {
                child.setHidden(false);
            }
        }

        Widget[] dynamicChildren = widget.getDynamicChildren();
        if (dynamicChildren != null)
        {
            for (Widget child : dynamicChildren)
            {
                child.setHidden(true);
            }
        }
    }
}
