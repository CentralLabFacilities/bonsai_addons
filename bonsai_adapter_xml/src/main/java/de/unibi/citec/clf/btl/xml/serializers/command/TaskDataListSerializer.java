
package de.unibi.citec.clf.btl.xml.serializers.command;



import de.unibi.citec.clf.btl.data.command.TaskData;
import de.unibi.citec.clf.btl.data.command.TaskDataList;
import de.unibi.citec.clf.btl.xml.XomListSerializer;
import de.unibi.citec.clf.btl.xml.XomSerializer;

/**
 *
 * @author skoester
 */
public class TaskDataListSerializer extends XomListSerializer<TaskData, TaskDataList>{

    @Override
    public XomSerializer<TaskData> getItemSerializer() {
        return new TaskDataSerializer();
    }

    @Override
    public TaskDataList getDefaultInstance() {
        return new TaskDataList();
    }
    
}
