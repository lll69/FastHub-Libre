package lll69.fasthub;

import com.fastaccess.data.dao.model.PullRequest;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UrlConverter {
    private static Pattern PULL_API_PATTERN;

    private static String computeHtmlUrl(PullRequest obj) {
        String url = obj.getUrl();
        if (url == null) {
            return null;
        }
        if (PULL_API_PATTERN == null) {
            PULL_API_PATTERN = Pattern.compile("https://api\\.github\\.com/repos/([^/]+)/([^/]+)/pulls/(\\d+)/?");
        }
        Matcher matcher = PULL_API_PATTERN.matcher(url);
        if (matcher.find()) {
            String owner = matcher.group(1);
            String repo = matcher.group(2);
            String pull = matcher.group(3);
            return "https://github.com/" + owner + "/" + repo + "/pull/" + pull;
        }
        return null;
    }

    public static String getHtmlUrl(PullRequest obj) {
        String result = obj.getHtmlUrl();
        if (result != null) {
            return result;
        }
        result = computeHtmlUrl(obj);
        if (result != null) {
            obj.setHtmlUrl(result);
        }
        return result;
    }
}
