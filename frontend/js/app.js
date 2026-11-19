const CURRENT_IP = "10.228.6.241"; // Laptop Wi-Fi IP address
const BASE_URL = (typeof window !== 'undefined' && window.location.hostname && window.location.hostname !== 'localhost' && window.location.hostname !== '127.0.0.1')
    ? `${window.location.protocol}//${window.location.hostname}:8080`
    : `http://${CURRENT_IP}:8080`;

let API_BASE = '/api';
if (typeof window !== 'undefined') {
    if (window.location.protocol === 'file:') {
        API_BASE = 'http://localhost:8080/api';
    } else if ((window.location.hostname === 'localhost' || window.location.hostname === '127.0.0.1') && window.location.port !== '8080') {
        API_BASE = 'http://localhost:8080/api';
    } else if (window.location.port !== '8080') {
        API_BASE = `${BASE_URL}/api`;
    }
}

// Simple Auth Check
function checkAuth() {
    const isLogin = window.location.pathname.endsWith('index.html') || window.location.pathname.endsWith('/');
    if (!localStorage.getItem('adminAuth') && !isLogin) {
        window.location.href = 'index.html';
    }
}
checkAuth();

async function apiCall(endpoint, method = 'GET', body = null) {
    const headers = { 'Content-Type': 'application/json' };
    const options = { method, headers };
    if (body) options.body = JSON.stringify(body);

    try {
        const res = await fetch(`${API_BASE}${endpoint}`, options);
        if (!res.ok) {
            let err;
            try {
                err = await res.json();
            } catch (e) {
                throw new Error(`Server Error (${res.status}): Please make sure you restarted the Spring Boot server!`);
            }
            throw new Error(err.error || err.message || 'API Error');
        }
        if (res.headers.get("content-type")?.includes("application/json")) {
            return await res.json();
        }
        return await res.text();
    } catch (err) {
        console.error(err);
        alert(err.message);
        throw err;
    }
}

function logout() {
    localStorage.removeItem('adminAuth');
    window.location.href = 'index.html';
}

function createSidebar(activeId) {
    const sidebarContainer = document.getElementById('sidebar-container');
    if (!sidebarContainer) return;

    sidebarContainer.innerHTML = `
        <div class="sidebar-overlay" id="sidebar-overlay" onclick="toggleSidebar()"></div>
        <div class="sidebar" id="app-sidebar">
            <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 2.5rem;">
                <h2 style="margin: 0; font-size: 1.4rem;"><i class="fas fa-microchip" style="margin-right:8px;"></i>ECE Lab</h2>
                <button class="mobile-menu-btn" onclick="toggleSidebar()" style="display: none;" id="close-sidebar-btn"><i class="fas fa-times"></i></button>
            </div>
            
            <a href="dashboard.html" id="nav-dashboard"><i class="fas fa-chart-pie"></i> Dashboard</a>
            <a href="inventory.html" id="nav-inventory"><i class="fas fa-box-open"></i> Components</a>
            <a href="students.html" id="nav-students"><i class="fas fa-user-graduate"></i> Students</a>
            <a href="history.html" id="nav-history"><i class="fas fa-history"></i> History Log</a>
        </div>
    `;
    if (activeId) {
        document.getElementById(activeId).classList.add('active');
    }
}

function toggleSidebar() {
    const sidebar = document.getElementById('app-sidebar');
    const overlay = document.getElementById('sidebar-overlay');

    if (sidebar.classList.contains('open')) {
        sidebar.classList.remove('open');
        overlay.classList.remove('show');
    } else {
        sidebar.classList.add('open');
        overlay.classList.add('show');
    }
}
