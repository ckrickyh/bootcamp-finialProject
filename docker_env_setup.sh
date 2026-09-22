#!/bin/bash

# 檢查並讀取 .env 中的 EXTERNAL_IP 設定
if grep -q "^EXTERNAL_IP=" .env 2>/dev/null; then
    # 讀取數值並過濾行尾註解、空白與引號
    EXTERNAL_IP=$(grep "^EXTERNAL_IP=" .env | cut -d '=' -f2 | cut -d '#' -f1 | tr -d ' "')
    echo "🔁 Running mode: EXTERNAL_IP=${EXTERNAL_IP} (detected from .env)"
else
    # 若無設定，預設為 localhost 並追加寫入
    EXTERNAL_IP="localhost"
    echo "EXTERNAL_IP=${EXTERNAL_IP}" >> .env
    echo "🔁 No EXTERNAL_IP found in .env, defaulted to: ${EXTERNAL_IP}"
fi

# ! Step 0: setup python virtual environment
source python_env_setup.sh

# ! Step 1: Stop down all docker container
docker compose stop data-supplier-app ui-app stock-data-app
# docker compose stop data-provider-app ui-app stock-data-app
echo "🔁Docker containers stopped"

# ! Step 2: Remove old container if any
# docker rm data-provider-app
docker rm data-supplier-app
docker rm stock-data-app
docker rm ui-app
echo "🔁Old Docker containers removed"

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
echo "✅Dockerfile built"

# ! Step 3.1: create_db localhost 5432 for local, 5532 for Docker
if [ "${EXTERNAL_IP}" = "localhost" ]; then
    docker compose up -d postgres
    sleep 3
    cd pythonProjects
    python create_bootcamp_db.py
    cd ..
    echo "✅DB created"
else
    echo "⏩ Remote mode detected, skipping local DB creation."
fi

# ! Step 4: docker run (docker-compose is shortcut for docker run, docker-compose.yml)
docker compose up -d
echo "✅Docker containers started"

# ! Step 5: python run
cd pythonProjects
python FYPHistory2DB.py
cd ..
echo "✅Python Historical data ingested"



# !!! when open this project, the first step is to run the below script in terminal
# source docker_env_setup.sh