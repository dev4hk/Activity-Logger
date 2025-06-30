# 📊User Activity Logger Dashboard 

**📄 Project Overview**  
- This project demonstrates a robust, real-time user activity logging and visualization dashboard built with a modern stack. It showcases a microservices-inspired architecture where user activity events are asynchronously processed via a message queue (**RabbitMQ**) and then pushed to a live frontend dashboard using **WebSockets**.

- This setup ensures high responsiveness, scalability, and data durability. It's an excellent example of building **decoupled systems** for handling streams of data, making it ideal for tracking user interactions, IoT device telemetry, or any event-driven application.

---

***✨ Features***

- **Real-time Activity Feed** – Live updates via WebSockets
- **Asynchronous Event Processing** – RabbitMQ decouples services
- **REST API for Event Publishing**
- **Event Dashboard** – Sortable table of recent activities
- **User-Specific Search**
- **Random Event Generation** – For testing
- **Robust Error Handling**
- **Modern Frontend** – React + Tailwind CSS
- **Dockerized RabbitMQ** – Easy local setup

---

***🛠️ Technologies Used***

**💻 Backend:**

- *Spring Boot*
- *Spring Cloud Stream*
- *RabbitMQ*
- *WebSocket (STOMP)*
- *Lombok*
- *Maven*
- *Java 17/21*

**🖥️ Frontend:**

- *React.js*
- *TypeScript*
- *Tailwind CSS*
- *SockJS-Client*
- *@stomp/stompjs*
- *Vite*
- *npm*

**🏗️ Infrastructure:**

- *Docker & Docker Compose*

---

***🏛️ Architecture Overview***

The system is **event-driven**, consisting of:

- **Frontend (React.js):**
    - Connects via REST + WebSocket
    - Displays real-time events
    - Allows publishing events

- **Backend (Spring Boot):**
    - `POST /api/events/publish`
    - `GET /api/events/dashboard`
    - `GET /api/events/by-user`
    - Publishes/Consumes events via RabbitMQ
    - Broadcasts via `/topic/activities` WebSocket channel

- **RabbitMQ:**
    - Message queue for `user-activity-events`
    - Ensures delivery and durability

