package com.haif.orderservice.controller;

import com.haif.orderservice.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/order")  
public class OrderController {
	
	@Autowired
	private InventoryService inventoryService;
	
	@PostMapping
	public String greeting(@RequestParam("productId") Long productId, @RequestParam("count") Long count) {

		System.out.println("创建订单成功");
		String result = inventoryService.deductStock(productId, count);
		System.out.println("扣减库存结果：" + result);
		System.out.println("商品配送成功");
		System.out.println("赠送积分成功");
		return "success";
	}
}
