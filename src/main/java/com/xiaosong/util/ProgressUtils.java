package com.xiaosong.util;

import java.util.ArrayList;
import java.util.HashMap;

public class ProgressUtils  extends ArrayList<HashMap> {

        private static ProgressUtils progressUtils;

        private ProgressUtils(){}

        public static ProgressUtils getInstance(){
            if(progressUtils == null){
                synchronized (ProgressUtils.class) {
                    if (progressUtils == null) {
                        progressUtils = new ProgressUtils();
                    }
                }
            }
            return progressUtils;
        }
}
