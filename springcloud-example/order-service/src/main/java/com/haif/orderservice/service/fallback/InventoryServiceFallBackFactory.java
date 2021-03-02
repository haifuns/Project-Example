package com.haif.orderservice.service.fallback;

import com.haif.orderservice.service.InventoryService;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
public class InventoryServiceFallBackFactory implements FallbackFactory<InventoryService> {

    @Override
    public InventoryService create(Throwable cause) {
        return new InventoryService() {
            @Override
            public String deductStock(Long productId, Long stock) {
                return "扣减库存失败";
            }
        };
    }
}
