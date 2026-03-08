package com.logitechhaptics;

public enum HapticEventType
{
	MENU_ITEM_HOVER("menuItemHover"),
	MENU_OPEN("menuOpen"),
	MENU_OPTION_SELECT("menuOptionSelect"),
	NPC_CLICK("npcClick"),
	OBJECT_CLICK("objectClick"),
	GROUND_ITEM_CLICK("groundItemClick"),
	INVENTORY_CLICK("inventoryClick"),
	MOVE_CLICK("moveClick"),
	NPC_HOVER("npcHover"),
	OBJECT_HOVER("objectHover");

	private final String eventName;

	HapticEventType(String eventName)
	{
		this.eventName = eventName;
	}

	public String getEventName()
	{
		return eventName;
	}
}
