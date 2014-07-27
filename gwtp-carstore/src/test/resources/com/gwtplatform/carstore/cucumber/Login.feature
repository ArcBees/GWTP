Feature: Login

  Scenario: Successful connection
    Given I navigate to the login page
    When I enter valid credential
    Then I'm connected

  Scenario: Successful connection
    Given I try to navigate to the rating page while not logged in
    When I click on the link to the login page
    And I enter valid credential
    Then I'm connected on the rating page

  Scenario: Refused connection
    Given I navigate to the login page
    When I enter invalid credential
    Then I'm disconnected

  Scenario: Refused connection
    Given I navigate to the login page
    When I enter semivalid credential
    Then I'm disconnected

  Scenario: Logout
    Given I'm logged in
    When I click on logout
    Then I'm disconnected
