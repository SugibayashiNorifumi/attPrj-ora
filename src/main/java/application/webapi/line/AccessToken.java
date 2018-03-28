package application.webapi.line;

import java.io.Serializable;

public final class AccessToken implements Serializable {

    public final String scope;
    public final String access_token;
    public final String token_type;
    public final Integer expires_in;
    public final String refresh_token;
    public final String id_token;

    public AccessToken(String scope, String access_token, String token_type, Integer expires_in, String refresh_token,
            String id_token) {
        this.scope = scope;
        this.access_token = access_token;
        this.token_type = token_type;
        this.expires_in = expires_in;
        this.refresh_token = refresh_token;
        this.id_token = id_token;
    }

    @Override
    public String toString() {
        return "AccessToken [scope=" + scope + ", access_token=" + access_token + ", token_type=" + token_type
                + ", expires_in=" + expires_in + ", refresh_token=" + refresh_token + ", id_token=" + id_token + "]";
    }

}
