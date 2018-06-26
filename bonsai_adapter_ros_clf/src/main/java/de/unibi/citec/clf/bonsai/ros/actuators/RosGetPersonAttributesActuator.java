
package de.unibi.citec.clf.bonsai.ros.actuators;



import de.unibi.citec.clf.bonsai.actuators.GetPersonAttributesActuator;
import de.unibi.citec.clf.bonsai.core.configuration.IObjectConfigurator;
import de.unibi.citec.clf.bonsai.core.exception.ConfigurationException;
import de.unibi.citec.clf.bonsai.ros.RosNode;
import de.unibi.citec.clf.bonsai.ros.helper.ResponseFuture;
import de.unibi.citec.clf.btl.data.person.PersonAttribute;
import de.unibi.citec.clf.btl.ros.MsgTypeFactory;
import de.unibi.citec.clf.btl.ros.RosSerializer;
import openpose_ros_msgs.GetPersonAttributes;
import openpose_ros_msgs.GetPersonAttributesRequest;
import openpose_ros_msgs.GetPersonAttributesResponse;
import org.ros.exception.ServiceNotFoundException;
import org.ros.exception.RosRuntimeException;
import org.ros.namespace.GraphName;
import org.ros.node.ConnectedNode;
import org.ros.node.service.ServiceClient;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author jkummert
 */
public class RosGetPersonAttributesActuator extends RosNode implements GetPersonAttributesActuator {

    String topic;
    private GraphName nodeName;
    private org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(getClass());

    ServiceClient<GetPersonAttributesRequest, GetPersonAttributesResponse> clientTrigger;

    public RosGetPersonAttributesActuator(GraphName gn) {
        initialized = false;
        this.nodeName = gn;
    }

    @Override
    public PersonAttribute getPersonAttributes(String id) throws InterruptedException, ExecutionException {
        final GetPersonAttributesRequest req = clientTrigger.newMessage();
        req.setPersonId(id);
        //set data
        final ResponseFuture<GetPersonAttributesResponse> res = new ResponseFuture<>();
        clientTrigger.call(req, res);
        while (!res.succeeded()) {
            try {
                Thread.sleep(250);
            } catch (InterruptedException ex) {
                logger.fatal(ex);
                Thread.currentThread().interrupt();
            }
        }
        PersonAttribute ret = new PersonAttribute();
        try {
            ret = MsgTypeFactory.getInstance().createType(res.get().getAttributes(), PersonAttribute.class);
        } catch (RosSerializer.DeserializationException ex) {
            Logger.getLogger(RosGetPersonAttributesActuator.class.getName()).log(Level.SEVERE, null, ex);
        }

        //return future that has the response
        return ret;

    }

    @Override
    public void configure(IObjectConfigurator conf) throws ConfigurationException {
        this.topic = conf.requestValue("topic");
    }

    @Override
    public void onStart(ConnectedNode connectedNode) {
        try {
            clientTrigger = connectedNode.newServiceClient(topic, GetPersonAttributes._TYPE);
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
