package com.haif.inventoryapi;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/inventory")
public interface InventoryApi {

	/**
	 * 扣减库存
	 */
	@PutMapping(value = "/deduct/{productId}/{stock}")
	String deductStock(@PathVariable("productId") Long productId, @PathVariable("stock") Long stock);
}
