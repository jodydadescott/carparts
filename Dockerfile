FROM anapsix/alpine-java:8u172b11_server-jre_unlimited
WORKDIR /
MAINTAINER jodyscott

RUN apk update

# Generic
RUN apk add bash curl bind-tools

RUN apk add git zip unzip

ADD dist/carparts /
ADD dist/entrypoint.sh /
RUN chmod +x /entrypoint.sh

CMD ["/entrypoint.sh"]
