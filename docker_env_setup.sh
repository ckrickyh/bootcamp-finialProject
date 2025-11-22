#Google Cloud Platform
# Fetch the VM's external IP address
EXTERNAL_IP=$(curl -H "Metadata-Flavor: Google" http://metadata.google.internal/computeMetadata/v1/instance/network-interfaces/0/access-configs/0/external-ip)

# Create/update a .env file that docker-compose will automatically read
echo "EXTERNAL_IP=${EXTERNAL_IP}" > .env

# Use the IP in docker-compose
# Note: Using env vars directly is easier if docker-compose.yml references them
# For example, if docker-compose.yml has:
#   environment:
#     - BASE_URL=http://${EXTERNAL_IP}:8102


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

# ! Step 3.1: create_db localhost 5432 for local, 5532 for Docker
cd pythonProjects
python create_bootcamp_db.py
cd ..

# ! Step 4: docker run (docker-compose is shortcut for docker run, docker-compose.yml)
docker compose up -d

# ! Step 5: python run
cd pythonProjects
python FYPHistory2DB.py
cd ..

# !!! when open this project, the first step is to run the below script in terminal
# source docker_env_setup.sh