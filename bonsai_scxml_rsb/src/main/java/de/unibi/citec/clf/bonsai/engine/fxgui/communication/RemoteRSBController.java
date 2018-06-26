
package de.unibi.citec.clf.bonsai.engine.fxgui.communication;



import de.unibi.citec.clf.bonsai.engine.communication.RSBController;
import java.util.LinkedList;
import java.util.List;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import rsb.Event;
import rsb.Factory;
import rsb.Handler;
import rsb.Listener;
import rst.statemachine.StateChangeType.StateChange;

/**
 * @author lruegeme
 */
public class RemoteRSBController extends RSBController implements FXGUISCXMLRemote {

    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(RemoteRSBController.class);

    private SimpleStringProperty status;

    private String scope;

    private static class LogPrinter implements Handler {

        @Override
        public void internalNotify(Event e) {
            String msg = (String) e.getData();
            System.out.println("LOG:" + msg);
        }
    }

    private static class StatusPrinter implements Handler {

        StringProperty s;

        StatusPrinter(StringProperty sp) {
            s = sp;
        }

        @Override
        public void internalNotify(Event e) {
            String msg = (String) e.getData();
            //System.out.println("STATUS:" + msg);
            s.set(msg);
        }
    }

    private static class TransitionPrinter implements Handler {

        @Override
        public void internalNotify(Event e) {
            if (e.getType() != StateChange.class) {
                logger.warn("event has wrong type. (not StateChange)");
                return;
            }
            StateChange a = (StateChange) e.getData();

            if (a.getFromState().isEmpty()) {
                logger.info("[SM]STARTING: >> " + a.getToState());
            } else if (a.getToState().isEmpty()) {
                logger.info("[SM]FINISHED: << " + a.getFromState());
            } else {
                logger.info("[SM]TRANSITION:"
                        + a.getFromState()
                        + " >>[" + a.getCause() + "]>> "
                        + a.getToState());
            }

        }
    }

    public RemoteRSBController(String scope) throws Exception {
        super(scope + "/server");
        this.scope = scope;

        status = new SimpleStringProperty();

        Listener listener = Factory.getInstance().createListener(scope + "/info/transitions");
        listener.addHandler(new TransitionPrinter(), true);
        listener.activate();

        Listener listener2 = Factory.getInstance().createListener(scope + "/alive");
        listener2.addHandler(new StatusPrinter(status), true);
        listener2.activate();

    }

    @Override
    public StringProperty getStatusProp() {
        return status;
    }

    @Override
    public void addStateTrigger(IStateListener list) {
        RemoteRSBController r = this;
        Task task = new Task<Void>() {

            List<String> old = new LinkedList<>();

            @Override
            protected Void call() throws Exception {
                while (true) {
                    List<String> ids = r.getStateIds();

                    if (ids!=null && changed(ids, old)) {
                        ObservableList o = FXCollections.observableArrayList();
                        o.addAll(ids);
                        list.setStateList(o);
                        old = ids;
                    }
                    Thread.sleep(500);
                }
            }

            private boolean changed(List<String> evs, List<String> ev) {
                return (evs.size() != ev.size() ||
                        !evs.containsAll(ev)
                );
            }

        };

        new Thread(task).start();
    }

    @Override
    public void addCurrentStateTrigger(ICurrentStateListener list) {
        logger.debug("added state List trigger");

        RemoteRSBController r = this;

        Task task = new Task<Void>() {

            List<String> evs = new LinkedList<>();

            @Override
            protected Void call() throws Exception {
                while (true) {
                    List<String> ids = r.getCurrentStates();
                    List<String> ev = r.getTransitions();
                    //logger.trace("triggered state refesh:" + ids);
                    //logger.trace("events " + ev);
                    ObservableList o = FXCollections.observableArrayList();
                    o.addAll(ids);
                    list.updateStateList(o);
                    if (changed(evs, ev)) {
                        //logger.trace("events changed!");
                        evs = ev;
                        o = FXCollections.observableArrayList();
                        o.addAll(ev);
                        list.updateEventList(o);
                    }

                    Thread.sleep(333);
                }
            }

            private boolean changed(List<String> evs, List<String> ev) {
                return (evs.size() != ev.size() ||
                        !evs.containsAll(ev)
                );
            }
        };

        new Thread(task).start();

    }

}
