package com.gwtplatform.samples.basic.rebind;

import com.google.gwt.core.ext.typeinfo.JClassType;

import java.util.ArrayList;
import java.util.List;

public class PresenterDefinitions {
  private final List<JClassType> standardPresenters;
  private final List<JClassType> codeSplitPresenters;
  private final List<JClassType> codeSplitBundlePresenters;

  public PresenterDefinitions() {
    this.standardPresenters = new ArrayList<JClassType>();
    this.codeSplitPresenters = new ArrayList<JClassType>();
    this.codeSplitBundlePresenters = new ArrayList<JClassType>();
  }

  public List<JClassType> getStandardPresenters() {
    return standardPresenters;
  }

  public List<JClassType> getCodeSplitPresenters() {
    return codeSplitPresenters;
  }

  public List<JClassType> getCodeSplitBundlePresenters() {
    return codeSplitBundlePresenters;
  }

  public void addStandardPresenter(JClassType presenter) {
    standardPresenters.add(presenter);
  }

  public void addCodeSplitPresenter(JClassType presenter) {
    codeSplitPresenters.add(presenter);
  }

  public void addCodeSplitBundlePresenter(JClassType presenter) {
    codeSplitBundlePresenters.add(presenter);
  }
}
