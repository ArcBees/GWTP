![GWTP] (http://i.imgur.com/LHvTZ.png "GWTP goo-teepee") *goo-teepee*

A complete model-view-presenter framework to simplify your next GWT project.

##New Home for GWTP
While we move, you can find the documentation here:
http://gwt-platform.googlecode.com

##News
10/23/2012 - GWTP starts moving more to GitHub, getting ready to push out 0.8 release.

02/01/2012 - GWTP 0.7 released with support for fancier tab with user-controlled data. 
Get it from the download section or Maven central. See the Release Notes and the migration details.

06/15/2011 - The slides for my PrairieDevCon 2011 talk on AppEngine are available here, 
the ones for my GWT & GWTP dojo are here, and you can get the accompanying project.

##License
GWTP is freely distributable under the [Apache 2.0 License](http://www.apache.org/licenses/LICENSE-2.0.html)

##Downloads
[Using Maven with GWTP](Using-Gwtp-with-Maven)

##Plugins
[Eclipse Plugin source](https://github.com/ArcBees/gwtp-eclipse-plugin)

##Why Use GWTP
GWT makes programming web apps look deceptively simple. However, building an efficient application that you 
can easily expand is far from being a trivial task. A good way to start your project on a strong footing is 
to adhere to a well-established architecture molded around GWT's best features.

GWTP (goo-teepee for short), is a collection of components that build up such an architecture. 
You can pick the components you need or build your new project from the ground up using the entire package. 
No matter which approach you choose, GWT optimized compilation will make sure only the features you really use 
are part of your final code. Read on for more details or get started right away!

At the heart of GWTP is a model-view-presenter architecture (MVP). Although this model has been lauded as one of 
the best approach to GWT development, it is still hard to find an out-of-the-box solution that supports all the 
requirements of modern web apps. GWTP aims to provide such a solution.

For example, adding history management and code splitting to your presenter is as simple as adding these lines 
to your class:
```java
  @ProxyCodeSplit
  @NameToken("myToken")
  public interface MyProxy extends ProxyPlace<MyPresenter> {}
```

##Goals
his summarizes the goal of GWTP: to offer a simple to use MVP architecture with minimal boilerplate, without sacrificing GWT's best features. Here are some of the features currently supported by GWTP:

* Dependency injection through GIN and Guice;
* Simple but powerful history management mechanism;
* Support for nested presenters;
* Lazy instantiation for presenter and view;
* Effortless and efficient code splitting;
* Integrated command pattern supporting undo/redo;
* Plus other cool PlannedFeatures soon!

##Features
Moreover, GWTP strives to use the event bus in a clear and efficient way. Events are used to decouple loosely 
related objects, while direct method invocation is used to clarify the program flow between strongly coupled 
components. The result is an application that is easy to understand and that can grow with time.

In addition, GWTP offers components that let you:

* Efficiently implement a Command pattern in your application ;
* Organize your internationalization strings ;
* Use annotation processors to generate event, actions & responses, and DTOs ;
* Easily support search-engine crawling on your GWT application (in development);
* Simplify and clean-up testing code when using GIN and Guice.
* To learn more about these components, check out the LibraryOverview.

##Notes
See the GettingStarted page for details. You can also get plenty of support from developers and fellow users 
in the Forum, cheer for us on ohloh, or follow us on Twitter @PhilBeaudoin and @ArcBees!

GWTP is a fork of gwt-dispatch and gwt-presenter, many thanks to the original authors of these packages. If you're 
used to gwt-presenter, you might like to see how it compares to GWTP or read what Andreas Borglin says about switching.
GWTP is actively used in various projects, including the open source PuzzleBazar and large-scale commercial products.
If you like this project and would like to contribute, send an email to philippe.beaudoin@gmail.com. You can also take 
a look at good starting issues for new contributors. In all cases, join the discussion.

##Thanks to
[![IntelliJ](https://lh6.googleusercontent.com/--QIIJfKrjSk/UJJ6X-UohII/AAAAAAAAAVM/cOW7EjnH778/s800/banner_IDEA.png)](http://www.jetbrains.com/idea/index.html)
