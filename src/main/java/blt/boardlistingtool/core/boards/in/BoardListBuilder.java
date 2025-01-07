package blt.boardlistingtool.core.boards.in;

import blt.boardlistingtool.core.boards.Models;

public interface BoardListBuilder {

    Models.BuildBoardListResult buildBoardList(String boardDataLocation);

}
