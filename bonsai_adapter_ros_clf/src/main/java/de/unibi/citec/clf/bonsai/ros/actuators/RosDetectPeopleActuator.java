
package de.unibi.citec.clf.bonsai.ros.actuators;


import com.github.rosjava_actionlib.ActionClient;
import com.github.rosjava_actionlib.ActionFuture;
import de.unibi.citec.clf.bonsai.actuators.DetectPeopleActuator;
import de.unibi.citec.clf.bonsai.core.configuration.IObjectConfigurator;
import de.unibi.citec.clf.bonsai.core.exception.ConfigurationException;
import de.unibi.citec.clf.bonsai.ros.helper.ResponseFuture;

import java.util.ArrayList;
import java.util.List;
import de.unibi.citec.clf.btl.data.person.PersonData;
import de.unibi.citec.clf.btl.data.person.PersonDataList;
import de.unibi.citec.clf.btl.ros.MsgTypeFactory;
import de.unibi.citec.clf.btl.ros.RosSerializer;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import openpose_ros_msgs.*;
import org.ros.message.Duration;
import org.ros.namespace.GraphName;
import org.ros.node.ConnectedNode;
import de.unibi.citec.clf.bonsai.ros.RosNode;
import org.ros.exception.RosRuntimeException;
import org.ros.exception.ServiceNotFoundException;
import org.ros.node.service.ServiceClient;


/**
 *
 * @author jkummert
 */
public class RosDetectPeopleActuator extends RosNode implements DetectPeopleActuator {

    String topic;
    String followRoiTopic;

    private GraphName nodeName;
    private org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(getClass());

    private ServiceClient<GetCrowdAttributesWithPoseRequest, GetCrowdAttributesWithPoseResponse> clientTrigger;
    private ServiceClient<GetFollowRoiRequest, GetFollowRoiResponse> clientFollowTrigger;

    private ActionClient<GetCrowdAttributesWithPoseActionGoal,
            GetCrowdAttributesWithPoseActionFeedback,
            GetCrowdAttributesWithPoseActionResult> actionClient;

    private boolean actionServerAvailable = false;

    public RosDetectPeopleActuator(GraphName gn) {
        initialized = false;
        this.nodeName = gn;
    }
    
    @Override
    public void configure(IObjectConfigurator conf) throws ConfigurationException {
        this.topic = conf.requestValue("topic");
        this.followRoiTopic = conf.requestValue("followRoiTopic");
    }

    @Override
    public GraphName getDefaultNodeName() {
        return nodeName;
    }

    @Override
    public void onStart(final ConnectedNode connectedNode) {
        try {
            clientTrigger           = connectedNode.newServiceClient(topic, GetCrowdAttributesWithPose._TYPE);
            clientFollowTrigger     = connectedNode.newServiceClient(followRoiTopic, GetFollowRoi._TYPE);
        } catch (ServiceNotFoundException e) {
            throw new RosRuntimeException(e);
        }

        actionClient = new ActionClient(connectedNode, this.topic, GetCrowdAttributesWithPoseActionGoal._TYPE,
                   GetCrowdAttributesWithPoseActionFeedback._TYPE, GetCrowdAttributesWithPoseActionResult._TYPE);
        actionServerAvailable = actionClient.waitForActionServerToStart(Duration.fromMillis(4000));
        logger.debug("ACTION SERVER AVAILABLE: "+actionServerAvailable);

        initialized = true;
    }

    @Override
    public void destroyNode() {
        if(clientTrigger!=null) clientTrigger.shutdown();
        if(clientFollowTrigger!=null) clientFollowTrigger.shutdown();
        if(actionClient!=null) actionClient.finish();
    }

