package de.unibi.citec.clf.btl.rst.serializers.navigation;



import com.google.protobuf.GeneratedMessage;
import de.unibi.citec.clf.btl.data.navigation.CommandResult;
import de.unibi.citec.clf.btl.rst.RstSerializer;
import rst.navigation.CommandResultType;

/**
 *
 * @author cklarhor
 */
public class CommandResultSerializer extends RstSerializer<CommandResult, CommandResultType.CommandResult> {

    @Override
    public CommandResult deserialize(CommandResultType.CommandResult msg) {
        CommandResult.Result resultType = CommandResult.Result.valueOf(msg.getType().name());
        return new CommandResult(msg.getDescription(),resultType,msg.getCode());
    }

    @Override
    public void serialize(CommandResult data, GeneratedMessage.Builder<?> abstractBuilder) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Class<CommandResultType.CommandResult> getMessageType() {
        return CommandResultType.CommandResult.class;
    }

    @Override
    public Class<CommandResult> getDataType() {
        return CommandResult.class;
    }

  
}
