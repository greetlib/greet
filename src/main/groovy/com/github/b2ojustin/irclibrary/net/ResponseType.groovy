package com.github.b2ojustin.irclibrary.net

@SuppressWarnings("SpellCheckingInspection")
public enum ResponseType {
    WELCOME(001), YOUR_HOST(002), CREATED(003), MY_INFO(004),
    UMODEIS(221), LUSERCLIENT(251), LUSER_OP(252), LUSER_CHANNELS(254),
    LUSERME(255), ADMIN_ME(256), TRY_AGAIN(263), LOCAL_USERS(265), GLOBAL_USERS(266),
    STATSCONN(250), UN_AWAY(305), NOW_AWAY(306), WHOIS_USER(311), WHOIS_SERVER(312),
    WHO_WAS_USER(314), END_OF_WHO(315), WHOIS_CHANOP(316), WHOIS_IDLE(317), END_OF_WHOIS(318),
    WHOIS_CHANNELS(319), LIST_START(321), LIST(322), LISTEND(323), CHANNEL_MODE_IS(324),
    NO_TOPIC(331), TOPIC(332), INVITING(341), INVITED(345), INVITE_LIST(346), END_OF_INVITE_LIST(347),
    VERSION(351), WHO_REPLY(352), NAM_REPLY(353), LINKS(364), END_OF_LINKS(365), END_OF_NAMES(366),
    BAN_LIST(367), END_OF_BAN_LIST(368), END_OF_WHO_WAS(369), INFO(371), MOTD(372), INFO_START(373),
    END_OF_INFO(374), MOTD_START(375), END_OF_MOTD(376), TIME(391), USERS_START(392), USERS(393),
    END_OF_USERS(394), NO_USERS(395), ERR_UNKNOWN_ERROR(401), ERR_NO_SUCH_NICK(401), ERR_NO_SUCH_SERVER(402),
    ERR_NO_SUCH_CHANNEL(403), ERR_CANNOT_SEND_TO_CHAN(404), ERR_WAS_NO_SUCH_NICK(406), ERR_TOO_MANY_TARGETS(407),
    NO_SUCH_SERVICE(408), ERR_NO_ORIGIN(409), ERR_NO_RECIPIENT(411), ERR_NO_TEXT_TO_SEND(412),
    ERR_NO_TOP_LEVEL(413), ERR_WILD_TOP_LEVEL(414), ERR_BADMASK(415), ERR_TOO_MANY_MATCHES(416),
    ERR_UNKNOWN_COMMAND(421), ERR_NO_MOTD(422), ERR_NO_ADMIN_INFO(423), ERR_NO_NICKNAME_GIVE(431),
    ERR_ERRONEUS_NICKNAME(423), ERR_NICKNAME_IN_USE(433),
    CMD_NOTICE("NOTICE")

    def rCode
    private ResponseType(def rCode) {
        this.rCode = rCode
    }

    static ResponseType byCode(def rCode) {
        values().each { if(it.rCode == rCode) return it }
        return null
    }
}