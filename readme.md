-- **Click the image** to view the video demonstration --
[![App Preview](projectPreview/FinalProject-StockHeatmapPost.png)](https://vimeo.com/manage/videos/1115271799)

US Stock Heatmap Link: http://34.169.230.10:8102/us/heatmap<br>
Video Back-End Demonstration: https://vimeo.com/manage/videos/1115271799<br>

**Step of Deploying Application**
===============================
0. Prerequisite : Docker installation for Docker-compose deployment
1. git clone the project to your location
2. cd to the project folder
3. run `docker_env_setup.sh` to build and run the docker containers

**Final Project: Stock Heatmap Application**
=================================
This application provides real-time insights into stock performance through an interactive heatmap featuring 60 stocks. Stock data is fetched from Finnhub every 30 seconds, ensuring users have access to the latest information. Key features include :

**Java & Spring Boot with micro-services**: This project utilizes Java and Spring Boot for the backend, providing a robust and scalable architecture. It comprises three microservices that work together to implement the business logic, where DTO and Entity serve as important medium to transfrom information between API message (DTO) <-> Database data (Entity):<br>

*Difference between DTO & Entty*<br>
 **#** | **Aspect**    | **Entity**                               | **DTO**                      
-------|---------------|------------------------------------------|------------------------------
 1     | Location      | Database layer                           | API/Service layer            
 2     | Annotations   | JPA annotations (@Entity, @Column, etc.) | No database annotations      
 3     | Purpose       | Database operations                      | Data transfer                
 4     | Relationships | Can have @ManyToOne, @OneToMany          | No relationships             
 5     | Data Source   | Single database table                    | Can combine multiple sources 
 6     | Used By       | Repository, Service (internal)           | Controller, API responses 


**Microservices Architecture**: The application is structured into three distinct microservices, each responsible for specific functionalities:<br>
1. Data Supplier: This microservice fetches real-time data from Finnhub using 4 API keys, with each key enabling 15 stock data retrievals.<br>

2. Stock Data: This service obtains real-time data from the Data Supplier and stores historical data in a PostgreSQL database.<br>

3. User Interface: This microservice provides a front-end interface for data visualization, utilizing the ECharts library for interactive and dynamic charts. This allows users to effectively analyze and interpret stock data.<br>

This microservices architecture allows changes to one service without impacting the others. For example, switching the data source from Finnhub to Yahoo or updating the user interface does not affect the other microservices.<br>

*The 3-Layer Architecture*<br>
![App Preview](projectPreview/MicroService.png)</br></br>

**Rule of Micro-service**<br>
 **#** | **Layer**  | **Can Access**                                | **Cannot Access**                   
-------|------------|-----------------------------------------------|-------------------------------------
 1     | Controller | ✅ Services, Mappers                           | ❌ Repositories, Entities (directly) 
 2     | Service    | ✅ Repositories, Other Services, External APIs | ❌ Controllers                       
 3     | Repository | ✅ Database (via JPA)                          | ❌ Services, Controllers   

*Separation of concerns:*<br>
  - Controllers handle HTTP requests/responses<br>
  - Services contain business logic<br>
  - Repositories handle database operations<br>

*Dependency injection:*<br>
  - @Autowired for dependency injection ("Hey Spring, please give me this object", reducing works in "new" an object = Creates an instance of it automatically)<br>
  - Controllers inject Services<br>
  - Services inject Repositories<br>

*Microservices communication:*<br>
  - Services communicate via RestTemplate (HTTP)<br>
  - No direct database access across services<br>

**Advantage of this Architecture**<br>
*Separation of concerns*: Each layer has a single responsibility<br>
*Testability*:            Easy to mock services/repositories<br>
*Maintainability*:        Changes in one layer don't break others<br>
*Reusability*:            Services can be used by multiple controllers<br>
*Security*:               Controllers don't expose database details<br>

**Redis**<br>
Fast data retrieval is facilitated by Redis, which efficiently manages stable data that changes infrequently. The Redis cache is cleared every 30 seconds to reflect real-time updates. In a practical scenario, where stock history is relevant, data retention would be adjusted to 24 hours, after which the system queries the database to refresh the Redis cache. <br>

*Without Redis*<br>
User Request → Controller → Service → Database (PostgreSQL) → Return Data<br>
                                    ⬆️<br>
                      Slow! Every time queries DB  <br><br> 
*With Redis*<br>
User Request → Controller → RedisService → Redis Cache → Return Data (FAST!)<br>
      ⬇️ (if not found)<br>
Database (PostgreSQL) → Save to Redis → Return Data<br>
![App Preview](projectPreview/RedisWorkFlow.png)</br>

**Python Integration**<br>
In this project, historical daily records are collected using Python, enabling the visualization of trends for selected stocks and offering essential context for investment decisions. Since 2022, approximately 46,000 records have been gathered for about 60 stocks. When Docker runs, this historical data will be fetched from Yahoo and stored in the database.<br>

**Docker**<br>
Docker-Compose manages the connections between the Dockerfiles of three microservices. During app development, Postman is utilized to test their functionality. JSON acts as a common data format, enabling seamless communication among the services. Each microservice transforms data transfer objects into entities for database interactions. Port 8102 is exposed for external access, while port 8092 is designated for internal micro-service communication.<br>

=================================</br>
**Component Diagram**</br>
![App Preview](projectPreview/component-diagram.jpg)</br>

=================================</br>
-- **Click the image** below to view the fast-forward video of the heatmap stock changes --</br>
[![App Preview](projectPreview/HeatmapPreview.png)](https://vimeo.com/1115589409)</br>

Candlestick chart for a specific stock, illustrating its historical trends</br>
![App Preview](projectPreview/CandleStickPreview.png)
