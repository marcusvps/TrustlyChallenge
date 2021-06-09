package com.trustly.challenge.constants;

public class BusinessConstants {

     public enum CSS {
        CSS_FILES_DIV_GIT_HUB("//span/a[@class='js-navigation-open Link--primary']"),
        CSS_INFOS_FILES_GIT_HUB("//div[@class='text-mono f6 flex-auto pr-3 flex-order-2 flex-md-order-1']");

        private String text;


         CSS(String text) {
            this.text = text;
        }

        public String getText() {
            return text;
        }
    };


     public enum REGEX{
         REGEX_FILE_EXTENSION("\\.[^.\\\\/:*?\"<>|\\r\\n]+$"),
         BLANK_SPACE(" ");

         private String text;

         REGEX(String text) {
             this.text = text;
         }

         public String getText() {
             return text;
         }
     }
}
