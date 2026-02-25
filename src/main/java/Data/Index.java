package Data;

import java.util.HashMap;
import java.util.Map;

class IndexNode {
    Map<Character, IndexNode> children;
    boolean isEndOfWord;
    public IndexNode() {
        children = new HashMap<>();
        isEndOfWord = false;
    }
}

public class Index {
    private final IndexNode root;
    private final String indexFile;
    public Index(String indexFile) {
        root = new IndexNode();
        this.indexFile = indexFile;
    }

    // Insert a word into the Index
    public void insert(String word) {
        if (!search(word)) {
            IndexNode node = root;
            for (char ch : word.toCharArray()) {
                node.children.putIfAbsent(ch, new IndexNode());
                node = node.children.get(ch);
            }
            node.isEndOfWord = true;
            Data.writeFile(indexFile, word);
        }
    }

    // Search for a complete word
    public boolean search(String word) {
        IndexNode node = root;
        for (char ch : word.toCharArray()) {
            if (!node.children.containsKey(ch)) {
                return false;
            }
            node = node.children.get(ch);
        }
        return node.isEndOfWord;
    }

    // Check if any word starts with the given prefix
    public boolean startsWith(String prefix) {
        IndexNode node = root;
        for (char ch : prefix.toCharArray()) {
            if (!node.children.containsKey(ch)) {
                return false;
            }
            node = node.children.get(ch);
        }
        return true;
    }
}