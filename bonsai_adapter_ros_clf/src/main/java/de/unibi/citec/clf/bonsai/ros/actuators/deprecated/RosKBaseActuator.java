package de.unibi.citec.clf.bonsai.ros.actuators.deprecated;

import de.unibi.citec.clf.bonsai.actuators.deprecated.KBaseActuator;
import de.unibi.citec.clf.bonsai.core.configuration.IObjectConfigurator;
import de.unibi.citec.clf.bonsai.core.exception.ConfigurationException;
import de.unibi.citec.clf.bonsai.ros.RosNode;
import de.unibi.citec.clf.bonsai.ros.helper.ResponseFuture;
import de.unibi.citec.clf.btl.List;
import de.unibi.citec.clf.btl.Type;
import de.unibi.citec.clf.btl.data.geometry.Point2D;
import de.unibi.citec.clf.btl.data.knowledgebase.*;
import de.unibi.citec.clf.btl.data.map.Viewpoint;
import de.unibi.citec.clf.btl.data.person.PersonData;
import de.unibi.citec.clf.btl.xml.XomSerializer;
import de.unibi.citec.clf.btl.xml.XomTypeFactory;
import knowledge_base_msgs.*;
import nu.xom.Builder;
import nu.xom.ParsingException;
import org.ros.exception.ServiceNotFoundException;
import org.ros.namespace.GraphName;
import org.ros.node.ConnectedNode;
import org.ros.node.service.ServiceClient;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author ffriese
 * @author rfeldhans
 */
@Deprecated
public class RosKBaseActuator extends RosNode implements KBaseActuator {

    String querytopic;
    String datatopic;
    private GraphName nodeName;
    private ServiceClient<QueryRequest, QueryResponse> sc;
    private ServiceClient<DataRequest, DataResponse> scd;
    private org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(getClass());
    private long timeout = 8000;

    public RosKBaseActuator(GraphName gn) {
        initialized = false;
        this.nodeName = gn;
    }

    @Override
    public void configure(IObjectConfigurator conf) throws ConfigurationException {
        this.querytopic = conf.requestValue("queryTopic");
        this.datatopic = conf.requestValue("dataTopic");
    }


    @Override
    public GraphName getDefaultNodeName() {
        return nodeName;
    }

    @Override
    public void onStart(final ConnectedNode connectedNode) {
        try {
            sc = connectedNode.newServiceClient(this.querytopic, Query._TYPE);
            scd = connectedNode.newServiceClient(this.datatopic, Data._TYPE);
        } catch (ServiceNotFoundException e) {
            logger.error(e.getMessage());
            initialized = false;
        }
        initialized = true;
        logger.debug("on start, RosKBaseActuator done");
    }

    @Override
    public void destroyNode() {
        if (sc != null) sc.shutdown();
        if (scd != null) scd.shutdown();
    }

    private QueryRequest generateQuery(String query) {
        QueryRequest req = sc.newMessage();
        req.setQuery(query);
        return req;
    }

    private DataRequest generateData(String data) {
        DataRequest req = scd.newMessage();
        req.setCommand(data);
        return req;
    }

    private <T extends Type> List<T> parseListFromXML(String xml, Class<T> type) {
        try {
            return XomTypeFactory.getInstance().createTypeList(new Builder().build(xml, null), type);
        } catch (XomSerializer.DeserializationException | ParsingException | IOException e) {
            e.printStackTrace();
            throw new RuntimeException("DESERIALIZING " + type.getName() + " OBJECT FAILED!");
        }
    }

    private <T extends Type> T parseFromXML(String xml, Class<T> type) {
        try {
            return XomTypeFactory.getInstance().createType(new Builder().build(xml, null), type);
        } catch (XomSerializer.DeserializationException | ParsingException | IOException e) {
            e.printStackTrace();
            throw new RuntimeException("DESERIALIZING " + type.getName() + " OBJECT FAILED!");
        }
    }

