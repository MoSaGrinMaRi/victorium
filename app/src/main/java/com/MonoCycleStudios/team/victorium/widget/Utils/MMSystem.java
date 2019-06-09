package com.MonoCycleStudios.team.victorium.widget.Utils;

import com.MonoCycleStudios.team.victorium.BuildConfig;

public class MMSystem {
    public static MMOut out = new MMOut();

    public static class MMOut {
        public void println(Object x) {
            if (BuildConfig.DEBUG) {
                System.out.println(x);
            }
        }
    }
}


