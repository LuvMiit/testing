#!/usr/bin/env bash

cd /opt/data
mkdir ../tmp
for f in *; do if [ ! -d "$f" ]; then d="${f:0:3}"; mkdir -p "../tmp/$d"; mv -t "../tmp/$d" -- "$f"; fi; done
mv ../tmp/* ./
sudo chown -R tomcat: /opt/data