    private <T extends Type> String parseToXML(T object) {
        try {
            return XomTypeFactory.getInstance().createDocument(object).toXML();
        } catch (XomSerializer.SerializationException e) {
            e.printStackTrace();
            throw new RuntimeException("SERIALIZING " + object.getClass().getName() + " OBJECT FAILED!");
        }
    }

    /*
     * TODO: implement future-stuff instead of 1-sec timeout
     */
    private QueryResponse queryKBase(String query) {
        ResponseFuture<QueryResponse> res = new ResponseFuture<>();
        sc.call(generateQuery(query), res);
        try {
            return res.get(timeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            e.printStackTrace();
            return null;
        }
    }

    private DataResponse dataQueryKBase(String data) {
        ResponseFuture<DataResponse> res = new ResponseFuture<>();
        scd.call(generateData(data), res);
        try {
            return res.get(timeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            e.printStackTrace();
            return null;
        }
    }

    /*
     * Used only if the query cannot throw exceptions
     */
    private <T extends Type> T sendSafeQuery(String query, Class<T> type) {
        QueryResponse r = queryKBase(query);
        if (r.getSuccess()) {
            String answer = r.getAnswer();
            return parseFromXML(answer, type);
        } else {
            try {
                handle_exception((int) r.getErrorCode(), r.getAnswer());
            } catch (BDONotFoundException | NoAreaFoundException | BDOHasInvalidAttributesException e) {
                throw new ImplementationException("incorrect use of 'sendSafeQuery' in RosKBaseActuator. Unhandled " +
                        e.getClass().toString() + ": " + e.getMessage());
            }
            return null;
        }
    }

    /*
     * Use for queries that only throw BDONotFoundException
     */
    private <T extends Type> T sendBDOQuery(String query, Class<T> type) throws BDONotFoundException {
        QueryResponse r = queryKBase(query);
        if (r.getSuccess()) {
            String answer = r.getAnswer();
            return parseFromXML(answer, type);
        } else {
            try {
                handle_exception((int) r.getErrorCode(), r.getAnswer());
            } catch (NoAreaFoundException | BDOHasInvalidAttributesException e) {
                throw new ImplementationException("incorrect use of 'sendBDOQuery' in RosKBaseActuator. Unhandled " +
                        e.getClass().toString() + ": " + e.getMessage());
            }
            return null;
        }
    }

    private <T extends Type> List<T> sendBDOListQuery(String query, Class<T> type) throws BDONotFoundException {
        QueryResponse r = queryKBase(query);
        if (r.getSuccess()) {
            String answer = r.getAnswer();
            return parseListFromXML(answer, type);
        } else {
            try {
                handle_exception((int) r.getErrorCode(), r.getAnswer());
            } catch (NoAreaFoundException | BDOHasInvalidAttributesException e) {
                throw new ImplementationException("incorrect use of 'sendBDOListQuery' in RosKBaseActuator. Unhandled " +
                        e.getClass().toString() + ": " + e.getMessage());
            }
            return null;
        }
    }

    /*
     * Send simple bdo query. used for delete
     */
    private boolean sendSimpleBDOQuery(String query) throws BDONotFoundException {
        QueryResponse r = queryKBase(query);
        if (r.getSuccess()) {
            return true;
        } else {
            try {
                handle_exception((int) r.getErrorCode(), r.getAnswer());
            } catch (NoAreaFoundException | BDOHasInvalidAttributesException e) {
                throw new ImplementationException("incorrect use of 'sendSimpleBDOQuery' in RosKBaseActuator. Unhandled " +
                        e.getClass().toString() + ": " + e.getMessage());
            }
            return false;
        }
    }

    /*
     * Use for queries for Locations and Rooms
     */
    private <T extends Type> T sendAreaQuery(String query, Class<T> type)
            throws BDONotFoundException, NoAreaFoundException {
        QueryResponse r = queryKBase(query);
        if (r.getSuccess()) {
            String answer = r.getAnswer();
            return parseFromXML(answer, type);
        } else {
            try {
                handle_exception((int) r.getErrorCode(), r.getAnswer());
            } catch (BDOHasInvalidAttributesException e) {
                throw new ImplementationException("incorrect use of 'sendAreaQuery' in RosKBaseActuator. Unhandled " +
                        e.getClass().toString() + ": " + e.getMessage());
            }
            return null;
        }
    }

    private boolean sendSavingQuery(String data) throws BDOHasInvalidAttributesException {
        DataResponse res = dataQueryKBase(data);
        if (res.getSuccess()) {
            return true;
        } else {
            try {
                handle_exception((int) res.getErrorCode(), String.valueOf(res.getErrorCode()));
            } catch (NoAreaFoundException | BDONotFoundException e) {
                throw new ImplementationException("incorrect use of 'sendSavingQuery' in RosKBaseActuator. Unhandled " +
                        e.getClass().toString() + ": " + e.getMessage());
            }
            return false;
        }
    }


    /*
     * Send simple query. used for delete
     */
    private boolean sendSimpleQuery(String data) {
        DataResponse res = dataQueryKBase(data);
        if (res.getSuccess()) {
            return true;
        } else {
            try {
                handle_exception((int) res.getErrorCode(), data);
            } catch (NoAreaFoundException | BDONotFoundException | BDOHasInvalidAttributesException e) {
                throw new ImplementationException("incorrect use of 'sendBDOQuery' in RosKBaseActuator. Unhandled " +
                        e.getClass().toString() + ": " + e.getMessage());
            }
            return false;
        }
    }


    private void handle_exception(int error_code, String error_text) throws BDONotFoundException, NoAreaFoundException, BDOHasInvalidAttributesException {
        switch (error_code) {
            case 0:  // "Error in implementation of Actuator: question word not supported or unknown"
            case 41: // "The name of the class of BDO is not viable."
            case 51: // "The name of the class of BDO is not viable."
            case 71: // "The name of the requested Class is not viable."
                throw new ImplementationException(error_text);
            case 11: // "There is no Object with the specified name."
            case 12: // "There is a BDO with the specified name but it is no Object."
            case 21: // "The name of the BDO provided by the query could not be found."
            case 31: // "The name of the BDO provided by the query could not be found."
            case 61: // "There is no Person with the specified name or uuid."
            case 62: // "There is a BDO with the specified name but it is no Person."
            case 83: // "There was no BDO with the specified identifier to be deleted."
                throw new BDONotFoundException(error_text);
            case 32: // "You asked in which Location a specific Room lies, which makes no real sense."
            case 81: // "Command was ill formatted. There was more than tree elements and the second one was not 'all'."
            case 82: // "The specified class of which all objects should be deleted is not allowed or supported."
            case 91: // "The given XML could not be parsed (aka was no valid xml)."
            case 92: // "The given XMLs basetag is not available for converting to a BDO."
            case 93: // "The given XML failed to be converted to a BDO."
                throw new IncorrectQueryException(error_text);
            case 33: // "The BDO/ Point specified by the query does not lie in any room/ location."
                throw new NoAreaFoundException(error_text);
            case 13: // "The type of the requested variable of the object is not xml-ifieable and could thus not send back.";
            case 42: // "The attribute specified by the query is not supported to be returned yet.";
                throw new UnsupportedOperationException("FATAL ERROR in KBase-Implementation: " + error_text);
            case 94: // "The BDO specified a Location that could not be found"
            case 95: // "The BDO specified a Room that could not be found"
                throw new BDOHasInvalidAttributesException("Error in saving to KBase: specified Location or Room not found.");
            default:
                throw new ImplementationException("FATAL ERROR: Error Code " + error_code +
                        " not handled in RosKBaseActuator. (Message: " + error_text + ")");
        }
    }

    @Override
    public Viewpoint getViewpoint(String uniqueId) throws BDONotFoundException, NoAreaFoundException {
        return getViewpoint(uniqueId, "main");
    }

    @Override
    public Viewpoint getViewpoint(String uniqueId, String viewpoint_label) throws BDONotFoundException, NoAreaFoundException {
        String query = "where is the " + uniqueId + " " + viewpoint_label;
        return sendAreaQuery(query, Viewpoint.class);
    }

    @Override
    public RCObject getRCObjectByName(String name) throws BDONotFoundException {
        String query = "what is a " + name;
        return sendBDOQuery(query, RCObject.class);
    }

    /*
     * TODO: CHECK FOR ERRORS, EXCEPTION HANDLING
     */
    @Override
    public String getRCObjectAttribute(String objectName, String attribute_name) {
        String query = "what is the " + attribute_name + " of the " + objectName;
        QueryResponse res = queryKBase(query);

        if (res.getSuccess()) {
            try {
                String value = new Builder().build(res.getAnswer(), null).
                        getRootElement().getAttributeValue("val");
                return value;
            } catch (ParsingException | IOException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    @Override
    public <T extends BDO> List<T> getBDOByAttribute(Class<T> type, String attribute, String value)
            throws BDONotFoundException {
        String query = "which " + type.getSimpleName() + " has " + attribute + " " + value;
        return sendBDOListQuery(query, type);
    }

    @Override
    public <T extends BDO> List<T> getBDOByName(Class<T> type, String name) throws BDONotFoundException {
        String query = "which " + type.getSimpleName() + " has name " + name;
        return sendBDOListQuery(query, type);
    }

    @Override
    public PersonData getPersonByName(String name) throws BDONotFoundException {
        String query = "who is " + name;
        return sendBDOQuery(query, PersonData.class);
    }

    @Override
    public Location getLocationForBDO(String objectId) throws BDONotFoundException, NoAreaFoundException {
        String query = "in which location is the " + objectId;
        return sendAreaQuery(query, Location.class);
    }

    @Override
    public Room getRoomForBDO(String objectName) throws BDONotFoundException, NoAreaFoundException {
        String query = "in which room is the " + objectName;
        return sendAreaQuery(query, Room.class);
    }

    @Override
    public Room getRoomForPoint(Point2D point) throws BDONotFoundException, NoAreaFoundException {
        Point2D point_to_ignore_subclass = new Point2D(point);
        String point_xml = parseToXML(point_to_ignore_subclass);
        String query = "in which room is the point " + point_xml;
        return sendAreaQuery(query, Room.class);
    }

    @Override
    public Location getLocationForPoint(Point2D point) throws BDONotFoundException, NoAreaFoundException {
        Point2D point_to_ignore_subclass = new Point2D(point);
        String point_xml = parseToXML(point_to_ignore_subclass);
        String query = "in which location is the point " + point_xml;
        return sendAreaQuery(query, Location.class);
    }

    /*
     * TODO: CHECK FOR ERRORS, EXCEPTION HANDLING
     */
    @Override
    public <T extends BDO> int getNumberOfDistinctAttributes(Class<T> type, String attribute) {
        String query = "how many " + attribute + " has " + type.getSimpleName();

        QueryResponse res = queryKBase(query);

        if (res.getSuccess()) {
            try {
                String value = new Builder().build(res.getAnswer(), null).
                        getRootElement().getAttributeValue("val");
                return Integer.parseInt(value);
            } catch (ParsingException | IOException e) {
                e.printStackTrace();
            }
        }
        return -1;
    }

    @Override
    public KBase getKBase() {
        String query = "get the kbase";
        return sendSafeQuery(query, KBase.class);
    }

    @Override
    public Arena getArena() {
        String query = "get the arena";
        return sendSafeQuery(query, Arena.class);
    }

    @Override
    public RCObjects getAllObjects() {
        String query = "get the objects";
        return sendSafeQuery(query, RCObjects.class);
    }

    @Override
    public Crowd getCrowd() {
        String query = "get the crowd";
        return sendSafeQuery(query, Crowd.class);
    }


    @Override
    public <T extends BDO> boolean storeBDO(T object) throws BDOHasInvalidAttributesException {
        String obj_xml = parseToXML(object);

        return sendSavingQuery("remember " + obj_xml);
    }

    @Override
    public <T extends BDO> boolean deleteBDO(T object) throws BDONotFoundException {
        String obj_xml = parseToXML(object);

        return sendSimpleBDOQuery("forget " + obj_xml);
    }

}
