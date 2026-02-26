package data;

import java.util.HashMap;
import java.util.Map;

/**
 * Not currently in use, might code a custom Data handler down the line
 */
class IndexNode {
    private boolean isEndOfWord;
    private Map<Character, IndexNode> children;
    /**
     * Not currently in use, might code a custom Data handler down the line
     */
    public IndexNode() {
        children = new HashMap<>();
        isEndOfWord = false;
    }
    /**
     * Not currently in use, might code a custom Data handler down the line
     */
    public Map<Character, IndexNode> getChildren() {
        return this.children;
    }
    /**
     * Not currently in use, might code a custom Data handler down the line
     */
    public boolean isEndOfWord() {
        return this.isEndOfWord;
    }
    /**
     * Not currently in use, might code a custom Data handler down the line
     */
    public void setEndOfWord() {
        this.isEndOfWord = true;
    }
}
/**
 * Not currently in use, might code a custom Data handler down the line
 */
public class Index {
    private final IndexNode root;
    private final String indexFile;
    /**
     * Not currently in use, might code a custom Data handler down the line
     */
    public Index(String indexFile) {
        root = new IndexNode();
        this.indexFile = indexFile;
    }
    /**
     * Not currently in use, might code a custom Data handler down the line
     */
    // Insert a word into the Index
    public void insert(String word) {
        if (!search(word)) {
            IndexNode node = root;
            for (char ch : word.toCharArray()) {
                node.getChildren().putIfAbsent(ch, new IndexNode());
                node = node.getChildren().get(ch);
            }
            node.setEndOfWord();
            Data.writeFile(indexFile, word);
        }
    }
    /**
     * Not currently in use, might code a custom Data handler down the line
     */
    // Search for a complete word
    public boolean search(String word) {
        IndexNode node = root;
        for (char ch : word.toCharArray()) {
            if (!node.getChildren().containsKey(ch)) {
                return false;
            }
            node = node.getChildren().get(ch);
        }
        return node.isEndOfWord();
    }
    /**
     * Not currently in use, might code a custom Data handler down the line
     */
    // Check if any word starts with the given prefix
    public boolean startsWith(String prefix) {
        IndexNode node = root;
        for (char ch : prefix.toCharArray()) {
            if (!node.getChildren().containsKey(ch)) {
                return false;
            }
            node = node.getChildren().get(ch);
        }
        return true;
    }
}
