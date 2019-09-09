package application;

import java.util.ArrayList;

import javafx.scene.Group;

public class Perceptron extends Group{
	int Times ; // 訓練次數
	double TrRecRate ; // 訓練辨識率
	
	ArrayList<Float> bestWeight ;
	ArrayList<Float> weight ;
	
	
	public Perceptron(ArrayList<Data> listOfTrainingData , int maxTimes , double learningRate , double minRecRate , float[] classification) {
		this.TrRecRate = 0 ;
		this.bestWeight  = new ArrayList<>() ;
		this.weight = new ArrayList<>() ;
		this.weight.add((float) (-1.0)) ;
		for (int i = 0; i < listOfTrainingData.get(0).dimension; i++) {
			this.weight.add((float)(2*Math.random()-1)) ;
		}
		training(listOfTrainingData , maxTimes , learningRate , minRecRate , classification) ;
	}
	
	private void training(ArrayList<Data> listOfTrainingData , int maxTimes , double learningRate , double minRecRate , float[] classification) {
		for (this.Times = 0 ; this.Times < maxTimes ; this.Times++) {
			int teg = (int)(Math.random()*listOfTrainingData.size()) ;
///////////////////////////////////////////////////////////// 感知機收斂定理 ///////////////////////////////////////////////////////////	
			if (!judge(this.weight , teg, listOfTrainingData, classification)) {
				if (listOfTrainingData.get(teg).d == classification[0]) {
					for (int i = 0; i < this.weight.size() ; i++) {
						this.weight.set(i,  (float) (this.weight.get(i)+ ((float)learningRate)*listOfTrainingData.get(teg).x[i])) ; 
					}
				}
				else {
					for (int i = 0; i < this.weight.size() ; i++) {
						this.weight.set(i,  (float) (this.weight.get(i)-((float)learningRate)*listOfTrainingData.get(teg).x[i])) ; 
					}
				}	

			}
			else {
				int correct = 0 ;
				for (int i = 0; i < listOfTrainingData.size() ; i++) {
					if (judge(this.weight , i, listOfTrainingData, classification)) {
						correct++ ;
					}
				}
				if (100*correct/listOfTrainingData.size() > this.TrRecRate) {
					this.TrRecRate = 100*correct/listOfTrainingData.size() ;
					bestWeight = weight ;
				}
				if (this.TrRecRate>=minRecRate) 
					break ;
			}
		}
	}
	
	public boolean judge(ArrayList<Float> weight , int teg , ArrayList<Data> listOfData , float[] classification) {
		float temp = 0 ;
		// 計算網路輸出值
		for (int i = 0; i <= listOfData.get(teg).dimension; i++) {
			temp += weight.get(i)*listOfData.get(teg).x[i] ;
		}
		// 分類
		if (temp >= 0.0) 
			listOfData.get(teg).y = classification[0] ;
		else 
			listOfData.get(teg).y = classification[1] ;
		// 判斷是否調整鍵結值
		if (listOfData.get(teg).y != listOfData.get(teg).d) 
			return false ; // 失敗，調整
		else
			return true ; // 成功
	}
}
