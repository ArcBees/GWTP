#/bin/sh
# maven release helper
# run link 'sh maven-release.sh xxxgpg-passxxx' replacing the 3 parameters

echo "Started"

#Be sure Java home is set to 1.7
#export JAVA_HOME=`/usr/libexec/java_home -v 1.7`

mvn clean deploy -Prelease -DskipTests

mvn release:clean release:prepare --batch-mode -Dgpg.passphrase=$1

mvn release:perform -Dgpg.passphrase=$1

#TODO add nexus close and release goals

echo "Finished"

#TODO - this is in the works
#https://github.com/ArcBees/GWTP/issues/89

