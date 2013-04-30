package com.gwtplatform.carstore.server.rest;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;

import org.codehaus.jackson.annotate.JsonMethod;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;

import static org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import static org.codehaus.jackson.map.SerializationConfig.Feature;

@Provider
public class JacksonProvider extends JacksonJsonProvider {
    @Override
    public void writeTo(Object value, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
            MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException {
        ObjectMapper mapper = locateMapper(type, mediaType);

        SerializationConfig newSerializerConfig = mapper.getSerializationConfig()
                .without(Feature.FAIL_ON_EMPTY_BEANS)
                .without(Feature.WRITE_DATES_AS_TIMESTAMPS);

        mapper.setSerializationConfig(newSerializerConfig);
        mapper.setVisibility(JsonMethod.FIELD, Visibility.ANY);

        super.writeTo(value, type, genericType, annotations, mediaType, httpHeaders, entityStream);
    }
}
