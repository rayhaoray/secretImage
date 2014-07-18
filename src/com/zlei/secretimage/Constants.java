package com.zlei.secretimage;

public final class Constants {

    public static final String[] IMAGES = new String[] {
            // Light images
            "http://c.wrzuta.pl/wm16596/a32f1a47002ab3a949afeb4f",
            "http://macprovid.vo.llnwd.net/o43/hub/media/1090/6882/01_headline_Muse.jpg",
            // Heavy images
            "https://lh6.googleusercontent.com/-55osAWw3x0Q/URquUtcFr5I/AAAAAAAAAbs/rWlj1RUKrYI/s1024/A%252520Photographer.jpg",
            "https://lh4.googleusercontent.com/--dq8niRp7W4/URquVgmXvgI/AAAAAAAAAbs/-gnuLQfNnBA/s1024/A%252520Song%252520of%252520Ice%252520and%252520Fire.jpg",
           // Special cases
            "assets://nature_1.jpg",
            "http://cdn.urbanislandz.com/wp-content/uploads/2011/10/MMSposter-large.jpg", // Very large image
            "http://4.bp.blogspot.com/-LEvwF87bbyU/Uicaskm-g6I/AAAAAAAAZ2c/V-WZZAvFg5I/s800/Pesto+Guacamole+500w+0268.jpg", // Image with "Mark has been invalidated" problem
            "https://www.eff.org/sites/default/files/chrome150_0.jpg", // Image from HTTPS
            "http://img001.us.expono.com/100001/100001-1bc30-2d736f_m.jpg", // EXIF
            "file://mnt/sdcard/DCIM/100ANDRO/DSC_0533.jpg",
    };

    private Constants() {
    }

    public static class Config {
        public static final boolean DEVELOPER_MODE = false;
    }
    
    public static class Extra {
        public static final String IMAGES = "com.zlei.secretimage.IMAGES";
        public static final String IMAGE_POSITION = "com.zlei.secretimage.IMAGE_POSITION";
    }
}
