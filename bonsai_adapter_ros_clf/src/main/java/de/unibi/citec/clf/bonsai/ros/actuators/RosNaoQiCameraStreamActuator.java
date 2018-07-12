package de.unibi.citec.clf.bonsai.ros.actuators;

import de.unibi.citec.clf.bonsai.actuators.CameraStreamActuator;
import de.unibi.citec.clf.bonsai.actuators.DetectPeopleActuator;
import de.unibi.citec.clf.bonsai.core.configuration.IObjectConfigurator;
import de.unibi.citec.clf.bonsai.core.exception.ConfigurationException;
import de.unibi.citec.clf.bonsai.ros.helper.ResponseFuture;
import de.unibi.citec.clf.btl.data.person.PersonData;
import de.unibi.citec.clf.btl.data.person.PersonDataList;
import de.unibi.citec.clf.btl.ros.MsgTypeFactory;
import de.unibi.citec.clf.btl.ros.RosSerializer;

import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.ros.namespace.GraphName;
import org.ros.node.ConnectedNode;
import de.unibi.citec.clf.bonsai.ros.RosNode;
import de.unibi.citec.clf.btl.List;
import de.unibi.citec.clf.btl.data.person.BodySkeleton;
import org.ros.exception.RosRuntimeException;
import org.ros.exception.ServiceNotFoundException;
import org.ros.node.service.ServiceClient;
import std_srvs.SetBool;
import std_srvs.SetBoolRequest;
import std_srvs.SetBoolResponse;

/**
 *
 * @author jkummert
 */
public class RosNaoQiCameraStreamActuator extends RosNode implements CameraStreamActuator {

    String colorTopic;
    String depthTopic;
    private GraphName nodeName;
    private org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(getClass());

    ServiceClient<SetBoolRequest, SetBoolResponse> colorClientTrigger;
    ServiceClient<SetBoolRequest, SetBoolResponse> depthClientTrigger;

    public RosNaoQiCameraStreamActuator(GraphName gn) {
        initialized = false;
        this.nodeName = gn;
    }

    @Override
    public void configure(IObjectConfigurator conf) throws ConfigurationException {
        this.colorTopic = conf.requestValue("colorTopic");
        this.depthTopic = conf.requestValue("depthTopic");
    }

    @Override
    public GraphName getDefaultNodeName() {
        return nodeName;
    }

    @Override
    public void onStart(final ConnectedNode connectedNode) {
        try {
            colorClientTrigger = connectedNode.newServiceClient(colorTopic, SetBool._TYPE);
            depthClientTrigger = connectedNode.newServiceClient(depthTopic, SetBool._TYPE);
        } catch (ServiceNotFoundException e) {
            throw new RosRuntimeException(e);
        }
        initialized = true;
    }

    @Override
    public void destroyNode() {
        if (colorClientTrigger != null) {
            colorClientTrigger.shutdown();
        }
        if (depthClientTrigger != null) {
            depthClientTrigger.shutdown();
        }
    }

    @Override
    public boolean enableDepthStream(boolean enable) {
        SetBoolRequest req = depthClientTrigger.newMessage();
        req.setData(enable);

        final ResponseFuture<SetBoolResponse> res = new ResponseFuture<>();
        depthClientTrigger.call(req, res);

        while (!res.succeeded()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                logger.error("enalbe depth stream service call interupted ");
                Thread.currentThread().interrupt();
            }
        }

        try {
            return res.get().getSuccess();
        } catch (InterruptedException | ExecutionException ex) {
            logger.warn("Could not get service call response: ", ex);
            return false;
        }
    }

    @Override
    public boolean enableColorStream(boolean enable) {
        SetBoolRequest req = colorClientTrigger.newMessage();
        req.setData(enable);

        final ResponseFuture<SetBoolResponse> res = new ResponseFuture<>();
        colorClientTrigger.call(req, res);

        while (!res.succeeded()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                logger.error("enalbe depth stream service call interupted ");
                Thread.currentThread().interrupt();
            }
        }

        try {
            return res.get().getSuccess();
        } catch (InterruptedException | ExecutionException ex) {
            logger.warn("Could not get service call response: ", ex);
            return false;
        }
    }
}
