

package com.gwtplatform.samples.basic.client.dispatch;

import com.gwtplatform.dispatch.shared.Action;
import com.gwtplatform.dispatch.shared.rest.HttpMethod;
import com.gwtplatform.samples.basic.shared.dispatch.TextService;

public class TextServiceImpl implements TextService {
    @Override
    public Action<com.gwtplatform.samples.basic.shared.dispatch.BigResult> getTexts() {
        return new TextService_GetTextsImpl();
    }
    @Override
    public Action<com.gwtplatform.samples.basic.shared.dispatch.SmallResult> getText(long id) {
        return new TextService_GetText_longImpl(id);
    }
    @Override
    public Action<com.gwtplatform.samples.basic.shared.dispatch.SmallResult> createText(java.lang.String body) {
        return new TextService_CreateText_StringImpl(body);
    }
}
