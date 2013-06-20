package org.scribe.examples;

import java.util.Scanner;

import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.Google2Api;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

public class Google2Example
{
    private static final String NETWORK_NAME = "Google";
    private static final String PROTECTED_RESOURCE_URL = "https://www.googleapis.com/oauth2/v1/userinfo?alt=json";
    private static final String SCOPE = "https://mail.google.com/ https://www.googleapis.com/auth/userinfo.email";
    private static final Token EMPTY_TOKEN = null;

    public static void main(String[] args)
    {
        Token accessToken = null;
        Verifier verifier = null;
        boolean refresh = true;

        // Put your own API key here.
        // String apiKey = "407408718192.apps.googleusercontent.com"; // Demo
        String apiKey = "800271916124.apps.googleusercontent.com";
        // String apiKey = "800271916124-5d34la28dceuvqdou7ofrp72cprh0j03.apps.googleusercontent.com";
        String apiSecret = "";
        // String apiSecret = "f4wCFUy3vqSFOokFPzhL_fO9";
        // String callbackUrl = "https://developers.google.com/oauthplayground"; // Demo
        String callbackUrl = "http://localhost/";
        // String callbackUrl = "http://relayme.tinywebgears.com/oauth2callback";

        OAuthService service = new ServiceBuilder().provider(Google2Api.class).apiKey(apiKey).apiSecret(apiSecret)
                .callback(callbackUrl).scope(SCOPE).offline(true).build();
        Scanner in = new Scanner(System.in);

        System.out.println("=== " + NETWORK_NAME + "'s OAuth Workflow ===");
        System.out.println();

        if (refresh)
        {
            accessToken = new Token("ya29.AHES6ZSxvPdXzZIHEVvA2OjeNwlYLM2FyC16U2R10MkKuIpvuAmZQg",
                    "1/UstOOW3JTfI_TbfRscwVVdAhguTNoSHSRcFXNOy4ZEQ");
        }
        else
        {
            // Obtain the Authorization URL
            System.out.println("Fetching the Authorization URL...");
            String authorizationUrl = service.getAuthorizationUrl(EMPTY_TOKEN);
            System.out.println("Got the Authorization URL!");
            System.out.println("Now go and authorize Scribe here:");
            System.out.println(authorizationUrl);
            System.out.println("And paste the authorization code here");
            System.out.print(">>");
            verifier = new Verifier(in.nextLine());
            System.out.println();

            // Trade the Request Token and Verfier for the Access Token
            System.out.println("Trading the Request Token for an Access Token...");
            accessToken = service.getAccessToken(EMPTY_TOKEN, verifier);
            System.out.println("Got the Access Token!");
            System.out.println("(if your curious it looks like this: " + accessToken + " )");
            System.out.println();
        }

        try
        {
            // Trade the Refresh Token for a new Access Token
            System.out.println("Trading the Refresh Token for a new Access Token...");
            Token newAccessToken = service.getAccessToken(accessToken, verifier);
            System.out.println("Got the Access Token!");
            System.out.println("(if your curious it looks like this: " + newAccessToken + " )");
            System.out.println();
            accessToken = newAccessToken;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        // Now let's go and ask for a protected resource!
        System.out.println("Now we're going to access a protected resource...");
        OAuthRequest request = new OAuthRequest(Verb.GET, PROTECTED_RESOURCE_URL);
        service.signRequest(accessToken, request);
        Response response = request.send();
        System.out.println("Got it! Lets see what we found...");
        System.out.println();
        System.out.println(response.getCode());
        System.out.println(response.getBody());

        System.out.println();
        System.out.println("Thats it man! Go and build something awesome with Scribe! :)");

    }
}