#!/bin/bash

# Detect if running on Google Cloud Platform or local machine
# Try to fetch the VM's external IP address from GCP metadata server
EXTERNAL_IP=$(curl -s -f -H "Metadata-Flavor: Google" --connect-timeout 2 http://metadata.google.internal/computeMetadata/v1/instance/network-interfaces/0/access-configs/0/external-ip 2>/dev/null)

# If not on GCP (curl failed), use localhost for local development
if [ -z "$EXTERNAL_IP" ]; then
    echo "Not running on GCP, using localhost for local development"
    EXTERNAL_IP="localhost"
else
    echo "Running on GCP, detected external IP: ${EXTERNAL_IP}"
fi

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
echo "Docker containers stopped"

# ! Step 2: Remove old container if any
# docker rm data-provider-app
docker rm data-supplier-app
docker rm stock-data-app
docker rm ui-app
echo "Old Docker containers removed"

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
echo "Dockerfile built"

# ! Step 3.1: create_db localhost 5432 for local, 5532 for Docker
cd pythonProjects
python create_bootcamp_db.py
cd ..
echo "DB created"

# ! Step 4: docker run (docker-compose is shortcut for docker run, docker-compose.yml)
docker compose up -d
echo "Docker containers started"

# ! Step 5: python run
cd pythonProjects
python FYPHistory2DB.py
cd ..
echo "Python Historical data ingested"

# ! Step 6: cloudflare-gcp docker connection
docker run cloudflare/cloudflared:latest tunnel --no-autoupdate run --token eyJhIjoiYzUyY2E4N2JlZjA1ZGQ3NjI4ZmY1MzIyNmMzNGQ0OWYiLCJ0IjoiYjZlMGM2NDAtMTEyNS00MDgxLWFkZjgtMzI5NTI0MzQ1OTBmIiwicyI6Ik9HSTNOamxrTURBdE5EWTJNeTAwWkRZMUxUaG1OekV0TURObE9ESmpabVJtT0dVdyJ9
echo "Cloudflare-GCP tunnel started"

# !!! when open this project, the first step is to run the below script in terminal
# source docker_env_setup.sh