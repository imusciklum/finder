package reader;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ReaderImpl implements Reader {

    private final BufferedReader br;

    public ReaderImpl(String source) throws FileNotFoundException {
        this.br = new BufferedReader(new FileReader(source));
    }

    @Override
    public void close() throws IOException {
        br.close();
    }

    @Override
    public List<String> readLines(int countOfLines) throws IOException {
        if (countOfLines <= 0) {
            throw new IllegalArgumentException("countOfLines should be greater than 0");
        }
        ArrayList<String> lines = new ArrayList<>();
        while (lines.size() < countOfLines) {
            String line = br.readLine();
            if (line == null) break;

            lines.add(line);
        }
        return lines;
    }
}
