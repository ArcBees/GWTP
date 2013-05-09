Feature: Manufacturers

  Background:
    Given I'm logged in
    And I navigate to the manufacturer page

  Scenario: Clicking on create a manufacturer shows me the page to fill informations
    When I click on the create manufacturer button
    Then I see the manufacturer editor

  Scenario: Add a manufacturer but hit the close button, table should be unaltered
    Given I click on the create manufacturer button
    And  I see the manufacturer editor
    When I enter Ford as the car manufacturer
    And  I click on the close button
    Then The number of rows is unchanged

  Scenario: Add a manufacturer and hit the save button, table should be unaltered
    Given I click on the create manufacturer button
    And   I see the manufacturer editor
    When  I enter Plymouth as the car manufacturer
    And   I click on the save button
    Then  I see a success message containing saved
    And   The number of rows is incremented
    And   The manufacturer table should show Plymouth as the last manufacturer

#  This one doesn't work yet because of a strange issue retrieving the contents of the editbox
#  Scenario: Clicking on edit on the first manufacturer should edit the first manufacturer
#    When I click on the first manufacturer edit button
#    Then I should see the manufacturer editor
#    And The shown manufacturer should match the edited manufacturer
#
  Scenario: Editing the first manufacturer should update the first manufacturer
    Given I click on the first manufacturer edit button
    When  I enter Ford as the car manufacturer
    And   I click on the save button
    Then  I see a success message containing saved
    And   The manufacturer table should show Ford as the first manufacturer

  Scenario: Delete a manufacturer
    When I delete the first manufacturer
    Then The first manufacturer gets removed
