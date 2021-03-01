package com.haif.orderservice.service;

import com.haif.inventoryapi.InventoryApi;
import com.haif.orderservice.service.fallback.InventoryServiceFallBackFactory;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = "inventory-service", fallbackFactory = InventoryServiceFallBackFactory.class)
public interface InventoryService extends InventoryApi {

}