# ! Step 1: Stop down all docker container
docker compose stop data-supplier-app ui-app stock-data-app
# docker compose stop data-provider-app ui-app stock-data-app


# ! Step 2: Remove old container if any
# docker rm data-provider-app
docker rm data-supplier-app
docker rm stock-data-app
docker rm ui-app

# ! Step 3: maven install and docker build
cd data-supplier
mvn clean install
docker build -t data-supplier:0.0.1 -f Dockerfile .
cd ..
cd stock-data
mvn clean install
docker build -t stock-data:0.0.1 -f Dockerfile .
cd ..
cd ui
mvn clean install
docker build -t ui:0.0.1 -f Dockerfile .
cd ..

# ! Step 4: docker run (docker-compose is shortcut for docker run)
docker compose up -d


# 乜都唔做，第一樣做這個 docker step
# source docker_env_setup.sh