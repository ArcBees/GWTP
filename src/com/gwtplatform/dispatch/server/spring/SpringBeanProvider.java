package com.gwtplatform.dispatch.server.spring;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.context.ApplicationContext;

import com.gwtplatform.dispatch.server.actionHandlerValidator.ActionHandlerValidatorLinkerHelper.BeanProvider;
import com.gwtplatform.dispatch.server.actionHandlerValidator.ActionHandlerValidatorLinkerHelper.CommonBindingDescriptor;
import com.gwtplatform.dispatch.server.spring.utils.SpringUtils;

public class SpringBeanProvider implements BeanProvider {

	/**
	 * Adapter for tranforming Guice Binding into BeanProvider implementation
	 * 
	 * @author Peter Simun (simun@seges.sk)
	 */
	public static class SpringBindingDescriptorAdapter<B> extends CommonBindingDescriptor<B> {

		public SpringBindingDescriptorAdapter(Entry<String, B> binding) {
			super(binding.getValue(), binding.getKey());
		}
	}

	private ApplicationContext applicationContext;

	public SpringBeanProvider(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	@Override
	public <B> B getInstance(Class<B> clazz) {
		return SpringUtils.getInstance(applicationContext, clazz);
	}

	@Override
	public <B> Iterator<BindingDescriptor<B>> getBindings(Class<B> clazz) {

		List<BindingDescriptor<B>> result = new ArrayList<BindingDescriptor<B>>();

		Map<String, B> beansOfType = applicationContext.getBeansOfType(clazz);

		for (Entry<String, B> beans : beansOfType.entrySet()) {
			result.add(new SpringBindingDescriptorAdapter<B>(beans));
		}

		return result.iterator();
	}
}