package com.philbeaudoin.gwtp.dispatch.server.actionHandlerValidator;

import java.util.List;

import com.google.inject.Binding;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.TypeLiteral;
import com.philbeaudoin.gwtp.dispatch.server.actionHandler.ActionHandler;
import com.philbeaudoin.gwtp.dispatch.server.actionValidator.ActionValidator;
import com.philbeaudoin.gwtp.dispatch.server.actionValidator.ActionValidatorRegistry;

/**
 * This class links any registered {@link ActionValidator} instances with
 * the default {@link ActionValidatorRegistry}
 * 
 * @author Christian Goudreau
 * 
 */
public class ActionHandlerValidatorLinker {
  private ActionHandlerValidatorLinker() {}
  
  @SuppressWarnings("unchecked")
  @Inject
  public static void linkValidators(Injector injector, ActionHandlerValidatorRegistry registry) {
      List<Binding<ActionHandlerValidatorMap>> bindings = injector.findBindingsByType(TypeLiteral.get(ActionHandlerValidatorMap.class));

      if (registry instanceof InstanceActionHandlerValidatorRegistry) {
        InstanceActionHandlerValidatorRegistry instanceRegistry = (InstanceActionHandlerValidatorRegistry) registry;

          for (Binding<ActionHandlerValidatorMap> binding : bindings) {
              Class<? extends ActionValidator> actionValidatorClass = binding.getProvider().get().getActionHandlerValidatorClass().getActionValidatorClass();
              Class<? extends ActionHandler<?, ?>> handlerClass = binding.getProvider().get().getActionHandlerValidatorClass().getActionHandlerClass();
              
              ActionHandlerValidatorInstance actionHandlerValidatorInstance = new ActionHandlerValidatorInstance(
                  injector.getInstance(actionValidatorClass), 
                  injector.getInstance(handlerClass));
              
              instanceRegistry.addActionHandlerValidator(binding.getProvider().get().getActionClass(), actionHandlerValidatorInstance);
          }
      } else if (registry instanceof ClassActionHandlerValidatorRegistry) {
        ClassActionHandlerValidatorRegistry classRegistry = (ClassActionHandlerValidatorRegistry) registry;

          for (Binding<ActionHandlerValidatorMap> binding : bindings) {
            ActionHandlerValidatorMap map = binding.getProvider().get();
              classRegistry.addActionHandlerValidatorClass(map.getActionClass(), map.getActionHandlerValidatorClass());
          }
      }
  }  
}
