#!/bin/sh

# TODO needs parameterizing

pkill java
nohup java -jar /srv/data-returns/data-returns-data-exchange-1.0-SNAPSHOT.one-jar.jar server /srv/data-returns/configuration_dev.yml &
