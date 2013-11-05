#Car Store
This is a test bed for the GWTP features. 

Login password for the login feature is: admin/qwerty

##Running Tests

###Prerequisites
We use chromedriver for running integration tests. You'll need chromedriver on your path.
[Download it here.](https://sites.google.com/a/chromium.org/chromedriver/)

###Maven integration tests
Running the tests using Maven
`mvn clean verify -Pintegration-test`

###IDE Integration tests
Running tests using the IDE
`mvn clean compile gwt:compile gae:run -Pintegration-test`

##Reference
* [Form Factors](https://github.com/ArcBees/GWTP/wiki/Form-Factors)
* [PhoneGap support](https://github.com/ArcBees/GWTP/wiki/Phonegap-support)

##App Engine
* [gwtp-carstore.appspot.com](http://gwtp-carstore.appspot.com/)
