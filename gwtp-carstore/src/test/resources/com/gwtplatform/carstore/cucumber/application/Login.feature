Feature: Login

  Scenario: Successful connection
    Given I navigate to the login page
    When I enter valid credential
    Then I'm connected

  Scenario: Refused connection
    Given I navigate to the login page
    When I enter invalid credential
    Then I'm disconnected

  Scenario: Logout
    Given I'm logged in
    When I click on logout
    Then I'm disconnected
