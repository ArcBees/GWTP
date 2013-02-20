#/bin/sh
# maven release helper
# run link 'sh maven-release.sh gpg github.username github.password' replacing the 3 parameters

echo "Started"

#TODO add to os enviroment, means I have to reboot and hadn't had time to do it.
export JAVA_HOME=`/usr/libexec/java_home -v 1.6`

mvn clean deploy -Prelease -DskipTests

mvn release:clean release:prepare --batch-mode -Dgpg.passphrase=$1 -Dgithub.username=$2 -Dgithub.password=$3

mvn release:perform -Dgpg.passphrase=$1

#TODO add nexus close and release goals

echo "Finished"

#TODO - this is in the works
#https://github.com/ArcBees/GWTP/issues/89

