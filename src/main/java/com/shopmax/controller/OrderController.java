package com.shopmax.controller;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.shopmax.dto.OrderDto;
import com.shopmax.dto.OrderHistDto;
import com.shopmax.service.OrderService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class OrderController {
	private final OrderService orderService;
	
	@PostMapping(value = "/order")
	public @ResponseBody ResponseEntity order(@RequestBody @Valid OrderDto orderDto,
			BindingResult bindingResult, Principal principal) {
		//Principal: 로그인한 사용자의 정보를 가져올 수 있다.
		
		if(bindingResult.hasErrors()) {
			StringBuilder sb = new StringBuilder();
			List<FieldError> fieldErrors = bindingResult.getFieldErrors();
			
			for (FieldError fieldError : fieldErrors) {
				sb.append(fieldError.getDefaultMessage()); //에러메세지를 합친다.
			}
			
			return new ResponseEntity<String>(sb.toString(), HttpStatus.BAD_REQUEST);
		}
		
		String email = principal.getName(); //id에 해당하는 정보를 가지고 온다(email)
		Long orderId;
		
		try {
			orderId = orderService.order(orderDto, email); //주문하기
		} catch (Exception e) {
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
		
		return new ResponseEntity<Long>(orderId, HttpStatus.OK); //성공시
	}
	
	
	//주문내역을 보여준다
	@GetMapping(value = {"/orders", "/orders/{page}"})
	public String orderHist(@PathVariable("page") Optional<Integer> page,
			Principal principal, Model model) {
		//1. 한페이지 당 4개의 데이터를 가지고 오도록 설정
		Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0, 4);
		
		//2. 서비스 호출
		Page<OrderHistDto> orderHistDtoList = 
				orderService.getOrderList(principal.getName(), pageable);
		
		//3. 서비스에서 가져온 값들을 view단에 model을 이용해 전송
		model.addAttribute("orders", orderHistDtoList);
		model.addAttribute("maxPage", 5); //하단에 보여줄 최대 페이지
		//model.addAttribute("page", pageable.getPageNumber()); //현재페이지
		
		return "order/orderHist";
	}

}
