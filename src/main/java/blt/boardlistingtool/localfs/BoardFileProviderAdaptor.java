package blt.boardlistingtool.localfs;

import blt.boardlistingtool.core.boards.Models;
import blt.boardlistingtool.core.boards.out.BoardFileProvider;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;


@Component
public class BoardFileProviderAdaptor implements BoardFileProvider {

    @Autowired
    public BoardFileProviderAdaptor(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    private final ObjectMapper mapper;

    @Override
    public List<String> getFilesInTheSource(String sourceFolder) {
        if (sourceFolder == null || sourceFolder.isBlank()) {
            throw new IllegalArgumentException("Invalid source provided");
        }
        var folderPath = Paths.get(sourceFolder);
        var folderFile = folderPath.toFile();
        if (!folderFile.exists() || !folderFile.isDirectory()) {
            throw new IllegalArgumentException("Source doesn't exist or is not a directory");
        }
        try (var files = Files.list(Paths.get(sourceFolder))) {
            var pathPrefix = sourceFolder.endsWith("/") ? sourceFolder : sourceFolder + "/";
            return files
                    .filter(path -> path.getFileName().toString().toUpperCase().endsWith(".JSON"))
                    .map(f -> pathPrefix + f.getFileName())
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Models.Board> getBoardsFromTheFile(String fileName) {
        if (fileName == null || fileName.isBlank() || !fileName.toUpperCase().endsWith(".JSON")
                || !Paths.get(fileName).toFile().exists()) {
            throw new IllegalArgumentException("Invalid file provided");
        }
        try {
            var file = mapper.readValue(Paths.get(fileName).toFile(), BoardListInputScheme.class);
            return file.boards;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static class BoardListInputScheme {
        @JsonProperty
        List<Models.Board> boards;
    }
}
