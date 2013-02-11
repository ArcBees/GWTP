package com.gwtplatform.samples.basic.client.application.response;

public class ResponseView_BinderImpl_TemplateImpl implements com.gwtplatform.samples.basic.client.application.response.ResponseView_BinderImpl.Template {
  
  public com.google.gwt.safehtml.shared.SafeHtml html1() {
    StringBuilder sb = new java.lang.StringBuilder();
    sb.append("Close");
return new com.google.gwt.safehtml.shared.OnlyToBeUsedInGeneratedCodeStringBlessedAsSafeHtml(sb.toString());
}

public com.google.gwt.safehtml.shared.SafeHtml html2(java.lang.String arg0,java.lang.String arg1,java.lang.String arg2,java.lang.String arg3,java.lang.String arg4) {
StringBuilder sb = new java.lang.StringBuilder();
sb.append("<h1>Remote Procedure Call</h1> <div class='");
sb.append(com.google.gwt.safehtml.shared.SafeHtmlUtils.htmlEscape(arg0));
sb.append("'>Sending name to server:</div> <span id='");
sb.append(com.google.gwt.safehtml.shared.SafeHtmlUtils.htmlEscape(arg1));
sb.append("'></span> <div class='");
sb.append(com.google.gwt.safehtml.shared.SafeHtmlUtils.htmlEscape(arg2));
sb.append("'>Server replies:</div> <span id='");
sb.append(com.google.gwt.safehtml.shared.SafeHtmlUtils.htmlEscape(arg3));
sb.append("'></span> <span id='");
sb.append(com.google.gwt.safehtml.shared.SafeHtmlUtils.htmlEscape(arg4));
sb.append("'></span>");
return new com.google.gwt.safehtml.shared.OnlyToBeUsedInGeneratedCodeStringBlessedAsSafeHtml(sb.toString());
}
}
