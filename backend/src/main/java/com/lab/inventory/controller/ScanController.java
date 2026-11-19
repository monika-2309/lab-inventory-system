package com.lab.inventory.controller;

import com.lab.inventory.entity.Component;
import com.lab.inventory.entity.Student;
import com.lab.inventory.entity.Transaction;
import com.lab.inventory.service.ComponentService;
import com.lab.inventory.service.StudentService;
import com.lab.inventory.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class ScanController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private StudentService studentService;

    @Autowired
    private ComponentService componentService;

    // Global variables as requested
    private String lastStudentQR = null;
    private String lastComponentQR = null;

    private String escape(String s) {
        if (s == null) return "";
        return s.replace("\"", "\\\"").replace("\n", " ").trim();
    }

    @GetMapping("/api/scan")
    public ResponseEntity<String> handleScanGET(@RequestParam String type, @RequestParam String qr) {
        if (qr == null || qr.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("<h1>Error: QR code is missing</h1>");
        }

        String entityName = "";
        String entityId = escape(qr);
        String statusMessage = "";
        boolean isCombined = false;
        String studId = "", studName = "", compId = "", compName = "";

        if ("student".equalsIgnoreCase(type)) {
            Student s = studentService.getStudentByQr(qr);
            if (s == null) s = studentService.getStudentByUsn(qr);
            if (s == null && qr.startsWith("STD_")) s = studentService.getStudentByUsn(qr.substring(4));

            if (s != null) {
                lastStudentQR = s.getQrCodeText() != null ? s.getQrCodeText() : ("STD_" + s.getUsn());
                entityName = s.getName();
                entityId = s.getUsn();
                statusMessage = "Student Verified Successfully";
            } else {
                statusMessage = "Failed: Invalid Student QR";
            }
        } else if ("component".equalsIgnoreCase(type)) {
            Component c = componentService.getComponentByQr(qr);
            if (c == null) c = componentService.getComponentByComponentId(qr);
            if (c == null && qr.startsWith("COMP_")) c = componentService.getComponentByComponentId(qr.replace("COMP_", "COMP"));
            if (c == null && qr.startsWith("COMP")) c = componentService.getComponentByQr(qr.replace("COMP", "COMP_"));

            if (c != null) {
                lastComponentQR = c.getQrCodeText() != null ? c.getQrCodeText() : c.getComponentId();
                entityName = c.getName();
                entityId = c.getComponentId();
                statusMessage = "Component Scanned Successfully";
            } else {
                statusMessage = "Failed: Invalid Component QR";
            }
        } else {
            statusMessage = "Failed: Invalid QR Format";
        }

        Map<String, String> response = new HashMap<>();
        response.put("type", type);
        response.put("qr", qr);
        messagingTemplate.convertAndSend("/topic/scan", response);

        if (lastStudentQR != null && lastComponentQR != null && !statusMessage.startsWith("Failed")) {
            isCombined = true;
            try {
                Student student = studentService.getStudentByQr(lastStudentQR);
                if (student == null) student = studentService.getStudentByUsn(lastStudentQR);
                if (student == null && lastStudentQR.startsWith("STD_")) student = studentService.getStudentByUsn(lastStudentQR.substring(4));

                Component component = componentService.getComponentByQr(lastComponentQR);
                if (component == null) component = componentService.getComponentByComponentId(lastComponentQR);
                if (component == null && lastComponentQR.startsWith("COMP_")) component = componentService.getComponentByComponentId(lastComponentQR.replace("COMP_", "COMP"));
                if (component == null && lastComponentQR.startsWith("COMP")) component = componentService.getComponentByQr(lastComponentQR.replace("COMP", "COMP_"));

                if (student == null) throw new RuntimeException("Invalid Student QR");
                if (component == null) throw new RuntimeException("Invalid Component QR");

                studId = student.getUsn();
                studName = student.getName();
                compId = component.getComponentId();
                compName = component.getName();

                final String targetStudentUsn = studId;
                final String targetComponentId = compId;

                boolean isAlreadyIssued = transactionService.getAllTransactions().stream()
                        .anyMatch(tx -> tx.getStudent().getUsn().equals(targetStudentUsn) 
                                     && tx.getComponent().getComponentId().equals(targetComponentId)
                                     && "ISSUED".equals(tx.getStatus()));

                Transaction t;
                String msg;
                if (isAlreadyIssued) {
                    t = transactionService.returnComponent(student.getUsn(), component.getComponentId());
                    msg = t.getStudent().getName() + " returned " + t.getComponent().getName();
                    statusMessage = "Component Returned Successfully!";
                } else {
                    t = transactionService.issueComponent(student.getUsn(), component.getComponentId());
                    msg = t.getStudent().getName() + " issued " + t.getComponent().getName();
                    statusMessage = "Component Issued Successfully!";
                }
                
                messagingTemplate.convertAndSend("/topic/activity", Map.of("message", msg, "time", java.time.LocalDateTime.now().toString()));
                
                // Keep lastStudentQR so we can return/issue more components for the same student
                lastComponentQR = null;
                
                messagingTemplate.convertAndSend("/topic/scan", Map.of("type", "auto-issue", "message", statusMessage));
            } catch (Exception e) {
                statusMessage = "Auto-issue failed: " + e.getMessage();
                // Keep lastStudentQR for subsequent retry
                lastComponentQR = null;
                messagingTemplate.convertAndSend("/topic/scan", Map.of("type", "auto-issue-failed", "message", e.getMessage()));
            }
        }

        String jsonContext = String.format("""
            {
                "type": "%s", "id": "%s", "name": "%s", "status": "%s",
                "isCombined": %b, "studentId": "%s", "studentName": "%s",
                "componentId": "%s", "componentName": "%s"
            }
            """, 
            escape(type), escape(entityId), escape(entityName), escape(statusMessage),
            isCombined, escape(studId), escape(studName), escape(compId), escape(compName)
        );

        String htmlTemplate = """
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
    <title>Scanner Result</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <style>
        @import url('https://fonts.googleapis.com/css2?family=Poppins:wght@400;500;600;700&display=swap');
        * { box-sizing: border-box; margin: 0; padding: 0; font-family: 'Poppins', sans-serif; }
        body { background: #0f172a; color: #f8fafc; display: flex; align-items: center; justify-content: center; min-height: 100vh; overflow: hidden; padding: 20px; }
        
        .glass-card {
            background: rgba(30, 41, 59, 0.7);
            backdrop-filter: blur(16px);
            -webkit-backdrop-filter: blur(16px);
            border: 1px solid rgba(255,255,255,0.05);
            border-radius: 18px;
            padding: 30px 25px;
            width: 100%;
            max-width: 380px;
            box-shadow: 0 25px 50px -12px rgba(0,0,0,0.5);
            text-align: center;
            animation: slideUpFade 0.4s ease-out forwards;
            opacity: 0;
            transform: translateY(20px) scale(0.95);
        }

        .avatar-circle {
            width: 90px; height: 90px; border-radius: 50%; object-fit: cover;
            border: 3px solid rgba(255,255,255,0.1); margin: 0 auto 20px;
            background: #1e293b; display: flex; align-items: center; justify-content: center;
            box-shadow: 0 10px 25px -5px rgba(0,0,0,0.3);
            font-size: 2rem; color: #3b82f6; overflow: hidden;
        }
        
        .avatar-circle img { width: 100%; height: 100%; object-fit: cover; }

        .id-text { font-size: 1.5rem; font-weight: 700; color: #fff; letter-spacing: 0.5px; margin-bottom: 4px; line-height: 1.2; }
        .name-text { font-size: 1.1rem; color: #94a3b8; font-weight: 500; margin-bottom: 25px; }

        .status-badge {
            background: rgba(16, 185, 129, 0.1); color: #10b981;
            padding: 12px 16px; border-radius: 12px; font-weight: 600; font-size: 0.95rem;
            display: inline-flex; align-items: center; gap: 8px; border: 1px solid rgba(16,185,129,0.3);
            justify-content: center; width: 100%;
        }
        
        .status-badge.error { background: rgba(239,68,68,0.1); color: #ef4444; border-color: rgba(239,68,68,0.3); }
        .status-badge.info { background: rgba(59,130,246,0.1); color: #3b82f6; border-color: rgba(59,130,246,0.3); }

        .combined-section { display: flex; flex-direction: column; gap: 15px; margin-bottom: 25px; }
        .mini-row { 
            display: flex; align-items: center; gap: 15px; background: rgba(15, 23, 42, 0.5); 
            padding: 15px; border-radius: 14px; text-align: left; border: 1px solid rgba(255,255,255,0.03); 
        }
        .mini-img { width: 50px; height: 50px; border-radius: 50%; object-fit: cover; background: #1e293b; display: flex; align-items: center; justify-content: center; color: #94a3b8; font-size: 1.2rem; }
        .mini-id { font-size: 1rem; font-weight: 600; color: #fff; }
        .mini-name { font-size: 0.85rem; color: #94a3b8; }
        .link-icon { color: #64748b; font-size: 1.2rem; margin: -5px 0; }

        .pulse-glow { animation: slideUpFade 0.4s ease-out forwards, pulseGlow 2s infinite 0.4s; }

        .btn-done {
            margin-top: 30px; background: #3b82f6; color: white; border: none; padding: 14px;
            width: 100%; border-radius: 12px; font-size: 1rem; font-weight: 600;
            cursor: pointer; transition: 0.2s; box-shadow: 0 4px 14px rgba(59,130,246,0.4);
        }
        .btn-done:active { transform: scale(0.98); }

        @keyframes slideUpFade { to { opacity: 1; transform: translateY(0) scale(1); } }
        @keyframes pulseGlow { 
            0% { box-shadow: 0 0 0 0 rgba(16, 185, 129, 0.4); border-color: rgba(16,185,129,0.5); } 
            70% { box-shadow: 0 0 0 15px rgba(16, 185, 129, 0); border-color: rgba(255,255,255,0.05); } 
            100% { box-shadow: 0 0 0 0 rgba(16, 185, 129, 0); } 
        }
        .auto-close-txt { margin-top: 15px; font-size: 0.8rem; color: #64748b; }
    </style>
</head>
<body>
    <div id="app"></div>
    <script>
        const ctx = %CONTEXT%;

        const componentImages = {
            "Arduino": "https://cdn-icons-png.flaticon.com/512/2162/2162391.png",
            "Raspberry": "https://cdn-icons-png.flaticon.com/512/3067/3067980.png",
            "Breadboard": "https://cdn-icons-png.flaticon.com/512/911/911412.png",
            "Resistor": "https://cdn-icons-png.flaticon.com/512/8657/8657444.png",
            "Capacitor": "https://cdn-icons-png.flaticon.com/512/5756/5756525.png",
            "Multimeter": "https://cdn-icons-png.flaticon.com/512/2884/2884784.png",
            "Oscilloscope": "https://cdn-icons-png.flaticon.com/512/1792/1792823.png"
        };

        function getImageUrl(name, type) {
            if (!name) return 'https://ui-avatars.com/api/?background=2563eb&color=fff&size=150&name=QR';
            if (type === 'student') return 'https://ui-avatars.com/api/?background=2563eb&color=fff&size=150&name=' + encodeURIComponent(name) + '&font-size=0.4';
            for (let key in componentImages) {
                if (name.toLowerCase().includes(key.toLowerCase())) return componentImages[key];
            }
            return 'https://cdn-icons-png.flaticon.com/512/1083/1083401.png';
        }

        const app = document.getElementById('app');
        let html = '';
        
        const isError = ctx.status.toLowerCase().includes('fail') || ctx.status.toLowerCase().includes('invalid');
        const statusClass = isError ? 'error' : (ctx.status.toLowerCase().includes('returned') ? 'info' : 'success');
        const glowClass = isError ? '' : 'pulse-glow';
        const icon = isError ? 'fa-times-circle' : 'fa-check-circle';
        const displayStatus = isError ? ctx.status.replace('Failed: ', '') : "✔ " + ctx.status;

        if (ctx.isCombined) {
            html = `
                <div class="glass-card ${glowClass}">
                    <div class="combined-section">
                        <div class="mini-row">
                            <img class="mini-img" src="${getImageUrl(ctx.studentName, 'student')}">
                            <div>
                                <div class="mini-id">${ctx.studentId || 'Unknown'}</div>
                                <div class="mini-name">${ctx.studentName || 'Unknown'}</div>
                            </div>
                        </div>
                        <div class="link-icon"><i class="fas fa-link"></i></div>
                        <div class="mini-row">
                            <img class="mini-img" src="${getImageUrl(ctx.componentName, 'component')}">
                            <div>
                                <div class="mini-id">${ctx.componentId || 'Unknown'}</div>
                                <div class="mini-name">${ctx.componentName || 'Unknown'}</div>
                            </div>
                        </div>
                    </div>
                    <div class="status-badge ${statusClass}">
                        <i class="fas ${icon}"></i> &nbsp;${displayStatus}
                    </div>
                    <button class="btn-done" onclick="window.close()">Done</button>
                    <div class="auto-close-txt">Closing automatically...</div>
                </div>
            `;
        } else {
            html = `
                <div class="glass-card">
                    <div class="avatar-circle">
                        <img src="${getImageUrl(ctx.name, ctx.type)}">
                    </div>
                    <div class="id-text">${ctx.id}</div>
                    <div class="name-text">${ctx.name || 'Unknown'}</div>
                    <div class="status-badge ${statusClass}">
                        <i class="fas ${icon}"></i> &nbsp;${displayStatus}
                    </div>
                    <button class="btn-done" onclick="window.close()">Done</button>
                    <div class="auto-close-txt">Closing automatically...</div>
                </div>
            `;
        }
        
        app.innerHTML = html;
        
        setTimeout(() => {
            window.close();
            document.body.innerHTML = "<h3 style='color:#64748b;'>You can close this tab now.</h3>";
        }, 10000);
    </script>
</body>
</html>
""";

        return ResponseEntity.ok().header("Content-Type", "text/html").body(htmlTemplate.replace("%CONTEXT%", jsonContext));
    }
}
