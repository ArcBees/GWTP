Feature: Reports

  Background:
    Given I'm logged in

  Scenario: Reports should show proper rating averages for all manufacturers
    Given I navigate to the Ratings page
    And I compute rating average for all manufacturers
    When I navigate to the Reports page
    Then I see the proper averages for all manufacturers
