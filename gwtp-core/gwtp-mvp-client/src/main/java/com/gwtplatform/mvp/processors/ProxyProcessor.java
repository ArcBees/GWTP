package com.gwtplatform.mvp.processors;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedOptions;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;

import com.google.auto.common.MoreTypes;
import com.google.auto.service.AutoService;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Sets;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyStandard;
import com.gwtplatform.mvp.client.presenter.slots.NestedSlot;
import com.gwtplatform.mvp.client.proxy.Proxy;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.gwtplatform.processors.tools.bindings.BindingContext;
import com.gwtplatform.processors.tools.bindings.BindingsProcessors;
import com.gwtplatform.processors.tools.domain.Type;
import com.gwtplatform.processors.tools.exceptions.UnableToProcessException;
import com.gwtplatform.processors.tools.logger.Logger;
import com.gwtplatform.processors.tools.outputter.Outputter;
import com.gwtplatform.processors.tools.utils.Utils;

import static com.google.auto.common.MoreElements.asType;
import static com.google.auto.common.MoreTypes.asTypeElement;

@SupportedSourceVersion(SourceVersion.RELEASE_6)
@SupportedOptions(Logger.DEBUG_OPTION)
@AutoService(Processor.class)
public class ProxyProcessor extends AbstractProcessor {
    public static final Type MVP_MODULE = new Type("com.gwtplatform.mvp.client.gin", "MvpModule");
    private Logger logger;
    private Outputter outputter;
    private Utils utils;
    private BindingsProcessors bindingProcessors;
    private int moduleIndex;

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Collections.singleton(ProxyStandard.class.getCanonicalName());
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        outputter = new Outputter(logger, new Type(ProxyProcessor.class), processingEnv.getFiler());
        logger = new Logger(processingEnv.getMessager(), processingEnv.getOptions());
        utils = new Utils(processingEnv.getTypeUtils(), processingEnv.getElementUtils());
        bindingProcessors = new BindingsProcessors(processingEnv);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        try {
            // Find the requested class
            Set<? extends Element> elementsAnnotatedWithProxyStandard =
                    roundEnv.getElementsAnnotatedWith(ProxyStandard.class);
            for (Element element : elementsAnnotatedWithProxyStandard) {
                // If it's not an interface it's a custom user-made proxy class. Don't use generator.
                if (!element.getKind().isInterface()) {
                    continue;
                }

                // check if interface is also annotated with NameToken
                NameToken nameTokenAnnotation = element.getAnnotation(NameToken.class);
                if (nameTokenAnnotation != null) {
                    List<? extends TypeMirror> interfaces = asType(element).getInterfaces();
                    TypeMirror typeMirror = extractFirstProxyInterface(element, interfaces, ProxyPlace.class);
                    TypeMirror presenterTypeMirror = extractPresenterTypeMirror(element, typeMirror);
                    List<String> nametokens = Arrays.asList(nameTokenAnnotation.value());
                    processProxyPlace(element, nametokens);
                    return false;
                } else {
                    processSimpleProxy(element);
                }
            }
            if (roundEnv.processingOver()) {
                bindingProcessors.processLast();
            }
        } catch (Exception e) {
            logger.error().throwable(e).log("Osti de " + e.getMessage());
        }

        return false;
    }

    private void processProxyPlace(Element element, List<String> nametokens) {
        List<? extends TypeMirror> interfaces = asType(element).getInterfaces();

        TypeMirror firstProxyInterface = extractFirstProxyInterface(element, interfaces, ProxyPlace.class);
        TypeMirror presenterTypeMirror = extractPresenterTypeMirror(element, firstProxyInterface);

        String packageName = new Type(asType(element)).getPackageName();
        String presenterName = new Type(presenterTypeMirror).getSimpleName();
        String proxyName = element.getSimpleName().toString();
        Set<String> slotNames = getSlotNames(presenterTypeMirror);

        Type implementationType =
                new Type(packageName, String.format("%s%sImpl", presenterName, proxyName));
        Type interfaceType = new Type(element.asType());

        outputter
                .withTemplateFile("com/gwtplatform/mvp/processors/ProxyPlaceImpl.vm")
                .withParam("proxy", interfaceType)
                .withParam("proxyName", proxyName)
                .withParam("presenterName", presenterName)
                .withParam("nametokens", nametokens)
                .withParam("slots", slotNames)
                .writeTo(implementationType);

        createGinBindings(implementationType, interfaceType);
    }

    private void processSimpleProxy(Element element) {
        List<? extends TypeMirror> interfaces = asType(element).getInterfaces();

        TypeMirror firstProxyInterface = extractFirstProxyInterface(element, interfaces, Proxy.class);
        TypeMirror presenterTypeMirror = extractPresenterTypeMirror(element, firstProxyInterface);

        String packageName = new Type(asType(element)).getPackageName();
        String presenterName = new Type(presenterTypeMirror).getSimpleName();
        String proxyName = element.getSimpleName().toString();

        Set<String> slotNames = getSlotNames(presenterTypeMirror);

        Type implementationType =
                new Type(packageName, String.format("%s%sImpl", presenterName, proxyName));
        Type interfaceType = new Type(element.asType());

        outputter
                .withTemplateFile("com/gwtplatform/mvp/processors/SimpleProxyImpl.vm")
                .withParam("proxy", interfaceType)
                .withParam("proxyName", proxyName)
                .withParam("presenterName", presenterName)
                .withParam("slots", slotNames)
                .writeTo(implementationType);

        createGinBindings(implementationType, interfaceType);
    }

    private void createGinBindings(Type implementationType, Type interfaceType) {
        Type localModuleType = new Type(implementationType.getPackageName(), "MvpModule" + moduleIndex++);
        BindingContext bindingContext = new BindingContext(localModuleType, implementationType);
        bindingContext.setEagerSingleton(true);
        bindingContext.setImplemented(interfaceType);

        bindingProcessors.process(bindingContext);
        bindingProcessors.process(new BindingContext(MVP_MODULE, localModuleType, true));
    }

    private TypeMirror extractFirstProxyInterface(Element element, List<? extends TypeMirror> interfaces, final Class<?> proxyClass) {
        TypeMirror firstProxyInterface = FluentIterable.from(interfaces).firstMatch(new Predicate<TypeMirror>() {
            @Override
            public boolean apply(TypeMirror input) {
                return asTypeElement(input).getQualifiedName()
                        .contentEquals(proxyClass.getCanonicalName());
            }
        }).orNull();

        if (firstProxyInterface == null) {
            logger.error().context(element).log("This Proxy interface must extend %s.", proxyClass.getSimpleName());
            throw new UnableToProcessException();
        }
        return firstProxyInterface;
    }

    private TypeMirror extractPresenterTypeMirror(Element element, TypeMirror firstProxyInterface) {
        List<? extends TypeMirror> proxyGenericTypes = MoreTypes.asDeclared(firstProxyInterface).getTypeArguments();
        if (proxyGenericTypes.size() == 0) {
            logger.error().context(element)
                    .log("This Proxy must specify the associated Presenter.");
            throw new UnableToProcessException();
        }

        return proxyGenericTypes.get(0);
    }

    private Set<String> getSlotNames(TypeMirror presenterType) {
        List<VariableElement> fieldsInPresenter =
                ElementFilter.fieldsIn(utils.getAllMembers(asTypeElement(presenterType)));

        Set<String> slotNames = Sets.newHashSet();

        for (VariableElement field : fieldsInPresenter) {
            if (MoreTypes.isTypeOf(NestedSlot.class, field.asType())) {
                slotNames.add(field.getSimpleName().toString());
            }
        }

        return slotNames;
    }
}
