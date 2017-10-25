#!/usr/bin/env bash
script_dir="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
base_dir="$( cd "$( dirname "${script_dir}/../../../" )" && pwd )"
echo "base_dir: ${base_dir}"


tmp_dir="$script_dir/tmp"
output_dir="$base_dir/src/main/resources/static/apidocs"
echo "Tmp dir: ${tmp_dir}"
echo "Output dir: ${output_dir}"

if [ -d ${tmp_dir} ]
then
    rm -Rf ${tmp_dir}
fi
mkdir ${tmp_dir}
cd ${tmp_dir}
git init
git remote add origin -f https://github.com/swagger-api/swagger-ui.git
git config core.sparsecheckout true
echo "dist/*" >> .git/info/sparse-checkout
git pull --depth=2 origin master

rm ${output_dir}/*
cp dist/* ${output_dir}
# Update the default url in the index.html
sed -i -e 's/http:\/\/petstore.swagger.io\/v2\/swagger.json/\/api\/v1\/swagger.json/g' ${output_dir}/index.html
# Add validatorUrl: null option (insert before dom_id) in index.html
sed -i -e 's/dom_id:/validatorUrl: null,\n    dom_id:/g' ${output_dir}/index.html

rm -Rf ${tmp_dir}