package blt.boardlistingtool;

import org.springframework.boot.Banner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class BoardListingToolApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(BoardListingToolApplication.class)
                .bannerMode(Banner.Mode.OFF)
                .run(args);
    }

}


