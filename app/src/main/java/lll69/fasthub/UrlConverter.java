package lll69.fasthub;

import android.net.Uri;
import com.fastaccess.data.dao.model.PullRequest;
import com.fastaccess.provider.scheme.LinkParserHelper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UrlConverter {
    private static Pattern PULL_API_PATTERN;
    private static Pattern RAW_URL_PATTERN;

    private static String computeHtmlUrl(PullRequest obj) {
        String url = obj.getUrl();
        if (url == null) {
            return null;
        }
        if (PULL_API_PATTERN == null) {
            PULL_API_PATTERN = Pattern.compile("^https://api\\.github\\.com/repos/([^/]+)/([^/]+)/pulls/(\\d+)/?$");
        }
        Matcher matcher = PULL_API_PATTERN.matcher(url);
        if (matcher.matches()) {
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

    public static String convertRawUrlToApiUrl(String url) {
        if (url == null) {
            return null;
        }
        if (RAW_URL_PATTERN == null) {
            RAW_URL_PATTERN = Pattern.compile("^https://raw\\.githubusercontent\\.com/([^/]+)/([^/]+)/([^/]+)/(.+)$");
        }
        Matcher matcher = RAW_URL_PATTERN.matcher(url);
        if (matcher.matches()) {
            String owner = matcher.group(1);
            String repo = matcher.group(2);
            String branch = matcher.group(3);
            String path = matcher.group(4);

            Uri.Builder urlBuilder = new Uri.Builder();
            urlBuilder.scheme("https");
            urlBuilder.authority(LinkParserHelper.API_AUTHORITY);
            urlBuilder.appendPath("repos");
            urlBuilder.appendPath(owner);
            urlBuilder.appendPath(repo);
            urlBuilder.appendPath("contents");
            urlBuilder.appendEncodedPath(path);
            urlBuilder.appendQueryParameter("ref", branch);
            return urlBuilder.toString();
        }
        return url;
    }
}
