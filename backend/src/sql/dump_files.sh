#!/usr/bin/env bash

psql -U asrb -d asrb -P pager=off -qAt -c "select id from asrb.kn.file where created between '2022-01-01 00:00:00.000' and '2022-12-31 23:59:59.999' order by id desc;" > old_files

for id in $(cat old_files); do
  echo $id
  psql -U asrb -d asrb -P pager=off -qAt -c "select encode(data,'base64') from asrb.kn.file where id = $id;" > $id.base64
  cat $id.base64 | base64 -d > $id
done
