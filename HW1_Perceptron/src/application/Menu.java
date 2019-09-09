package application;

import java.io.* ;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;

public class Menu extends Scene {
	Button loadFileB = (Button)lookup("#loadFile");
	Button newTrainingB = (Button)lookup("#newTraining") ;
	Text path = (Text)lookup("#path") ;
	Text trainingTimes = (Text)lookup("#trainingTimes") ;
	Text threshold = (Text)lookup("#threshold") ;
	Text weights = (Text)lookup("#weights") ;
	Text trainingRecRate = (Text)lookup("#trainingRecRate") ;
	Text testingRecRate = (Text)lookup("#testingRecRate") ;
	TextField learningRateT = (TextField)lookup("#learningRate") ;
	TextField maxTimesT = (TextField)lookup("#maxTimes") ;
	TextField minRecRateT = (TextField)lookup("#minRecRate") ;
	
	VBox boxTest = (VBox)lookup("#boxTest") ;
	VBox boxTrain = (VBox)lookup("#boxTrain") ;
	
	ArrayList<Data> listOfData ;
	ArrayList<Data> listOfTrainingData ;
	ArrayList<Data> listOfTestingData ;
	
	File file ;
	
	int numberOfTrain ;
	int numberOfTest ;
	int numberOfData ;
	int maxTimes ;
	double learningRate ;
	double minRecRate ;
	int classCount ;
	float[] classification ;
	float maxX ;
	float minX ;
	
	
	public Menu(Main m,Parent root) {
		super(root);
		initial() ;
		loadFileB.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				FileChooser fileChooser = new FileChooser();
				FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TEXT files (*.txt)", "*.txt");
	            fileChooser.getExtensionFilters().add(extFilter);
	            fileChooser.setTitle("Choose a txt file");
	            file = fileChooser.showOpenDialog(m.stage);
	            listOfData = new ArrayList<>();	
            	listOfTrainingData = new ArrayList<>();	
            	listOfTestingData = new ArrayList<>();
                if (file != null) {
                    loadFile(file) ;
                    run() ;
                }
			}
		});
		
		newTrainingB.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (file != null) {
					run() ;
				}
			}
		});	
		
		learningRateT.textProperty().addListener(new ChangeListener<String>() {
		    @Override
		    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
		    	if (newValue.isEmpty() || newValue.matches("[0]") || newValue.matches("[0][.]") || newValue.matches("[0][.]([0-9]*)[1-9]([0-9]*)") ) {
		    		learningRateT.setStyle("-fx-text-inner-color: black;"); 
	    	    	learningRate = Double.parseDouble(learningRateT.getText());
	    	    } else {
	    	    	learningRateT.setStyle("-fx-text-inner-color: red;"); 
		        }
		    }
		});
		
		maxTimesT.textProperty().addListener(new ChangeListener<String>() {
			@Override
		    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
		    	if (newValue.isEmpty() || newValue.matches("[1-9]([0-9]*)")) {
		    		maxTimesT.setStyle("-fx-text-inner-color: black;"); 
	    	    	maxTimes = Integer.parseInt(maxTimesT.getText());
	    	    } else {
	    	    	maxTimesT.setStyle("-fx-text-inner-color: red;"); 
	    	    }
		    }
		});
		
		minRecRateT.textProperty().addListener(new ChangeListener<String>() {
			@Override
		    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
		    	if (newValue.isEmpty() || newValue.matches("([1-9]?)([0-9])(([.][0-9]*)?)") || newValue.matches("100([.][0]*)?")) {
		    		minRecRateT.setStyle("-fx-text-inner-color: black;"); 
	    	    	minRecRate = Double.parseDouble(minRecRateT.getText());
	    	    } else {
	    	    	minRecRateT.setStyle("-fx-text-inner-color: red;"); 
		        }
		    }
		});
	}

	private void initial() {
    	maxTimes = 1000 ;
    	learningRate = 0.1 ;
    	minRecRate = 100 ;
    	classification = new float[20] ;
    	classCount = 0 ;
	}

	private void run() {
		DecimalFormat df = new DecimalFormat("######0.00");
		Perceptron perceptron = new Perceptron(listOfTrainingData, maxTimes, learningRate, minRecRate, classification) ;
		trainingTimes.setText(""+perceptron.Times) ;
		
		if (perceptron.bestWeight.size()==0) {
			run() ;
		}
		for (int i = 0; i < perceptron.bestWeight.size(); i++) {
			if (i == 0) {
				threshold.setText(""+df.format(perceptron.bestWeight.get(i)));
				weights.setText("( ") ;
			}
			else
				weights.setText(weights.getText() + df.format(perceptron.bestWeight.get(i)) ) ;
			if (0 < i && i < perceptron.bestWeight.size()-1) {
				weights.setText(weights.getText() + " , " ) ;
			}
		}
		weights.setText(weights.getText()+" )") ;
		
		double tr = perceptron.TrRecRate ;
		trainingRecRate.setText(""+ df.format(tr) + " %");
		
		double correct = 0 ;
		for (int j = 0; j < listOfTestingData.size() ; j++) {
			if (perceptron.judge(perceptron.bestWeight , j , listOfTestingData, classification)) {
				correct++ ;
			}
		}
		correct = 100*correct/listOfTestingData.size() ;
		testingRecRate.setText("" + df.format(correct) + " %");
		
		draw(perceptron , boxTest , listOfTestingData) ;
		draw(perceptron , boxTrain , listOfTrainingData) ;
	}
	
	private void draw(Perceptron perceptron , VBox box , ArrayList<Data> listOfData) {
		double unit = maxX-minX/20 ;
		XYChart.Series<Number,Number> series1 = new XYChart.Series<>();
        XYChart.Series<Number,Number> series2 = new XYChart.Series<>();
        XYChart.Series<Number,Number> series3 = new XYChart.Series<>();
        
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLowerBound(minX-unit/2.5);
        xAxis.setUpperBound(maxX+unit/2.5);
        xAxis.setAutoRanging(false);
        xAxis.setTickUnit(unit);
        
        final LineChart<Number,Number> chart = new LineChart<>(xAxis , yAxis);
        
        chart.setLegendVisible(false);
        chart.getData().add(series1) ;
        chart.getData().add(series2) ;
        chart.getData().add(series3) ;
        chart.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
        
        for (int i = 0; i < listOfData.size() ; i++) {
        	if (listOfData.get(i).d == classification[0]) {
        		series1.getData().add(new XYChart.Data<>(listOfData.get(i).x[1] , listOfData.get(i).x[2] ));
			}
        	else {
        		series2.getData().add(new XYChart.Data<>(listOfData.get(i).x[1] , listOfData.get(i).x[2] ));
        	}
        }
        for (double x = minX-unit ; x <= maxX+unit; x += 0.05) {
            double y = (perceptron.bestWeight.get(0)-perceptron.bestWeight.get(1)*x)/perceptron.bestWeight.get(2) ;
            series3.getData().add(new XYChart.Data<>(x,y));
        }
        if (!box.getChildren().isEmpty()) {
			box.getChildren().remove(0) ;
		}
        box.getChildren().add(chart) ;
	}
	
	private void loadFile(File file) {
    	maxX = -100000 ;
    	minX = 100000 ;
		BufferedReader bufferedReader = null ;
		Data data ;
		path.setText(file.getPath());
		
		try {
			bufferedReader = new BufferedReader(new FileReader(file));
            String text;
            while ((text = bufferedReader.readLine()) != null) {
            	String[] array = text.split(" ");
            	data = new Data(array) ;
            	listOfData.add(data) ;
            }
		}catch (Exception e) {
			// TODO: handle exception
        } finally {
            try {
                bufferedReader.close();
                Collections.shuffle(listOfData);
            } catch (Exception e) {}
        }
		
		boolean add = true ;
		for (int i = 0; i < listOfData.size() ; i++) {
			for (int j = 0; j < classCount ; j++) {
				if (listOfData.get(i).d == classification[j]) {
					add = false ;
					break ;
				}
			}
			if (add) {
				classification[classCount++] = listOfData.get(i).d ;
			}
		}
		
		numberOfData = listOfData.size() ;
		numberOfTrain = (int)Math.ceil((double)(listOfData.size()*2)/3) ;
		numberOfTest = numberOfData-numberOfTrain ;

		for (int i = 0 ; i < numberOfTrain ; i++) {
			if (listOfData.get(0).x[1] > maxX) {
				maxX = listOfData.get(0).x[1] ;
			}
			if (listOfData.get(0).x[1] < minX) {
				minX = listOfData.get(0).x[1] ;
			}
			listOfTrainingData.add(listOfData.get(0)) ;
			listOfData.remove(0) ;
		}
		for (int i = 0 ; i < numberOfTest ; i++) {
			if (listOfData.get(0).x[1] > maxX) {
				maxX = listOfData.get(0).x[1] ;
			}
			if (listOfData.get(0).x[1] < minX) {
				minX = listOfData.get(0).x[1] ;
			}
			listOfTestingData.add(listOfData.get(0)) ;
			listOfData.remove(0) ;
		}
	}
}