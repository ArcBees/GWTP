package com.gwtplatform.samples.basic.client.application.response;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.safehtml.shared.UriUtils;
import com.google.gwt.uibinder.client.UiBinderUtil;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiBinderUtil;
import com.google.gwt.user.client.ui.Widget;

public class ResponseView_BinderImpl implements UiBinder<com.google.gwt.user.client.ui.Widget, com.gwtplatform.samples.basic.client.application.response.ResponseView>, com.gwtplatform.samples.basic.client.application.response.ResponseView.Binder {

  interface Template extends SafeHtmlTemplates {
    @Template("Close")
    SafeHtml html1();
     
    @Template("<h1>Remote Procedure Call</h1> <div class='{0}'>Sending name to server:</div> <span id='{1}'></span> <div class='{2}'>Server replies:</div> <span id='{3}'></span> <span id='{4}'></span>")
    SafeHtml html2(String arg0, String arg1, String arg2, String arg3, String arg4);
     
  }

  Template template = GWT.create(Template.class);


  public com.google.gwt.user.client.ui.Widget createAndBindUi(final com.gwtplatform.samples.basic.client.application.response.ResponseView owner) {


    return new Widgets(owner).get_f_HTMLPanel1();
  }

  /**
   * Encapsulates the access to all inner widgets
   */
  class Widgets {
    private final com.gwtplatform.samples.basic.client.application.response.ResponseView owner;


    public Widgets(final com.gwtplatform.samples.basic.client.application.response.ResponseView owner) {
      this.owner = owner;
      build_style();  // generated css resource must be always created. Type: GENERATED_CSS. Precedence: 1
      build_domId0();  // more than one getter call detected. Type: DOM_ID_HOLDER, precedence: 2
      build_domId1();  // more than one getter call detected. Type: DOM_ID_HOLDER, precedence: 2
      build_domId2();  // more than one getter call detected. Type: DOM_ID_HOLDER, precedence: 2
      build_domId0Element();  // more than one getter call detected. Type: DEFAULT, precedence: 2
      build_domId1Element();  // more than one getter call detected. Type: DEFAULT, precedence: 2
      build_domId2Element();  // more than one getter call detected. Type: DEFAULT, precedence: 2
    }

    SafeHtml template_html1() {
      return template.html1();
    }
    SafeHtml template_html2() {
      return template.html2("" + get_style().label() + "", get_domId0(), "" + get_style().label() + "", get_domId1(), get_domId2());
    }

    /**
     * Getter for clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay called 1 times. Type: GENERATED_BUNDLE. Build precedence: 1.
     */
    private com.gwtplatform.samples.basic.client.application.response.ResponseView_BinderImpl_GenBundle get_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay() {
      return build_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay();
    }
    private com.gwtplatform.samples.basic.client.application.response.ResponseView_BinderImpl_GenBundle build_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay() {
      // Creation section.
      final com.gwtplatform.samples.basic.client.application.response.ResponseView_BinderImpl_GenBundle clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay = (com.gwtplatform.samples.basic.client.application.response.ResponseView_BinderImpl_GenBundle) GWT.create(com.gwtplatform.samples.basic.client.application.response.ResponseView_BinderImpl_GenBundle.class);
      // Setup section.


      return clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay;
    }

    /**
     * Getter for style called 3 times. Type: GENERATED_CSS. Build precedence: 1.
     */
    private com.gwtplatform.samples.basic.client.application.response.ResponseView_BinderImpl_GenCss_style style;
    private com.gwtplatform.samples.basic.client.application.response.ResponseView_BinderImpl_GenCss_style get_style() {
      return style;
    }
    private com.gwtplatform.samples.basic.client.application.response.ResponseView_BinderImpl_GenCss_style build_style() {
      // Creation section.
      style = get_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay().style();
      // Setup section.
      style.ensureInjected();


      return style;
    }

    /**
     * Getter for f_HTMLPanel1 called 1 times. Type: DEFAULT. Build precedence: 1.
     */
    private com.google.gwt.user.client.ui.HTMLPanel get_f_HTMLPanel1() {
      return build_f_HTMLPanel1();
    }
    private com.google.gwt.user.client.ui.HTMLPanel build_f_HTMLPanel1() {
      // Creation section.
      final com.google.gwt.user.client.ui.HTMLPanel f_HTMLPanel1 = new com.google.gwt.user.client.ui.HTMLPanel(template_html2().asString());
      // Setup section.
      f_HTMLPanel1.setStyleName("" + get_style().root() + "");

      // Attach section.
      UiBinderUtil.TempAttachment attachRecord1 = UiBinderUtil.attachToDom(f_HTMLPanel1.getElement());
      get_domId0Element().get();
      get_domId1Element().get();
      get_domId2Element().get();

      // Detach section.
      attachRecord1.detach();
      f_HTMLPanel1.addAndReplaceElement(get_textToServer(), get_domId0Element().get());
      f_HTMLPanel1.addAndReplaceElement(get_serverResponse(), get_domId1Element().get());
      f_HTMLPanel1.addAndReplaceElement(get_closeButton(), get_domId2Element().get());

      return f_HTMLPanel1;
    }

