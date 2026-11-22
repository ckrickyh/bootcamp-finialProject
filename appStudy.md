# Azure Container Apps 微服務部署完整指南

本指南詳述如何將本專案的三層 Java Spring Boot 微服務（`data-supplier`、`stock-data`、`ui`）打包為容器映像檔，推送至 Azure Container Registry（ACR），並部署至 Azure Container Apps（ACA）環境。

- App Link: https://ui-app.blackstone-fcaa9eac.westus2.azurecontainerapps.io/us/heatmap
- ACA Console: https://ms.portal.azure.com/#@d09e60f5-a8cc-4a4c-814e-36db4325133c/resource/subscriptions/033c6961-0018-4912-99fd-89158933840e/resourceGroups/resource/providers/Microsoft.App/containerApps/ui-app
---

## 一、 前置準備與環境檢查

### 1. 工具需求
* **Azure CLI**：已安裝並完成登入（`az login`）。
* **Java 21 & Maven**：本機編譯 Spring Boot 專案所需。
* **Docker Desktop**：映像檔打包與上傳所需（若在 Apple Silicon Mac 上執行，映像檔需指定編譯為 `linux/amd64`）。

### 2. Azure 帳號與登錄庫設定
確認 Azure Container Registry 存在，且管理使用者（Admin User）已啟用，以供 Container Apps 通過身分驗證拉取映像檔：

```bash
# 檢查現有 ACR 清單
az acr list -o table

# 啟用 ACR 管理使用者（若 adminUserEnabled 為 false）
az acr update --name acrstock2504 --admin-enabled true

# 取得 ACR 登入密碼（後續 Container Apps 部署使用）
az acr credential show --name acrstock2504 --query "passwords[0].value" -o tsv

# 本機登入 ACR
az acr login --name acrstock2504
```

---

## 二、 階段一：Maven 編譯與 Docker 映像檔建置

### 1. 本機 Maven 編譯 JAR 檔
在各模組根目錄中執行打包，略過單元測試以加快建置流程：

```bash
# 編譯 data-supplier
cd data-supplier && mvn clean package -DskipTests && cd ..

# 編譯 stock-data
cd stock-data && mvn clean package -DskipTests && cd ..

# 編譯 ui
cd ui && mvn clean package -DskipTests && cd ..
```

### 2. 建置跨平台 Docker 映像檔
Azure Container Apps 運行於 x86_64（`linux/amd64`）架構。若在 macOS Apple Silicon（M 系列晶片）建置，必須加入 `--platform linux/amd64`：

```bash
# 建置 data-supplier 映像檔
docker build --platform linux/amd64 -t acrstock2504.azurecr.io/data-supplier:0.0.1 -f data-supplier/Dockerfile data-supplier/

# 建置 stock-data 映像檔
docker build --platform linux/amd64 -t acrstock2504.azurecr.io/stock-data:0.0.3 -f stock-data/Dockerfile stock-data/

# 建置 ui 映像檔
docker build --platform linux/amd64 -t acrstock2504.azurecr.io/ui:0.0.3 -f ui/Dockerfile ui/
```

### 3. 推送映像檔至 Azure Container Registry
```bash
docker push acrstock2504.azurecr.io/data-supplier:0.0.1
docker push acrstock2504.azurecr.io/stock-data:0.0.3
docker push acrstock2504.azurecr.io/ui:0.0.3
```

---

## 三、 階段二：Azure Container Apps 微服務部署

### 1. 建立/確認 Container Apps 託管環境
若尚未建立 Managed Environment，需先建立環境以承載微服務叢集：

```bash
# 建立資源群組（若無）
az group create --name resource --location westus2

# 建立 Container Apps 託管環境（若無）
az containerapp env create \
  --name managedEnvironment-resource-8dea \
  --resource-group resource \
  --location westus2
```

### 2. 部署 `data-supplier-app`（內部行情微服務）
* 運算規格：`0.25 vCPU / 0.5 GiB`
* 網路設定：內部（`internal`），Target Port 為 `8090`

```bash
az containerapp create \
  --name data-supplier-app \
  --resource-group resource \
  --environment managedEnvironment-resource-8dea \
  --image acrstock2504.azurecr.io/data-supplier:0.0.1 \
  --registry-server acrstock2504.azurecr.io \
  --registry-username acrstock2504 \
  --registry-password $(az acr credential show --name acrstock2504 --query "passwords[0].value" -o tsv) \
  --target-port 8090 \
  --ingress internal \
  --cpu 0.25 \
  --memory 0.5Gi \
  --min-replicas 1 \
  --max-replicas 1 \
  --env-vars EXTERNAL_IP=remote
```

