Feature: Ratings

  Background:
    Given I'm logged in
    And I navigate to the rating page

  Scenario: Add a rating
    When I fill the rating form
    Then A rating is created

  Scenario: Delete a rating
    When I delete the first rating
    Then It gets removed
