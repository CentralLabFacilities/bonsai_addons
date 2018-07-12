package de.unibi.citec.clf.bonsai.ros.actuators;

import de.unibi.citec.clf.bonsai.actuators.SegmentationActuator;
import de.unibi.citec.clf.bonsai.core.configuration.IObjectConfigurator;
import de.unibi.citec.clf.bonsai.core.exception.ConfigurationException;
import de.unibi.citec.clf.bonsai.ros.RosNode;
import de.unibi.citec.clf.bonsai.ros.helper.ResponseFuture;
import de.unibi.citec.clf.btl.data.object.ObjectShapeData;
import de.unibi.citec.clf.btl.data.object.ObjectShapeList;
import de.unibi.citec.clf.btl.ros.MsgTypeFactory;
import de.unibi.citec.clf.btl.ros.RosSerializer;
import object_tracking_msgs.ObjectShape;
import org.ros.exception.ServiceNotFoundException;
import org.ros.namespace.GraphName;
import org.ros.node.ConnectedNode;
import org.ros.node.service.ServiceClient;
import object_tracking_msgs.Segment;
import object_tracking_msgs.SegmentRequest;
import object_tracking_msgs.SegmentResponse;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author rfeldhans
 */
public class RosSegmentationActuator extends RosNode implements SegmentationActuator {
    private GraphName nodeName;
    private String serviceTopic;
    private ServiceClient<SegmentRequest, SegmentResponse> client;
    private org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(getClass());

    public RosSegmentationActuator(GraphName gn) {
        initialized = false;
        nodeName = gn;
    }


    @Override
    public void configure(IObjectConfigurator conf) throws ConfigurationException {
        this.serviceTopic = conf.requestValue("topic");
    }

    @Override
    public void onStart(ConnectedNode connectedNode) {
        try {
            client = connectedNode.newServiceClient(serviceTopic, Segment._TYPE);
            initialized = true;
        } catch (ServiceNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void destroyNode() {
        if (client != null) {
            client.shutdown();
        }
    }

    @Override
    public GraphName getDefaultNodeName() {
        return nodeName;
    }

    @Override
    public Future<ObjectShapeList> segment(String label) throws IOException {
        final SegmentRequest req = client.newMessage();
        req.setLabel(label);


        final ResponseFuture<SegmentResponse> res = new ResponseFuture<>();
        client.call(req, res);

        Future<ObjectShapeList> future = new Future<ObjectShapeList>() {
            @Override
            public boolean cancel(boolean b) {
                return res.cancel(b);
            }

            @Override
            public boolean isCancelled() {
                return res.isCancelled();
            }

            @Override
            public boolean isDone() {
                return res.isDone();
            }

            @Override
            public ObjectShapeList get() throws InterruptedException, ExecutionException {
                ObjectShapeList list = new ObjectShapeList();
                for (ObjectShape shape : res.get().getObjectShapeList()){
                    try {
                        list.add(MsgTypeFactory.getInstance().createType(shape, ObjectShapeData.class));
                    } catch (RosSerializer.DeserializationException ex) {
                        logger.error("could not serialize ObjectShape(Data) Response");
                    }
                }
                return list;
            }

            @Override
            public ObjectShapeList get(long l, TimeUnit timeUnit) throws InterruptedException, ExecutionException, TimeoutException {
                ObjectShapeList list = new ObjectShapeList();
                for (ObjectShape shape : res.get(l, timeUnit).getObjectShapeList()){
                    try {
                        list.add(MsgTypeFactory.getInstance().createType(shape, ObjectShapeData.class));
                    } catch (RosSerializer.DeserializationException ex) {
                        logger.error("could not serialize ObjectShape(Data) Response");
                    }
                }
                return list;
            }
        };
        return future;

    }
}