    @Override
    public Future<PersonDataList> getPeople() {
        final GetCrowdAttributesWithPoseRequest req = clientTrigger.newMessage();
        //set data
        final ResponseFuture<GetCrowdAttributesWithPoseResponse> res = new ResponseFuture<>();
        clientTrigger.call(req, res);

        return new Future<PersonDataList>() {
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
            public PersonDataList get() throws InterruptedException, ExecutionException {

                return getListFromResponse(res.get().getAttributes());
            }

            @Override
            public PersonDataList get(long l,  TimeUnit timeUnit) throws InterruptedException, ExecutionException, TimeoutException {

                return getListFromResponse(res.get(l, timeUnit).getAttributes());
            }
        };
    }

    @Override
    public Future<PersonDataList> getPeople(boolean do_gender_and_age, boolean do_face_id) {
        return getPeople(do_gender_and_age, do_face_id, 0.0f);
    }

    /**
     *
     * @param do_gender_and_age
     * @param do_face_id
     * @param resize_out_ratio 4.0 -> 2secs; 8.0 -> 4secs but much improved results
     * @return
     */
    @Override
    public Future<PersonDataList> getPeople(boolean do_gender_and_age, boolean do_face_id, float resize_out_ratio) {
        if(!actionServerAvailable){
            logger.debug("ACTION CALL NOT AVAILABLE, TRYING SERVICE CALL");
            return getPeople();
        }else{
            logger.debug("TRYING ACTION CALL!!");
            GetCrowdAttributesWithPoseActionGoal msg = actionClient.newGoalMessage();
            GetCrowdAttributesWithPoseGoal goal = msg.getGoal();
            goal.setFaceId(do_face_id);
            goal.setGenderAndAge(do_gender_and_age);
            goal.setResizeOutRatio(resize_out_ratio);

            ActionFuture<GetCrowdAttributesWithPoseActionGoal,
                    GetCrowdAttributesWithPoseActionFeedback,
                    GetCrowdAttributesWithPoseActionResult> fut = actionClient.sendGoal(msg);
            return new Future<PersonDataList>() {
                @Override
                public boolean cancel(boolean b) {
                    return fut.cancel(b);
                }

                @Override
                public boolean isCancelled() {
                    return fut.isCancelled();
                }

                @Override
                public boolean isDone() {
                    return fut.isDone();
                }

                @Override
                public PersonDataList get() throws InterruptedException, ExecutionException {

                    return getListFromResponse(fut.get().getResult().getAttributes());
                }

                @Override
                public PersonDataList get(long l,  TimeUnit timeUnit) throws InterruptedException, ExecutionException, TimeoutException {

                    return getListFromResponse(fut.get(l, timeUnit).getResult().getAttributes());
                }
            };
        }
    }

    @Override
    public Future<List<Integer>> getFollowROI(){
        final GetFollowRoiRequest req = clientFollowTrigger.newMessage();
        //set data
        final ResponseFuture<GetFollowRoiResponse> res = new ResponseFuture<>();
        clientFollowTrigger.call(req, res);

        return new Future<List<Integer>>() {
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
            public List<Integer> get() throws InterruptedException, ExecutionException {
                return getListFromResponse(res.get());
            }

            @Override
            public List<Integer> get(long l,  TimeUnit timeUnit) throws InterruptedException, ExecutionException, TimeoutException {

                return getListFromResponse(res.get(l, timeUnit));
            }
        };
    }

    private PersonDataList getListFromResponse(List<PersonAttributesWithPose> attributes){
        PersonDataList list = new PersonDataList();
        for (PersonAttributesWithPose attribute : attributes) {
            try {
                list.add(MsgTypeFactory.getInstance().createType(attribute, PersonData.class));
            } catch (RosSerializer.DeserializationException ex) {
                logger.error("could not serialize GetCrowdAttributesWithPose Response");
            }
        }
        return list;
    }

    private List<Integer> getListFromResponse(GetFollowRoiResponse response){
        List<Integer> list = new ArrayList<>();
        list.add(response.getRoi().getXOffset());
        list.add(response.getRoi().getYOffset());
        list.add(response.getRoi().getHeight());
        list.add(response.getRoi().getWidth());
        return list;
    }
}
