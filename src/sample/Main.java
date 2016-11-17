package sample;


import com.interactivemesh.jfx.importer.stl.StlMeshImporter;

import com.sun.javaws.exceptions.InvalidArgumentException;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.AmbientLight;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.PerspectiveCamera;
import javafx.scene.PointLight;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Mesh;
import javafx.scene.shape.MeshView;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;

import java.io.File;
import java.io.StringReader;
import java.util.List;

public class Main extends Application {

    private TextField path;
    private static String filePath;

    private static final Color lightColor = Color.rgb(244, 255, 250);
    private static final Color modelColor = Color.rgb(0, 190, 222);

    private static final double MODEL_SCALE_FACTOR = 30;
    private static final double MODEL_X_OFFSET = 0; // standard
    private static final double MODEL_Y_OFFSET = 0; // standard

    private static final int VIEWPORT_SIZE = 800;

    private MeshView meshView;

    static MeshView loadView(){
        File file = new File(filePath);
        StlMeshImporter importer = new StlMeshImporter();
        importer.read(file);
        Mesh mesh = importer.getImport();
        return new MeshView(mesh);
    }

    private Group buildModel(){
        meshView = loadView();


        //here some centering and sizing
        meshView.setTranslateX(VIEWPORT_SIZE / 2 + MODEL_X_OFFSET);
        meshView.setTranslateY(VIEWPORT_SIZE / 2 + MODEL_Y_OFFSET);
        meshView.setTranslateZ(VIEWPORT_SIZE / 2);
        meshView.setScaleX(MODEL_SCALE_FACTOR);
        meshView.setScaleY(MODEL_SCALE_FACTOR);
        meshView.setScaleZ(MODEL_SCALE_FACTOR);


        //Creating models material
        PhongMaterial material = new PhongMaterial(modelColor);
        material.setSpecularColor(lightColor);
        material.setSpecularPower(16);

        meshView.setMaterial(material);

        //
        meshView.getTransforms().setAll(new Rotate(90, Rotate.Z_AXIS), new Rotate(90, Rotate.X_AXIS));



        meshView.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                meshView.getTransforms().setAll(new Rotate(-event.getSceneX(), Rotate.Y_AXIS),
                                                new Rotate(-event.getSceneY(), Rotate.X_AXIS));
            }
        });

        //point lights - for some sense of 3D
        PointLight pointLight = new PointLight(lightColor);
        pointLight.setTranslateX(VIEWPORT_SIZE*3/4);
        pointLight.setTranslateY(VIEWPORT_SIZE/2);
        pointLight.setTranslateZ(VIEWPORT_SIZE/2);
        PointLight pointLight2 = new PointLight(lightColor);
        pointLight2.setTranslateX(VIEWPORT_SIZE*1/4);
        pointLight2.setTranslateY(VIEWPORT_SIZE*3/4);
        pointLight2.setTranslateZ(VIEWPORT_SIZE*3/4);
        PointLight pointLight3 = new PointLight(lightColor);
        pointLight3.setTranslateX(VIEWPORT_SIZE*5/8);
        pointLight3.setTranslateY(VIEWPORT_SIZE/2);
        pointLight3.setTranslateZ(0);

        Color ambientColor = Color.rgb(80, 80, 80, 0);
        AmbientLight ambient = new AmbientLight(ambientColor);



        Group root = new Group(meshView);

        //here adding all the lights
        root.getChildren().add(pointLight);
        root.getChildren().add(pointLight2);
        root.getChildren().add(pointLight3);
        root.getChildren().add(ambient);


        /*root.setOnScroll(new EventHandler<ScrollEvent>() {
            @Override
            public void handle(ScrollEvent event) {

                if (meshView.getScaleX()+ 0.1*event.getDeltaY() >= 0.0 || event.getDeltaY() > 0.0){
                    meshView.setScaleX(meshView.getScaleX() + 0.1*event.getDeltaY());
                    meshView.setScaleY(meshView.getScaleY() + 0.1*event.getDeltaY());
                    meshView.setScaleZ(meshView.getScaleZ() + 0.1*event.getDeltaY());
                }
            }
        });*/

        return root;
    }

    private PerspectiveCamera addCamera(Scene scene) {
        PerspectiveCamera perspectiveCamera = new PerspectiveCamera();
        scene.setCamera(perspectiveCamera);

        return perspectiveCamera;
    }



    @Override
    public void start(Stage primaryStage) throws Exception{

        List<String> args = getParameters().getRaw();

        //if (args.size() != 1) throw new IllegalArgumentException("wrong number of arguments");

        //filePath = args.get(0);

        primaryStage.setTitle("STL Viewer");


        Button btn = new Button();
        btn.setText("Open STL file");

        GridPane root = new GridPane();
        root.setAlignment(Pos.CENTER);
        root.setHgap(10);
        root.setVgap(10);
        root.setPadding(new Insets(25, 25, 25, 25));
        root.add(btn,1,2);

        Label label1 = new Label("Path:");
        TextField textField = new TextField ();
        //HBox hb = new HBox();
        root.add(label1,0,1);
        root.add(textField,1,1);
        //hb.setSpacing(10);

        primaryStage.setScene(new Scene(root, 300, 250));
        primaryStage.show();

        btn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {

                filePath=textField.getText();

                if (textField.getText()==null || textField.getText().isEmpty()) {
                    
                    throw new IllegalArgumentException("bad path");
                }

                Group group  = buildModel();

                //some transformations of group - probably to fit the viewing frame fine

                Scene scene = new Scene(group); //not specifing the size for now
                scene.setFill(Color.rgb(10, 10, 40));
                addCamera(scene);
                scene.setOnScroll(new EventHandler<ScrollEvent>() {
                    @Override
                    public void handle(ScrollEvent event) {

                        if (meshView.getScaleX()+ 0.1*event.getDeltaY() >= 0.0 || event.getDeltaY() > 0.0){
                            meshView.setScaleX(meshView.getScaleX() + 0.1*event.getDeltaY());
                            meshView.setScaleY(meshView.getScaleY() + 0.1*event.getDeltaY());
                            meshView.setScaleZ(meshView.getScaleZ() + 0.1*event.getDeltaY());
                        }
                    }
                });

                //primaryStage.setTitle("STL viewer - " + filePath);
                primaryStage.setScene(scene);
                primaryStage.show();
            }
        });


    }


    public static void main(String[] args) {
        launch(args);
    }
}
