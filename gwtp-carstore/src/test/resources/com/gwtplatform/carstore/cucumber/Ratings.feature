Feature: Ratings

  Background:
    Given I'm logged in
    And I navigate to the Ratings page

  Scenario: Add a rating
    When I click on the create rating button
    And I fill the rating form
    Then A rating is created

  Scenario: Delete a rating
    When I delete the first rating
    Then The first rating gets removed
