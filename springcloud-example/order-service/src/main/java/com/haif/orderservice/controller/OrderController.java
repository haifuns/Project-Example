package com.haif.orderservice.controller;

import com.haif.orderservice.service.InventoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/order")  
public class OrderController {

	private final Logger LOG = LoggerFactory.getLogger(OrderController.class);

	@Autowired
	private InventoryService inventoryService;
	
	@PostMapping
	public String greeting(@RequestParam("productId") Long productId, @RequestParam("count") Long count) {

		LOG.info("创建订单成功");
		String result = inventoryService.deductStock(productId, count);
		LOG.info("扣减库存结果：" + result);
		LOG.info("商品配送成功");
		LOG.info("赠送积分成功");
		return "success";
	}

	@GetMapping("/timeout")
	public String timeout() throws InterruptedException {
		LOG.info("test timeout fallback");
		Thread.sleep(60000);
		LOG.info("test timeout end");
		return "timeout";
	}
}
