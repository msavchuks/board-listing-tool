package blt.boardlistingtool.cli;

import org.springframework.boot.ExitCodeGenerator;

public class CommandNotAbleToCompleteException extends RuntimeException implements ExitCodeGenerator {

    CommandNotAbleToCompleteException() {}

    CommandNotAbleToCompleteException(Exception e) {
        super(e);
    }

    @Override
    public int getExitCode() {
        return 1;
    }
}
