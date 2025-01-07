package blt.boardlistingtool.localfs;

import blt.boardlistingtool.config.CliAppConfiguration;
import blt.boardlistingtool.core.boards.Models;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Import(CliAppConfiguration.class)
class BoardFileProviderAdaptorTest {

    @Autowired
    private BoardFileProviderAdaptor sut;

    @Test
    void getFilesInTheSourceThrowsIllegalArgumentExceptionIfInvalidDirectoryPassed() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            sut.getFilesInTheSource("");
        });
        assertEquals("Invalid source provided", exception.getMessage());
    }

    @Test
    void getFilesInTheSourceThrowsIllegalArgumentExceptionIfFolderDoesNotExist() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            sut.getFilesInTheSource("src/test/resources/bad-folder");
        });
        assertEquals("Source doesn't exist or is not a directory", exception.getMessage());
    }

    @Test
    void getFilesInTheSourceThrowsIllegalArgumentExceptionIfInputIsNotADirectory() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            sut.getFilesInTheSource("src/test/resources/example-boards/boards-1.json");
        });
        assertEquals("Source doesn't exist or is not a directory", exception.getMessage());
    }

    @Test
    void getFilesInTheSourceListsJsonFilesInTheDirectory() {
        var folder = "src/test/resources/example-boards";
        var files = sut.getFilesInTheSource(folder);
        assertIterableEquals(List.of(folder + "/boards-1.json", folder + "/boards-2.JSON"), files);
    }

    @Test
    void getFilesFromTheBoardThrowsIllegalArgumentExceptionIfFileIsInvalid() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            sut.getBoardsFromTheFile("");
        });
        assertEquals("Invalid file provided", exception.getMessage());
    }

    @Test
    void getFilesFromTheBoardThrowsIllegalArgumentExceptionIfFileIsNotJson() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            sut.getBoardsFromTheFile("file.txt");
        });
        assertEquals("Invalid file provided", exception.getMessage());
    }

    @Test
    void getFilesFromTheBoardThrowsIllegalArgumentExceptionIfFileDoesNotExist() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            sut.getBoardsFromTheFile("file.txt");
        });
        assertEquals("Invalid file provided", exception.getMessage());
    }

    @Test
    void getFilesFromTheBoardReadsBoardsFromFile() {
        var boards = sut.getBoardsFromTheFile("src/test/resources/example-boards/boards-1.json");
        assertNotNull(boards);
        assertEquals(2, boards.size());
        assertIterableEquals(List.of(
                new Models.Board("B7-400X", "Boards R Us", "Cortex-M7", true),
                new Models.Board("Low_Power", "Tech Corp.", "Cortex-M0+", false)), boards);
    }
}