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

package com.gwtplatform.samples.basic.client;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

import com.gwtplatform.mvp.client.ViewImpl;

/**
 * @author Philippe Beaudoin
 */
public class MainPageView extends ViewImpl implements MainPagePresenter.MyView {

  private static String html = "<h1>Web Application Starter Project</h1>\n"
      + "<table align=\"center\">\n"
      + "  <tr>\n"
      + "    <td colspan=\"2\" style=\"font-weight:bold;\">Please enter your name:</td>\n"
      + "  </tr>\n"
      + "  <tr>\n"
      + "    <td id=\"nameFieldContainer\"></td>\n"
      + "    <td id=\"sendButtonContainer\"></td>\n"
      + "  </tr>\n"
      + "  <tr>\n"
      + "    <td colspan=\"2\" style=\"color:red;\" id=\"errorLabelContainer\"></td>\n"
      + "  </tr>\n" + "</table>\n";

  HTMLPanel panel = new HTMLPanel(html);

  private final Label errorLabel;
  private final TextBox nameField;
  private final Button sendButton;

  @Inject
  public MainPageView() {
    sendButton = new Button("Send");
    nameField = new TextBox();
    nameField.setText("GWT User");
    errorLabel = new Label();

    // We can add style names to widgets
    sendButton.addStyleName("sendButton");

    // Add the nameField and sendButton to the RootPanel
    // Use RootPanel.get() to get the entire body element
    panel.add(nameField, "nameFieldContainer");
    panel.add(sendButton, "sendButtonContainer");
    panel.add(errorLabel, "errorLabelContainer");
  }

  @Override
  public Widget asWidget() {
    return panel;
  }

  @Override
  public String getName() {
    return nameField.getText();
  }

  @Override
  public Button getSendButton() {
    return sendButton;
  }

  @Override
  public void resetAndFocus() {
    // Focus the cursor on the name field when the app loads
    nameField.setFocus(true);
    nameField.selectAll();
  }

  @Override
  public void setError(String errorText) {
    errorLabel.setText(errorText);
  }
}
