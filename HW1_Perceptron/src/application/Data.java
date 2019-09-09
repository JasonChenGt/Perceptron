package application;

import javafx.scene.Group;

public class Data extends Group{
	int dimension ; // 維度p
	float[] x = new float[20] ; // x0 and 輸入值x1...xp
	float d ; // 期望輸出d
	float y ; // 實際輸出
	
	public Data(String array[]) { // 資料設定處理
		this.dimension = array.length-1 ; 
		this.d = Float.parseFloat(array[this.dimension]) ; 
		this.y = 0 ;
		this.x[0] = -1 ; // x0
		for (int i = 0 ; i < array.length ; i++) { 
			this.x[i+1] = Float.parseFloat(array[i]) ;
		}
	}
}
