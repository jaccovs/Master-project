package org.exquisite.datamodel;

import org.exquisite.datamodel.ExquisiteEnums.EngineType;
import org.exquisite.datamodel.ExquisiteEnums.ExquisiteLocaleFlag;

public class ExquisiteUserSettings {

	
        private int colorIndexInput;
        private int colorIndexInterim;
        private int colorIndexoutput;
        private EngineType diagnosisEngine;
        private int searchDepth;
        private int maxDiagnoses;
        private double probabilityThreshold;
        private ExquisiteLocaleFlag localeFlag;
        private boolean showServerDebugMessages;

       /**
        * Initialisiert eine neue Instanz der ExquisiteUserSettings-Klasse.
        */        
        public ExquisiteUserSettings()
        {
            colorIndexInput = -1;
            colorIndexInterim = -1;
            colorIndexoutput = -1;
            diagnosisEngine = EngineType.HSDagStandardQX;
            searchDepth = -1;
            maxDiagnoses = -1;
            probabilityThreshold = 0d;
            localeFlag = ExquisiteLocaleFlag.German;
            showServerDebugMessages = false;
        }

        /**
         * Initialisiert eine neue Instanz der ExquisiteUserSettings-Klasse.
         */
        public ExquisiteUserSettings(int colorIndexInput, 
            int colorIndexInterim, int colorIndexoutput, EngineType diagnosisEngine, int searchDepth, int maxDiagnoses, double probabilityThreshold, ExquisiteLocaleFlag localeFlag, boolean showServerDebugMessages)
        {
            this.colorIndexInput = colorIndexInput;
            this.colorIndexInterim = colorIndexInterim;
            this.colorIndexoutput = colorIndexoutput;
            this.diagnosisEngine = diagnosisEngine;
            this.searchDepth = searchDepth;
            this.maxDiagnoses = maxDiagnoses;
            this.probabilityThreshold = probabilityThreshold;
            this.localeFlag = localeFlag;
            this.showServerDebugMessages = showServerDebugMessages;
        }

        /**
           Gibt einen String zurueck, der das aktuelle Objekt darstellt.
         * @return Ein String, der das aktuelle Objekt darstellt.
         */
        @Override
        public String toString()
        {
            return
                String.format(
                    "ColorIndexInput: {0}, ColorIndexInterim: {1}, ColorIndexoutput: {2}, DiagnosisEngine: {3}, SearchDepth: {4}, MaxDiagnoses: {5}, ProbabilityThreshold: {6}, LocaleFlag: {7}",
                    getColorIndexInput(), getColorIndexInterim(), getColorIndexoutput(), getDiagnosisEngine(),
                    getSearchDepth(), getMaxDiagnoses(), getProbabilityThreshold(), getLocaleFlag());
        }
        
       
		/**
		 * ColorIndexInput
		 */       
        public int getColorIndexInput() {
            return colorIndexInput; 
        }
        public void setColorIndexInput(int value){           
            colorIndexInput = value;
        }

        /**
         * ColorIndexInterim
         */
        public int getColorIndexInterim(){
            return colorIndexInterim;
        }        
        public void setColorIndexInterim(int value){
           colorIndexInterim = value; 
        }

        /**
         * ColorIndexOutput
         */
        public int getColorIndexoutput(){
            return colorIndexoutput;            
        }
        public void setColorIndexoutput(int value){
            colorIndexoutput = value; 
        }
        
        /**
         * DiagnosisEngine
         */
        public EngineType getDiagnosisEngine() {
			return diagnosisEngine;
		}
        public void setDiagnosisEngine(EngineType diagnosisEngine) {
			this.diagnosisEngine = diagnosisEngine;
		}

        /**
         * SearchDepth
         */
        public int getSearchDepth(){
            return searchDepth;            
        }
        public void setSearchDepth(int value){
           searchDepth = value; 
        }

        /**
         * MaxDiagnosis
         */
        public int getMaxDiagnoses(){
            return maxDiagnoses;
        }
        public void setMaxDiagnoses(int value){
           maxDiagnoses = value; 
        }
        
        /**
         * ProbabilityThreshold
         */
        public double getProbabilityThreshold() {
			return probabilityThreshold;
		}
        public void setProbabilityThreshold(double probabilityThreshold) {
			this.probabilityThreshold = probabilityThreshold;
		}

        /**
         * LocaleFlag
         */
        public ExquisiteLocaleFlag getLocaleFlag(){
            return localeFlag;
        }
        public void setLocaleFlag(ExquisiteLocaleFlag value){           
           localeFlag = value;
        }


       /**
        * ShowServerDebugMessages.
        */
        public boolean getShowServerDebugMessages(){
            return showServerDebugMessages;
        }
        public void setShowServerDebugMessages(boolean value){
            showServerDebugMessages = value;
        } 
    }