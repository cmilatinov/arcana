package com.arcana.menus;

public class Slider {
	
	private int index = 0;
	
	private String[] values;
	
	private boolean disabled = false;
	
	public Slider(String[] values){
		this.values = values;	
	}
	
	public void disableSlider(){
		disabled = true;
	}
	
	public void enableSlider(){
		disabled = false;
	}
	
	public boolean isDisabled(){
		return disabled;
	}
	
	public void moveSliderRight(){
		if(index + 1 > values.length - 1)
			index = 0;
		else
			index++;
	}
	
	public void moveSliderLeft(){
		if(index - 1 < 0)
			index = values.length - 1;
		else
			index--;
	}
	
	public String getCurrentValue(){
		return values[index];
	}
	
	public void setIndex(int i){
		index = i;
	}
	
	public void setToHighestValue(){
		index = values.length - 1;
	}
	
	public void setValue(String s){
		for(int i = 0; i < values.length; i++){
			if(values[i].equals(s)){
				index = i;
				break;
			}
		}
	}
}
