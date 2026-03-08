package com.logitechhaptics;

import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.Menu;
import net.runelite.api.MenuEntry;
import net.runelite.api.Point;

@Slf4j
@Singleton
public class MenuHoverTracker
{
	private static final int MENU_ROW_HEIGHT = 15;
	private static final int MENU_HEADER_HEIGHT = 19;

	private final Client client;
	private int lastHoverIndex = -1;
	private boolean tracking = false;

	@Inject
	public MenuHoverTracker(Client client)
	{
		this.client = client;
	}

	public void startTracking()
	{
		tracking = true;
		lastHoverIndex = -1;
	}

	public void stopTracking()
	{
		tracking = false;
		lastHoverIndex = -1;
	}

	/**
	 * Check if the hovered menu entry has changed.
	 * Must be called on client tick while menu is open.
	 *
	 * @return true if the hovered entry changed since last check
	 */
	public boolean checkHoverChanged()
	{
		if (!tracking || !client.isMenuOpen())
		{
			return false;
		}

		int currentIndex = getHoveredEntryIndex();
		if (currentIndex < 0)
		{
			return false;
		}

		if (currentIndex != lastHoverIndex)
		{
			lastHoverIndex = currentIndex;
			return true;
		}

		return false;
	}

	private int getHoveredEntryIndex()
	{
		Menu menu = client.getMenu();
		MenuEntry[] entries = menu.getMenuEntries();
		if (entries == null || entries.length == 0)
		{
			return -1;
		}

		int menuX = menu.getMenuX();
		int menuY = menu.getMenuY();
		int menuWidth = menu.getMenuWidth();
		int menuHeight = menu.getMenuHeight();

		Point mouse = client.getMouseCanvasPosition();
		int mouseX = mouse.getX();
		int mouseY = mouse.getY();

		// Check if mouse is within menu bounds
		if (mouseX < menuX || mouseX > menuX + menuWidth ||
			mouseY < menuY || mouseY > menuY + menuHeight)
		{
			return -1;
		}

		// Calculate which row the mouse is over
		// Header area is at the top, then entries follow
		int relativeY = mouseY - menuY - MENU_HEADER_HEIGHT;
		if (relativeY < 0)
		{
			return -1;
		}

		int rowIndex = relativeY / MENU_ROW_HEIGHT;

		// Account for scroll offset in scrollable menus
		if (client.isMenuScrollable())
		{
			rowIndex += client.getMenuScroll();
		}

		// The entry array is reversed: last element = top of visual menu
		// So visual row 0 (top) = entries[entries.length - 1]
		int entryIndex = entries.length - 1 - rowIndex;

		if (entryIndex < 0 || entryIndex >= entries.length)
		{
			return -1;
		}

		return entryIndex;
	}
}
