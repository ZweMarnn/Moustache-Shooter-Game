package application;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class ScoreBoardController implements Initializable {

	@FXML
	private Text highestLevel;

	@FXML
	private Text highestScore;

	@FXML
	private Text lblHighestLevel;

	@FXML
	private Text lblHighestScore;

	@FXML
	void backToMain(ActionEvent event) {
		Pane root;
		try {
			root = (Pane) FXMLLoader.load(getClass().getResource("Menu.fxml"));
			Scene scene = new Scene(root);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			Stage primaryStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			primaryStage.hide();
			primaryStage.setScene(scene);
			primaryStage.setTitle("Menu Stage");
			primaryStage.setResizable(false);
			primaryStage.show();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		List<String> recordList = FileTest.readRecords();
		System.out.println( "RecordList size"+recordList.size());

		if (recordList.size() == 0) {
			lblHighestLevel.setText("---");
			lblHighestScore.setText("---");
		} else {
			List<Integer> levelList = FileTest.getLevelList(recordList);
			List<Integer> scoreList = FileTest.getScoreList(recordList);
			int maxLevel = Collections.max(levelList);
			int maxScore = Collections.max(scoreList);

			lblHighestLevel.setText(String.valueOf(maxLevel));
			lblHighestScore.setText(String.valueOf(maxScore));

		}
	}

}
