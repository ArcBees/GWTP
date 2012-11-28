#/bin/sh
# Zip GWTP Jars
# This will zip of the GWTP library jars into ./distribution/downloads folder.

# Editable
GWTPVER=0.8-SNAPSHOT
REPO=~/.m2/repository/com/gwtplatform

# Don't Edit Below *********
echo "Started"

CURRENTDIR=`pwd`
DISTRIBUTION=$CURRENTDIR/distribution
ZIPDIR=$DISTRIBUTION/downloads

# Clean House
# Don't delete distribution if deploying to a repository
rm -rf $DISTRIBUTION
mkdir $DISTRIBUTION
mkdir $ZIPDIR

# Zip Samples
zip -r $ZIPDIR/gwtp-samples-$GWTPVER.zip gwtp-samples -x "*/.*"

# Maven Building
mvn clean install

# Maven options to building
# Build local snapshots maven repository
#mvn -DaltDeploymentRepository=snapshot-repo::default::file:$DISTRIBUTION/snapshots clean deploy
# Build local release
#mvn -Prelease -DaltDeploymentRepository=release::default::file://$DISTRIBUTION/release clean deploy

# Copy and zip jars
cd $ZIPDIR
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

echo "\nSee the zips in folder: $DISTRIBUTION\n"

echo "Finished"
