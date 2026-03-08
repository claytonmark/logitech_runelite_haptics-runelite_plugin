package com.logitechhaptics;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

@ConfigGroup("runeliteHaptics")
public interface RuneLiteHapticsConfig extends Config
{
	@ConfigSection(
		name = "Click Haptics",
		description = "Waveform for click interactions",
		position = 0
	)
	String clickSection = "clickHaptics";

	@ConfigItem(
		keyName = "npcClickWaveform",
		name = "NPC Click",
		description = "Haptic when clicking an NPC",
		section = clickSection,
		position = 0
	)
	default HapticWaveform npcClickWaveform()
	{
		return HapticWaveform.SHARP_STATE_CHANGE;
	}

	@ConfigItem(
		keyName = "objectClickWaveform",
		name = "Object Click",
		description = "Haptic when clicking a game object",
		section = clickSection,
		position = 1
	)
	default HapticWaveform objectClickWaveform()
	{
		return HapticWaveform.SHARP_STATE_CHANGE;
	}

	@ConfigItem(
		keyName = "groundItemClickWaveform",
		name = "Ground Item Click",
		description = "Haptic when clicking a ground item",
		section = clickSection,
		position = 2
	)
	default HapticWaveform groundItemClickWaveform()
	{
		return HapticWaveform.SUBTLE_COLLISION;
	}

	@ConfigItem(
		keyName = "inventoryClickWaveform",
		name = "Inventory Click",
		description = "Haptic when clicking an inventory item",
		section = clickSection,
		position = 3
	)
	default HapticWaveform inventoryClickWaveform()
	{
		return HapticWaveform.DAMP_COLLISION;
	}

	@ConfigItem(
		keyName = "moveClickWaveform",
		name = "Move Click",
		description = "Haptic when clicking to walk/run",
		section = clickSection,
		position = 4
	)
	default HapticWaveform moveClickWaveform()
	{
		return HapticWaveform.OFF;
	}

	// --- Hover Haptics ---

	@ConfigSection(
		name = "Hover Haptics",
		description = "Waveform for hover interactions",
		position = 1
	)
	String hoverSection = "hoverHaptics";

	@ConfigItem(
		keyName = "npcHoverWaveform",
		name = "NPC Hover",
		description = "Haptic when cursor hovers over an NPC",
		section = hoverSection,
		position = 0
	)
	default HapticWaveform npcHoverWaveform()
	{
		return HapticWaveform.SUBTLE_COLLISION;
	}

	@ConfigItem(
		keyName = "objectHoverWaveform",
		name = "Object Hover",
		description = "Haptic when cursor hovers over a game object",
		section = hoverSection,
		position = 1
	)
	default HapticWaveform objectHoverWaveform()
	{
		return HapticWaveform.SUBTLE_COLLISION;
	}

	// --- Menu Haptics ---

	@ConfigSection(
		name = "Menu Haptics",
		description = "Waveform for menu interactions",
		position = 2
	)
	String menuSection = "menuHaptics";

	@ConfigItem(
		keyName = "menuHoverWaveform",
		name = "Menu Hover Detents",
		description = "Haptic click when hovering between menu entries",
		section = menuSection,
		position = 0
	)
	default HapticWaveform menuHoverWaveform()
	{
		return HapticWaveform.DAMP_COLLISION;
	}

	@ConfigItem(
		keyName = "menuOpenWaveform",
		name = "Menu Open",
		description = "Haptic when right-click menu opens",
		section = menuSection,
		position = 1
	)
	default HapticWaveform menuOpenWaveform()
	{
		return HapticWaveform.DAMP_STATE_CHANGE;
	}

	@ConfigItem(
		keyName = "menuSelectWaveform",
		name = "Menu Select",
		description = "Haptic when selecting a menu option",
		section = menuSection,
		position = 2
	)
	default HapticWaveform menuSelectWaveform()
	{
		return HapticWaveform.SHARP_STATE_CHANGE;
	}
}
