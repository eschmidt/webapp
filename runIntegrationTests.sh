#!/bin/bash

mvn -DskipITs=false \
	-Dspring.social.facebook.appId="$FACEBOOK_APP_ID" \
	-Dspring.social.facebook.appSecret="$FACEBOOK_APP_SECRET" \
	\
	-Dtest.facebook.email="$FACEBOOK_TEST_EMAIL" \
	-Dtest.facebook.password="$FACEBOOK_TEST_PASSWORD" \
	-Dtest.facebook.displayName="$FACEBOOK_TEST_DISPLAY_NAME" \
	\
	clean verify
