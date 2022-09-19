package eu.rechenwerk.soundboard.controller;

import eu.rechenwerk.soundboard.converters.JSONConverter;
import eu.rechenwerk.soundboard.model.Config;
import eu.rechenwerk.soundboard.model.exceptions.OsNotSupportedException;
import eu.rechenwerk.soundboard.model.microphone.Terminal;
import eu.rechenwerk.soundboard.model.microphone.VirtualMicrophone;
import eu.rechenwerk.soundboard.view.MicrophoneCell;
import eu.rechenwerk.soundboard.view.SoundPane;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import static eu.rechenwerk.soundboard.framework.IO.*;

import java.io.*;
import java.util.List;

public class SoundBoardController {
	public final static String CONFIG_FILE = "config.json";

	private String sounds;
	@FXML private TextField soundNameField;
	@FXML private TextField soundImageField;
	@FXML private TextField soundFileField;
	@FXML private TextField virtualMicrophoneNameField;
	@FXML private ComboBox<String> inputDevicesComboBox;
	@FXML private ListView<MicrophoneCell> microphoneListView;
	@FXML private GridPane soundGridPane;

	public void init(Stage stage) throws FileNotFoundException {
		Config config = JSONConverter.CONFIG.deserialize(readResource(CONFIG_FILE));
		sounds = config.sounds();
		microphoneListView
			.getItems()
			.addAll(
				config
					.microphones()
					.stream()
					.map(it ->
						new MicrophoneCell(it, true)
					).toList()
			);
		for (int row = 0; row < soundGridPane.getRowCount(); row++) {
			for (int col = 0; col < soundGridPane.getColumnCount(); col++) {
				soundGridPane.add(new SoundPane(), row, col);
			}
		}


		stage.setOnCloseRequest(event -> cleanup());
	}

	@FXML protected void onAddSoundClick() {
		String audioFileString = soundNameField.getText();
		String audioImageString = soundImageField.getText();
	}

	@FXML protected void onCreateMicrophoneClick() throws OsNotSupportedException {
		String microphoneName = virtualMicrophoneNameField.getText();
		if(microphoneName == null || microphoneName.isBlank()) {
			return;
		}
		microphoneListView
			.getItems()
			.add(
				new MicrophoneCell(VirtualMicrophone.create(
					microphoneName, inputDevicesComboBox.getSelectionModel().getSelectedItem()
				),
				false)
			);
	}

	@FXML protected void onChooseImageClick() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Select image file");
		File audio = fileChooser.showOpenDialog(soundImageField.getScene().getWindow());
		if(audio != null) {
			soundImageField.setText(audio.getAbsolutePath());
		}
	}

	@FXML protected void onChooseFileClick() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Select audio file");
		File audio = fileChooser.showOpenDialog(soundNameField.getScene().getWindow());
		if(audio != null) {
			soundFileField.setText(audio.getAbsolutePath());
		}
	}

	@FXML protected void refreshCombobox() throws OsNotSupportedException {
		List<String> devices = Terminal.getInstance().listOutputDevices();
		inputDevicesComboBox.setItems(FXCollections.observableList(devices));
	}

	private void persist() {
		try {
			writeResource(
				CONFIG_FILE,
				JSONConverter.CONFIG.serializeIndented(new Config(
					sounds,
					microphoneListView
						.getItems()
						.stream()
						.filter(MicrophoneCell::isLocked)
						.map(MicrophoneCell::getMicrophone)
						.toList()
				), 4)
			);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private void cleanup(){
		persist();
		microphoneListView
			.getItems()
			.stream()
			.map(MicrophoneCell::getMicrophone)
			.forEach(VirtualMicrophone::delete);
	}
}