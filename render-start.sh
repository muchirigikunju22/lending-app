#!/bin/sh
set -e

if [ -n "$DATABASE_URL" ]; then
    case "$DATABASE_URL" in
        postgres://*)
            USERINFO=$(echo "$DATABASE_URL" | sed -n 's|^postgres://\([^@]*\)@.*|\1|p')
            HOSTPORTPATH=$(echo "$DATABASE_URL" | sed -n 's|^postgres://[^@]*@\(.*\)|\1|p')
            HOST=$(echo "$HOSTPORTPATH" | sed -n 's|^\([^:]*\):.*|\1|p')
            PORTPATH=$(echo "$HOSTPORTPATH" | sed -n 's|^[^:]*:\(.*\)|\1|p')
            PORT=$(echo "$PORTPATH" | sed -n 's|^\([0-9]*\)/.*|\1|p')
            DB=$(echo "$PORTPATH" | sed -n 's|^[0-9]*/\(.*\)|\1|p')
            USER=$(echo "$USERINFO" | sed -n 's|^\([^:]*\):.*|\1|p')
            PASS=$(echo "$USERINFO" | sed -n 's|^[^:]*:\(.*\)|\1|p')

            export SPRING_DATASOURCE_URL="jdbc:postgresql://${HOST}:${PORT}/${DB}"
            export SPRING_DATASOURCE_USERNAME="${USER}"
            export SPRING_DATASOURCE_PASSWORD="${PASS}"
            ;;
    esac
fi

exec java -jar app.jar
