package com.example;

import com.gwtplatform.annotation.GenDispatch;
import com.gwtplatform.annotation.In;
import com.gwtplatform.annotation.Out;

@GenDispatch(secured=true)
public class NewUser {
	@In(1) String firstName;
	@In(2) String lastName;
	@Out(1) int userId;
}
