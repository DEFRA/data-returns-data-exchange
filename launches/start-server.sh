#!/bin/bash
script_dir="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
base_dir="$( cd "$(dirname "$script_dir")" && pwd )"
config_file=$base_dir/target/external_resources/config/configuration_local.yml
clear
echo -e "Starting the Data Returns backend using configuration $config_file\n\n"
cd $base_dir

mvn -DconfigFile=$config_file resources:resources liquibase:dropAll liquibase:update package

# Get return code from maven
rc=$?;
if [[ $rc != 0 ]]; then 
	echo -e "\nMaven build failed, not starting server."
	exit $rc;
fi

server_cmd="java -jar $base_dir/target/data-returns-data-exchange-1.0-SNAPSHOT.jar server $config_file"
clear
echo -e "Maven build successful, starting server: $server_cmd"
$server_cmd;
