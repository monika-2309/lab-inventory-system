package com.lab.inventory.service;

import com.lab.inventory.entity.Component;
import com.lab.inventory.repository.ComponentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ComponentService {
    @Autowired
    private ComponentRepository componentRepository;

    public Component addComponent(Component component) {
        java.util.List<Component> existing = componentRepository.findAll();
        java.util.Set<Integer> used = new java.util.HashSet<>();
        for(Component c : existing) {
            String qr = c.getQrCodeText();
            if(qr != null && qr.matches("COMP_\\d{3}")) {
                used.add(Integer.parseInt(qr.substring(5)));
            }
        }
        int nextNum = 1;
        while(used.contains(nextNum)) {
            nextNum++;
        }
        component.setQrCodeText(String.format("COMP_%03d", nextNum));
        return componentRepository.save(component);
    }
    
    public Component updateComponent(Long id, Component componentDetails) {
        Component comp = componentRepository.findById(id).orElseThrow();
        comp.setName(componentDetails.getName());
        comp.setQuantity(componentDetails.getQuantity());
        comp.setComponentId(componentDetails.getComponentId());
        return componentRepository.save(comp);
    }

    public void deleteComponent(Long id) {
        componentRepository.deleteById(id);
    }

    public List<Component> getAllComponents() {
        return componentRepository.findAll();
    }
    
    public Component getComponentByQr(String qrCodeText) {
        return componentRepository.findByQrCodeText(qrCodeText);
    }
    
    public Component getComponentByComponentId(String componentId) {
        return componentRepository.findByComponentId(componentId);
    }
}
