package com.platform.cubism.test;


import com.platform.cubism.util.FileUtils;

public class Main {

	public static void main(String[] args) {
		FileUtils.zip("E:/doc/*.doc", "d:/gbg/", "g.nd",false);
		
		System.out.println("ok");
	}
}
