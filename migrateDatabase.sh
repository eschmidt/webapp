#!/bin/bash

if [ -r "$CATALINA_BASE/bin/checkittwice.env" ]; then
  . "$CATALINA_BASE/bin/checkittwice.env"
elif [ -r "$CATALINA_HOME/bin/checkittwice.env" ]; then
  . "$CATALINA_HOME/bin/checkittwice.env"
fi

mvn -P $ACTIVE_PROFILE \
	-Dspring.profiles.active="$ACTIVE_PROFILE" \
	\
	-Dspring.datasource.driverClassName="$DATASOURCE_DRIVER" \
	-Dspring.datasource.url="$DATASOURCE_URL" \
	-Dspring.datasource.username="$DATASOURCE_USERNAME" \
	-Dspring.datasource.password="$DATASOURCE_PASSWORD" \
	-Dspring.jpa.database.platform="$DATASOURCE_DIALECT" \
	\
	initialize flyway:migrate