### 3. 部署 `stock-data-app`（內部資料處理與快取微服務）
* 運算規格：`0.50 vCPU / 1.0 GiB`
* 網路設定：內部（`internal`），Target Port 為 `8091`
* 整合：注入 Neon PostgreSQL 與 Upstash Redis 連線字串，並設定 `DATA_SUPPLIER_HOST` 與 `DATA_SUPPLIER_PORT`。

```bash
az containerapp create \
  --name stock-data-app \
  --resource-group resource \
  --environment managedEnvironment-resource-8dea \
  --image acrstock2504.azurecr.io/stock-data:0.0.3 \
  --registry-server acrstock2504.azurecr.io \
  --registry-username acrstock2504 \
  --registry-password $(az acr credential show --name acrstock2504 --query "passwords[0].value" -o tsv) \
  --target-port 8091 \
  --ingress internal \
  --cpu 0.5 \
  --memory 1.0Gi \
  --min-replicas 1 \
  --max-replicas 1 \
  --env-vars \
    EXTERNAL_IP=remote \
    DATA_SUPPLIER_HOST=data-supplier-app \
    DATA_SUPPLIER_PORT=80 \
    SPRING_DATASOURCE_URL="jdbc:postgresql://ep-wandering-surf-b5lp54nl-pooler.c-7.us-east-2.aws.neon.tech/neondb?sslmode=require" \
    SPRING_DATASOURCE_USERNAME=neondb_owner \
    SPRING_DATASOURCE_PASSWORD=npg_nDOuHep8ZFy9 \
    SPRING_DATA_REDIS_HOST=glowing-shrimp-282590.upstash.io \
    SPRING_DATA_REDIS_PORT=6379 \
    SPRING_DATA_REDIS_PASSWORD=gQAAAAAABE_eAAIgcDI3ZTNlZThhYTc4ZDI0YjY1OWExYjc0MzgxMzM2ZGUwOQ \
    SPRING_DATA_REDIS_SSL=true
```

### 4. 部署 `ui-app`（對外前端微服務）
* 運算規格：`0.50 vCPU / 1.0 GiB`
* 網路設定：外部（`external`），Target Port 為 `8092`
* 整合：公開 FQDN 綁定與 `STOCK_DATA_HOST`、`STOCK_DATA_PORT` 設定。

```bash
az containerapp create \
  --name ui-app \
  --resource-group resource \
  --environment managedEnvironment-resource-8dea \
  --image acrstock2504.azurecr.io/ui:0.0.3 \
  --registry-server acrstock2504.azurecr.io \
  --registry-username acrstock2504 \
  --registry-password $(az acr credential show --name acrstock2504 --query "passwords[0].value" -o tsv) \
  --target-port 8092 \
  --ingress external \
  --cpu 0.5 \
  --memory 1.0Gi \
  --min-replicas 1 \
  --max-replicas 1 \
  --env-vars \
    EXTERNAL_IP=remote \
    STOCK_DATA_HOST=stock-data-app \
    STOCK_DATA_PORT=80 \
    BASE_URL="https://ui-app.blackstone-fcaa9eac.westus2.azurecontainerapps.io" \
    SPRING_DATASOURCE_URL="jdbc:postgresql://ep-wandering-surf-b5lp54nl-pooler.c-7.us-east-2.aws.neon.tech/neondb?sslmode=require" \
    SPRING_DATASOURCE_USERNAME=neondb_owner \
    SPRING_DATASOURCE_PASSWORD=npg_nDOuHep8ZFy9
```

---

### 5. 日常程式碼變更與更新現有 App（Update SOP）

當本地程式碼（例如前端 JavaScript、頁面或後端 Service）修改後，請依循以下標準作業程序更新至 Azure Container Apps：

#### 步驟 1：本機登入 ACR
```bash
az acr login --name acrstock2504
```

#### 步驟 2：重新編譯修改的模組（以 `ui` 為例）
```bash
cd ui && mvn clean package -DskipTests && cd ..
```
*(若修改的是 `stock-data`，則執行 `cd stock-data && mvn clean package -DskipTests && cd ..`)*

