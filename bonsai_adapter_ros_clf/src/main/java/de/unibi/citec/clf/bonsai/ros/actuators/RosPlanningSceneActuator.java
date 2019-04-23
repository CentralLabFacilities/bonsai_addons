
package de.unibi.citec.clf.bonsai.ros.actuators;

import actionlib_msgs.GoalStatusArray;
import com.github.rosjava_actionlib.ActionClient;
import com.github.rosjava_actionlib.ActionClientListener;
import de.unibi.citec.clf.btl.data.object.ObjectShapeList;
import org.apache.commons.lang.NotImplementedException;
import org.ros.namespace.GraphName;
import org.ros.node.ConnectedNode;

import de.unibi.citec.clf.bonsai.actuators.PlanningSceneActuator;
import de.unibi.citec.clf.bonsai.core.configuration.IObjectConfigurator;
import de.unibi.citec.clf.bonsai.core.exception.ConfigurationException;
import de.unibi.citec.clf.bonsai.ros.RosNode;
import java.util.concurrent.Future;
import org.ros.message.Duration;

import planning_scene_manager_msgs.PlanningSceneManagerRequestActionFeedback;
import planning_scene_manager_msgs.PlanningSceneManagerRequestActionGoal;
import planning_scene_manager_msgs.PlanningSceneManagerRequestActionResult;

/**
 *
 * @author llach
 */
public class RosPlanningSceneActuator extends RosNode implements PlanningSceneActuator, ActionClientListener<PlanningSceneManagerRequestActionFeedback, PlanningSceneManagerRequestActionResult> {

    String topic;
    private GraphName nodeName;
    private ActionClient<PlanningSceneManagerRequestActionGoal, PlanningSceneManagerRequestActionFeedback, PlanningSceneManagerRequestActionResult> ac = null;
    private org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(getClass());

    public RosPlanningSceneActuator(GraphName gn) {
        initialized = false;
        this.nodeName = gn;
    }
    
    @Override
    public void configure(IObjectConfigurator conf) throws ConfigurationException {
        this.topic = conf.requestValue("topic");
    }

    @Override
    public Future<Boolean> clearScene() {
        throw new NotImplementedException();
    }

    @Override
    public Future<Boolean> addObjects(ObjectShapeList objects) {
        throw new NotImplementedException();
    }

    public Future<Boolean> manage() {

        if (!ac.waitForActionServerToStart(new Duration(4))) {
            logger.error("action server not started on " + this.topic);
        }
        PlanningSceneManagerRequestActionGoal goalMessage = ac.newGoalMessage();

        Future<Boolean> res = ac.sendGoal(goalMessage).toBooleanFuture();
        return res;
    }

    @Override
    public GraphName getDefaultNodeName() {
        return nodeName;
    }

    @Override
    public void onStart(final ConnectedNode connectedNode) {
        ac = new ActionClient(connectedNode, this.topic, PlanningSceneManagerRequestActionGoal._TYPE, PlanningSceneManagerRequestActionFeedback._TYPE, PlanningSceneManagerRequestActionResult._TYPE);
        ac.attachListener(this);
        initialized = true;
        logger.fatal("on start, RosNaoQiSpeechActuator done");
    }

    @Override
    public void destroyNode() { if(ac!=null) ac.finish();}

    @Override
    public void resultReceived(PlanningSceneManagerRequestActionResult t) {

        logger.trace("result recieved");

    }

    @Override
    public void feedbackReceived(PlanningSceneManagerRequestActionFeedback t) {

        logger.trace("feedback recieved");

    }

    @Override
    public void statusReceived(GoalStatusArray gsa) {

        //logger.trace("status recieved ");
    }

}
