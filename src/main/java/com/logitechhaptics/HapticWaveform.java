package com.logitechhaptics;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum HapticWaveform
{
	OFF("Off", null),
	SUBTLE_COLLISION("Subtle Collision", "subtle_collision"),
	DAMP_COLLISION("Damp Collision", "damp_collision"),
	DAMP_STATE_CHANGE("Damp State Change", "damp_state_change"),
	SHARP_COLLISION("Sharp Collision", "sharp_collision"),
	SHARP_STATE_CHANGE("Sharp State Change", "sharp_state_change"),
	KNOCK("Knock", "knock");

	private final String displayName;
	private final String suffix;

	@Override
	public String toString()
	{
		return displayName;
	}

	public boolean isOff()
	{
		return this == OFF;
	}
}
