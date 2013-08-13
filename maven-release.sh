#/bin/sh
# maven release helper
# notes: https://github.com/ArcBees/GWTP/issues/89

if [ -z $1 ]; then
    echo "Please provide the gpg password > sh maven-release.sh xxxgpg-passxxx";
    exit
fi

if [ -z $JAVA_HOME ]; then
    echo "Please set JAVA_HOME > export JAVA_HOME=`/usr/libexec/java_home -v 1.7`";
    exit
fi

if [ -z $MAVEN_OPTS ]; then
    echo "Setting some MAVEN_OPTS > export MAVEN_OPTS='-Xmx512m -XX:MaxPermSize=128m'";
    export MAVEN_OPTS="-Xmx512m -XX:MaxPermSize=128m"
fi

echo "Started..."

mvn clean deploy -Prelease -DskipTests

mvn release:clean release:prepare --batch-mode -Dgpg.passphrase=$1

mvn release:perform -Dgpg.passphrase=$1

#TODO add nexus close and release goals

#TODO build javadoc

echo "...Finished"

