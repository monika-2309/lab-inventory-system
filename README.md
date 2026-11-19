# ECE Lab Smart Inventory Management System (Real-Time Edition)

A modern, real-time, full-stack Java Spring Boot web application designed to automate the issuing and returning of ECE lab components. This system eliminates manual logbooks by utilizing **Native Smartphone QR Code Scanning** and **WebSockets** for a seamless, zero-click checkout experience.

---

## 🚀 Key Features

- **Native Mobile Scanning:** Use your phone's built-in camera to scan QR codes. No app installation, no HTTPS tunnels, and no mobile frontend required.
- **URL-Embedded Smart QRs:** Every generated QR code contains a direct URL to your local/hosted API (e.g., `http://<ip-or-domain>:8080/api/scan?...`).
- **Real-Time Auto-Issue Workflow:**
  1. Scan a Student's QR code with your phone. 
  2. Scan a Component's QR code with your phone.
  3. The item is **automatically issued** to the student instantly.
- **WebSocket Live Updates:** The dashboard acts as a "Reception Screen." As items are scanned on any phone, the dashboard and History Log instantly light up and refresh data without page reloads.
- **Glassmorphism UI:** A premium, dark-themed, responsive frontend powered by HTML, CSS, and Vanilla JS.
- **Advanced Analytics:** Chart.js integration tracks daily usage trends, late returns, and actionable low-stock alerts.
- **Cloud & Container Ready:** Multi-stage `Dockerfile` and `docker-compose.yml` for instant 1-click cloud deployment.

---

## 📂 Project Structure

```text
lab-inventory-system/
├── backend/                              # Java Spring Boot Backend
│   ├── src/main/java/com/lab/inventory/
│   │   ├── config/                       # WebMvc, WebSocket & Exception Handling
│   │   ├── controller/                   # REST Endpoints (Auth, Scan, Components, etc.)
│   │   ├── entity/                       # JPA Database Entities
│   │   ├── repository/                   # Spring Data JPA Repositories
│   │   └── service/                      # Business Logic & QR Generation
│   ├── src/main/resources/
│   │   ├── static/                       # Production Frontend (HTML, CSS, JS)
│   │   └── application.properties        # App & Database Config (with env fallbacks)
│   ├── mvnw & mvnw.cmd                   # Maven Wrappers (cross-platform build)
│   └── pom.xml                           # Maven Dependencies
├── frontend/                             # Raw Frontend Source Files
│   ├── css/                              # Glassmorphism styling
│   ├── js/                               # API client and WebSocket handlers
│   ├── prototypes/                       # Scanner UI prototypes
│   └── package.json                      # Optional local static preview script
├── database/
│   └── schema.sql                        # Database schema & sample seed data
├── Dockerfile                            # Production multi-stage container
├── docker-compose.yml                    # Local & server container stack with MySQL
├── .gitignore                            # Clean git exclusion rules
└── README.md                             # Project documentation
```

---

## 🛠️ Local Setup Instructions

### 1. Database Configuration
1. Open your MySQL client (e.g., MySQL Workbench).
2. Connect using `root` / `root`. *(If your credentials differ, update `backend/src/main/resources/application.properties` or set environment variables)*.
3. Run the script located in `database/schema.sql` to initialize the `lab_inventory` database.
   - Default login credentials are created automatically: **admin / admin**

### 2. Configure Local IP For Mobile Scanning
To allow your phone's camera to communicate directly with your laptop:
1. Find your laptop's local Wi-Fi IP address (e.g. `10.228.6.241`).
2. Open `backend/src/main/resources/static/js/app.js` and `frontend/js/app.js`.
3. Set `CURRENT_IP` to your laptop's Wi-Fi IP:
   ```javascript
   const CURRENT_IP = "10.228.6.241"; // Replace with your actual IP Address
   ```

### 3. Start the Server

#### Option A: Running with Maven / Maven Wrapper (Standard)
```bash
cd backend
mvn spring-boot:run
# OR on Windows PowerShell:
.\mvnw spring-boot:run
```

#### Option B: Running with Docker Compose (Everything in 1 step)
```bash
docker compose up --build
```

The application will start on **`http://localhost:8080`**.

---

## 🌐 How to Host Live on the Cloud

You can deploy this full-stack application online for free or low-cost using **Render** or **Railway**:

### Deploying to Render (Recommended Free/Easy Option):
1. **Push your code to GitHub** (see instructions below).
2. **Create a MySQL Database**:
   - On [Render](https://render.com) or [Aiven](https://aiven.io), create a free MySQL database.
   - Run `database/schema.sql` on your cloud database using MySQL Workbench or DBeaver.
3. **Create a Web Service on Render**:
   - Select **New +** -> **Web Service** -> Connect your GitHub repository.
   - **Environment**: Docker (Render will auto-detect the root `Dockerfile`).
   - Add these **Environment Variables** in the Render dashboard:
     - `SPRING_DATASOURCE_URL`: `jdbc:mysql://<your-db-host>:<port>/<db-name>?useSSL=true`
     - `SPRING_DATASOURCE_USERNAME`: `<your-db-username>`
     - `SPRING_DATASOURCE_PASSWORD`: `<your-db-password>`
4. Click **Deploy Web Service**. Render will build and host your app with an HTTPS URL (e.g. `https://lab-inventory.onrender.com`).
5. In `app.js`, the app automatically detects the live domain and generates QR codes matching your live URL!

---

## 🐙 Pushing to GitHub

To push this project to your GitHub account:

1. **Create a new repository** on [github.com/new](https://github.com/new) (e.g., named `lab-inventory-system`). *Leave "Initialize with README" unchecked.*
2. Open your terminal in the project root (`lab-inventory-system`):
   ```bash
   git init
   git add .
   git commit -m "Initial commit: ECE Lab Inventory System with QR Code and WebSockets"
   git branch -M main
   git remote add origin https://github.com/<YOUR_GITHUB_USERNAME>/lab-inventory-system.git
   git push -u origin main
   ```

---

## 📱 How to Use (Scanning Workflow)

1. **Access Dashboard:** Open `http://localhost:8080` (or your live hosted URL) and log in with `admin` / `admin`.
2. **Register Entities:** Add Students and Components via the sidebar. The system automatically creates Smart QR Codes.
3. **Open QR Codes:** Click "📥 QR Image" next to any component or student.
4. **Scan with Phone Camera:**
   - Scan the Student's QR code.
   - Scan the Component's QR code.
   - The item is **automatically issued** and the Reception Screen updates in real time!
5. **Scan again to Return:** Scanning the same student + component automatically returns the item.

---

## 🧰 Tech Stack
- **Backend:** Java 17, Spring Boot 3, Spring Data JPA, Spring WebSockets (STOMP).
- **Database:** MySQL 8.0, HikariCP.
- **Frontend:** HTML5, CSS3 (Glassmorphism), Vanilla JavaScript, Chart.js, SockJS, Stomp.js.
- **DevOps:** Docker, Docker Compose, Maven.
