module eu.rechenwerk.soundboard.soundboard {
	requires javafx.fxml;
	requires javafx.web;
	requires org.json;
	requires java.desktop;

	exports eu.rechenwerk.soundboard;
	exports eu.rechenwerk.soundboard.model.microphone;
	exports eu.rechenwerk.soundboard.model.sounds;
	exports eu.rechenwerk.soundboard.controller;
	exports eu.rechenwerk.soundboard.records;
	exports eu.rechenwerk.framework;
	exports eu.rechenwerk.soundboard.model.terminal;

	opens eu.rechenwerk.soundboard;
	opens eu.rechenwerk.soundboard.controller;
	opens eu.rechenwerk.framework;
}