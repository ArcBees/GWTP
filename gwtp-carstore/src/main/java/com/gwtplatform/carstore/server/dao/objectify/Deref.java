package com.gwtplatform.carstore.server.dao.objectify;

import java.util.List;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.googlecode.objectify.Ref;

public class Deref {
    public static class Func<T> implements Function<Ref<T>, T> {
        public static Func<Object> INSTANCE = new Func<Object>();

        @Override
        public T apply(Ref<T> ref) {
            return deref(ref);
        }
    }

    public static <T> T deref(Ref<T> ref) {
        return ref == null ? null : ref.get();
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <T> List<T> deref(List<Ref<T>> reflist) {
        if (reflist == null) {
            return null;
        }
        
        return Lists.transform(reflist, (Func) Func.INSTANCE);
    }
}