#### 步驟 3：建置跨平台映像檔並推送到 ACR（建議版本號遞增）
```bash
# 建置 linux/amd64 映像檔（以版本 0.0.4 為例）
docker build --platform linux/amd64 -t acrstock2504.azurecr.io/ui:0.0.4 -f ui/Dockerfile ui/

# 推送至 ACR
docker push acrstock2504.azurecr.io/ui:0.0.4
```

#### 步驟 4：更新 Azure Container App 映像檔
執行更新後，Azure 會自動建立新的 Revision，並以零停機時間（Rolling update）切換流量：

```bash
# 1. 僅更新映像檔（既有環境變數會自動保留）
az containerapp update \
  --name ui-app \
  --resource-group resource \
  --image acrstock2504.azurecr.io/ui:0.0.4

# 2. 若需同時更新環境變數（例如 BASE_URL）：
az containerapp update \
  --name ui-app \
  --resource-group resource \
  --image acrstock2504.azurecr.io/ui:0.0.4 \
  --set-env-vars BASE_URL="https://ui-app.blackstone-fcaa9eac.westus2.azurecontainerapps.io"
```

#### 步驟 5：驗證最新版本狀態
```bash
# 檢查 Revision 列表與運行狀態
az containerapp revision list --name ui-app --resource-group resource -o table
```

---

## 四、 微服務內部服務發現（Service Discovery）核心機制

1. **ACA 內部 DNS 與 Envoy Proxy**：
   * 在同一個 Managed Environment 下，容器可直接透過容器名稱（如 `http://data-supplier-app` 或 `http://stock-data-app`）通訊。
   * **關鍵差異**：內部 Ingress 預設由 Envoy 代理接聽 HTTP **Port 80**，再自動轉發給容器本身的 Target Port（如 8090 或 8091）。因此微服務間互相呼叫時，連接埠請指向 `80`。
2. **Spring Boot 動態組態支援**：
   * `ProviderServiceImpl.java` 與 `UiServiceImpl.java` 透過 `@Value("${service.xxx.host}")` 與 `@Value("${service.xxx.port}")` 注入主機與連接埠。
   * 本地環境（`localhost`）：使用 `8090` 與 `8091`。
   * 雲端環境（`remote`）：自動對應至 `80`。

---

## 五、 驗證與維運指令

### 1. 查詢容器狀態與公開 FQDN
```bash
az containerapp list --resource-group resource -o table
```

### 2. 即時查看容器日誌（Logs）
若需排查啟動或運行異常，可查看指定容器日誌：

```bash
# 查看 ui-app 即時日誌
az containerapp logs show --name ui-app --resource-group resource --tail 50 --follow

# 查看 stock-data-app 即時日誌
az containerapp logs show --name stock-data-app --resource-group resource --tail 50 --follow

# 查看 data-supplier-app 即時日誌
az containerapp logs show --name data-supplier-app --resource-group resource --tail 50 --follow
```

### 3. 線上端點驗證
* **熱力圖介面**：`https://ui-app.blackstone-fcaa9eac.westus2.azurecontainerapps.io/us/heatmap`
* **K 線歷史走勢圖**：`https://ui-app.blackstone-fcaa9eac.westus2.azurecontainerapps.io/us/history/AAPL`

### 4. 費用最佳化策略（Scale to Zero）
專案展示或非使用期間，可將各容器設定為休眠模式以節省免費額度：

```bash
# 閒置時自動縮容至 0（有請求時自動喚醒）
az containerapp update --name ui-app --resource-group resource --min-replicas 0
az containerapp update --name stock-data-app --resource-group resource --min-replicas 0
az containerapp update --name data-supplier-app --resource-group resource --min-replicas 0
```

---

## 六、 歷史踩坑紀錄與重要注意事項（Mistakes & Key Considerations）

在本次部署與微服務串接過程中，曾遭遇並排查解決以下核心問題，日後維護與重新部署時必須特別留意：

### 1. ACR 管理者身分驗證未開啟（`adminUserEnabled: false`）
* **錯誤現象**：執行 `az acr login` 或 ACA 拉取映像檔時回傳 `Authentication Required` 或權限遭拒。
* **原因分析**：Azure 建立 ACR 登錄庫時，預設不會開啟管理者帳號（Admin User）。
* **正確做法**：建立 ACR 後，必須立即執行 `az acr update --name <ACR名稱> --admin-enabled true`，並以 `az acr credential show` 取得憑證注入給 Container Apps。

