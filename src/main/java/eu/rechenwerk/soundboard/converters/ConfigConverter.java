package eu.rechenwerk.soundboard.converters;

import eu.rechenwerk.soundboard.model.microphone.VirtualMicrophone;
import eu.rechenwerk.soundboard.records.Config;
import org.json.JSONObject;

import java.util.List;

public class ConfigConverter extends JSONConverter<Config>{
	private final static String MICROPHONES = "microphones";
	private final static String SELECTED = "selected";
	private final static String COLORS = "colors";

	@Override
	public String serialize(Config obj) {
		return
			startObject() +
				putArray(MICROPHONES, obj.microphones(), new VirtualMicrophoneConverter()) + comma() +
				putString(SELECTED, obj.selected() != null ? obj.selected().getName() : "") + comma() +
				putArray(COLORS, obj.colors(), new ColorConverter()) +
			endObject();
	}

	@Override
	public Config deserialize(String json) {
		JSONObject jsonObject = new JSONObject(json);
		List<VirtualMicrophone> virtualMicrophones = getArray(jsonObject.getJSONArray(MICROPHONES).toString(), new VirtualMicrophoneConverter());
		return new Config(
			virtualMicrophones,
			virtualMicrophones
				.stream()
				.filter(it -> it
					.getName()
					.equals(jsonObject.getString(SELECTED)))
				.findFirst()
				.orElse(null), getArray(jsonObject
				.getJSONArray(COLORS)
				.toString(),
			new ColorConverter())
		);
	}
}
