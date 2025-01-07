package blt.boardlistingtool.cli;

import blt.boardlistingtool.core.boards.Models;
import blt.boardlistingtool.core.boards.in.BoardListBuilder;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jline.terminal.Terminal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.command.annotation.Option;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import java.io.IOException;
import java.nio.file.Path;
import java.util.SortedSet;

@ShellComponent
public class BuildBoardListCommand {

    @Autowired
    public BuildBoardListCommand(BoardListBuilder boardListBuilder, Terminal terminal, ObjectMapper objectMapper) {
        this.boardListBuilder = boardListBuilder;
        this.terminal = terminal;
        this.objectMapper = objectMapper;
    }


    private final BoardListBuilder boardListBuilder;
    private final Terminal terminal;
    private final ObjectMapper objectMapper;

    @ShellMethod(key = "build", value = "Combine board data from multiple json files into a single json file")
    public void buildBoardList(
            @Option(longNames = "inputDir", required = true) String inputDir,
            @Option(longNames = "outputFile", required = true) String outputFile) {

        if (!outputFile.toLowerCase().endsWith(".json")) {
            terminal.writer().println("Option '--outputFile' must specify a JSON file");
            terminal.writer().flush();
            throw new CommandNotAbleToCompleteException();
        }
        try {
            var result = boardListBuilder.buildBoardList(inputDir);
            var outputObject = new BoardListOutputScheme(result.metadata(), result.boards());

            objectMapper.writeValue(Path.of(outputFile).toFile(), outputObject);
        } catch (IOException | RuntimeException e) {
            terminal.writer().write(e.getMessage());
            terminal.writer().flush();
            throw new CommandNotAbleToCompleteException(e);
        }
    }


    public record BoardListOutputScheme(@JsonProperty(value = "_metadata") Models.Metadata metadata,
                                        SortedSet<Models.Board> boards) {
    }


}
