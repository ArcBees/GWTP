/**
 * Copyright 2011 ArcBees Inc.
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

package com.gwtplatform.mvp.rebind.util;

import com.google.gwt.dev.javac.testing.impl.MockJavaResource;
import com.google.gwt.dev.resource.Resource;
import com.google.gwt.dev.util.RealJavaResource;
import com.google.gwt.inject.client.GinModule;
import com.gwtplatform.mvp.client.ApplicationController;
import com.gwtplatform.mvp.client.Bootstrapper;
import com.gwtplatform.mvp.client.PreBootstrapper;

/**
 * Contains GWTP and dependency sources for testing.
 */
public class GwtpResourceBase {
    public static final MockJavaResource DEFAULT_BOOTSTRAPPER =
            new MockJavaResource("com.gwtplatform.mvp.client.DefaultBootstrapper") {
                @Override
                public CharSequence getContent() {
                    StringBuilder code = new StringBuilder();
                    code.append("package com.gwtplatform.mvp.client;\n");
                    code.append("public class DefaultBootstrapper implements Bootstrapper {\n");
                    code.append("  public void onBootstrap() {};\n");
                    code.append("}\n");
                    return code;
                }
            };

    public static final MockJavaResource GINBINDER =
            new MockJavaResource("com.google.gwt.inject.client.binder.GinBinder") {
                @Override
                public CharSequence getContent() {
                    StringBuilder code = new StringBuilder();
                    code.append("package com.google.gwt.inject.client.binder;\n");
                    code.append("public interface GinBinder {\n");
                    code.append("}\n");
                    return code;
                }
            };

    public static final MockJavaResource BARMODULE =
            new MockJavaResource("com.gwtplatform.mvp.rebind.model.BarModule") {
                @Override
                public CharSequence getContent() {
                    StringBuilder code = new StringBuilder();
                    code.append("package com.gwtplatform.mvp.rebind.model;\n");
                    code.append("import com.google.gwt.inject.client.binder.GinBinder;\n");
                    code.append("public class BarModule {\n");
                    code.append("  public void configure(GinBinder binder) {}\n");
                    code.append("}\n");
                    return code;
                }
            };

    public static final MockJavaResource FOOMODULE =
            new MockJavaResource("com.gwtplatform.mvp.rebind.model.FooModule") {
                @Override
                public CharSequence getContent() {
                    StringBuilder code = new StringBuilder();
                    code.append("package com.gwtplatform.mvp.rebind.model;\n");
                    code.append("import com.google.gwt.inject.client.binder.GinBinder;\n");
                    code.append("public class FooModule {\n");
                    code.append("  public void configure(GinBinder binder) {}\n");
                    code.append("}\n");
                    return code;
                }
            };

    public static final MockJavaResource CUSTOMBOOTSTRAPPER1 =
            new MockJavaResource("com.gwtplatform.mvp.rebind.model.CustomBootstrapper1") {
                @Override
                public CharSequence getContent() {
                    StringBuilder code = new StringBuilder();
                    code.append("package com.gwtplatform.mvp.rebind.model;\n");
                    code.append("import com.gwtplatform.mvp.client.Bootstrapper;\n");
                    code.append("import com.gwtplatform.mvp.client.annotations.Bootstrap;\n");
                    code.append("@Bootstrap\n");
                    code.append("public class CustomBootstrapper1 implements Bootstrapper {\n");
                    code.append("  public void onBootstrap() {}\n");
                    code.append("}\n");
                    return code;
                }
            };

    public static final MockJavaResource CUSTOMBOOTSTRAPPER2 =
            new MockJavaResource("com.gwtplatform.mvp.rebind.model.CustomBootstrapper2") {
                @Override
                public CharSequence getContent() {
                    StringBuilder code = new StringBuilder();
                    code.append("package com.gwtplatform.mvp.rebind.model;\n");
                    code.append("import com.gwtplatform.mvp.client.Bootstrapper;\n");
                    code.append("import com.gwtplatform.mvp.client.annotations.Bootstrap;\n");
                    code.append("@Bootstrap\n");
                    code.append("public class CustomBootstrapper2 implements Bootstrapper {\n");
                    code.append("  public void onBootstrap() {}\n");
                    code.append("}\n");
                    return code;
                }
            };

    public static final MockJavaResource CUSTOMBOOTSTRAPPER3 =
            new MockJavaResource("com.gwtplatform.mvp.rebind.model.CustomBootstrapper3") {
                @Override
                public CharSequence getContent() {
                    StringBuilder code = new StringBuilder();
                    code.append("package com.gwtplatform.mvp.rebind.model;\n");
                    code.append("import com.gwtplatform.mvp.client.annotations.Bootstrap;\n");
                    code.append("@Bootstrap\n");
                    code.append("public class CustomBootstrapper3 {\n");
                    code.append("}\n");
                    return code;
                }
            };

    public static final MockJavaResource CUSTOMPREBOOTSTRAPPER1 =
            new MockJavaResource("com.gwtplatform.mvp.rebind.model.CustomPreBootstrapper1") {
                @Override
                public CharSequence getContent() {
                    StringBuilder code = new StringBuilder();
                    code.append("package com.gwtplatform.mvp.rebind.model;\n");
                    code.append("import com.gwtplatform.mvp.client.PreBootstrapper;\n");
                    code.append("import com.gwtplatform.mvp.client.annotations.PreBootstrap;\n");
                    code.append("@PreBootstrap\n");
                    code.append("public class CustomPreBootstrapper1 implements PreBootstrapper {\n");
                    code.append("  public void onPreBootstrap() {}\n");
                    code.append("}\n");
                    return code;
                }
            };

