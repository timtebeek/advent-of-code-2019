package com.github.timtebeek.day4.password;

public class Passwords {

	static boolean onlyIncreasingDigits(String password) {
		// Only increasing digits
		char a = password.charAt(0);
		for (char b : password.substring(1).toCharArray()) {
			if (a > b) {
				return false;
			}
			a = b;
		}
		return true;
	}

	static boolean atLeastTwoAdjacentDigits(String password) {
		char a = password.charAt(0);
		char b = password.charAt(1);
		char c = password.charAt(2);
		char d = password.charAt(3);
		char e = password.charAt(4);
		char f = password.charAt(5);
		// Contains at least two adjacent digits
		return a == b || b == c || c == d || d == e || e == f;
	}

	static boolean exactlyTwoAdjacentDigits(String password) {
		char a = password.charAt(0);
		char b = password.charAt(1);
		char c = password.charAt(2);
		char d = password.charAt(3);
		char e = password.charAt(4);
		char f = password.charAt(5);
		// Contains exactly two adjacent digits
		boolean ab = a == b && b != c;
		boolean bc = a != b && b == c && c != d;
		boolean cd = b != c && c == d && d != e;
		boolean de = c != d && d == e && e != f;
		boolean ef = d != e && e == f;
		return ab || bc || cd || de || ef;
	}

}
