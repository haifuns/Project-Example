package com.haif.inventory.controller;


import com.haif.inventoryapi.InventoryApi;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InventoryController implements InventoryApi {

	@Override
	public String deductStock(@PathVariable("productId") Long productId, @PathVariable("stock") Long stock) {
		System.out.println("对商品【productId=" + productId + "】扣减库存：" + stock);
		return "{'msg': 'success'}";
	}
}