package util;

import java.util.*;

public class TCProxy {

    private static final String HOME_PAGE_URL = "https://community.topcoder.com/";

    private static final String CHARSET = "UTF-8";

    public static String getMMStandings(int contestId, boolean finished, boolean useCache) {
        String standingsUrl = finished ?
                HOME_PAGE_URL + "longcontest/stats/?module=ViewOverview&rd=" + contestId :
                HOME_PAGE_URL + "longcontest/?module=ViewStandings&rd=" + contestId;
        StringBuilder builder = new StringBuilder(1 << 20);
        for (int i = 1; ; ) {
            String content = Utils.readUrl(standingsUrl + "&sc=&sd=&nr=100&sr=" + i, CHARSET, useCache);
            builder.append(content);
            if ((i += 100) > getNumCompetitors(content)) {
                return builder.toString();
            }
        }
    }

    public static List<Contestant> parseRankings(int id, boolean finished, boolean useCache) {
        String standings = TCProxy.getMMStandings(id, finished, useCache);

        List<Contestant> list = new ArrayList<>();

        int ind = 0;
        while (true) {
            ind = Utils.find(standings, ind, "tc?module=MemberProfile&amp;cr=");
            if (ind < 0) {
                break;
            }
            Member member = readMember(Utils.readInt(standings, ind));
            ind = Utils.find(standings, ind, "\" align=\"center\">");
            list.add(new Contestant(member, Utils.readInt(standings, ind)));
        }

        return list;
    }


    public static Member readMember(int id) {
        return readMember(getMemberProfileUrl(id));
    }

    public static Member readMember(String url) {
        String content = Utils.readUrl(url, CHARSET, true);

        int ind = 0;

        ind = content.indexOf("Marathon Matches Competitions");
        ind = content.indexOf("<span class=", ind);
        ind = content.indexOf('>', ind) + 1;
        int mmRating = Utils.readInt(content, ind);

        ind = content.indexOf("Volatility", ind);
        ind = content.indexOf("class=", ind);
        ind = content.indexOf('>', ind) + 1;
        int vol = Utils.readInt(content, ind);

        ind = content.indexOf("Competitions", ind);
        ind = content.indexOf("href=", ind);
        ind = content.indexOf('>', ind) + 1;
        int n = Utils.readInt(content, ind);

        ind = content.indexOf("http://www.topcoder.com/tc?module=MemberProfile&amp;cr=");
        ind = content.indexOf(">", ind) + 1;
        String handle = Utils.readString(content, ind);

        return new Member(handle, mmRating, vol, n);
    }


    private static int getNumCompetitors(String content) {
        String template = "Competitors:";
        return Utils.readInt(content, content.indexOf(template) + template.length());
    }

    private static String getMemberProfileUrl(int id) {
        return HOME_PAGE_URL + String.format("tc?module=MemberProfile&cr=%d&tab=long", id);
    }

}
