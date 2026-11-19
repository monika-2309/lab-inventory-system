# ECE Lab Smart Inventory Management System

A real-time lab inventory management system built with **Spring Boot 3**, **MySQL**, and **WebSockets**. It automates issuing and returning lab equipment using native smartphone QR code scanning, eliminating physical logbooks.

---

## Features

- **Native QR Code Scanning:** Scan QR codes directly with your smartphone's default camera app without installing any app.
- **Real-Time Auto-Issue & Return:**
  - Scan Student QR + Component QR &rarr; Component is automatically issued.
  - Scan again &rarr; Component is marked as returned.
- **Live Reception Dashboard:** Real-time updates via WebSockets (STOMP) as items are scanned.
- **Analytics & Alerts:** Daily usage trends, overdue returns, and low-stock indicators.
- **Docker Ready:** Includes `Dockerfile` and `docker-compose.yml` for quick deployment.

---

## Tech Stack

- **Backend:** Java 17, Spring Boot 3.2.4, Spring Data JPA, WebSockets (STOMP)
- **Database:** MySQL 8.0, HikariCP
- **Frontend:** Vanilla HTML5, CSS3, JavaScript, Chart.js, SockJS, Stomp.js
- **QR Code Engine:** ZXing 3.5.3
- **Build Tool:** Maven / Maven Wrapper

---

## Quick Start

### 1. Prerequisites
- **Java 17+**
- **MySQL 8.0+**

### 2. Setup the Database
1. Open your MySQL client and run the script in `database/schema.sql`:
   ```sql
   source database/schema.sql;
   ```
2. Default login credentials:
   - **Username:** `admin`
   - **Password:** `admin`

### 3. Run the Backend
Navigate to the `backend/` folder and start the application:

```bash
# On Windows PowerShell
cd backend
.\mvnw spring-boot:run

# Or with Maven installed
mvn spring-boot:run
```

Once running, open **[http://localhost:8080](http://localhost:8080)** in your browser.

---

## Mobile QR Scanning Setup (Local Wi-Fi)

To scan QR codes from your smartphone while running locally:

1. Connect your phone and laptop to the **same Wi-Fi network**.
2. Find your laptop's Wi-Fi IP using `ipconfig` (e.g., `10.228.6.241`).
3. Set `CURRENT_IP` in `backend/src/main/resources/static/js/app.js`:
   ```javascript
   const CURRENT_IP = "10.228.6.241"; // Your Wi-Fi IP
   ```
4. Access the dashboard from your laptop, display a student/component QR code, and scan it with your phone's camera.

---

## Docker Quick Start

Run both the application and MySQL with a single command:

```bash
docker compose up --build
```
Access the application at **[http://localhost:8080](http://localhost:8080)**.

---

## Pushing to GitHub

```bash
git branch -M main
git remote add origin https://github.com/<YOUR_USERNAME>/lab-inventory-system.git
git push -u origin main
```

---

## License

This project is licensed under the MIT License.
