ARG NUXEO_VERSION=2021

FROM docker-private.packages.nuxeo.com/nuxeo/nuxeo:${NUXEO_VERSION}

ARG BUILD_TAG=unknown
ARG SCM_REF=unknown
ARG VERSION=unknown

LABEL org.nuxeo.java-client.build-tag=$BUILD_TAG
LABEL org.nuxeo.java-client.scm-ref=$SCM_REF
LABEL org.nuxeo.java-client.version=$VERSION

RUN --mount=type=bind,source=./nuxeo-java-client-test/target,target=/tmp/local-packages \
  /install-packages.sh --offline /tmp/local-packages/nuxeo-java-client-test-*.zip
