export JAVA_HOME=/opt/jdk1.8.0_45
export PATH=$JAVA_HOME/bin:$PATH
pkill java
java -version
nohup java -jar target/data-returns-data-exchange-1.0-SNAPSHOT.one-jar.jar server configuration_local.yml &
tail -f nohup.out
