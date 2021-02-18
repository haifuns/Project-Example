package com.haif.orderservice.service;

import com.haif.inventoryapi.InventoryApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = "inventory-service")
public interface InventoryService extends InventoryApi {

}