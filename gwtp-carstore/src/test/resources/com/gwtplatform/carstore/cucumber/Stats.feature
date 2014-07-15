Feature: Stats

  Background:
    Given I'm logged in

  Scenario: Extract year should display the year from the date sent to the server
    Given I navigate to the stats page
    And I select 2012-12-25 in the date picker
    When I click on Extract Year
    Then I should see 2012 as the extracted year
