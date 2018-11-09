BUILD_NUMBER := latest
PROJECT_NAME := carparts
DOCKER_REGISTRY := jodydadescott
DOCKER_IMAGE_NAME?=$(PROJECT_NAME)
DOCKER_IMAGE_TAG?=$(BUILD_NUMBER)

java:
	make proto
	make target/_java

proto:
	protoc --java_out=src/main/java schema.proto

clean:
	$(RM) target/_java
	$(RM) target/_docker
	$(RM) -rf dist

push:
	make docker
	docker push $(DOCKER_REGISTRY)/$(DOCKER_IMAGE_NAME):$(DOCKER_IMAGE_TAG)

dist:
	mkdir -p dist
	make java
	cp target/carparts-core.jar dist/carparts.jar
	chmod 644 dist/carparts.jar
	cp -r src/main/resources/webapp dist
	cp -r src/main/resources/entrypoint.sh dist
	chmod +x dist/entrypoint.sh

docker:
	make dist
	make target/_docker

dockerrun:
	make docker
	docker run -it --entrypoint /bin/bash $(DOCKER_REGISTRY)/$(PROJECT_NAME):$(BUILD_NUMBER)||:

run:
	make java
	mvn exec:java||:

################################################################################

target/_docker:
	docker build -t $(DOCKER_REGISTRY)/$(DOCKER_IMAGE_NAME):$(DOCKER_IMAGE_TAG) .
	touch target/_docker

target/_java:
	mvn clean install -Dmaven.test.skip=true
	touch target/_java
