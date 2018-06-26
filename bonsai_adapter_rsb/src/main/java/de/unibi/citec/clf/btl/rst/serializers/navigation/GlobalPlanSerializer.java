

package de.unibi.citec.clf.btl.rst.serializers.navigation;



import com.google.protobuf.GeneratedMessage;
import de.unibi.citec.clf.btl.data.navigation.GlobalPlan;
import de.unibi.citec.clf.btl.data.navigation.NavigationGoalData;
import de.unibi.citec.clf.btl.rst.RstSerializer;
import de.unibi.citec.clf.btl.units.AngleUnit;
import de.unibi.citec.clf.btl.units.LengthUnit;
import rst.geometry.PoseType;
import rst.geometry.RotationType;
import rst.geometry.TranslationType;
import rst.navigation.PathType;


/**
 *
 * @author alangfel
 */
public class GlobalPlanSerializer extends RstSerializer<GlobalPlan, PathType.Path>{

    private final int WAYPOINTS_PER_MESSAGE = 10;
    
    @Override
    public GlobalPlan deserialize(PathType.Path msg) throws DeserializationException {
        
        GlobalPlan global = new GlobalPlan();
        for(PoseType.Pose pose : msg.getPosesList()) {
            //TODO Transoform poses???
            
            
            NavigationGoalData waypoint = new NavigationGoalData();
            waypoint.setX(pose.getTranslation().getX(), LengthUnit.METER);
            waypoint.setY(pose.getTranslation().getY(), LengthUnit.METER);
            
            //y of rotation is the yaw.
            waypoint.setYaw(pose.getRotation().getQy(), AngleUnit.RADIAN);
            
            global.add(waypoint);
        }
        return global;
    }

    @Override
    public void serialize(GlobalPlan data, GeneratedMessage.Builder<?> abstractBuilder) throws SerializationException {
        PathType.Path.Builder builder = (PathType.Path.Builder) abstractBuilder;
        
        // skip some points for efficiency
	int step = data.size() / WAYPOINTS_PER_MESSAGE;
	if (step == 0)
		++step;
        
        for(int i = 0; i < data.size(); i = i + step) {
            NavigationGoalData navGoal = data.get(i);
            PoseType.Pose.Builder poseb = PoseType.Pose.newBuilder();
            
            RotationType.Rotation.Builder rotationb = RotationType.Rotation.newBuilder();
            rotationb.setQw(0.0);
            rotationb.setQx(0.0);
            rotationb.setQy(navGoal.getYaw(AngleUnit.RADIAN));
            rotationb.setQz(0.0);
            
            RotationType.Rotation rotation = rotationb.build();
            poseb.setRotation(rotation);
            
            TranslationType.Translation.Builder translationb = TranslationType.Translation.newBuilder();
            translationb.setX(navGoal.getX(LengthUnit.METER));
            translationb.setY(navGoal.getY(LengthUnit.METER));
            translationb.setZ(0.0);
            
            TranslationType.Translation translation = translationb.build();
            poseb.setTranslation(translation);
            
            PoseType.Pose pose = poseb.build();
            
            builder.addPoses(i, pose);
        }
    }

    @Override
    public Class<PathType.Path> getMessageType() {
        return PathType.Path.class;
    }

    @Override
    public Class<GlobalPlan> getDataType() {
        return GlobalPlan.class;
    }
    
}
