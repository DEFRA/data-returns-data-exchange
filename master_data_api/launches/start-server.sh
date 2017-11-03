#!/bin/bash
script_dir="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
base_dir="$( cd "$(dirname "$script_dir")" && pwd )"
clear

# Build and install all modules
cd ${base_dir}/../ > /dev/null
mvn -Dcheckstyle.skip=true -Ddependency-check.skip=true -DskipTests=true install

echo "Current directory ${base_dir}"

echo -e "Starting the Data Returns Master Data API"
cd ${base_dir} > /dev/null
mvn -Dcheckstyle.skip=true -Ddependency-check.skip=true -DskipTests=true spring-boot:run -Drun.arguments="$1"
