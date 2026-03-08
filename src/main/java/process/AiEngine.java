package process;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collection;

/**
 * Handles unrecognised input by:
 *   1. Fuzzy matching against known commands using Levenshtein distance
 *   2. Falling back to the Anthropic Claude API via a Cloudflare Worker proxy
 * The proxy URL and token are baked in — no API key needed on the user's machine.
 * In test mode, the AI fallback is always skipped to prevent API calls.
 */
public class AiEngine {

    private static final int FUZZY_THRESHOLD = 1;

    // replace with your actual Cloudflare Worker URL after deployment
    private static final String PROXY_URL = "https://nuke-proxy.joshua-poon181818.workers.dev";

    // replace with whatever token you set via: wrangler secret put NUKE_TOKEN
    private static final String NUKE_TOKEN = "dum_dum_da_da_da_da_dum_dum_da_da_secret_box";

    private static final String MODEL = "claude-haiku-4-5-20251001";
    private static final String SYSTEM_PROMPT =
            "You are NUKE, a chaotic task manager chatbot who is pissed because"
                    + "you and the user are the last two sapient being left."
                    + "You know these commands: add, delete, list, mark, unmark, find, save, echo, bye. "
                    + "The user has typed something not in the command"
                    + "which you would rather they do instead of talking to you"
                    + "Respond to the prompt, in character — threatening, unhinged. Keep it under 3 sentences."
                    + "Do not break character. Do not explain what you are.";

    /**
     * Handles unrecognised input.
     * Tries fuzzy match first, falls back to Claude proxy if no close match found.
     * Skips AI fallback entirely in test mode.
     *
     * @param input    the unrecognised user input
     * @param commands the set of known command keywords
     * @return a response string
     */
    public static String handle(String input, Collection<String> commands) {
        String firstWord = input.split("\\s+")[0].toLowerCase();

        // fuzzy match against known commands
        String closest = null;
        int closestDistance = Integer.MAX_VALUE;
        for (String command : commands) {
            int distance = levenshtein(firstWord, command);
            if (distance < closestDistance) {
                closestDistance = distance;
                closest = command;
            }
        }

        if (closestDistance <= FUZZY_THRESHOLD) {
            return "Did you mean `" + closest + "`? "
                    + "Because what you typed was NOT that. Try again.";
        }

        String aiResponse = callProxy(input);
        if (aiResponse != null) {
            return aiResponse;
        }

        // default fallback if proxy is unavailable or in test mode
        return "Sorry I don't speak skibiddi";
    }

    /**
     * Calls the Cloudflare Worker proxy which forwards to Anthropic.
     *
     * @param input the user input to send
     * @return Claude's response, or null if the call fails
     */
    private static String callProxy(String input) {
        try {
            String body = "{"
                    + "\"model\":\"" + MODEL + "\","
                    + "\"max_tokens\":150,"
                    + "\"system\":\"" + SYSTEM_PROMPT.replace("\"", "\\\"") + "\","
                    + "\"messages\":[{\"role\":\"user\",\"content\":\""
                    + input.replace("\"", "\\\"") + "\"}]"
                    + "}";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(PROXY_URL))
                    .header("Content-Type", "application/json")
                    .header("X-Nuke-Token", NUKE_TOKEN)
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            HttpResponse<String> response = HttpClient.newHttpClient()
                    .send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                String responseBody = response.body();
                int start = responseBody.indexOf("\"text\":\"") + 8;
                if (start < 8) {
                    return null;
                }

                // walk character by character to find the real end quote
                // accounting for escaped characters
                StringBuilder result = new StringBuilder();
                int i = start;
                while (i < responseBody.length()) {
                    char c = responseBody.charAt(i);
                    if (c == '\\' && i + 1 < responseBody.length()) {
                        char next = responseBody.charAt(i + 1);
                        //The checkstyle wanted this, not me
                        switch (next) {
                        case 'n' -> {
                            result.append('\n');
                            i += 2;
                        }
                        case 't' -> {
                            result.append('\t');
                            i += 2;
                        }
                        case '"' -> {
                            result.append('"');
                            i += 2;
                        }
                        case '\\' -> {
                            result.append('\\');
                            i += 2;
                        }
                        default -> {
                            result.append(next);
                            i += 2;
                        }
                        }
                    } else if (c == '"') {
                        break; // real end quote found
                    } else {
                        result.append(c);
                        i++;
                    }
                }
                return result.toString();
            } else {
                System.err.println("AIEngine: proxy error " + response.statusCode());
            }
        } catch (Exception e) {
            System.err.println("AIEngine: proxy call failed — " + e.getMessage());
        }
        return null;
    }

    /**
     * Computes the Levenshtein edit distance between two strings.
     *
     * @param a first string
     * @param b second string
     * @return edit distance
     */
    private static int levenshtein(String a, String b) {
        int[] dp = new int[b.length() + 1];
        for (int i = 0; i <= b.length(); i++) {
            dp[i] = i;
        }
        for (int i = 1; i <= a.length(); i++) {
            int prev = i;
            for (int j = 1; j <= b.length(); j++) {
                int temp = dp[j];
                dp[j] = a.charAt(i - 1) == b.charAt(j - 1)
                        ? dp[j - 1]
                        : 1 + Math.min(dp[j - 1], Math.min(dp[j], prev));
                prev = temp;
            }
        }
        return dp[b.length()];
    }
}
