#/bin/sh

# Editable
GWTPVER=0.8

# Don't Edit Below *********
CURRENTDIR=`pwd`
DISTRIBUTION=$CURRENTDIR/distribution
RELEASE=$DISTRIBUTION/release/com/gwtplatform
ZIPDIR=$DISTRIBUTION/downloads

echo "Started"

# Clean House
# Don't delete distribution if deploying to a repository
rm -rf $DISTRIBUTION
mkdir $DISTRIBUTION
mkdir $ZIPDIR

# Zip Samples
zip -x ".*" -r $ZIPDIR/gwtp-samples-$GWTPVER.zip gwtp-samples

# Maven Building

# Build local snapshots maven repository
#mvn -DaltDeploymentRepository=snapshot-repo::default::file:$DISTRIBUTION/snapshots clean deploy

# Build local release
mvn -Prelease -DaltDeploymentRepository=release::default::file://$DISTRIBUTION/release clean deploy

# Copy and zip jars
mkdir $ZIPDIR/gwtp-all
cp $RELEASE/gwtp-all/*.jar $ZIPDIR/gwtp-all

cd $ZIPDIR/gwtp-all
zip -x ".*" $ZIPDIR/gwtp-all/gwtp-all-$GWTPVER.zip *.jar

mkdir $ZIPDIR/gwtp-separate
for d in `ls -d $RELEASE/gwtp-clients* $RELEASE/gwtp-crawler* $RELEASE/gwtp-dispatch* $RELEASE/gwtp-mvp* $RELEASE/gwtp-processors* $RELEASE/gwtp-tester*`; do cp $d/$GWTPVER/*.jar $ZIPDIR/gwtp-separate/; done

cd $ZIPDIR/gwtp-separate
zip -x ".*" $ZIPDIR/gwtp-separate/gwtp-separate-$GWTPVER.zip *.jar

# Remove tmp directories
rm -rf $ZIPDIR/gwtp-samples/ $ZIPDIR/gwtp-separate/ $ZIPDIR/gwtp-all/

echo "Finished"