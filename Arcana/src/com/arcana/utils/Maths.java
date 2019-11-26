package com.arcana.utils;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class Maths {
	
	public static NumberFormat getNetworkNumberFormat(){
		return new DecimalFormat("#0.0000");
	}
	
}
