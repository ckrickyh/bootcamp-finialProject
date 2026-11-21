#!/bin/sh
set -eu

case "${EXTERNAL_IP:-localhost}" in
  local|localhost)
    export SPRING_DATA_REDIS_HOST=redis-container
    export SPRING_DATA_REDIS_PORT=6379
    export SPRING_DATA_REDIS_PASSWORD=
    export SPRING_DATA_REDIS_SSL_ENABLED=false
    ;;
esac

exec java -jar /stock-data-0.0.1-SNAPSHOT.jar
