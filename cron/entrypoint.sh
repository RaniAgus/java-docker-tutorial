#!/usr/bin/env sh

echo "$CRON_SCHEDULE java -cp application.jar io.github.raniagus.example.bootstrap.Bootstrap" > /etc/crontabs/appuser

crond -f -d 8
