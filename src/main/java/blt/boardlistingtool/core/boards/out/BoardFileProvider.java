package blt.boardlistingtool.core.boards.out;

import blt.boardlistingtool.core.boards.Models;

import java.util.List;

public interface BoardFileProvider {

    List<String> getFilesInTheSource(String sourceFolder);

    List<Models.Board> getBoardsFromTheFile(String fileName);

}
