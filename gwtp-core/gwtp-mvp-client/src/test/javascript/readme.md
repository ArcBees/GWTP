#User Agent Sorter

User Agent Sorter is a java console program that helps sort useragents into desktop mobile and tablet.

To run

```
cd uasorter
mvn exec:java
```

You will see a list of category descriptions followed by a user agent string.

Type d, t, or m depending on which formfactor the user agent belongs to.

The user agent string will automatically be added to the test suite.

---

You can make uasorter repeat by replacing <repeat> with the number of times you want to run the program below

```
mvn exec:jave -Dexec.args="<repeat>"
```