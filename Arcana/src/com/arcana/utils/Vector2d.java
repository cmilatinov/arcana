package com.arcana.utils;

public class Vector2d {
	
	private double x, y;
	
	public Vector2d(double x, double y){
		this.x = x;
		this.y = y;
	}
	
	public Vector2d normalize(){
		return new Vector2d(x / magnitude(), y / magnitude());
	}
	
	public Vector2d add(Vector2d vec){
		return new Vector2d(vec.x + this.x, vec.y + this.y);
	}
	
	public Vector2d subtract(Vector2d vec){
		return new Vector2d(this.x - vec.x, this.y - vec.y);
	}
	
	public double dot(Vector2d vec){
		return (this.x * vec.x) + (this.y * vec.y);
	}
	
	public double angle(){
		return this.x > 0 ? Math.asin(normalize().getY()) : Math.PI - Math.asin(normalize().getY());
	}
	
	public double magnitude(){
		return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
	}
	
	public double getX(){
		return x;
	}
	
	public double getY(){
		return y;
	}
}
