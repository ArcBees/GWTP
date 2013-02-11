package com.gwtplatform.dispatch.client.rest;

import com.gwtplatform.dispatch.client.rest.AbstractSerializerProvider;
import com.gwtplatform.dispatch.client.rest.SerializedType;

public class SerializerProviderImpl extends AbstractSerializerProvider {
  public SerializerProviderImpl() {
    registerSerializer(com.gwtplatform.samples.basic.client.dispatch.TextService_GetTexts_ActionImpl.class, SerializedType.RESPONSE, new com.gwtplatform.dispatch.client.rest.com_gwtplatform_samples_basic_shared_dispatch_BigResultSerializer());
    registerSerializer(com.gwtplatform.samples.basic.client.dispatch.TextService_GetText_long_ActionImpl.class, SerializedType.RESPONSE, new com.gwtplatform.dispatch.client.rest.com_gwtplatform_samples_basic_shared_dispatch_SmallResultSerializer());
    registerSerializer(com.gwtplatform.samples.basic.client.dispatch.TextService_CreateText_String_ActionImpl.class, SerializedType.BODY, new com.gwtplatform.dispatch.client.rest.java_lang_StringSerializer());
    registerSerializer(com.gwtplatform.samples.basic.client.dispatch.TextService_CreateText_String_ActionImpl.class, SerializedType.RESPONSE, new com.gwtplatform.dispatch.client.rest.com_gwtplatform_samples_basic_shared_dispatch_SmallResultSerializer());
  }
}
