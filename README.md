# Appium TDD Framework

This repo contains the source code for Appium Java TDD Framework designed during the Udemy course of Omparkash Chavan
* [Link to Course](https://www.udemy.com/course/the-complete-appium-course-for-ios-and-android/)

<img src="doc/framework-design.png">

--

## Libraries and Tools

* Maven 3.9.2
* JDK 17.0.2
* TestNG 7.8.0
* Appium Java client 8.5.1
* Appium server 2.0
  * drivers
    * uiautomator2@2.29.2
    * xcuitest@4.32.19
* Appium inspector 2023.5.2
* Maven surefire plugin 3.1.2
* Demo Apps
    * [Sauce Labs Native Sample Application](https://github.com/saucelabs/sample-app-mobile)

--

## Course Notes
Following sections summarize the important notes taken during the framework development.

### Part 1 - Automate Test Cases using TestNG and go through Bad Practices

* Appium Java Client will also download the Selenium Libraries as dependent libraries.

> Important! Please create all packages and class files in src/test/java and not in src/main/java. Recently, Appium has
> changed ths scope of one of its transitive Selenium dependency (Support UI package) from "compile" to "runtime". Due to
> this, the dependency may not resolve under src/main/java. We can certainly try to change the scope to compile, but lets
> be safe and create everything under src/test/java. This is where typically test automation resides. if you still want to
> use src/main/java, then please add the entire "selenium-java" package as a separate dependency in pom.xml. make sure to
> match the version with the selenium version that is shipped with the Appium Java client.

> For iOS real devices, remember you cannot use the IPA from the download location of application directly. You will
> need to get the source code, then go through the code signing process to generate the IPA. You can follow the iOS real
> device setup videos for the code signing process.

* Code Duplication:
  * Username and password steps are being repeatedly used in each test case.
  * Distribute the element definition globally and reuse in each test case.
* Readability:
  * code for performing login is not readable.
* Hardcoding - Test Data
  * username and password are being hardcoded in the test cases.
  * We should add abstraction between the test data and the code.
  * So change of test data will not impact test cases, and multiple tests can be ran from using same test data.
* Hardcoding - Static text
  * The error messages and hard coded which should also be abstracted away and read from external JSON/XML file.
* Scalability Issue
  * Driver initialization is confined to only single class, and cannot be reused.
  * Moreover, the capabilities should also be read from external file or through commandline.
* Waits
  * Implicit wait is not a good practice.
  * Use explicit wait and manage all the waits from single place.
* Multiple platform support
  * Test cases should be designed to support both iOS and Android platforms with minor changes.
  * Driver should be initialized for the corresponding platform based on test cases.
* Logging
  * Logging framework should be used instead of printing information on console.
  * In case of parallel execution, separate logs for each device should be used.
* Reporting
  * Reporting libraries should be used to create visually appealing report with screenshots and videos
* Exception Handling
  * Exceptions should be handled and printed to logs
* Configuration
  * Appium server urls and ports should be derived from config properties files

* Design the framework to achieve scalability, maintainability, abstraction, parameterization, robust logging and reporting,

### Part 2 - Implement Page Object Model (POM) design

<img src="doc/framework-structure.png" width="1200">

* Page Classes are added and extended from the BaseTest to reuse the methods and specify locators inside page classes.
* We achieved following objectives so far:
  * Avoid code duplication by reusing the elements.
  * Improve code readability by adding actions methods for login in page.
  * Scalable Automation by initializing the driver at BaseTest.
  * Wait and Driver commands handling at single place i.e. BaseTest super class
  * Modular and Independent test cases
  * Parameterization of global properties through config.properties and testng parameters for device.
    * All the parameters defined at test level will also be available to nested classes and methods in testng.xml
    * If the parameters are provided at test level and driver is initialized at class level, it will work for sequential execution.
    * But if you run the test classes in parallel, then it will try to install the same application parallel to all the
      test classes because we are providing the same parameters to all the test classes.
    * 

* Inheritance approach has some disadvantages like one can create methods in child classes which will override the methods in
  parent class, or declare the elements inside Test Class instead of Pages which is not a good practice.
* 

### Part 3 - Alternate Design | Abstract Test Data & Static Text | Exception Handling

* Go to the git branch `part3/alternate-design` to see the source code for this part.
* This lecture will discuss:
  * Alternate Design (no Inheritance)
  * Exception Handling (Try/Catch, TestNG Listener)

* This time we initialize the driver in test class itself with @BeforeClass annotated method which means that driver
  will be initialized separately for each of the test class.
* This is fine for sequential execution, but not for parallel runs because of the thread conflicts and also the same app
  cannot be installed on same device for each class simultaneously. For this, we need multiple devices.

### Part 4 - Support iOS Platform

### Part 5 - Add more test cases | Define common elements | Write independent tests

### Scrolling - UIAutomator2 | Mobile Scroll

### Capture Screenshots

### Record Videos

### Parallel Execution using Real Android and iOS devices

### Log4j2 Logging framework integration

### Log4j2 - Logging in multi-threaded environment (parallel execution)

### Start Appium server programmatically

### Extent Reports integration
