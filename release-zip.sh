#/bin/sh

rm -f ../gwtp-samples-$GWTPVER.zip ../gwtp-separate-$GWTPVER.zip ../gwtp-all-$GWTPVER.zip
rm -rf ../gwtp-samples/ ../gwtp-separate/ ../gwtp-all/
hg update gwtp-$GWTPVER
mvn clean
find . -name ".?*" -a -not -name ".hg*" -exec rm -rf {} \;
zip -r ../gwtp-samples-$GWTPVER.zip gwtp-samples
mvn install
cd ..
REPO=~/.m2/repository/com/gwtplatform
mkdir gwtp-all
cp  $REPO/gwtp-all/$GWTPVER/*.jar gwtp-all
cd gwtp-all
zip ../gwtp-all-$GWTPVER.zip *.jar
cd ..
mkdir gwtp-separate
for d in `ls -d $REPO/gwtp-clients* $REPO/gwtp-crawler* $REPO/gwtp-dispatch* $REPO/gwtp-mvp* $REPO/gwtp-processors* $REPO/gwtp-tester*`; do cp $d/$GWTPVER/*.jar gwtp-separate/; done
cd gwtp-separate
zip ../gwtp-separate-$GWTPVER.zip *.jar
cd ..
rm -rf gwtp-samples/ gwtp-separate/ gwtp-all/
