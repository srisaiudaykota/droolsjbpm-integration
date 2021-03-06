package org.jbpm.simulation.handler;

import java.util.List;

import org.eclipse.bpmn2.CompensateEventDefinition;
import org.eclipse.bpmn2.ErrorEventDefinition;
import org.eclipse.bpmn2.EventDefinition;
import org.eclipse.bpmn2.FlowElement;
import org.eclipse.bpmn2.LinkEventDefinition;
import org.eclipse.bpmn2.MessageEventDefinition;
import org.eclipse.bpmn2.SignalEventDefinition;
import org.jbpm.simulation.PathContext;
import org.jbpm.simulation.PathContextManager;

public class ThrowEventElementHandler extends DefaultElementHandler {

    @Override
    public boolean handle(FlowElement element, PathContextManager manager) {
        List<EventDefinition> throwDefinitions = getEventDefinitions(element);
    
        if (throwDefinitions != null && throwDefinitions.size() > 0) {
            for (EventDefinition def : throwDefinitions) {
                String key = "";
                if (def instanceof SignalEventDefinition) {
                    key = ((SignalEventDefinition) def).getSignalRef();
                } else if (def instanceof MessageEventDefinition) {
                    key = ((MessageEventDefinition) def).getMessageRef()
                            .getId();
                } else if (def instanceof LinkEventDefinition) {
                    key = ((LinkEventDefinition) def).getName();
                } else if (def instanceof CompensateEventDefinition) {
                    key = ((CompensateEventDefinition) def)
                            .getActivityRef().getId();
                } else if (def instanceof ErrorEventDefinition) {
                    key = ((ErrorEventDefinition) def)
                            .getErrorRef().getId();
                }

                FlowElement catchEvent = manager.getCatchingEvents().get(key);
                if (catchEvent != null) {
                    PathContext context = manager.getContextFromStack();
                    boolean canBeFinished = context.isCanBeFinished();
                    context.setCanBeFinished(false);
                    super.handle(catchEvent, manager);
                    context.setCanBeFinished(canBeFinished);
                }
            }

        }
        return super.handle(element, manager);

    }

}
