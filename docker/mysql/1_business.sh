#!/bin/bash
set -e
for sql_file in /seed/business/*.sql; do
    echo "Importing $sql_file"
    MYSQL_PWD="$MYSQL_ROOT_PASSWORD" mysql --default-character-set=utf8mb4 -uroot < "$sql_file"
done
