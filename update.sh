#!/bin/bash

# This re-copies sources from ../ocs3

echo 🔶  Setting up 'db'
rm -r modules/db/*
cp -r ../ocs3/modules/db/src modules/db/

echo 🔶  Setting up 'gen'
rm -r modules/gen/*
mkdir -p modules/gen/src/main
cp -r ../ocs3/modules/sql/src/main/scala modules/gen/src/main/

echo 🔶  Setting up 'schema'
rm -r modules/schema/*
mkdir -p modules/schema/src/main
cp -r ../ocs3/modules/sql/src/main/resources modules/schema/src/main/
# these files need newlines at the end
echo "" >> ./modules/schema/src/main/resources/db/migration/V060__Light_Sinks.sql
echo "" >> ./modules/schema/src/main/resources/db/migration/V058__Dhs_KeywordNames.sql

echo 🔶  Setting up 'model'
rm -r modules/model/*
cp -r ../ocs3/modules/core/* modules/model/