    public static final MockJavaResource CUSTOMPREBOOTSTRAPPER2 =
            new MockJavaResource("com.gwtplatform.mvp.rebind.model.CustomPreBootstrapper2") {
                @Override
                public CharSequence getContent() {
                    StringBuilder code = new StringBuilder();
                    code.append("package com.gwtplatform.mvp.rebind.model;\n");
                    code.append("import com.gwtplatform.mvp.client.PreBootstrapper;\n");
                    code.append("import com.gwtplatform.mvp.client.annotations.PreBootstrap;\n");
                    code.append("@PreBootstrap\n");
                    code.append("public class CustomPreBootstrapper2 implements PreBootstrapper {\n");
                    code.append("  public void onPreBootstrap() {}\n");
                    code.append("}\n");
                    return code;
                }
            };

    public static final MockJavaResource CUSTOMPREBOOTSTRAPPER3 =
            new MockJavaResource("com.gwtplatform.mvp.rebind.model.CustomPreBootstrapper3") {
                @Override
                public CharSequence getContent() {
                    StringBuilder code = new StringBuilder();
                    code.append("package com.gwtplatform.mvp.rebind.model;\n");
                    code.append("import com.gwtplatform.mvp.client.annotations.PreBootstrap;\n");
                    code.append("@PreBootstrap\n");
                    code.append("public class CustomPreBootstrapper3 {\n");
                    code.append("}\n");
                    return code;
                }
            };

    public static final MockJavaResource GINJECTOR_RETURNVALUE1 =
            new MockJavaResource("com.gwtplatform.mvp.rebind.model.ReturnValue1") {
                @Override
                public CharSequence getContent() {
                    StringBuilder code = new StringBuilder();
                    code.append("package com.gwtplatform.mvp.rebind.model;\n");
                    code.append("public interface ReturnValue1 {\n");
                    code.append("}\n");
                    return code;
                }
            };

    public static final MockJavaResource GINJECTOR_RETURNVALUE2 =
            new MockJavaResource("com.gwtplatform.mvp.rebind.model.ReturnValue2") {
                @Override
                public CharSequence getContent() {
                    StringBuilder code = new StringBuilder();
                    code.append("package com.gwtplatform.mvp.rebind.model;\n");
                    code.append("public interface ReturnValue2 {\n");
                    code.append("}\n");
                    return code;
                }
            };

    public static final MockJavaResource GINJECTOREXTENSION1 =
            new MockJavaResource("com.gwtplatform.mvp.rebind.model.GinjectorExtension1") {
                @Override
                public CharSequence getContent() {
                    StringBuilder code = new StringBuilder();
                    code.append("package com.gwtplatform.mvp.rebind.model;\n");
                    code.append("public interface GinjectorExtension1 {\n");
                    code.append("  ReturnValue1 getReturnValue1();\n");
                    code.append("}\n");
                    return code;
                }
            };

    public static final MockJavaResource GINJECTOREXTENSION2 =
            new MockJavaResource("com.gwtplatform.mvp.rebind.model.GinjectorExtension2") {
                @Override
                public CharSequence getContent() {
                    StringBuilder code = new StringBuilder();
                    code.append("package com.gwtplatform.mvp.rebind.model;\n");
                    code.append("public interface GinjectorExtension2 {\n");
                    code.append("  ReturnValue2 getReturnValue2();\n");
                    code.append("}\n");
                    return code;
                }
            };

    public static final MockJavaResource GINJECTOREXTENSION3 =
            new MockJavaResource("com.gwtplatform.mvp.rebind.model.GinjectorExtension3") {
                @Override
                public CharSequence getContent() {
                    StringBuilder code = new StringBuilder();
                    code.append("package com.gwtplatform.mvp.rebind.model;\n");
                    code.append("public interface GinjectorExtension3 {\n");
                    code.append("  ReturnValue1 getReturnValue1();\n");
                    code.append("}\n");
                    return code;
                }
            };

    public static final MockJavaResource GINJECTOREXTENSION4 =
            new MockJavaResource("com.gwtplatform.mvp.rebind.model.GinjectorExtension4") {
                @Override
                public CharSequence getContent() {
                    StringBuilder code = new StringBuilder();
                    code.append("package com.gwtplatform.mvp.rebind.model;\n");
                    code.append("public interface GinjectorExtension4 {\n");
                    code.append("  ReturnValue1 getReturnValue();\n");
                    code.append("}\n");
                    return code;
                }
            };

    public static Resource[] getResources() {
        return new Resource[]{
                GINBINDER,
                new RealJavaResource(GinModule.class),
                new RealJavaResource(ApplicationController.class),
                new RealJavaResource(Bootstrapper.class),
                new RealJavaResource(PreBootstrapper.class),
                BARMODULE,
                FOOMODULE,
                DEFAULT_BOOTSTRAPPER,
                GINJECTOR_RETURNVALUE1,
                GINJECTOR_RETURNVALUE2,
                GINJECTOREXTENSION1,
                GINJECTOREXTENSION2,
                GINJECTOREXTENSION3,
                GINJECTOREXTENSION4
        };
    }
}
