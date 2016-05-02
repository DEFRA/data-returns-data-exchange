#!/bin/bash
script_dir="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
base_dir="$( cd "$(dirname "$script_dir")" && pwd )"
clear
echo -e "Starting the Data Returns backend"
cd $base_dir

mvn -DskipTests=true package

# Get return code from maven
rc=$?;
if [[ $rc != 0 ]]; then 
	echo -e "\nMaven build failed, not starting server."
	exit $rc;
fi

server_cmd="$base_dir/target/data-returns-data-exchange-1.0-SNAPSHOT.jar"
clear
echo -e "Maven build successful, starting server: $server_cmd"
$server_cmd;
