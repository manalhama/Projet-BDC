import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.text.Font;


import java.util.HashMap;
import java.util.Map;

import com.orientechnologies.orient.core.config.OGlobalConfiguration;
import com.orientechnologies.orient.core.db.ODatabaseSession;
import com.orientechnologies.orient.core.db.OrientDB;
import com.orientechnologies.orient.core.db.OrientDBConfig;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.OEdge;
import com.orientechnologies.orient.core.record.OVertex;
import com.orientechnologies.orient.core.sql.executor.OResult;
import com.orientechnologies.orient.core.sql.executor.OResultSet;

public class MyTextField extends Application{
	@Override
	public void start(Stage stage) {
		Scene scene = new Scene(new Group(), 500, 250);
		stage.setTitle("Details about a song");

		TextField singerTxt = new TextField();
		TextField musicTxt = new TextField();
		TextField yearTxt = new TextField();
		Button insertBtn=new Button("Insert");

		singerTxt.setText("Singer");
		musicTxt.setText("Music");
		yearTxt.setText("Year");

		GridPane grid = new GridPane();
		grid.setVgap(5);
		grid.setHgap(10);
		grid.setPadding(new Insets(5,5,5,5));
		grid.add(new Label("Singer:"),0,0);
		grid.add(singerTxt,1,0);
		grid.add(new Label("Music:"),0,1);
		grid.add(musicTxt,1,1);
		grid.add(new Label("Year:"),0,2);
		grid.add(yearTxt,1,2);
		grid.add(insertBtn, 2, 0);

		insertBtn.setOnMouseClicked((MouseEvent event) -> {
			OrientDB orient = new OrientDB("remote:localhost", OrientDBConfig.defaultConfig());
			ODatabaseSession db = orient.open("Musique", "root", "orientdb");

		    createSchema(db);
		    OVertex singer = createSinger(db, singerTxt.getText(),singerTxt.getText() );
		    OVertex release = createYear(db, yearTxt.getText());
		    OVertex song = createSong(db, musicTxt.getText());
		    OEdge edge1 = song.addEdge(singer, "SongBy");
		    edge1.save();
		    OEdge edge2 = song.addEdge(release, "ReleaseYear");
		    edge2.save();
		    OVertex singer2 = createSinger(db, "Sam", "Smith");
		    OVertex song2 = createSong(db, "Dancing with a stranger");
		    OEdge edge3 = song2.addEdge(singer2, "SongBy");
		    edge3.save();
		    OEdge edge4 = song2.addEdge(release, "ReleaseYear");
		    edge4.save();

		    executeAQuery(db);

			
			
			db.close();    
			orient.close();
            
		});

		Group root =  (Group) scene.getRoot();
		root.getChildren().add(grid);
		stage.setScene(scene);
		stage.show();


	}



		public static void createSchema(ODatabaseSession db) {
		
	    OClass singer = db.getClass("Singer");
	    OClass year = db.getClass("Year");
	    OClass song = db.getClass("Song");
	    
	    if (singer == null) {
	        singer = db.createVertexClass("Singer");
	    }
	    if (year == null) {
	        year = db.createVertexClass("Year");
	    }
	    
	    if (song == null) {
	        song = db.createVertexClass("Song");
	    }
	    
	    if (singer.getProperty("name") == null) {
	        singer.createProperty("name", OType.STRING);
	        singer.createIndex("Song_name_index", OClass.INDEX_TYPE.NOTUNIQUE, "name");
	    }
	    if (year.getProperty("year") == null) {
	        year.createProperty("year", OType.STRING);
	        year.createIndex("Song_year_index", OClass.INDEX_TYPE.NOTUNIQUE, "year");
	    }
	    if (song.getProperty("title") == null) {
	        song.createProperty("title", OType.STRING);
	        song.createIndex("Song_title_index", OClass.INDEX_TYPE.NOTUNIQUE, "title");
	    }
	    if (db.getClass("SongBy") == null) {
	        db.createEdgeClass("SongBy");
	    }
	    if (db.getClass("ReleaseYear") == null) {
	        db.createEdgeClass("ReleaseYear");
	    }
	}
	public static OVertex createSinger(ODatabaseSession db, String name, String surname) {
	    OVertex result = db.newVertex("Singer");
	    result.setProperty("name", name);
	    result.setProperty("surname", surname);
	    result.save();
	    return result;
	}
	public static OVertex createYear(ODatabaseSession db, String year) {
	    OVertex result2 = db.newVertex("Year");
	    result2.setProperty("year", year);
	    result2.save();
	    return result2;
	}
	public static OVertex createSong(ODatabaseSession db, String title) {
	    OVertex result3 = db.newVertex("Song");
	    result3.setProperty("title", title);
	    result3.save();
	    return result3;
	}
	
	
	private static void executeAQuery(ODatabaseSession db) {
	    String query = "SELECT * FROM Singer WHERE name = ?";
	    
	    Map<String, Object> params = new HashMap<String, Object>();
	    params.put("name1", "Alice");
	    params.put("name2", "Jim");

	    OResultSet rs = db.query(query, params);
	    while (rs.hasNext()) {
	        OResult item = rs.next();
	        System.out.println("Singer: " + item.getProperty("name"));
	    }
	    rs.close();
	}
	


	public static void main(String[] args){
		Application.launch(args);
	}


}
