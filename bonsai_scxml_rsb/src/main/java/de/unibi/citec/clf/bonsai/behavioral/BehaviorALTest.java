
package de.unibi.citec.clf.bonsai.behavioral;



import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import rsb.*;
import rsb.patterns.RemoteServer;

/**
 * @author lruegeme
 */
public class BehaviorALTest extends Application {

    private static class StateHandler implements Handler {
        @Override
        public void internalNotify(Event e) {
            System.out.println("CURRENT STATES: " + e.getData().toString());
        }
    }

    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(BehaviorALTest.class);

    public static void main(String[] args) throws IOException, RSBException, ExecutionException, TimeoutException, InterruptedException {
        System.out.println("Behavioral tester: ");

        Listener listener = Factory.getInstance().createListener(BehaviorALStarter.DEFAULT_REMOTE_BONSAI_SCOPE + "/states");
        listener.addHandler(new StateHandler(), true);
        listener.activate();
        //RemoteServer remoteServer = Factory.getInstance().createRemoteServer("/behaviors/server");
        //remoteServer.activate();
        //remoteServer.call("start", "Wave,location=table_one");
        if (args.length > 0) {
            try {
                RemoteServer remoteServer = Factory.getInstance().createRemoteServer(BehaviorALStarter.DEFAULT_SERVER_SCOPE);
                remoteServer.activate();
                System.out.println("server activate on: " + BehaviorALStarter.DEFAULT_SERVER_SCOPE);
                String param = "";
                for (String s : args) {
                    if (!param.isEmpty()) param += " ";
                    param += s;
                }
                System.out.println("call [" + RSBBehaviorALServer.CB_START +"] with: (" + param + ")");
                remoteServer.call(RSBBehaviorALServer.CB_START, param);
                Future<Event> a = remoteServer.callAsync(RSBBehaviorALServer.CB_DONE);
                while (!a.isDone()) {
                    System.out.println("is not done");
                    Thread.sleep(250);
                }
                System.out.println("is done, Result:" + (boolean) a.get().getData());
            } catch (Exception e) {
                System.out.println("error" + e.getMessage());
            }
        } else {
            launch(args);
        }

    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        String server = BehaviorALStarter.DEFAULT_SERVER_SCOPE;
        RemoteServer remoteServer = Factory.getInstance().createRemoteServer(server);
        logger.info("using server on " + server);
        remoteServer.activate();

        primaryStage.setOnCloseRequest(we -> System.exit(0));

        primaryStage.setTitle("Remote BehaviorAL Trigger");

        final TextField actionField = new TextField();
        final Button buttonSM = new Button();
        final Button buttonAction = new Button();

        Event ret = new Event();
        try {
            ret = remoteServer.call(RSBBehaviorALServer.CB_LISTSM);
        } catch (Exception ex) {
            System.out.println(ex);
            System.exit(1);
        }
        String rets = (String) ret.getData();
        String[] sms = rets.split(";");
        ObservableList smList = FXCollections.observableArrayList(sms);

        final ComboBox statemachines = new ComboBox(smList);
        final ComboBox<String> actionsBox = new ComboBox<>();

        buttonSM.setText("switch");
        buttonSM.setOnAction(event -> {
            try {
                System.out.println("loading statemachine");
                String cur = (String) statemachines.getValue();
                remoteServer.call(RSBBehaviorALServer.CB_SETSM, cur);
            } catch (RSBException | ExecutionException | TimeoutException | InterruptedException ex) {
                logger.error(ex);
            }
        });

        buttonAction.setText("trigger");
        buttonAction.setOnAction(event -> {
            try {
                logger.info("starting action");
                remoteServer.call(RSBBehaviorALServer.CB_START, actionField.getText());
                Future<Event> a = remoteServer.callAsync(RSBBehaviorALServer.CB_DONE);
                while (!a.isDone()) {
                    System.out.println("is not done");
                    Thread.sleep(250);
                }
                System.out.println("is done, Result:" + (boolean) a.get().getData());
            } catch (RSBException | ExecutionException | TimeoutException | InterruptedException ex) {
                logger.error(ex);
            }
        });

        HBox state = new HBox(new Label("Scenario") ,statemachines , buttonSM);
        HBox box = new HBox(new Label("Action"), actionField, buttonAction);
        VBox vbox = new VBox(state, box);
        primaryStage.setScene(new Scene(vbox, 400, 400));

        vbox.requestLayout();
        primaryStage.show();
    }
}
