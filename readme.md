-- Click the picture to view the video demonstration --
[![App Preview](projectPreview/FinalProject-StockHeatmapPost.png)](https://vimeo.com/manage/videos/1115271799)

**Final Project: Stock Heatmap Application**
=================================
This application provides real-time insights into stock performance through an interactive heatmap featuring 60 stocks. Key features include:

**Java & Spring Boot with micro-services**: This project utilizes Java and Spring Boot for the backend, providing a robust and scalable architecture. It comprises three microservices that work together to implement the business logic:

1. Data Supplier: This microservice fetches real-time data from Finnhub using four API keys, with each key enabling 15 stock data retrievals.

2. Stock Data: This service obtains real-time data from the Data Supplier and stores historical data in a PostgreSQL database.

3. User Interface: This microservice provides a front-end interface for data visualization, utilizing the ECharts library for interactive and dynamic charts. This allows users to effectively analyze and interpret stock data.

Docker-Compose will manage the connections between the Dockerfiles of the three microservices. Postman is utilized to test the functionality of the microservices. JSON is utilized as a common data format to facilitate communication between these services. Each microservice transforms data transfer objects into entities for interactions with the database. This microservices architecture is vital, as it enables changes to be made to one service without impacting the others. For example, switching the data source from Finnhub to Yahoo or updating the user interface will not modify the program of other microservices. Exposed localhost 8102 will be used 

**Redis**: Fast data retrieval is facilitated by Redis, which efficiently manages stable data that changes infrequently. The Redis cache is cleared every 30 seconds to reflect real-time updates. In a practical scenario, where stock history is relevant, data retention would be adjusted to 24 hours, after which the system queries the database to refresh the Redis cache.

**Real-Time Updates**: Stock data is fetched from Finnhub every 30 seconds, ensuring users have access to the latest information.

**Python Integration**: In this project, historical daily records are collected using Python, enabling the visualization of trends for selected stocks and offering essential context for investment decisions. Since 2022, approximately 46,000 data points have been gathered for about 60 stocks. When Docker runs, this historical data will be fetched from Yahoo and stored in the database.

