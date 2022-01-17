package reader;

import java.io.IOException;
import java.util.List;

public interface Reader extends AutoCloseable {

    List<String> readLines(int countOfLines) throws IOException;

}
