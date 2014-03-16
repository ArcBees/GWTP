#User Agent Sorter

User Agent Sorter is a java console program that helps sort user agents into desktop mobile and tablet.

To run

```
cd uasorter
mvn exec:java
```

You will be asked how many user agents you wish to sort.

You will see a list of category descriptions followed by the name of the browser and then its user agent string.

Type d, t, or m depending on which formfactor the user agent belongs to.

The user agent string will automatically be added to the test suite.

---

User agents are originally loaded from useragentswitcher.xml which can be found in this directory.

To ensure that you are using the latest version of useragentswitcher.xml replace the one in this folder with http://techpatterns.com/downloads/firefox/useragentswitcher.xml

This file supplies user agents to firefox's user agent switcher extension and appears to be kept relatively up to date.

Once all user agents in the file have been sorted you will see the message:

```
All the user agents I found had already been sorted
```



