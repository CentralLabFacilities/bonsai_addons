
package de.unibi.citec.clf.btl.xml.serializers.command;

import de.unibi.citec.clf.btl.data.command.TaskData;
import de.unibi.citec.clf.btl.xml.XomSerializer;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.ParsingException;



/**
 *
 * @author skoester
 */
public class TaskDataSerializer extends XomSerializer<TaskData>{

    public TaskDataSerializer() {
        super();
        
    }

    @Override
    public TaskData doFromElement(Element element) throws ParsingException, DeserializationException {
        TaskData taskData= new TaskData();
        
        String tsk = element.getFirstChildElement("TASK").getValue();
        String act = element.getFirstChildElement("ACTION").getValue();
        String obj = element.getFirstChildElement("OBJECT").getValue();
        
        try {
            taskData.setId(Integer.parseInt(element.getFirstChildElement("ID").getValue()));
            taskData.setTextpos(Integer.parseInt(element.getFirstChildElement("TEXTPOS").getValue()));
            taskData.setTextpos(Integer.parseInt(element.getFirstChildElement("OCC").getValue()));
        } catch (NumberFormatException e) {
            System.err.println(e);
        }
        
        taskData.setTask(tsk);
        taskData.setAction(act);
        taskData.setObject(obj);
        
        return taskData;
    }

    @Override
    public void doSanitizeElement(Element parent) {
    }

    @Override
    public void doFillInto(TaskData data, Element parent) throws SerializationException {
        Elements task = parent.getChildElements("TASK");
        for (int i = 0; i < task.size(); i++) {
            parent.removeChild(task.get(i));
        }
        Elements acts = parent.getChildElements("ACTION");
        for (int i = 0; i < acts.size(); i++) {
            parent.removeChild(acts.get(i));
        }
        Elements obj = parent.getChildElements("OBJECT");
        for (int i = 0; i < obj.size(); i++) {
            parent.removeChild(obj.get(i));
        }
        Elements id = parent.getChildElements("ID");
        for (int i = 0; i < id.size(); i++) {
            parent.removeChild(id.get(i));
        }
        Elements textpos = parent.getChildElements("TEXTPOS");
        for (int i = 0; i < textpos.size(); i++) {
            parent.removeChild(textpos.get(i));
        }        
        Elements occ = parent.getChildElements("OCC");
        for (int i = 0; i < occ.size(); i++) {
            parent.removeChild(occ.get(i));
        }
        
        if (parent.getChildElements("TASK").size() == 0) {
            Element e = new Element("TASK");
            parent.appendChild(e);
        }
        if (parent.getChildElements("ACTION").size() == 0) {
            Element e = new Element("ACTION");
            parent.appendChild(e);
        }
        if (parent.getChildElements("OBJECT").size() == 0) {
            Element e = new Element("OBJECT");
            parent.appendChild(e);
        }
        if (parent.getChildElements("ID").size() == 0) {
            Element e = new Element("ID");
            parent.appendChild(e);
        }
        if (parent.getChildElements("TEXTPOS").size() == 0) {
            Element e = new Element("TEXTPOS");
            parent.appendChild(e);
        }
        if (parent.getChildElements("OCC").size() == 0) {
            Element e = new Element("OCC");
            parent.appendChild(e);
        }
        
        parent.getFirstChildElement("TASK").appendChild(data.getTask());
        parent.getFirstChildElement("ACTION").appendChild(data.getAction());
        parent.getFirstChildElement("OBJECT").appendChild(data.getObject());
        parent.getFirstChildElement("ID").appendChild(String.valueOf(data.getId()));
        parent.getFirstChildElement("TEXTPOS").appendChild(String.valueOf(data.getTextpos()));
        parent.getFirstChildElement("OCC").appendChild(String.valueOf(data.getOcc()));
    }

    @Override
    public Class<TaskData> getDataType() {
        return TaskData.class;
    }

    @Override
    public String getBaseTag() {
        return "TASKDATA";
    }


    
}
