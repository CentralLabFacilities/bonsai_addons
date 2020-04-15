
package de.unibi.citec.clf.bonsai.ros.actuators;

import de.unibi.citec.clf.bonsai.actuators.PlanningSceneActuator;
import de.unibi.citec.clf.bonsai.core.configuration.IObjectConfigurator;
import de.unibi.citec.clf.bonsai.core.exception.ConfigurationException;
import de.unibi.citec.clf.bonsai.ros.RosNode;
import de.unibi.citec.clf.bonsai.ros.helper.ResponseFuture;
import de.unibi.citec.clf.btl.data.object.ObjectShapeList;
import org.apache.commons.lang.NotImplementedException;
import org.ros.exception.ServiceNotFoundException;
import org.ros.namespace.GraphName;
import org.ros.node.ConnectedNode;
import org.ros.node.service.ServiceClient;
import std_srvs.SetBool;
import std_srvs.SetBoolRequest;
import std_srvs.SetBoolResponse;

import java.util.concurrent.Future;

/**
 *
 * @author lruegeme
 */
public class ClfGraspingPlanningSceneActuator extends RosNode implements PlanningSceneActuator {

    String topicClear;
    private GraphName nodeName;
    private ServiceClient<SetBoolRequest, SetBoolResponse> serviceClear;
    private org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(getClass());

    public ClfGraspingPlanningSceneActuator(GraphName gn) {
        initialized = false;
        this.nodeName = gn;
    }
    
    @Override
    public void configure(IObjectConfigurator conf) throws ConfigurationException {
        this.topicClear = conf.requestValue("topic_clear_planning_scene");
    }

    @Override
    public Future<Boolean> clearScene(boolean keep_attached_objects) {
        SetBoolRequest a = serviceClear.newMessage();
        a.setData(keep_attached_objects);
        ResponseFuture<SetBoolResponse> res = new ResponseFuture<>();
        serviceClear.call(a,res);
        return res.toBooleanFuture();
    }

    @Override
    public Future<Boolean> addObjects(ObjectShapeList objects) {
        throw new NotImplementedException();
    }

    public Future<Boolean> manage() {
        throw new NotImplementedException();
    }

    @Override
    public GraphName getDefaultNodeName() {
        return nodeName;
    }

    @Override
    public void onStart(final ConnectedNode connectedNode) {
        try {
            serviceClear = connectedNode.newServiceClient(topicClear, SetBool._TYPE);
        } catch (ServiceNotFoundException e) {
            return;
        }
        initialized = true;
    }

    @Override
    public void destroyNode() {
        if(serviceClear!=null) serviceClear.shutdown();
    }


}
