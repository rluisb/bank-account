.DEFAULT_GOAL := run-application

tear-down-docker-compose:
	echo "Tearing down docker containers for this docker-compose"
	docker-compose down

run-application:
	$(MAKE) tear-down-docker-compose
	echo "Starting database and application for this docker-compose"
	@docker-compose up --build --remove-orphans -d