package com.gwtplatform.mvp.client;

/**
 * ApplicationController when bound in your .gwt.xml will trigger the generation of your Ginjector. To activate the
 * generation of your Ginjector, remove this line from your module.gwt.xml file:
 * <pre>{@code
 * <inherits name='com.gwtplatform.mvp.Mvp'/>
 * }</pre>
 * and replace it by:
 * <pre>{@code
 * <inherits name='com.gwtplatform.mvp.MvpGinjectorGenerator'/>
 * }</pre>
 *
 * The next step is to replace on your Ginjector the annotation {@link com.google.gwt.inject.client.GinModules
 * GinModules} by {@link com.gwtplatform.mvp.client.annotations.GWTPGinModules GWTPGinModules}.
 * <br/>
 * The final step is to call {@code GWTP.create(ApplicationController.class)} inside your entry point.
 *
 */
public interface ApplicationController {
}
