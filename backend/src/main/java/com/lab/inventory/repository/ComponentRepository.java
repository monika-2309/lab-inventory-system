package com.lab.inventory.repository;

import com.lab.inventory.entity.Component;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ComponentRepository extends JpaRepository<Component, Long> {
    Component findByQrCodeText(String qrCodeText);
    Component findByComponentId(String componentId);
    List<Component> findByQuantityLessThan(Integer quantity);
}
