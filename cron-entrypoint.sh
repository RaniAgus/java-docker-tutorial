#!/usr/bin/env sh

# Exit immediately if a command exits with a failure status
set -e

# Set default values for environment variables controlling the cron schedule
BOOTSTRAP_SCHEDULE=${BOOTSTRAP_SCHEDULE:-"*/5 * * * *"}
HELLO_SCHEDULE=${HELLO_SCHEDULE:-"*/15 * * * * * *"}

# Create the supercronic crontab file
echo "${BOOTSTRAP_SCHEDULE} java -Dapplication.env=prod -cp application.jar io.github.raniagus.example.Bootstrap" > ./crontab
echo "${HELLO_SCHEDULE} echo 'Hello from supercronic!'" >> ./crontab

# Run supercronic with the created crontab file
supercronic "$@" ./crontab
