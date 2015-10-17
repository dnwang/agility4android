package org.pinwheel.demo4agility.entity;

public class TestBuilder {

	private int param ;
	
	private String extar1 ;
	private String extar2 ;
	private String extar3 ;
	private String extar4 ;
	
	private  TestBuilder(Builder builder){
		param = builder.param ;
		extar1 = builder.extar1 ;
		extar2 = builder.extar2 ;
		extar3 = builder.extar3 ;
		extar4 = builder.exrat4 ;
	} 
	
	public static class Builder{
		
		private int param ;
		
		private String extar1 = "" ;
		private String extar2 = "" ;
		private String extar3 = "" ;
		private String exrat4 = "" ;
		
		public Builder(int val){
			param = val ;
		}
		
		public Builder exrat1(String val){
			extar1 = val ;
			return this ;
		}
		public Builder exrat2(String val){
			extar2 = val ;
			return this ;
		}
		public Builder exrat3(String val){
			extar3 = val ;
			return this ;
		}
		public Builder exrat4(String val){
			exrat4 = val ;
			return this ;
		}
		
		public TestBuilder create(){
			return new TestBuilder(this) ;
		}
		
	}
}
