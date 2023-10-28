#!/usr/bin/env bash

for id in $(cat old_files); do
  echo $id
  psql -U asrb -d asrb -qAt -c "update asrb.kn.file set data = '302 file moved'::bytea where id = $id;"
done
