package blt.boardlistingtool.core.boards;

import java.util.SortedSet;

public class Models {
    public record Board(String name, String vendor, String core, Boolean hasWifi) {
    }

    public record Metadata(Integer totalVendors, Integer totalBoards) {
    }

    public record BuildBoardListResult(Metadata metadata, SortedSet<Board> boards) {
    }
}
