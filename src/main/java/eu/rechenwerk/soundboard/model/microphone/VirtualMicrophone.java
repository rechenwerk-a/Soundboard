package eu.rechenwerk.soundboard.model.microphone;

import eu.rechenwerk.soundboard.SoundBoard;
import eu.rechenwerk.soundboard.model.sounds.Sound;
import eu.rechenwerk.soundboard.model.terminal.Terminal;

public final class VirtualMicrophone {
	public static String INPUT = "-INPUT";
	public static String SINK = "-SINK";
	private final String name;
	private final String device;
	private final Terminal terminal;

	private int volume;

	private Process runningSoundProcess;

	private VirtualMicrophone(String name, String other) {
		this.name = name;
		this.device = other;
		volume = 100;
		terminal = SoundBoard.TERMINAL;
		terminal.addMicrophone(this, other);
	}

	public static VirtualMicrophone create(String name, String other) {
		return new VirtualMicrophone(name, other);
	}

	public static VirtualMicrophone create(String name, String other, int volume) {
		VirtualMicrophone mic = new VirtualMicrophone(name, other);
		mic.setVolume(volume);
		return mic;
	}

	public void play(Sound audio) {
		runningSoundProcess = terminal.playSound(this, audio);
		runningSoundProcess.onExit().thenAccept(p -> {
			p.destroy();
			runningSoundProcess = null;
		});
	}

	public void stopSound() {
		if(runningSoundProcess != null && runningSoundProcess.isAlive()) {
			runningSoundProcess.destroy();
			runningSoundProcess = null;
		}
	}

	public void setVolume(int volume) {
		if(volume < 0 || volume > 100) return;
		this.volume = volume;
		terminal.setVolume(volume, this);
	}

	public void delete() {
		stopSound();
		terminal.removeMicrophone(this);
	}

	public String getName() {
		return name;
	}

	public String getSinkName() {
		return name + SINK;
	}

	public String getInputName() {
		return name + INPUT;
	}

	public String getDevice() {
		return device;
	}

	public int getVolume() {
		return volume;
	}
}