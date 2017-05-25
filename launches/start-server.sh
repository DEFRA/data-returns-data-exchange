#!/bin/bash
script_dir="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
base_dir="$( cd "$(dirname "$script_dir")" && pwd )"
clear
echo -e "Starting the Data Returns backend"
cd ${base_dir}

mvn -DskipTests=true spring-boot:run
