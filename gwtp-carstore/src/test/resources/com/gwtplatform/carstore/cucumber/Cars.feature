Feature: Ratings

  Background:
    Given I'm logged in
    And I navigate to the Cars page

  Scenario: Clicking on create a car shows me the page to fill informations
    When I click on the create car button
    Then I should be on the newCar page

  Scenario: Clicking on edit on the first car should edit the first car
    When I click on the first car's edit button
    Then I should be on the edited car page
    And The shown car should match the edited car

  Scenario: Editing the first car should update the first car
    Given I click on the first car's edit button
    When I choose Honda as the car manufacturer
    And I type Fit as the car model
    And I click to save the car
    And I click to close the car
    Then I see a success message containing saved
    Then The car table should show Honda / Fit as the first car

  Scenario: Delete a car
    When I delete the first car
    Then The first car gets removed
