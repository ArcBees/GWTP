package com.gwtplatform.dispatch.server.guice;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.inject.Binding;
import com.google.inject.Injector;
import com.google.inject.TypeLiteral;
import com.gwtplatform.dispatch.server.actionHandlerValidator.ActionHandlerValidatorLinkerHelper.BeanProvider;
import com.gwtplatform.dispatch.server.actionHandlerValidator.ActionHandlerValidatorLinkerHelper.CommonBindingDescriptor;

public class GuiceBeanProvider implements BeanProvider {

	/**
	 * Adapter for tranforming Guice Binding into BeanProvider implementation
	 * 
	 * @author Peter Simun (simun@seges.sk)
	 */
	public static class GuiceBindingDescriptorAdapter<B> extends CommonBindingDescriptor<B> {

		public GuiceBindingDescriptorAdapter(Binding<B> binding) {
			super(binding.getProvider().get(), binding.getKey().toString());
		}
	}

	private Injector injector;

	public GuiceBeanProvider(Injector injector) {
		this.injector = injector;
	}

	@Override
	public <B> B getInstance(Class<B> clazz) {
		return injector.getInstance(clazz);
	}

	@Override
	public <B> Iterator<BindingDescriptor<B>> getBindings(Class<B> clazz) {

		List<BindingDescriptor<B>> result = new ArrayList<BindingDescriptor<B>>();

		for (Binding<B> binding : injector.findBindingsByType(TypeLiteral.get(clazz))) {
			result.add(new GuiceBindingDescriptorAdapter<B>(binding));
		}

		return result.iterator();
	}
}