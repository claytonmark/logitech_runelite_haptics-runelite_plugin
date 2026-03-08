package com.logitechhaptics;

import java.awt.event.MouseEvent;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.MenuAction;
import net.runelite.api.MenuEntry;
import net.runelite.api.events.ClientTick;
import net.runelite.api.events.MenuOpened;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.widgets.InterfaceID;
import net.runelite.api.widgets.Widget;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.input.MouseAdapter;
import net.runelite.client.input.MouseManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import com.google.inject.Provides;

@Slf4j
@PluginDescriptor(
	name = "Logitech Haptics",
	description = "Haptic mouse feedback for OSRS interactions via Logitech MX Master 4",
	tags = {"haptics", "logitech", "feedback", "mouse"}
)
public class RuneLiteHapticsPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private RuneLiteHapticsConfig config;

	@Inject
	private HapticEventSender hapticSender;

	@Inject
	private MenuHoverTracker menuHoverTracker;

	@Inject
	private MouseManager mouseManager;

	private boolean middleMouseHeld = false;
	private boolean menuWasOpen = false;
	private HapticEventType lastHoverType = null;
	private int lastHoverId = -1;
	private int lastHoverParam0 = -1;
	private int lastHoverParam1 = -1;

	private final MouseAdapter mouseListener = new MouseAdapter()
	{
		@Override
		public MouseEvent mousePressed(MouseEvent e)
		{
			if (e.getButton() == MouseEvent.BUTTON2)
			{
				middleMouseHeld = true;
			}
			return e;
		}

		@Override
		public MouseEvent mouseReleased(MouseEvent e)
		{
			if (e.getButton() == MouseEvent.BUTTON2)
			{
				middleMouseHeld = false;
			}
			return e;
		}
	};

	@Provides
	RuneLiteHapticsConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(RuneLiteHapticsConfig.class);
	}

	@Override
	protected void startUp()
	{
		mouseManager.registerMouseListener(mouseListener);
		log.info("Logitech Haptics started");
	}

	@Override
	protected void shutDown()
	{
		mouseManager.unregisterMouseListener(mouseListener);
		menuHoverTracker.stopTracking();
		middleMouseHeld = false;
		menuWasOpen = false;
		lastHoverType = null;
		lastHoverId = -1;
		lastHoverParam0 = -1;
		lastHoverParam1 = -1;
		log.info("Logitech Haptics stopped");
	}

	@Subscribe
	public void onMenuOpened(MenuOpened event)
	{
		sendHaptic(HapticEventType.MENU_OPEN, config.menuOpenWaveform());
		menuHoverTracker.startTracking();
		menuWasOpen = true;
	}

	@Subscribe
	public void onMenuOptionClicked(MenuOptionClicked event)
	{
		if (menuWasOpen)
		{
			sendHaptic(HapticEventType.MENU_OPTION_SELECT, config.menuSelectWaveform());
		}

		MenuAction action = event.getMenuAction();
		HapticEventType clickType = categorizeClick(action, event);

		if (clickType != null)
		{
			sendHaptic(clickType, getWaveform(clickType));
		}

		if (menuWasOpen)
		{
			menuHoverTracker.stopTracking();
			menuWasOpen = false;
		}
	}

	@Subscribe
	public void onClientTick(ClientTick event)
	{
		if (client.isMenuOpen())
		{
			if (menuHoverTracker.checkHoverChanged())
			{
				sendHaptic(HapticEventType.MENU_ITEM_HOVER, config.menuHoverWaveform());
			}
			return;
		}

		if (menuWasOpen)
		{
			menuHoverTracker.stopTracking();
			menuWasOpen = false;
		}

		checkHoverTarget();
	}

	private void checkHoverTarget()
	{
		MenuEntry[] entries = client.getMenu().getMenuEntries();
		if (entries == null || entries.length == 0)
		{
			lastHoverType = null;
			lastHoverId = -1;
			return;
		}

		// Scan entries for interactive NPC/object options (not Examine-only).
		// Only fire hover haptic for entities with a real action (Bank, Attack, Chop, etc.)
		HapticEventType hoverType = null;
		int hoverId = -1;
		int hoverParam0 = -1;
		int hoverParam1 = -1;

		for (int i = entries.length - 1; i >= 0; i--)
		{
			MenuAction action = entries[i].getType();
			switch (action)
			{
				case NPC_FIRST_OPTION:
				case NPC_SECOND_OPTION:
				case NPC_THIRD_OPTION:
				case NPC_FOURTH_OPTION:
				case NPC_FIFTH_OPTION:
					hoverType = HapticEventType.NPC_HOVER;
					hoverId = entries[i].getIdentifier();
					hoverParam0 = entries[i].getParam0();
					hoverParam1 = entries[i].getParam1();
					break;

				case GAME_OBJECT_FIRST_OPTION:
				case GAME_OBJECT_SECOND_OPTION:
				case GAME_OBJECT_THIRD_OPTION:
				case GAME_OBJECT_FOURTH_OPTION:
				case GAME_OBJECT_FIFTH_OPTION:
					hoverType = HapticEventType.OBJECT_HOVER;
					hoverId = entries[i].getIdentifier();
					hoverParam0 = entries[i].getParam0();
					hoverParam1 = entries[i].getParam1();
					break;

				default:
					continue;
			}
			break;
		}

		if (hoverType == null)
		{
			if (lastHoverType != null)
			{
				lastHoverType = null;
				lastHoverId = -1;
				lastHoverParam0 = -1;
				lastHoverParam1 = -1;
			}
			return;
		}

		if (hoverType == lastHoverType && hoverId == lastHoverId
			&& hoverParam0 == lastHoverParam0 && hoverParam1 == lastHoverParam1)
		{
			return;
		}

		lastHoverType = hoverType;
		lastHoverId = hoverId;
		lastHoverParam0 = hoverParam0;
		lastHoverParam1 = hoverParam1;

		sendHaptic(hoverType, getWaveform(hoverType));
	}

	private HapticEventType categorizeClick(MenuAction action, MenuOptionClicked event)
	{
		switch (action)
		{
			case NPC_FIRST_OPTION:
			case NPC_SECOND_OPTION:
			case NPC_THIRD_OPTION:
			case NPC_FOURTH_OPTION:
			case NPC_FIFTH_OPTION:
				return HapticEventType.NPC_CLICK;

			case GAME_OBJECT_FIRST_OPTION:
			case GAME_OBJECT_SECOND_OPTION:
			case GAME_OBJECT_THIRD_OPTION:
			case GAME_OBJECT_FOURTH_OPTION:
			case GAME_OBJECT_FIFTH_OPTION:
				return HapticEventType.OBJECT_CLICK;

			case GROUND_ITEM_FIRST_OPTION:
			case GROUND_ITEM_SECOND_OPTION:
			case GROUND_ITEM_THIRD_OPTION:
			case GROUND_ITEM_FOURTH_OPTION:
			case GROUND_ITEM_FIFTH_OPTION:
				return HapticEventType.GROUND_ITEM_CLICK;

			case WIDGET_FIRST_OPTION:
			case WIDGET_SECOND_OPTION:
			case WIDGET_THIRD_OPTION:
			case WIDGET_FOURTH_OPTION:
			case WIDGET_FIFTH_OPTION:
			case CC_OP:
			case CC_OP_LOW_PRIORITY:
				if (event.isItemOp())
				{
					return HapticEventType.INVENTORY_CLICK;
				}
				return null;

			case WALK:
				return HapticEventType.MOVE_CLICK;

			default:
				return null;
		}
	}

	private HapticWaveform getWaveform(HapticEventType type)
	{
		switch (type)
		{
			case NPC_CLICK:
				return config.npcClickWaveform();
			case OBJECT_CLICK:
				return config.objectClickWaveform();
			case GROUND_ITEM_CLICK:
				return config.groundItemClickWaveform();
			case INVENTORY_CLICK:
				return config.inventoryClickWaveform();
			case MOVE_CLICK:
				return config.moveClickWaveform();
			case NPC_HOVER:
				return config.npcHoverWaveform();
			case OBJECT_HOVER:
				return config.objectHoverWaveform();
			case MENU_ITEM_HOVER:
				return config.menuHoverWaveform();
			case MENU_OPEN:
				return config.menuOpenWaveform();
			case MENU_OPTION_SELECT:
				return config.menuSelectWaveform();
			default:
				return HapticWaveform.OFF;
		}
	}

	private boolean shouldSuppressHaptics()
	{
		if (middleMouseHeld)
		{
			return true;
		}
		Widget bank = client.getWidget(InterfaceID.BANK, 0);
		return bank != null && !bank.isHidden();
	}

	private void sendHaptic(HapticEventType type, HapticWaveform waveform)
	{
		if (shouldSuppressHaptics())
		{
			return;
		}
		hapticSender.send(type, waveform);
	}
}
