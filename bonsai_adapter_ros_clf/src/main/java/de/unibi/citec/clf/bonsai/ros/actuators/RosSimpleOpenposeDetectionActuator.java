
package de.unibi.citec.clf.bonsai.ros.actuators;


import de.unibi.citec.clf.bonsai.actuators.DetectPeopleActuator;
import de.unibi.citec.clf.bonsai.core.configuration.IObjectConfigurator;
import de.unibi.citec.clf.bonsai.core.exception.ConfigurationException;
import de.unibi.citec.clf.bonsai.ros.helper.ResponseFuture;

import java.util.List;
import de.unibi.citec.clf.btl.data.person.PersonData;
import de.unibi.citec.clf.btl.data.person.PersonDataList;
import de.unibi.citec.clf.btl.ros.MsgTypeFactory;
import de.unibi.citec.clf.btl.ros.RosSerializer;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import openpose_ros_msgs.GetFollowRoiResponse;
import clf_person_recognition_msgs.*;
import org.apache.commons.lang.NotImplementedException;
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
public class RosSimpleOpenposeDetectionActuator extends RosNode implements DetectPeopleActuator {

    String topic;
    private GraphName nodeName;
    private org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(getClass());

    private ServiceClient<GetCrowdAttributesWithPoseRequest, GetCrowdAttributesWithPoseResponse> clientTrigger;


    public RosSimpleOpenposeDetectionActuator(GraphName gn) {
        initialized = false;
        this.nodeName = gn;
    }
    
    @Override
    public void configure(IObjectConfigurator conf) throws ConfigurationException {
        this.topic = conf.requestValue("topic");
    }

    @Override
    public GraphName getDefaultNodeName() {
        return nodeName;
    }

    @Override
    public void onStart(final ConnectedNode connectedNode) {
        try {
            clientTrigger           = connectedNode.newServiceClient(topic, GetCrowdAttributesWithPose._TYPE);
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
    public Future<PersonDataList> getPeople() {
        final GetCrowdAttributesWithPoseRequest req = clientTrigger.newMessage();
        //set data
        final ResponseFuture<GetCrowdAttributesWithPoseResponse> res = new ResponseFuture<>();
        clientTrigger.call(req, res);

        return new PersonDataListFuture(res);
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
        return getPeople();
    }

    @Override
    public Future<List<Integer>> getFollowROI(){
       throw new NotImplementedException("not implemented");
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
        throw new NotImplementedException("TODO");
    }

    private class PersonDataListFuture implements Future<PersonDataList> {
        private final ResponseFuture<GetCrowdAttributesWithPoseResponse> res;

        public PersonDataListFuture(ResponseFuture<GetCrowdAttributesWithPoseResponse> res) {
            this.res = res;
        }

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
    }
}
