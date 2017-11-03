#!/bin/bash
script_dir="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
base_dir="$( cd "$(dirname "$script_dir")" && pwd )"
cd ${base_dir}

# Build and install all modules
cd ${base_dir}/../ > /dev/null
mvn -Dcheckstyle.skip=true -Ddependency-check.skip=true -DskipTests=true -T 1C -pl master_data_api -am install
# Get return code from maven
rc=$?;
if [[ ${rc} != 0 ]]; then
	echo -e "\nAn error occurred when attempting to compile project artifacts."
	exit ${rc};
fi


echo "Current directory ${base_dir}"

# Execute maven commands to rebuild the database
cd ${base_dir} > /dev/null
mvn -Dcheckstyle.skip=true -Ddependency-check.skip=true -DskipTests=true sql:execute@drop-database sql:execute@create-database exec:java@load-data

# Get return code from maven
rc=$?;
if [[ ${rc} != 0 ]]; then
	echo -e "\nAn error occurred when attempting to rebuild the database."
	exit ${rc};
fi
