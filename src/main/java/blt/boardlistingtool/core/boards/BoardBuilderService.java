package blt.boardlistingtool.core.boards;

import blt.boardlistingtool.core.boards.in.BoardListBuilder;
import blt.boardlistingtool.core.boards.out.BoardFileProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.TreeSet;


public class BoardBuilderService implements BoardListBuilder {

    private final static Logger LOGGER = LoggerFactory.getLogger(BoardBuilderService.class);

    public BoardBuilderService(BoardFileProvider fileProvider) {
        this.fileProvider = fileProvider;
    }

    BoardFileProvider fileProvider;

    @Override
    public Models.BuildBoardListResult buildBoardList(String boardDataLocation) {
        HashSet<String> allVendors = new HashSet<>();
        TreeSet<Models.Board> allBoards = new TreeSet<>((Models.Board d1, Models.Board d2) -> {
            var difference = d1.vendor().compareTo(d2.vendor());
            if (difference == 0) {
                difference = d1.name().compareTo(d2.name());
            }
            if (difference == 0 && !d1.equals(d2)) {
                LOGGER.warn("{} and {} have matching name and vendor but different properties", d1, d2);
                difference = d1.core().compareTo(d2.core());
                if (difference == 0) {
                    return d1.hasWifi().compareTo(d2.hasWifi());
                }
            }
            return difference;
        });


        var files = fileProvider.getFilesInTheSource(boardDataLocation);

        for (var file : files) {
            LOGGER.debug("Processing file {}", file);
            var boards = fileProvider.getBoardsFromTheFile(file);
            for (var board : boards) {
                LOGGER.debug("Processing board {}", board);
                allBoards.add(board);
                allVendors.add(board.vendor());
            }
        }

        return new Models.BuildBoardListResult(new Models.Metadata(allVendors.size(), allBoards.size()), allBoards);
    }
}
