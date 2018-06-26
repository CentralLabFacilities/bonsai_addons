/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.unibi.citec.clf.bonsai.ros.actuators;

import de.unibi.citec.clf.bonsai.actuators.PostureActuator;
import de.unibi.citec.clf.bonsai.core.configuration.IObjectConfigurator;
import de.unibi.citec.clf.bonsai.core.exception.ConfigurationException;
import de.unibi.citec.clf.bonsai.ros.RosNode;
import de.unibi.citec.clf.bonsai.ros.helper.ResponseFuture;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.apache.log4j.Logger;
import org.ros.exception.RosRuntimeException;
import org.ros.exception.ServiceNotFoundException;
import org.ros.namespace.GraphName;
import org.ros.node.ConnectedNode;
import org.ros.node.service.ServiceClient;

// #layzimport
import posture_execution_msgs.*;

/**
 *
 * @author llach
 */
public class RosPostureExecutionActuator extends RosNode implements PostureActuator {
    
    private static final Logger logger = Logger.getLogger(RosPostureExecutionActuator.class);

    private ServiceClient<ExecuteNamedTargetRequest, ExecuteNamedTargetResponse> namedTargetSC;
    private ServiceClient<ExecutePostureRequest, ExecutePostureResponse> postureSC;

    private String namedTopic;
    private String postureTopic;
    
    private final GraphName nodeName;
    private ConnectedNode node;

    public RosPostureExecutionActuator(GraphName gn) {
        this.namedTargetSC = null;
        this.postureSC = null;
        this.nodeName = gn;
    }
    
    @Override
    public void configure(IObjectConfigurator conf) throws ConfigurationException {
        this.namedTopic = conf.requestValue("named_target_topic");
        this.postureTopic = conf.requestValue("posture_execution_topic");
    }

    @Override
    public void onStart(ConnectedNode connectedNode) {
        this.node = connectedNode;
        
        try {
            this.namedTargetSC = connectedNode.newServiceClient(this.namedTopic, ExecuteNamedTarget._TYPE);
            this.postureSC = connectedNode.newServiceClient(this.postureTopic, ExecutePosture._TYPE);

        } catch (ServiceNotFoundException ex) {
            throw new RosRuntimeException((ex.getMessage()));
        }
        
    }

    @Override
    public void destroyNode() {
        if (this.namedTargetSC != null) this.namedTargetSC.shutdown();
        if (this.postureSC != null) this.postureSC.shutdown();
    }

    @Override
    public GraphName getDefaultNodeName() {
        return nodeName;
    }

    @Override
    public Future<Boolean> executeMotion(String motion, String group) {
        ExecutePostureRequest req = postureSC.newMessage();
        req.setGroupName(group);
        req.setPosture(motion);
        
        ResponseFuture<ExecutePostureResponse> res = new ResponseFuture<>();
        this.postureSC.call(req, res);
        
        return new Future<Boolean>() {
            @Override
            public boolean cancel(boolean bln) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public boolean isCancelled() {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public boolean isDone() {
                return res.isDone();
            }

            @Override
            public Boolean get() throws InterruptedException, ExecutionException {
                logger.fatal(res);
                return res.get().getSuccess();
            }

            @Override
            public Boolean get(long l, TimeUnit tu) throws InterruptedException, ExecutionException, TimeoutException {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        };
    }

    @Override
    public List<String> listMotions(String group) {
        // those who want it may implement it.
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Future<Boolean> assumePose(String pose, String group) {
        ExecuteNamedTargetRequest req = namedTargetSC.newMessage();
        req.setGroupName(group);
        req.setTarget(pose);
        
        ResponseFuture<ExecuteNamedTargetResponse> res = new ResponseFuture<>();
        this.namedTargetSC.call(req, res);
        
        return res.toBooleanFuture();        
    }


}