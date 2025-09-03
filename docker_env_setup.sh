# ! Step 0: setup python virtual environment
source python_env_setup.sh

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

# ! Step 5: python run
cd pythonProjects
python FYPHistory2DB.py
cd ..

# !!! when open this project, the first step is to run the below script in terminal
# source docker_env_setup.sh