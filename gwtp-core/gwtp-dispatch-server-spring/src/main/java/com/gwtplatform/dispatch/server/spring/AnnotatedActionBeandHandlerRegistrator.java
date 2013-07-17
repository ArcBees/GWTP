/**
*
* Licensed under the Apache License, Version 2.0 (the "License"); you may not
* use this file except in compliance with the License. You may obtain a copy of
* the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
* WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
* License for the specific language governing permissions and limitations under
* the License.
*/
package com.gwtplatform.dispatch.server.spring;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.core.Ordered;

import com.gwtplatform.dispatch.server.actionhandler.ActionHandler;
import com.gwtplatform.dispatch.server.actionhandlervalidator.ActionHandlerValidatorClass;
import com.gwtplatform.dispatch.server.actionhandlervalidator.ActionHandlerValidatorMapImpl;
import com.gwtplatform.dispatch.server.actionhandlervalidator.ActionHandlerValidatorRegistry;
import com.gwtplatform.dispatch.server.actionhandlervalidator.LazyActionHandlerValidatorRegistry;
import com.gwtplatform.dispatch.server.spring.annotation.RegisterActionHandler;
import com.gwtplatform.dispatch.server.spring.utils.SpringUtils;

/**
 * Annotation bean post processing to register ActionHadlers annotate
 * with {@link RegisterActionHandler}.
 * @author David Ignjic
 *
 */
public class AnnotatedActionBeandHandlerRegistrator implements BeanPostProcessor, Ordered {

    @Autowired
    protected ApplicationContext applicationContext;

    @Autowired
    protected ActionHandlerValidatorRegistry actionHandlerValidatorRegistry;

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        if ((bean instanceof ActionHandler<?, ?>)) {
            ActionHandler<?, ?> actionHandler = (ActionHandler<?, ?>) bean;
            RegisterActionHandler registerHandler = bean.getClass().getAnnotation(RegisterActionHandler.class);
            if (registerHandler != null) {
                ActionHandlerValidatorClass actionHandlerValidatorClass = new ActionHandlerValidatorClass(actionHandler.getClass(),
                        registerHandler.validator());
                SpringUtils.registerBean(applicationContext, new ActionHandlerValidatorMapImpl(actionHandler.getActionType(),
                        actionHandlerValidatorClass));
                if (actionHandlerValidatorRegistry instanceof LazyActionHandlerValidatorRegistry) {
                    ((LazyActionHandlerValidatorRegistry) actionHandlerValidatorRegistry).addActionHandlerValidatorClass(actionHandler.getActionType(), actionHandlerValidatorClass);
                }
            }
        }

        return bean;
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }
}
