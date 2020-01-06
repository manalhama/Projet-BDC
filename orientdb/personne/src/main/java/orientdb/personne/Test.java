package orientdb.personne;
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


public class Test extends Application{

 
   
  @Override
	public void start(Stage stage) {
		Scene scene = new Scene(new Group(), 500, 250);
		stage.setTitle("Who is this person's friend?");

		TextField personneTxt = new TextField();
		final TextField friendTxt = new TextField();
		
		Button searchBtn=new Button("Search");

		personneTxt.setText("person");
		

		GridPane grid = new GridPane();
		grid.setVgap(5);
		grid.setHgap(10);
		grid.setPadding(new Insets(5,5,5,5));
		grid.add(new Label("Person:"),0,0);
		grid.add(personneTxt,1,0);
		grid.add(new Label("Friend:"),0,1);
		grid.add(friendTxt,1,1);
		
		grid.add(searchBtn, 2, 0);

		searchBtn.setOnMouseClicked((MouseEvent event) -> {
			OrientDB orient = new OrientDB("remote:localhost", OrientDBConfig.defaultConfig());
			ODatabaseSession db = orient.open("test", "root", "orientdb");

			String query =
			        " MATCH                                           " +
			        "   {class:Person, as:a, where: (name = :name1)}, " +
			        "   {class:Person, as:b, where: (name = :name2)}, " +
			        "   {as:a} -FriendOf-> {as:x} -FriendOf-> {as:b}  " +
			        " RETURN x.name as friend                         ";

			    Map<String, Object> params = new HashMap<String, Object>();
			    params.put("name1", "Alice");
			    params.put("name2", "Jim");

			    OResultSet rs = db.query(query, params);

			    while (rs.hasNext()) {
			      OResult item = rs.next();
			      friendTxt.setText((String) item.getProperty("friend"));
			      
			    }

			    rs.close();

		
			
			
			db.close();    
			orient.close();
          
		});

		Group root =  (Group) scene.getRoot();
		root.getChildren().add(grid);
		stage.setScene(scene);
		stage.show();


	}


  private static void createSchema(ODatabaseSession db) {
    OClass person = db.getClass("Person");

    if (person == null) {
      person = db.createVertexClass("Person");
    }

    if (person.getProperty("name") == null) {
      person.createProperty("name", OType.STRING);
      person.createIndex("Person_name_index", OClass.INDEX_TYPE.NOTUNIQUE, "name");
    }

    if (db.getClass("FriendOf") == null) {
      db.createEdgeClass("FriendOf");
    }

  }

  private static void createPeople(ODatabaseSession db) {
    OVertex alice = createPerson(db, "Alice", "Foo");
    OVertex bob = createPerson(db, "Bob", "Bar");
    OVertex jim = createPerson(db, "Jim", "Baz");

    OEdge edge1 = alice.addEdge(bob, "FriendOf");
    edge1.save();
    OEdge edge2 = bob.addEdge(jim, "FriendOf");
    edge2.save();
  }

  private static OVertex createPerson(ODatabaseSession db, String name, String surname) {
    OVertex result = db.newVertex("Person");
    result.setProperty("name", name);
    result.setProperty("surname", surname);
    result.save();
    return result;
  }


	public static void main(String[] args){
		Application.launch(args);
	}
 

}