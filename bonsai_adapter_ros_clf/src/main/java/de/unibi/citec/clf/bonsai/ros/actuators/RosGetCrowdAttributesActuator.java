
package de.unibi.citec.clf.bonsai.ros.actuators;


import de.unibi.citec.clf.bonsai.actuators.GetCrowdAttributesActuator;
import de.unibi.citec.clf.bonsai.core.configuration.IObjectConfigurator;
import de.unibi.citec.clf.bonsai.core.exception.ConfigurationException;
import de.unibi.citec.clf.bonsai.ros.RosNode;
import de.unibi.citec.clf.bonsai.ros.helper.ResponseFuture;
import de.unibi.citec.clf.btl.data.person.PersonAttribute;
import de.unibi.citec.clf.btl.ros.MsgTypeFactory;
import de.unibi.citec.clf.btl.ros.RosSerializer;
import openpose_ros_msgs.GetCrowdAttributes;
import openpose_ros_msgs.GetCrowdAttributesRequest;
import openpose_ros_msgs.GetCrowdAttributesResponse;
import openpose_ros_msgs.PersonAttributes;
import org.ros.namespace.GraphName;
import org.ros.node.ConnectedNode;
import org.ros.node.service.ServiceClient;
import org.ros.exception.ServiceNotFoundException;
import org.ros.exception.RosRuntimeException;


import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author jkummert
 */
public class RosGetCrowdAttributesActuator extends RosNode implements GetCrowdAttributesActuator {

    String topic;
    private GraphName nodeName;
    private org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(getClass());

    ServiceClient<GetCrowdAttributesRequest, GetCrowdAttributesResponse> clientTrigger;

    public RosGetCrowdAttributesActuator(GraphName gn) {
        initialized = false;
        this.nodeName = gn;
    }

    @Override
    public List<PersonAttribute> getCrowdAttributes() throws InterruptedException, ExecutionException {
        final GetCrowdAttributesRequest req = clientTrigger.newMessage();
        //set data
        final ResponseFuture<GetCrowdAttributesResponse> res = new ResponseFuture<>();
        clientTrigger.call(req, res);
        while (!res.succeeded()) {
            try {
                Thread.sleep(250);
            } catch (InterruptedException ex) {
                logger.fatal(ex);
                Thread.currentThread().interrupt();
            }
        }
        List<PersonAttribute> ret = new LinkedList<>();
        GetCrowdAttributesResponse response = res.get();

        try {
            for(Iterator<PersonAttributes> it = response.getAttributes().iterator(); it.hasNext();){
                ret.add(MsgTypeFactory.getInstance().createType(it.next(), PersonAttribute.class));
            }
        } catch (RosSerializer.DeserializationException ex) {
            logger.fatal(ex);
        }

        return ret;

    }

    @Override
    public void configure(IObjectConfigurator conf) throws ConfigurationException {
        this.topic = conf.requestValue("topic");
    }

    @Override
    public void onStart(ConnectedNode connectedNode) {
        try {
            clientTrigger = connectedNode.newServiceClient(topic, GetCrowdAttributes._TYPE);
        } catch (ServiceNotFoundException e) {
            throw new RosRuntimeException(e);
        }
        initialized = true;
    }

    @Override
    public void destroyNode() {
        if(clientTrigger!=null) clientTrigger.shutdown();
    }

    @Override
    public GraphName getDefaultNodeName() {
        return nodeName;
    }
}
