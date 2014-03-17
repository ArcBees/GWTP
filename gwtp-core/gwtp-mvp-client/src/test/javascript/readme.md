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

---

User Agent Sorter will also fetch useragents from uascom.json which was originally fetched from:
http://www.useragentstring.com/pages/All/ and then converted to json.

This file is a little flakey and User Agent Sorter may safely crash to desktop every now and then while using it.

You can just restart the program when this happens or comment out the following line from UserAgentSorter.java to stop the crashes from occurring.

```
 userAgentProviders.add(new UasComUserAgentProvider());
```