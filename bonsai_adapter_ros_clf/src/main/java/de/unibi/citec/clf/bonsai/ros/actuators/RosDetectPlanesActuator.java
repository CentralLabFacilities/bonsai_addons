
package de.unibi.citec.clf.bonsai.ros.actuators;



import de.unibi.citec.clf.bonsai.actuators.DetectPlanesActuator;
import de.unibi.citec.clf.bonsai.core.configuration.IObjectConfigurator;
import de.unibi.citec.clf.bonsai.core.exception.ConfigurationException;
import de.unibi.citec.clf.bonsai.ros.RosNode;
import de.unibi.citec.clf.bonsai.ros.helper.ResponseFuture;
import de.unibi.citec.clf.btl.ros.MsgTypeFactory;
import de.unibi.citec.clf.btl.ros.RosSerializer;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ros.namespace.GraphName;
import org.ros.node.ConnectedNode;
import de.unibi.citec.clf.bonsai.ros.RosNode;
import de.unibi.citec.clf.btl.data.vision3d.PlanePatch;
import de.unibi.citec.clf.btl.data.vision3d.PlanePatchList;
import object_tracking_msgs.DetectPlanes;
import object_tracking_msgs.DetectPlanesRequest;
import object_tracking_msgs.DetectPlanesResponse;
import org.ros.exception.ServiceNotFoundException;
import org.ros.node.service.ServiceClient;

/**
 *
 * @author ffriese
 */
public class RosDetectPlanesActuator extends RosNode implements DetectPlanesActuator{
    
    private ServiceClient<DetectPlanesRequest, DetectPlanesResponse> sc;
    private String serviceTopic;
    private final GraphName nodeName;
    
    public RosDetectPlanesActuator(GraphName gn){
        this.nodeName = gn;
    }
    
    @Override
    public void configure(IObjectConfigurator conf) throws ConfigurationException {
        this.serviceTopic = conf.requestValue("topic");
    }

    @Override
    public PlanePatchList detect() throws InterruptedException, ExecutionException {
        DetectPlanesRequest request = sc.newMessage();
        final ResponseFuture<DetectPlanesResponse> res = new ResponseFuture<>();
        sc.call(request, res);
        while(!res.succeeded()){
            try {
                Thread.sleep(250);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
        DetectPlanesResponse response = res.get();
        
        PlanePatchList planes = new PlanePatchList();
        for(grasping_msgs.Object obj :response.getSupportSurfaces()){
            try {
                PlanePatch pl = MsgTypeFactory.getInstance().createType(obj, PlanePatch.class);
                planes.add(pl);
            } catch (RosSerializer.DeserializationException ex) {
                Logger.getLogger(RosDetectPlanesActuator.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return planes;
    }

    @Override
    public void onStart(ConnectedNode connectedNode) {
     try {
         sc = connectedNode.newServiceClient(serviceTopic, DetectPlanes._TYPE);
     } catch (ServiceNotFoundException ex) {
         Logger.getLogger(RosDetectPlanesActuator.class.getName()).log(Level.SEVERE, null, ex);
     }
        
    }

    @Override
    public void destroyNode() {
        if(sc!=null) sc.shutdown();
    }

    @Override
    public GraphName getDefaultNodeName() {
        return nodeName;
    }



}
