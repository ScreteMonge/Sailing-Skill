package com.sailingskill;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("example")
public interface SailingConfig extends Config
{
	enum BoatType {
		NORMAL,
		OAK,
		WILLOW,
		MAPLE,
		YEW,
		MAGIC,
		REDWOOD
	}

	@ConfigItem(
			keyName = "disableGuideOverlay",
			name = "Disable Guide Overlay",
			description = "Disables the Guide Overlay",
			position = 1
	)
	default boolean disableGuideOverlay()
	{
		return false;
	}

	@ConfigItem(
			keyName = "windChangeRate",
			name = "Wind Change Rate",
			description = "Number of ticks for wind to change direction",
			position = 2
	)
	default int windChangeRate()
	{
		return 300;
	}

	@ConfigItem(
			keyName = "overlayScale",
			name = "Overlay Scale",
			description = "Multiplies the size of the wind overlay",
			position = 3
	)
	default double overlayScale()
	{
		return 1;
	}

	@ConfigItem(
			keyName = "boatType",
			name = "Boat Type",
			description = "Determines the health and cannon strength of your boat",
			position = 4
	)
	default BoatType boatType()
	{
		return BoatType.NORMAL;
	}

	@ConfigItem(
			keyName = "enableAutoCamera",
			name = "Auto Camera Rotation",
			description = "Crudely rotates the camera to follow the boat's direction (best used with camera zoom extension and Detached Camera plugin",
			position = 5
	)
	default boolean enableAutoCamera()
	{
		return false;
	}

	@ConfigItem(
			keyName = "turnLeftHotkey",
			name = "Turn Left Hotkey",
			description = "The hotkey for turning your boat left if Keyboard Control is toggled on",
			position = 6
	)
	default String turnLeftHotkey()
	{
		return "q";
	}

	@ConfigItem(
			keyName = "turnRightHotkey",
			name = "Turn Right Hotkey",
			description = "The hotkey for turning your boat right if Keyboard Control is toggled on",
			position = 7
	)
	default String turnRightHotkey()
	{
		return "e";
	}

	@ConfigItem(
			keyName = "sailLeftHotkey",
			name = "Sail Left Hotkey",
			description = "The hotkey for turning your sail left if Keyboard Control is toggled on",
			position = 8
	)
	default String sailLeftHotkey()
	{
		return "1";
	}

	@ConfigItem(
			keyName = "sailRightHotkey",
			name = "Sail Right Hotkey",
			description = "The hotkey for turning your sail right if Keyboard Control is toggled on",
			position = 9
	)
	default String sailRightHotkey()
	{
		return "3";
	}
}
