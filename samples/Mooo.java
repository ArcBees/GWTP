package com.example;

import com.gwtplatform.annotation.GenEvent;
import com.gwtplatform.annotation.Order;

@GenEvent
public class Mooo {
 	@Order(0) int apple;
 	@Order(4) int bob;
 	boolean cats;
 	@Order(3) String[] banana;
}
