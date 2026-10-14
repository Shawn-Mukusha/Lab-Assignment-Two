import java.util.ArrayList;
import java.util.List;

/** Single-line CSV with quoted commas and doubled quotes. Names cannot contain newlines. */
final class Csv {
    private Csv() { }
    static String row(String... values) {
        List<String> fields = new ArrayList<>();
        for (String value : values) fields.add("\"" + value.replace("\"", "\"\"") + "\"");
        return String.join(",", fields);
    }
    static List<String> parse(String line) {
        List<String> result = new ArrayList<>();
        StringBuilder field = new StringBuilder();
        boolean quoted = false, closed = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (quoted) {
                if (c == '"') {
                    if (i + 1 < line.length() && line.charAt(i + 1) == '"') { field.append('"'); i++; }
                    else { quoted = false; closed = true; }
                } else field.append(c);
            } else if (c == ',') {
                result.add(field.toString()); field.setLength(0); closed = false;
            } else if (c == '"' && field.length() == 0 && !closed) {
                quoted = true;
            } else {
                if (closed || c == '"') throw new IllegalArgumentException("Invalid CSV quoting");
                field.append(c);
            }
        }
        if (quoted) throw new IllegalArgumentException("Unclosed CSV quote");
        result.add(field.toString());
        return result;
    }
}
