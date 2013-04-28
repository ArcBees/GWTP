Feature: Ratings

  Background:
    Given I'm logged in
    And I navigate to the cars page

  Scenario: Clicking on create a car shows me the page to fill informations
    When I click on the create car button
    Then I should be on the newCar page

  Scenario: Delete a car
    When I delete the first car
    Then The first car gets removed
