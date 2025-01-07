package blt.boardlistingtool.core.boards;

import blt.boardlistingtool.core.boards.out.BoardFileProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class BoardBuilderServiceTest {

    private BoardBuilderService sut;
    private BoardFileProvider mockBoardFileProvider;

    @BeforeEach
    void setUp() {
        mockBoardFileProvider = Mockito.mock(BoardFileProvider.class);
        sut = new BoardBuilderService(mockBoardFileProvider);
    }

    @Test
    void loadsBoardsFromTheSourceAndReturnsOrderedList() {
        String sourceUri = "example-boards";
        List<String> boardFiles = List.of("boards-1.json", "boards-2.JSON");
        List<Models.Board> boards1 = List.of(
                new Models.Board("A", "A", "CoreA", false),
                new Models.Board("B", "B", "CoreB", true)
        );
        List<Models.Board> boards2 = List.of(
                new Models.Board("B", "A", "CoreC", false)
        );
        when(mockBoardFileProvider.getFilesInTheSource(sourceUri)).thenReturn(boardFiles);
        when(mockBoardFileProvider.getBoardsFromTheFile(boardFiles.get(0))).thenReturn(boards1);
        when(mockBoardFileProvider.getBoardsFromTheFile(boardFiles.get(1))).thenReturn(boards2);

        Models.BuildBoardListResult result = sut.buildBoardList(sourceUri);
        assertNotNull(result);
        assertEquals(3, result.metadata().totalBoards());
        assertEquals(2, result.metadata().totalVendors());
        Models.Board[] boards = result.boards().toArray(new Models.Board[0]);
        assertEquals(3, boards.length);
        assertEquals(boards1.get(0), boards[0]);
        assertEquals(boards2.get(0), boards[1]);
        assertEquals(boards1.get(1), boards[2]);
    }

    @Test
    void loadsBoardsFromTheSourceAndFiltersOutDuplicates() {
        String sourceUri = "example-boards";
        List<String> boardFiles = List.of("boards-1.json", "boards-2.JSON");
        List<Models.Board> boards1 = List.of(
                new Models.Board("A", "A", "CoreA", false),
                new Models.Board("B", "B", "CoreB", true)
        );
        List<Models.Board> boards2 = List.of(
                new Models.Board("B", "A", "CoreC", false),
                new Models.Board("B", "B", "CoreB", true)
        );
        when(mockBoardFileProvider.getFilesInTheSource(sourceUri)).thenReturn(boardFiles);
        when(mockBoardFileProvider.getBoardsFromTheFile(boardFiles.get(0))).thenReturn(boards1);
        when(mockBoardFileProvider.getBoardsFromTheFile(boardFiles.get(1))).thenReturn(boards2);

        Models.BuildBoardListResult result = sut.buildBoardList(sourceUri);
        assertNotNull(result);
        assertEquals(3, result.metadata().totalBoards());
        assertEquals(2, result.metadata().totalVendors());
        Models.Board[] boards = result.boards().toArray(new Models.Board[0]);
        assertEquals(3, boards.length);
        assertEquals(boards1.get(0), boards[0]);
        assertEquals(boards2.get(0), boards[1]);
        assertEquals(boards1.get(1), boards[2]);
    }

    @Test
    void givenBoardsWithMatchingNameAndVendorButDifferentCoresKeepsBoth() {
        String sourceUri = "example-boards";
        List<String> boardFiles = List.of("boards-1.json", "boards-2.JSON");
        List<Models.Board> boards1 = List.of(
                new Models.Board("A", "A", "CoreA", false),
                new Models.Board("B", "B", "CoreB", true)
        );
        List<Models.Board> boards2 = List.of(
                new Models.Board("A", "A", "CoreC", false),
                new Models.Board("B", "B", "CoreB", true)
        );
        when(mockBoardFileProvider.getFilesInTheSource(sourceUri)).thenReturn(boardFiles);
        when(mockBoardFileProvider.getBoardsFromTheFile(boardFiles.get(0))).thenReturn(boards1);
        when(mockBoardFileProvider.getBoardsFromTheFile(boardFiles.get(1))).thenReturn(boards2);

        Models.BuildBoardListResult result = sut.buildBoardList(sourceUri);
        assertNotNull(result);
        assertEquals(3, result.metadata().totalBoards());
        assertEquals(2, result.metadata().totalVendors());
        Models.Board[] boards = result.boards().toArray(new Models.Board[0]);
        assertEquals(3, boards.length);
        assertEquals(boards1.get(0), boards[0]);
        assertEquals(boards2.get(0), boards[1]);
        assertEquals(boards1.get(1), boards[2]);
    }

}