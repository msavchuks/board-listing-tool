package blt.boardlistingtool.config;

import blt.boardlistingtool.core.boards.BoardBuilderService;
import blt.boardlistingtool.core.boards.in.BoardListBuilder;
import blt.boardlistingtool.core.boards.out.BoardFileProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CliAppConfiguration {

    @Bean
    public BoardListBuilder getBoardListBuilder(@Autowired BoardFileProvider adaptor) {
        return new BoardBuilderService(adaptor);
    }

    @Bean
    public ObjectMapper getObjectMapper() {
        return new ObjectMapper().setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
    }

}