### 2. Mac Apple Silicon（M 系列晶片）跨平台架構不符（ARM64 vs AMD64）
* **錯誤現象**：映像檔成功推送到 ACR，但在 Container Apps 啟動時容器不斷崩潰（`CrashLoopBackOff`）或顯示 `exec format error`。
* **原因分析**：在 macOS（Apple Silicon）上執行 `docker build` 預設會編譯為 ARM64 架構，而 Azure Container Apps 底層虛擬機節點為 x86_64（AMD64）架構。
* **正確做法**：建置指令一律強制指定 `--platform linux/amd64`：
  ```bash
  docker build --platform linux/amd64 -t <映像檔標籤> -f Dockerfile .
  ```

### 3. 微服務內部通訊寫死連接埠（8090 / 8091 vs 80）
* **錯誤現象**：前端 `ui-app` 呼叫後端 `stock-data-app` 時發生 `Connection Refused` 或請求超時（Timeout 60s）。
* **原因分析**：
  * 在本地 Docker Compose 中，微服務間可直接存取 `http://stock-data-app:8091`。
  * 在 Azure Container Apps 中，內部 Ingress 是由環境內建的 **Envoy 反向代理**統一接聽 **Port 80**，再由 Envoy 轉發至容器設定的 Target Port（8091）。若程式碼硬編碼指定 `port(8091)`，流量將無法打通 Envoy 代理。
* **正確做法**：
  * 在 Java 程式碼中將連接埠改為 `@Value("${service.xxx.port}")` 動態注入。
  * 本地配置 `8091` / `8090`，雲端環境則配置 `80`（或省略連接埠）。

### 4. 內部微服務設定 `min-replicas 0` 導致冷啟動超時與找不到複本
* **錯誤現象**：存取網頁時後端回應 `Could not find a replica for this app`，或前端等待超過 60 秒報錯。
* **原因分析**：
  * `min-replicas 0`（縮容至零）在無外部 HTTP 流量時會將容器完全關閉。
  * Java 21 Spring Boot 冷啟動初始化（連線 Neon DB、讀取 Finnhub、建立 JPA EntityManager）約需 6 至 10 秒；若內部呼叫鏈多層休眠，會直接超出 HTTP 客戶端預設連線逾時時間。
* **正確做法**：
  * 正式運行或專案展示期間，建議將各微服務設為 `min-replicas 1`，確保容器處於隨時待命狀態（Hot Standby）。
  * 確定長期不使用且需節省免費額度時，再批次改為 `min-replicas 0`。

### 5. 免費訂用帳戶無法執行 `az acr build` 雲端建置
* **錯誤現象**：執行 `az acr build` 時報錯 `(TasksOperationsNotAllowed) ACR Tasks requests are not permitted`。
* **原因分析**：Azure 針對特定訂用帳戶（如免費試用帳戶或特定區域配額限制）預設停用 ACR Tasks 雲端自動建置功能。
* **正確做法**：回歸標準流程：本機執行 `mvn clean package -DskipTests` 編譯 JAR 檔，啟動本機 Docker Desktop 進行跨架構打包（`docker build --platform linux/amd64`），最後透過 `docker push` 直推 ACR。

### 6. JVM 記憶體規格不足引發 OOMKilled
* **錯誤現象**：容器啟動到一半突然無預警重啟，日誌中斷無錯誤訊息。
* **原因分析**：Java 21 搭配 Spring Boot、Hibernate 與 HikariCP 連線池在初始化時需要充足記憶體，若配置小於 0.5 GiB，容易在載入實體類別時觸發 Linux 核心 OOM（Out Of Memory）終止程序。
* **正確做法**：
  * 輕量轉發服務（`data-supplier`）：配置 `0.25 vCPU / 0.5 GiB`。
  * 具備資料庫與快取連線的服務（`stock-data`、`ui`）：至少配置 `0.50 vCPU / 1.0 GiB`。

### 7. 映像檔版本標籤覆蓋快取陷阱
* **錯誤現象**：修改 Java 程式碼並推送同名標籤（如 `0.0.1`）後，Azure 容器未更新至最新版本。
* **原因分析**：Container Apps 節點可能快取了舊的同名映像檔層（Image Layer）。
* **正確做法**：每次程式碼有實質變更時，建議更新映像檔版本號標籤（如 `0.0.2`、`0.0.3`），並在更新 Container App 時明確指定最新標籤。
