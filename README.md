# 🔬 ECE Lab Smart Inventory Management System
> **Automated Lab Component Checkout with Native Smartphone QR Scanning & Real-Time WebSockets**

[![Java](https://img.shields.io/badge/Java-17%2B-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2.4-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)](https://spring.io/projects/spring-boot)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1?style=for-the-badge&logo=mysql&logoColor=white)](https://www.mysql.com/)
[![WebSockets](https://img.shields.io/badge/WebSockets-STOMP-010101?style=for-the-badge&logo=socketdotio&logoColor=white)](https://spring.io/)
[![Docker](https://img.shields.io/badge/Docker-Ready-2496ED?style=for-the-badge&logo=docker&logoColor=white)](https://www.docker.com/)

---

## 📖 Overview

The **ECE Lab Smart Inventory Management System** eliminates traditional pen-and-paper lab logbooks by transforming any laptop into a **Real-Time Reception Dashboard** and enabling **zero-install smartphone scanning**.

Using their native phone cameras, lab coordinators or students simply scan a **Student QR code** followed by a **Component QR code**. The backend verifies stock, creates the transaction, deducts inventory, and broadcasts live updates to the dashboard via WebSockets in milliseconds.

```
┌─────────────────────────────────┐           ┌─────────────────────────────────┐
│     Smartphone Camera Scan      │           │      Reception Dashboard        │
│  (No App / Pure Native Camera)  │           │      (Laptop Screen / TV)       │
└───────────────┬─────────────────┘           └─────────────────▲───────────────┘
                │ HTTP GET /api/scan                            │
                ▼                                               │ WebSocket STOMP
┌───────────────────────────────────────────────────────────────┴───────────────┐
│                           Spring Boot 3 Backend                               │
│  ┌────────────────────────┐  ┌───────────────────┐  ┌──────────────────────┐  │
│  │   Scan & Issue Engine  │  │  WebSocket Broker │  │   ZXing QR Service   │  │
│  └───────────┬────────────┘  └───────────────────┘  └──────────────────────┘  │
└──────────────┼────────────────────────────────────────────────────────────────┘
               ▼
┌─────────────────────────────────┐
│          MySQL 8.0 DB           │
│ (Students, Components, Logs)    │
└─────────────────────────────────┘
```

---

## ⚡ Key Highlights

- 📱 **Zero-Install Mobile QR Scanning**: Works with standard phone camera apps (iOS & Android). No third-party mobile apps, tunnels, or app store downloads required.
- ⚡ **Real-Time Auto-Issue & Return**:
  1. Scan **Student QR** ➔ Verified on screen.
  2. Scan **Component QR** ➔ System automatically issues the component.
  3. Scan the same combination again ➔ Automatically registers a return.
- 📡 **Instant Reception Screen (WebSockets)**: The laptop dashboard lights up in real time with visual pulses and sound cues as items are scanned and issued.
- 📊 **Lab Analytics & Insights**: Integrated **Chart.js** displays daily checkout volumes, overdue return warnings, and automated low-stock alerts (< 5 items).
- 🎨 **Glassmorphism Dark UI**: Modern responsive interface built with vanilla HTML5, CSS3, and JavaScript—served directly by Spring Boot without heavy frontend frameworks.
- 🐳 **Cloud & Docker Ready**: Includes a multi-stage `Dockerfile` and `docker-compose.yml` for 1-command deployment locally or on cloud platforms.

---

## 📂 Project Architecture

```text
lab-inventory-system/
├── backend/                              # Spring Boot Core Application
│   ├── src/main/java/com/lab/inventory/
│   │   ├── config/                       # WebMvc, WebSocket STOMP & Exception Handling
│   │   ├── controller/                   # REST Endpoints (Auth, Scan, Students, etc.)
│   │   ├── entity/                       # JPA Database Entities (Student, Component, Tx)
│   │   ├── repository/                   # Spring Data JPA Repositories
│   │   └── service/                      # Business Logic, Auto-checkout, & ZXing QR
│   ├── src/main/resources/
│   │   ├── static/                       # Production Web UI (Dashboard, Inventory, Logs)
│   │   └── application.properties        # App configuration with cloud env variable fallbacks
│   ├── mvnw & mvnw.cmd                   # Maven Wrappers (cross-platform build without setup)
│   └── pom.xml                           # Dependencies & plugins
├── frontend/                             # Raw UI templates & local preview script
│   ├── css/                              # Glassmorphism design stylesheet
│   ├── js/                               # WebSocket connection and API helper
│   ├── prototypes/                       # Mobile scanner card preview templates
│   └── package.json                      # Optional static preview runner
├── database/
│   └── schema.sql                        # Database schema definition with initial sample data
├── Dockerfile                            # Production multi-stage container
├── docker-compose.yml                    # Stack definition (Spring Boot + MySQL 8.0)
├── .gitignore                            # Production git exclusions
└── README.md                             # Project documentation
```

---

## 🚀 Quick Start Guide

### Prerequisites
- **Java 17+** (JDK)
- **MySQL 8.0+**
- **Maven** (or use the included `mvnw` wrapper)

---

### Step 1: Initialize Database
1. Open your MySQL client (e.g. MySQL Workbench, DBeaver, or CLI):
   ```sql
   mysql -u root -p
   ```
2. Run the initialization script found at `database/schema.sql`:
   ```sql
   source /path/to/lab-inventory-system/database/schema.sql;
   ```
   *Default Admin credentials created automatically:*
   - **Username**: `admin`
   - **Password**: `admin`

---

### Step 2: Configure Wi-Fi IP for Phone Scanning (Local Mode)
To allow your phone's camera to reach your laptop:
1. Open terminal and find your laptop's Wi-Fi IPv4 address:
   ```powershell
   # Windows PowerShell
   ipconfig
   ```
2. Open `backend/src/main/resources/static/js/app.js` and `frontend/js/app.js`.
3. Set `CURRENT_IP` to your laptop's Wi-Fi IP (e.g. `10.228.6.241`):
   ```javascript
   const CURRENT_IP = "10.228.6.241"; // Your laptop's local Wi-Fi IP
   ```
   *(Note: When deployed to a live cloud domain, the app automatically detects the host domain).*

---

### Step 3: Run the Application

#### Option A: Run via Maven (Standard)
```powershell
cd backend

# Using installed Maven:
mvn spring-boot:run

# OR using the included Maven Wrapper (no installation required):
.\mvnw spring-boot:run
```

#### Option B: Run via Docker Compose (All-in-One with MySQL)
```bash
docker compose up --build
```

The application will start on **`http://localhost:8080`**.

---

## 📱 How to Use (Step-by-Step)

```
1. Login  ──>  2. View QR  ──>  3. Scan Student  ──>  4. Scan Component  ──>  5. Live Auto-Issue!
```

1. **Access the Portal**: Open `http://localhost:8080` on your laptop and log in with `admin` / `admin`.
2. **Register Entities**:
   - Navigate to **Students** to add student records.
   - Navigate to **Components** to register lab items and quantities.
3. **Open QR Codes**: Click **"📥 QR Image"** next to any record to view its Smart QR on screen.
4. **Scan with Phone**:
   - Ensure your phone is on the **same Wi-Fi** as your laptop (or using the live hosted domain).
   - Point your phone camera at the **Student QR** code and tap the link. The laptop dashboard will highlight the student.
   - Point your phone camera at the **Component QR** code. The component will automatically be issued to that student!
5. **Return Component**: Scan the same student and component again—the system detects the open transaction and marks it as returned.

---

## ❗ Troubleshooting Mobile Camera Links

If your mobile browser shows **"This site can't provide a secure connection"** (`ERR_SSL_PROTOCOL_ERROR`):
- **Why**: Modern mobile browsers automatically upgrade `http://` addresses to `https://`.
- **Quick Fix**:
  1. Manually ensure the address in the phone browser begins with `http://` (not `https://`).
  2. Open an **Incognito / Private tab** on your mobile browser (bypasses HTTPS-Upgrade rules).
  3. Alternatively, deploy the project to **Render** or **Railway** (see below) to get free automatic HTTPS!

---

## 🌐 Cloud Deployment (Host Live Online)

Because the project includes a cloud-ready `Dockerfile` and dynamic environment variables, it can be deployed on [Render](https://render.com) for free:

### 1. Create a Cloud MySQL Database
- Provision a free MySQL database on [Aiven.io](https://aiven.io), [Clever Cloud](https://www.clever-cloud.com), or Render.
- Run `database/schema.sql` on the cloud database to create tables.

### 2. Deploy Web Service on Render
1. Push your repository to **GitHub**.
2. Go to [render.com](https://render.com) ➔ Click **New +** ➔ **Web Service** ➔ Connect your GitHub repo.
3. Choose **Docker** as the Runtime environment (Render will auto-detect the root `Dockerfile`).
4. In **Environment Variables**, add:
   | Variable | Value |
   |---|---|
   | `SPRING_DATASOURCE_URL` | `jdbc:mysql://<host>:<port>/<db>?useSSL=true` |
   | `SPRING_DATASOURCE_USERNAME` | `<db-user>` |
   | `SPRING_DATASOURCE_PASSWORD` | `<db-password>` |
5. Click **Deploy Web Service**. You will receive a live HTTPS URL (e.g. `https://lab-inventory.onrender.com`).

---

## 🐙 Pushing to GitHub

To push this project to your GitHub repository:

```bash
# 1. Initialize git and switch to main
git branch -M main

# 2. Add your GitHub remote repository
git remote add origin https://github.com/<YOUR_GITHUB_USERNAME>/lab-inventory-system.git

# 3. Push to GitHub
git push -u origin main
```

---

## 🔌 API Reference Summary

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/auth/login` | Authenticate lab coordinator / admin |
| `GET` | `/api/dashboard` | Summary statistics (total, available, issued, late) |
| `GET` | `/api/dashboard/stats` | Chart.js time-series transaction analytics |
| `GET` | `/api/dashboard/alerts` | Active low-stock and overdue return alerts |
| `GET` | `/api/students` | Retrieve all student records |
| `POST` | `/api/students` | Register a new student |
| `GET` | `/api/components` | Retrieve all lab components |
| `POST` | `/api/components` | Add a component and allocate QR code |
| `GET` | `/api/scan?type={t}&qr={qr}` | Mobile phone camera scan webhook |
| `POST` | `/api/issue` | Manually issue a component |
| `POST` | `/api/return` | Manually return a component |
| `GET` | `/api/transactions/history` | Full transaction audit trail |
| `GET` | `/api/qrcode?text={url}` | Generates PNG QR code image via ZXing |
| `WS` | `/ws` | STOMP WebSocket endpoint (`/topic/scan`, `/topic/activity`) |

---

## 🛠️ Technology Stack

- **Backend**: Java 17, Spring Boot 3.2.4 (Data JPA, Web, WebSocket STOMP)
- **Database**: MySQL 8.0, HikariCP Connection Pooling, Hibernate ORM
- **QR Engine**: Google ZXing (`core` & `javase` 3.5.3)
- **Frontend**: Vanilla HTML5, CSS3 Glassmorphism, JavaScript, Chart.js, SockJS, Stomp.js
- **DevOps**: Docker, Docker Compose, Maven, Maven Wrapper

---

## 📄 License

This project is open-source and available under the [MIT License](LICENSE).
