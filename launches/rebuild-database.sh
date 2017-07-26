#!/bin/bash
script_dir="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
base_dir="$( cd "$(dirname "$script_dir")" && pwd )"
cd ${base_dir}
clear

mvn resources:resources sql:execute@drop-database sql:execute@create-database

# Get return code from maven
rc=$?;
if [[ ${rc} != 0 ]]; then
	echo -e "\nAn error occurred when attempting to rebuild the database."
	exit ${rc};
fi
