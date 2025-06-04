package gitlet.tools;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

public class GitIgnoreSet {
    private static Set<String> ignoreSet;

    public GitIgnoreSet() throws IOException {
        ignoreSet = new HashSet<>();

        byte[] bytes = Files.readAllBytes(Path.of("./.gitlet/.gitletIgnore"));
        String cont = new String(bytes);
        String[] lines = cont.split("\\R");
        for (String line : lines) {
            if (!line.isBlank()) {
                ignoreSet.add(line.trim());
            }
        }
    }

    public boolean contains(String filename) {
        return ignoreSet.contains(filename);
    }

    public Set<String> getIgnoreSet() {
        return ignoreSet;
    }
}
