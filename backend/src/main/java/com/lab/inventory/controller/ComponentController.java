package com.lab.inventory.controller;

import com.lab.inventory.entity.Component;
import com.lab.inventory.service.ComponentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/components")
public class ComponentController {

    @Autowired
    private ComponentService componentService;

    @GetMapping
    public List<Component> getAllComponents() {
        return componentService.getAllComponents();
    }

    @PostMapping
    public ResponseEntity<Component> addComponent(@RequestBody Component component) {
        return ResponseEntity.ok(componentService.addComponent(component));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Component> updateComponent(@PathVariable Long id, @RequestBody Component component) {
        return ResponseEntity.ok(componentService.updateComponent(id, component));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteComponent(@PathVariable Long id) {
        componentService.deleteComponent(id);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/scan")
    public ResponseEntity<Component> getComponentByQr(@RequestParam String qr) {
        Component c = componentService.getComponentByQr(qr);
        if(c != null) return ResponseEntity.ok(c);
        return ResponseEntity.notFound().build();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Component> getComponentById(@PathVariable String id) {
        Component c = componentService.getComponentByComponentId(id);
        if (c == null) c = componentService.getComponentByQr(id);
        if (c == null && id.startsWith("COMP_")) c = componentService.getComponentByComponentId(id.replace("COMP_", "COMP"));
        if (c == null && id.startsWith("COMP")) c = componentService.getComponentByQr(id.replace("COMP", "COMP_"));
        if (c != null) return ResponseEntity.ok(c);
        return ResponseEntity.notFound().build();
    }
}
