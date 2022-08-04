package com.ufu.crar.util;

public class Classifier {
	
		
	public enum ClassifierEnum{
		
		//PCQADup
		classifierPCQADupTitlesDbow("classifierPCQADupTitlesDbow","dbow",0,0,0,0,0,0,0,0),
		classifierPCQADupBodiesDbow("classifierPCQADupBodiesDbow","dbow",0,0,0,0,0,0,0,0),
		classifierPCQADupConcatDbow("classifierPCQADupConcatDbow","dbow",0,0,0,0,0,0,0,0),
	
		classifierPCQADupTitlesDm("classifierPCQADupTitlesDm","dm",0,0,0,0,0,0,0,0),
		classifierPCQADupBodiesDm("classifierPCQADupBodiesDm","dm",0,0,0,0,0,0,0,0),
		classifierPCQADupConcatDm("classifierPCQADupConcatDm","dm",0,0,0,0,0,0,0,0),
		
		//FirstTest
		classifierFirstTestTitlesDbow("classifierFirstTestTitlesDbow","dbow",0,0,0,0,0,0,0,0),
		classifierFirstTestBodiesDbow("classifierFirstTestBodiesDbow","dbow",0,0,0,0,0,0,0,0),
		classifierFirstTestConcatDbow("classifierFirstTestConcatDbow","dbow",0,0,0,0,0,0,0,0),
	
		classifierFirstTestTitlesDm("classifierFirstTestTitlesDm","dm",0,0,0,0,0,0,0,0),
		classifierFirstTestBodiesDm("classifierFirstTestBodiesDm","dm",0,0,0,0,0,0,0,0),
		classifierFirstTestConcatDm("classifierFirstTestConcatDm","dm",0,0,0,0,0,0,0,0),
		
		//PaperEmpiricalEvaluation
		classifierPaperEmpiricalEvaluationTitlesDbow("classifierPaperEmpiricalEvaluationTitlesDbow","dbow",0,0,0,0,0,0,0,0),
		classifierPaperEmpiricalEvaluationBodiesDbow("classifierPaperEmpiricalEvaluationBodiesDbow","dbow",0,0,0,0,0,0,0,0),
		classifierPaperEmpiricalEvaluationConcatDbow("classifierPaperEmpiricalEvaluationConcatDbow","dbow",0,0,0,0,0,0,0,0),
	
		classifierPaperEmpiricalEvaluationTitlesDm("classifierPaperEmpiricalEvaluationTitlesDm","dm",0,0,0,0,0,0,0,0),
		classifierPaperEmpiricalEvaluationBodiesDm("classifierPaperEmpiricalEvaluationBodiesDm","dm",0,0,0,0,0,0,0,0),
		classifierPaperEmpiricalEvaluationConcatDm("classifierPaperEmpiricalEvaluationConcatDm","dm",0,0,0,0,0,0,0,0),
		
		
		DupeClassifier("Dupe","",0,0,0,0,0,0,0,0);
		
		
		private String classifierName;
		private String modelType;
		private Integer hits100000;
		private Integer hits1000;
		private Integer hits100;
		private Integer hits50;
		private Integer hits20;
		private Integer hits10; 
		private Integer hits5;
		private Integer hits1;
		
		private ClassifierEnum(String classifierName,String modelType, Integer hits100000, Integer hits1000, Integer hits100, Integer hits50, Integer hits20, Integer hits10, Integer hits5, Integer hits1) {
			this.classifierName = classifierName;
			this.modelType = modelType;
			this.hits100000 = hits100000;
			this.hits1000 = hits1000;
			this.hits100 = hits100;
			this.hits50 = hits50;
			this.hits20 = hits20;
			this.hits10 = hits10;
			this.hits5 = hits5;
			this.hits1 = hits1;
		}

		public String getClassifierName() {
			return classifierName;
		}

		public void setClassifierName(String classifierName) {
			this.classifierName = classifierName;
		}

		public Integer getHits1000() {
			return hits1000;
		}

		public void setHits1000(Integer hits1000) {
			this.hits1000 = hits1000;
		}

		public Integer getHits100() {
			return hits100;
		}

		public void setHits100(Integer hits100) {
			this.hits100 = hits100;
		}

		public Integer getHits50() {
			return hits50;
		}

		public void setHits50(Integer hits50) {
			this.hits50 = hits50;
		}

		public Integer getHits20() {
			return hits20;
		}

		public void setHits20(Integer hits20) {
			this.hits20 = hits20;
		}

		public Integer getHits10() {
			return hits10;
		}

		public void setHits10(Integer hits10) {
			this.hits10 = hits10;
		}

		public Integer getHits5() {
			return hits5;
		}

		public void setHits5(Integer hits5) {
			this.hits5 = hits5;
		}

		public Integer getHits1() {
			return hits1;
		}

		public void setHits1(Integer hits1) {
			this.hits1 = hits1;
		}

		public String getModelType() {
			return modelType;
		}

		public void setModelType(String modelType) {
			this.modelType = modelType;
		}

		public Integer getHits100000() {
			return hits100000;
		}

		public void setHits100000(Integer hits100000) {
			this.hits100000 = hits100000;
		}
		
		
		

		
	}
}
