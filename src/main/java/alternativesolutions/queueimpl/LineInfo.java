package alternativesolutions.queueimpl;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LineInfo {
    private final String line;
    private final Integer lineIndex;
}
