package com.shopmax.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shopmax.dto.OrderDto;
import com.shopmax.dto.OrderHistDto;
import com.shopmax.dto.OrderItemDto;
import com.shopmax.entity.Item;
import com.shopmax.entity.ItemImg;
import com.shopmax.entity.Member;
import com.shopmax.entity.Order;
import com.shopmax.entity.OrderItem;
import com.shopmax.repository.ItemImgRepository;
import com.shopmax.repository.ItemRepository;
import com.shopmax.repository.MemberRepository;
import com.shopmax.repository.OrderRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {
	private final ItemRepository itemRepository;
	private final MemberRepository memberRepository;
	private final OrderRepository orderRepository;
	private final ItemImgRepository itemImgRepository;
	
	//주문하기
	public Long order(OrderDto orderDto, String email) {
		
		//1.주문할 상품을 조회
		Item item = itemRepository.findById(orderDto.getItemId())
				                  .orElseThrow(EntityNotFoundException::new);
		
		//2.현재 로그인한 회원의 이메일을 이용해 회원정보를 조회
		Member member = memberRepository.findByEmail(email);
		
		//3.주문할 상품 엔티티와 주문 수량을 이용하여 주문 상품 엔티티를 생성
		List<OrderItem> orderItemList = new ArrayList<>();
		OrderItem orderItem = OrderItem.createOrderItem(item, orderDto.getCount());
		orderItemList.add(orderItem);
		
		//4.회원 정보와 주문할 상품 리스트 정보를 이용하여 주문 엔티티를 생성
		Order order = Order.createOrder(member, orderItemList);
		orderRepository.save(order); //insert
		
		return order.getId();		
		
	}
	
	
	//주문 목록을 가져오는 서비스
	@Transactional(readOnly = true)
	public Page<OrderHistDto> getOrderList(String email, Pageable pageable) {
		//1. 유저 아이디와 페이징 조건을 이용하여 주문 목록을 조회
		List<Order> orders = orderRepository.findOrders(email, pageable);
		
		//2. 유저의 주문 총 개수를 구한다
		Long totalCount = orderRepository.countOrder(email);
		
		List<OrderHistDto> orderHistDtos = new ArrayList<>();
		
		//3. 주문 리스트를 순회하면서 구매 이력 페이지에 전달할 DTO(OrderHistDto)를 생성
		for (Order order : orders) {
			OrderHistDto orderHistDto = new OrderHistDto(order);
			List<OrderItem> orderItems = order.getOrderItems();
			
			for (OrderItem orderItem : orderItems) {
				//상품의 대표 이미지
				ItemImg itemImg = itemImgRepository.findByItemIdAndRepimgYn(orderItem.getItem().getId(), "Y");
				OrderItemDto orderItemDto = new OrderItemDto(orderItem, itemImg.getImgUrl());
				orderHistDto.addOrderItemDto(orderItemDto);
			}
			
			orderHistDtos.add(orderHistDto);
		}

		return new PageImpl<>(orderHistDtos, pageable, totalCount); //4.페이지 구현 객체를 생성하여 return
	}
}





