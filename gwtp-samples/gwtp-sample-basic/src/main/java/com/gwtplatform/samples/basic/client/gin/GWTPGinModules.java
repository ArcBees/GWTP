package com.gwtplatform.samples.basic.client.gin;

import com.google.gwt.inject.client.GinModule;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation to put on {@code @Ginjector} subtypes to indicate which
 * {@code GinModule} implementations to use. List the {@link com.google.gwt.inject.client.GinModule} classes
 * using the {@code value} parameter. If you wish to specify gin module classes
 * from a GWT module file, list the name of the configuration properties as
 * string using the {@code properties} parameter.
 *
 * <p>Example:
 * <pre>  @GinModules(value=MyGinModule.class, properties="example.ginModules")
 *  public interface ConfigurationModulesGinjector extends Ginjector {
 *    // ...
 *  }</pre>
 *
 * In <b>MyApp.gwt.xml</b>:
 * <pre>  &lt;define-configuration-property name="example.ginModules" is-multi-valued="true" /&gt;
 *  &lt;extend-configuration-property name="example.ginModules"
 *      value="com.company.myapp.client.ExampleModule1" /&gt;
 *  &lt;extend-configuration-property name="example.ginModules"
 *      value="com.company.myapp.client.ExampleModule2" /&gt;</pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface GWTPGinModules {
  Class<? extends GinModule>[] value();
  String[] properties() default {};
}
