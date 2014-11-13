package com.zlei.secretimage;

public final class Constants {

    public static final String[] IMAGES = new String[] {
            // Light images
            "file://storage/sdcard0/DCIM/Camera/001.jpg",
            //"http://172.100.3.83/image/grass1.png",
            "https://lh6.ggpht.com/hGnzb0TzcUKDC63BIxs-x0wig5iq7SJd7F1iSRBywX3fbKyLoMDZWAsJJNxyOx9F1rUk=w300",
            "http://www.sessionm.com/wp-content/themes/sessionm/images/m.png",
            "https://lh4.ggpht.com/xnSLC4nMwjNT0KIyvn9ZSbzvOI3etNFcVCCF5bdGddjga2qD85sluBsa6J6G8672Ayc=w300",
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
