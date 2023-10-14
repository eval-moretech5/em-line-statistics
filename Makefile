
docker-build:
	docker build -t eshurupov/em23-linestat:1.0 -t eshurupov/em23-linestat:latest .

docker-login:
	docker login

docker-push:
	docker push --all-tags eshurupov/em23-linestat

docker-build-push: docker-build docker-push

build:
	./mvnw clean package -DskipTests

docker-fast-build: build
	docker build -f DockerfileFast -t eshurupov/em23-linestat:1.0 -t eshurupov/em23-linestat:latest .

docker-fast-build-push: docker-fast-build docker-push