    /**
     * Getter for domId0 called 2 times. Type: DOM_ID_HOLDER. Build precedence: 2.
     */
    private java.lang.String domId0;
    private java.lang.String get_domId0() {
      return domId0;
    }
    private java.lang.String build_domId0() {
      // Creation section.
      domId0 = com.google.gwt.dom.client.Document.get().createUniqueId();
      // Setup section.


      return domId0;
    }

    /**
     * Getter for textToServer called 1 times. Type: DEFAULT. Build precedence: 2.
     */
    private com.google.gwt.user.client.ui.HTML get_textToServer() {
      return build_textToServer();
    }
    private com.google.gwt.user.client.ui.HTML build_textToServer() {
      // Creation section.
      final com.google.gwt.user.client.ui.HTML textToServer = (com.google.gwt.user.client.ui.HTML) GWT.create(com.google.gwt.user.client.ui.HTML.class);
      // Setup section.


      owner.textToServer = textToServer;

      return textToServer;
    }

    /**
     * Getter for domId0Element called 2 times. Type: DEFAULT. Build precedence: 2.
     */
    private com.google.gwt.uibinder.client.LazyDomElement domId0Element;
    private com.google.gwt.uibinder.client.LazyDomElement get_domId0Element() {
      return domId0Element;
    }
    private com.google.gwt.uibinder.client.LazyDomElement build_domId0Element() {
      // Creation section.
      domId0Element = new com.google.gwt.uibinder.client.LazyDomElement<Element>(get_domId0());
      // Setup section.


      return domId0Element;
    }

    /**
     * Getter for domId1 called 2 times. Type: DOM_ID_HOLDER. Build precedence: 2.
     */
    private java.lang.String domId1;
    private java.lang.String get_domId1() {
      return domId1;
    }
    private java.lang.String build_domId1() {
      // Creation section.
      domId1 = com.google.gwt.dom.client.Document.get().createUniqueId();
      // Setup section.


      return domId1;
    }

    /**
     * Getter for serverResponse called 1 times. Type: DEFAULT. Build precedence: 2.
     */
    private com.google.gwt.user.client.ui.HTML get_serverResponse() {
      return build_serverResponse();
    }
    private com.google.gwt.user.client.ui.HTML build_serverResponse() {
      // Creation section.
      final com.google.gwt.user.client.ui.HTML serverResponse = (com.google.gwt.user.client.ui.HTML) GWT.create(com.google.gwt.user.client.ui.HTML.class);
      // Setup section.


      owner.serverResponse = serverResponse;

      return serverResponse;
    }

    /**
     * Getter for domId1Element called 2 times. Type: DEFAULT. Build precedence: 2.
     */
    private com.google.gwt.uibinder.client.LazyDomElement domId1Element;
    private com.google.gwt.uibinder.client.LazyDomElement get_domId1Element() {
      return domId1Element;
    }
    private com.google.gwt.uibinder.client.LazyDomElement build_domId1Element() {
      // Creation section.
      domId1Element = new com.google.gwt.uibinder.client.LazyDomElement<Element>(get_domId1());
      // Setup section.


      return domId1Element;
    }

    /**
     * Getter for domId2 called 2 times. Type: DOM_ID_HOLDER. Build precedence: 2.
     */
    private java.lang.String domId2;
    private java.lang.String get_domId2() {
      return domId2;
    }
    private java.lang.String build_domId2() {
      // Creation section.
      domId2 = com.google.gwt.dom.client.Document.get().createUniqueId();
      // Setup section.


      return domId2;
    }

    /**
     * Getter for closeButton called 1 times. Type: DEFAULT. Build precedence: 2.
     */
    private com.google.gwt.user.client.ui.Button get_closeButton() {
      return build_closeButton();
    }
    private com.google.gwt.user.client.ui.Button build_closeButton() {
      // Creation section.
      final com.google.gwt.user.client.ui.Button closeButton = (com.google.gwt.user.client.ui.Button) GWT.create(com.google.gwt.user.client.ui.Button.class);
      // Setup section.
      closeButton.setHTML(template_html1().asString());


      owner.closeButton = closeButton;

      return closeButton;
    }

    /**
     * Getter for domId2Element called 2 times. Type: DEFAULT. Build precedence: 2.
     */
    private com.google.gwt.uibinder.client.LazyDomElement domId2Element;
    private com.google.gwt.uibinder.client.LazyDomElement get_domId2Element() {
      return domId2Element;
    }
    private com.google.gwt.uibinder.client.LazyDomElement build_domId2Element() {
      // Creation section.
      domId2Element = new com.google.gwt.uibinder.client.LazyDomElement<Element>(get_domId2());
      // Setup section.


      return domId2Element;
    }
  }
}
