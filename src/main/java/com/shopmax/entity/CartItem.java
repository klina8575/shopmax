package com.shopmax.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="cart_item")
@Getter
@Setter
@ToString
public class CartItem {
	
	@Id
	@Column(name = "cart_item_id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cart_id")
	private Cart cart;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "item_id")
	private Item item;
	
	private int count;
}





