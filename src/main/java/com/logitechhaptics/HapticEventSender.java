package com.logitechhaptics;

import java.util.concurrent.ScheduledExecutorService;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Slf4j
@Singleton
public class HapticEventSender
{
	private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
	private static final int PORT = 8484;

	private final OkHttpClient httpClient;
	private final ScheduledExecutorService executor;

	@Inject
	public HapticEventSender(OkHttpClient httpClient, ScheduledExecutorService executor)
	{
		this.httpClient = httpClient;
		this.executor = executor;
	}

	public void send(HapticEventType eventType, HapticWaveform waveform)
	{
		if (waveform.isOff())
		{
			return;
		}

		String wireEventName = eventType.getEventName() + "." + waveform.getSuffix();

		executor.execute(() ->
		{
			String url = "http://localhost:" + PORT + "/haptic/";
			String json = "{\"event\":\"" + wireEventName + "\"}";

			Request request = new Request.Builder()
				.url(url)
				.post(RequestBody.create(JSON, json))
				.build();

			try (Response response = httpClient.newCall(request).execute())
			{
				if (!response.isSuccessful())
				{
					log.warn("Haptic event {} failed: {}", wireEventName, response.code());
				}
			}
			catch (Exception e)
			{
				log.debug("Haptic send failed (Logi plugin may not be running): {}", e.getMessage());
			}
		});
	}
}
